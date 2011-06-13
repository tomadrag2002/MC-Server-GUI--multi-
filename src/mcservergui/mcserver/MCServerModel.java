/*
 * MCServerModel.java
 */

package mcservergui.mcserver;

import java.io.*;
import javax.swing.SwingUtilities;
import java.util.Observable;
import java.util.Observer;
import java.util.List;
import mcservergui.config.Config;
import org.hyperic.sigar.ptql.ProcessFinder;
import org.hyperic.sigar.Sigar;
import mcservergui.proxyserver.ProxyServer;
import mcservergui.gui.GUI;

/**
 *
 * @author dumptruckman
 */
public class MCServerModel extends Observable implements Observer, java.beans.PropertyChangeListener {
    
    public MCServerModel(Config newConfig)
    {
        config = newConfig;
        serverRunning = false;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    // Method for building the cmdLine
    public void setCmdLine(List<String> args) {
        cmdLine = args;
    }

    // Method for starting the server
    public String start() {
        File jar = new File(config.cmdLine.getServerJar());
        /*
        try {
            guiServer = new MCServerGUIHTTPServer(25566);
            guiServer.start();
        } catch (IOException ioe) {
            System.err.println("Could not start http server");
            return "ERROR";
        }
         */
        proxyServer = new ProxyServer(gui, config);
        try {
            // Run the server
            ProcessFinder pf = new ProcessFinder(new Sigar());
            ProcessBuilder pb = new ProcessBuilder(cmdLine);
            pb.redirectErrorStream(true);
            ps = null;
            try {
                long[] pidlistbefore = pf.find("State.Name.sw=java");
                ps = pb.start();
                long[] pidlistafter = pf.find("State.Name.sw=java");
                if (pidlistafter.length - pidlistbefore.length == 1) {
                    pid = pidlistafter[pidlistafter.length-1];
                    setChanged();
                    notifyObservers("pid");
                } else {
                    pid = 0;
                    setChanged();
                    notifyObservers("piderror");
                }
            } catch (UnsatisfiedLinkError ule) { 
                if (ps == null) {
                    ps = pb.start();
                }
            }
            
            // Flag this as started
            serverRunning = true;
            setChanged();
            notifyObservers("serverStarted");

            receivedFromServer = "";

            // Collect necessary streams
            br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            osw = new OutputStreamWriter(ps.getOutputStream());

            serverReceiver = new MCServerReceiver(br);
            serverReceiver.addObserver(this);
            addObserver(serverReceiver.backgroundWork);
           
            return "SUCCESS";
        } catch (Exception e) {
            System.out.println("Problem launching server");
            return "ERROR";
        }
    }

    @Override public void update(Observable o, Object arg) {
        receivedFromServer = serverReceiver.get();
        this.setChanged();
        notifyObservers("newOutput");
    }

    @Override public void propertyChange(java.beans.PropertyChangeEvent evt) {
        if (evt.getNewValue().equals(false)) {
            serverRunning = false;
            pid = 0;
            //guiServer.stop();
            proxyServer.stop();
            setChanged();
            notifyObservers("serverStopped");
            setChanged();
            notifyObservers("pid");
        }
    }

    public long getPid() {
        return pid;
    }

    public String getReceived() {
        return receivedFromServer;
    }

    public boolean isRunning() {
        return serverRunning;
    }

    // Method for sending commands to the server
    public void send(final String string) {
        Runnable serverSender = new Runnable() {
            public void run() {
                try {
                    osw.write(string + "\n");
                    osw.flush();
                } catch (IOException e) {
                    System.out.println("[GUI] Error sending server data.");
                }
            }
        };
        SwingUtilities.invokeLater(serverSender);
    }

    // Method for stopping server
    public void stop() {
        send("stop");
        MCServerStopper serverStopper = new MCServerStopper(ps, br, osw);
        serverStopper.addPropertyChangeListener(this);
        serverStopper.execute();
    }

    private Process ps;
    private long pid;
    private List<String> cmdLine;
    private String receivedFromServer;
    private BufferedReader br;
    private OutputStreamWriter osw;
    private MCServerReceiver serverReceiver;
    private Config config;
    private ProxyServer proxyServer;
    private boolean serverRunning;
    private GUI gui;
}
