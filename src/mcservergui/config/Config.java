/*
 * Config.java
 */

package mcservergui.config;

import java.util.Arrays;
import mcservergui.task.event.EventModel;
import mcservergui.task.ServerWarning;
import org.codehaus.jackson.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author dumptruckman
 */
public class Config {

    public Config() {
        _windowTitle = "MC Server GUI";
        _inputHistoryMaxSize = 30;
        _extPort = 25565;
        cmdLine = new CMDLine();
        backups = new Backups();
        schedule = new Schedule();
        display = new Display();
    }
    
    private String _windowTitle;
    private int _inputHistoryMaxSize;
    private int _extPort;
    public CMDLine cmdLine;
    public Backups backups;
    public Schedule schedule;
    public Display display;

    public int getExtPort() { return _extPort; }
    public String getWindowTitle() { return _windowTitle; }
    public int getInputHistoryMaxSize() { return _inputHistoryMaxSize; }

    public void setExtPort(int i) { _extPort = i; }
    public void setWindowTitle(String s) { _windowTitle = s; }
    public void setInputHistoryMaxSize(int i) { _inputHistoryMaxSize = i; }

    public class Display {
        public Display() {
            _textColor = "000000";
            _bgColor = "FFFFFF";
            _infoColor = "339900";
            _warningColor = "CC6600";
            _severeColor = "FF0000";
            _textSize = 3;
        }
        private String _textColor, _bgColor, _infoColor, _warningColor, _severeColor;
        private int _textSize;

        public int getTextSize() {
            return _textSize;
        }

        public String getBgColor() {
            return _bgColor;
        }

        public String getInfoColor() {
            return _infoColor;
        }

        public String getSevereColor() {
            return _severeColor;
        }

        public String getTextColor() {
            return _textColor;
        }

        public String getWarningColor() {
            return _warningColor;
        }

        public void setTextSize(int _textSize) {
            this._textSize = _textSize;
        }

        public void setBgColor(String _bgColor) {
            this._bgColor = _bgColor;
        }

        public void setTextColor(String _textColor) {
            this._textColor = _textColor;
        }

        public void setSevereColor(String _severeColor) {
            this._severeColor = _severeColor;
        }

        public void setInfoColor(String _infoColor) {
            this._infoColor = _infoColor;
        }

        public void setWarningColor(String _warningColor) {
            this._warningColor = _warningColor;
        }
    }

    public class CMDLine {
        public CMDLine () {
            _javaExec = "java";
            _bukkit = true;
            _xmx = "1024M";
            _xincgc = true;
            _serverJar = "craftbukkit.jar";
            _extraArgs = "";
            _useCustomLaunch = false;
            _customLaunch = "";
        }

        private String _javaExec, _xmx, _serverJar, _extraArgs, _customLaunch;
        private boolean _bukkit, _xincgc, _useCustomLaunch;

        public String getJavaExec() { return _javaExec; }
        public String getXmx() { return _xmx; }
        public String getServerJar() { return _serverJar; }
        public String getExtraArgs() { return _extraArgs; }
        public String getCustomLaunch() { return _customLaunch; }
        public boolean getBukkit() { return _bukkit; }
        public boolean getXincgc() { return _xincgc; }
        public boolean getUseCustomLaunch() { return _useCustomLaunch; }

        public void setJavaExec(String s) { _javaExec = s; }
        public void setXmx(String s) { _xmx = s; }
        public void setServerJar(String s) { _serverJar = s; }
        public void setExtraArgs(String s) { _extraArgs = s; }
        public void setCustomLaunch(String s) { _customLaunch = s; }
        public void setBukkit(boolean b) { _bukkit = b; }
        public void setXincgc(boolean b) { _xincgc = b; }
        public void setUseCustomLaunch(boolean b) { _useCustomLaunch = b; }

        public List<String> getCmdLine() {
            List<String> cmdLine = new ArrayList<String>();
            if (!getUseCustomLaunch()) {
                cmdLine.add(getJavaExec());
                if (getBukkit()) {
                    cmdLine.add("-Djline.terminal=jline.UnsupportedTerminal");
                }
                cmdLine.add("-Xmx" + getXmx());
                if (getXincgc()) {
                    cmdLine.add("-Xincgc");
                }
                cmdLine.add("-jar");
                cmdLine.add(getServerJar());
                cmdLine.add("nogui");
                if (getBukkit()) {
                    cmdLine.add("-d");
                    cmdLine.add("\"yyyy-MM-dd HH:mm:ss\"");
                }
            } else {
                cmdLine.addAll(Arrays.asList(getCustomLaunch().split(" ")));
                if (getBukkit()) {
                    if (!cmdLine.contains("-Djline.terminal=jline.UnsupportedTerminal")) {
                        ArrayList<String> newCmdLine = new ArrayList<String>();
                        int index = cmdLine.indexOf("-jar");
                        int i = 0;
                        for (i = 0; i < index; i++) {
                            newCmdLine.add(cmdLine.get(i));
                        }
                        newCmdLine.add("-Djline.terminal=jline.UnsupportedTerminal");
                        for (int j = i; j < cmdLine.size(); j++) {
                            newCmdLine.add(cmdLine.get(j));
                        }
                        cmdLine = newCmdLine;
                    }
                    if (!cmdLine.contains("-d")) {
                        //List<String> newCmdLine = cmdLine;
                        cmdLine.add("-d");
                        cmdLine.add("\"yyyy-MM-dd HH:mm:ss\"");
                        //cmdLine = newCmdLine;
                    }
                }
            }
            return cmdLine;
        }

        public String parseCmdLine() {
            List<String> cmdLine = getCmdLine();
            StringBuilder parsedCmdLine = new StringBuilder();
            int i = 0;
            while (i < cmdLine.size()) {
                parsedCmdLine.append(cmdLine.get(i));
                parsedCmdLine.append(" ");
                i++;
            }
            return parsedCmdLine.toString();
        }
    }

    public class Backups {
        public Backups() {
            _zip = true;
            try {
                _path = new File(".").getCanonicalPath() + System.getProperty("file.separator") + "mcservergui-backups";
            } catch (IOException e) {
                _path = "";
                System.out.println("WARNING: Could not set a default backup path.");
            }
            _pathsToBackup = new ArrayList<String>();
        }

        private String _path;
        private boolean _zip;
        private List<String> _pathsToBackup;

        public String getPath() { return _path; }
        public boolean getZip() { return _zip; }
        public List<String> getPathsToBackup() { return _pathsToBackup; }

        public void setPath(String s) { _path = s; }
        public void setZip(boolean b) { _zip = b; }
        public void setPathsToBackup(List<String> l) { _pathsToBackup = l; }
    }

    public class Schedule {
        public Schedule() {
            events = new java.util.ArrayList<EventModel>();
        }

        private java.util.List<EventModel> events;

        public java.util.List<EventModel> getEvents() { return events; }

        public void setEvents(java.util.List<EventModel> e) { events = e; }
    }

    public boolean load() {
        File configFile = new File("guiconfig.json");

        // Check to make sure config file exist and if not, creates it.
        try {
            configFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Error creating file guiconfig.json!");
            return false;
        }

        //Read in the config file to string configData.
        StringBuilder temp = new StringBuilder();
        try {
            FileInputStream is = new FileInputStream("guiconfig.json");
            boolean eof = false;
            while(!eof) {
                try {
                    int Char = is.read();
                    if (Char != -1) {
                        temp.append((char)Char);
                    } else {
                        eof = true;
                    }
                } catch (IOException e) {
                    System.out.println("Error reading in data");
                }
            }
            try {
                is.close();
            } catch (IOException e) {
                System.out.println("Error closing FileInputStream!");
            }
        } catch (FileNotFoundException e) {
            System.out.println("guiconfig.json not found!");
            return false;
        }
        String configData = temp.toString();

        // Begin parsing json configData
        JsonFactory jf = new JsonFactory();
        try {
            JsonParser jp = jf.createJsonParser(configData);
            jp.nextToken();
            if (jp.getCurrentToken() != null) {
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    String fieldname = jp.getCurrentName();
                    jp.nextToken();
                    // General Config options
                    if ("Window Title".equals(fieldname)) {
                        setWindowTitle(jp.getText());
                    } else if ("Input History Max Size".equals(fieldname)) {
                        setInputHistoryMaxSize(jp.getIntValue());
                    } else if ("Display".equals(fieldname)) {
                        while (jp.nextToken() != JsonToken.END_OBJECT) {
                            String displayfield = jp.getCurrentName();
                            jp.nextToken();
                            if ("Text Color".equals(displayfield)) {
                                display.setTextColor(jp.getText());
                            } else if ("Background Color".equals(displayfield)) {
                                display.setBgColor(jp.getText());
                            } else if ("[INFO] Color".equals(displayfield)) {
                                display.setInfoColor(jp.getText());
                            } else if ("[WARNING] Color".equals(displayfield)) {
                                display.setWarningColor(jp.getText());
                            } else if ("[SEVERE] Color".equals(displayfield)) {
                                display.setSevereColor(jp.getText());
                            } else if ("Text Size".equals(displayfield)) {
                                display.setTextSize(jp.getNumberValue().intValue());
                            }
                        }
                    } else if ("CMD Line".equals(fieldname)) {
                        // CMD Line config options
                        while (jp.nextToken() != JsonToken.END_OBJECT) {
                            String cmdlinefield = jp.getCurrentName();
                            jp.nextToken();
                            if ("Java Executable".equals(cmdlinefield)) {
                                cmdLine.setJavaExec(jp.getText());
                            } else if ("Server Jar File".equals(cmdlinefield)) {
                                cmdLine.setServerJar(jp.getText());
                            } else if ("Bukkit".equals(cmdlinefield)) {
                                cmdLine.setBukkit(jp.getBooleanValue());
                            } else if ("Xmx Memory".equals(cmdlinefield)) {
                                cmdLine.setXmx(jp.getText());
                            } else if ("Xincgc".equals(cmdlinefield)) {
                                cmdLine.setXincgc(jp.getBooleanValue());
                            } else if ("Extra Arguments".equals(cmdlinefield)) {
                                cmdLine.setExtraArgs(jp.getText());
                            } else if ("Use Custom Launch".equals(cmdlinefield)) {
                                cmdLine.setUseCustomLaunch(jp.getBooleanValue());
                            } else if ("Custom Launch Line".equals(cmdlinefield)) {
                                cmdLine.setCustomLaunch(jp.getText());
                            }
                        }
                    } else if ("Backups".equals(fieldname)) {
                        while (jp.nextToken() != JsonToken.END_OBJECT) {
                            String backupfield = jp.getCurrentName();
                            jp.nextToken();
                            if ("Path".equals(backupfield)) {
                                backups.setPath(jp.getText());
                            } else if ("Zip Backup".equals(backupfield)) {
                                backups.setZip(jp.getBooleanValue());
                            } else if ("Paths to Backup".equals(backupfield)) {
                                List<String> pathlist = new ArrayList<String>();
                                jp.nextToken();
                                while (jp.getCurrentToken() != JsonToken.END_ARRAY) {
                                    pathlist.add(jp.getText());
                                    jp.nextToken();
                                }
                                backups.setPathsToBackup(pathlist);
                            }
                        }
                    } else if ("Schedule".equals(fieldname)) {
                        while (jp.nextToken() != JsonToken.END_OBJECT) {
                            String schedulefield = jp.getCurrentName();
                            jp.nextToken();
                            if ("Events".equals(schedulefield)) {
                                List<EventModel> eventlist =
                                        new ArrayList<EventModel>();
                                while (jp.nextToken() != JsonToken.END_OBJECT) {
                                    EventModel event =
                                            new EventModel();
                                    event.setName(jp.getCurrentName());
                                    while (jp.nextToken() != JsonToken.END_OBJECT) {
                                        String eventfield = jp.getCurrentName();
                                        //jp.nextToken();
                                        if ("Cron Expression".equals(eventfield)) {
                                            event.setCronEx(jp.getText());
                                        } else if("Task".equals(eventfield)) {
                                            event.setTask(jp.getText());
                                        } else if ("Parameters".equals(eventfield)) {
                                            List<String> params = new ArrayList<String>();
                                            jp.nextToken();
                                            jp.nextToken();
                                            while (jp.getCurrentToken() != JsonToken.END_ARRAY) {
                                                params.add(jp.getText());
                                                jp.nextToken();
                                            }
                                            event.setParams(params);
                                        } else if ("Warnings".equals(eventfield)) {
                                            List<ServerWarning> warninglist =
                                                    new ArrayList<ServerWarning>();
                                            while (jp.nextToken() != JsonToken.END_OBJECT) {
                                                if (jp.getCurrentToken().equals(JsonToken.START_ARRAY)) {
                                                    ServerWarning warning =
                                                            new ServerWarning();
                                                    jp.nextToken();
                                                    warning.setMessage(jp.getText());
                                                    jp.nextToken();
                                                    warning.setTime(jp.getNumberValue().intValue());
                                                    warninglist.add(warning);
                                                }
                                            }
                                            event.setWarningList(warninglist);
                                        }
                                    }
                                    eventlist.add(event);
                                }
                                schedule.setEvents(eventlist);
                            }
                        }
                    }
                }
            } else {
                return false;
            }
            jp.close();
            return true;
        } catch (IOException e) {
            System.out.println("Error creating JsonParser");
            return false;
        }
    }

    public void save() {
        new File(backups.getPath()).mkdir();
        JsonFactory jf = new JsonFactory();
        try {
            JsonGenerator jg = jf.createJsonGenerator(new File("guiconfig.json"), JsonEncoding.UTF8);
            jg.useDefaultPrettyPrinter();
            jg.writeStartObject();
            //General Config Options
            jg.writeStringField("Window Title", getWindowTitle());
            jg.writeNumberField("Input History Max Size", getInputHistoryMaxSize());
            jg.writeObjectFieldStart("Display");
            // Display Config Options
            jg.writeStringField("Text Color", display.getTextColor());
            jg.writeStringField("Background Color", display.getBgColor());
            jg.writeStringField("[INFO] Color", display.getInfoColor());
            jg.writeStringField("[WARNING] Color", display.getWarningColor());
            jg.writeStringField("[SEVERE] Color", display.getSevereColor());
            jg.writeNumberField("Text Size", display.getTextSize());
            jg.writeEndObject(); // End of Display Config options
            jg.writeObjectFieldStart("CMD Line");
            // CMD Line Config Options
            jg.writeStringField("Java Executable", cmdLine.getJavaExec());
            jg.writeStringField("Server Jar File", cmdLine.getServerJar());
            jg.writeBooleanField("Bukkit", cmdLine.getBukkit());
            jg.writeStringField("Xmx Memory", cmdLine.getXmx());
            jg.writeBooleanField("Xincgc", cmdLine.getXincgc());
            jg.writeStringField("Extra Arguments", cmdLine.getExtraArgs());
            jg.writeBooleanField("Use Custom Launch", cmdLine.getUseCustomLaunch());
            jg.writeStringField("Custom Launch Line", cmdLine.getCustomLaunch());
            jg.writeEndObject();  // End of CMD Line Config Options
            jg.writeObjectFieldStart("Backups");
            // Backups Config Options
            jg.writeStringField("Path", backups.getPath());
            jg.writeBooleanField("Zip Backup", backups.getZip());
            // Paths to Backup list
            jg.writeArrayFieldStart("Paths to Backup");
            for (int i = 0; i < backups.getPathsToBackup().size(); i++) {
                jg.writeString(backups.getPathsToBackup().get(i));
            }
            jg.writeEndArray(); //End of Paths to Backup list
            jg.writeEndObject();  // End of Backups Config Options
            // Schedule Config Options
            jg.writeObjectFieldStart("Schedule");
            // EventModel list
            jg.writeObjectFieldStart("Events");
            for (int i = 0; i < schedule.getEvents().size(); i++) {
                jg.writeObjectFieldStart(schedule.getEvents().get(i).getName());
                jg.writeStringField("Cron Expression", schedule.getEvents().get(i).getCronEx());
                jg.writeStringField("Task", schedule.getEvents().get(i).getTask());
                jg.writeArrayFieldStart("Parameters");
                for (int j = 0; j < schedule.getEvents().get(i).getParams().size(); j++) {
                    jg.writeString(schedule.getEvents().get(i).getParams().get(j));
                }
                jg.writeEndArray();
                jg.writeObjectFieldStart("Warnings");
                for (int j = 0; j < schedule.getEvents().get(i).getWarningList().size(); j++) {
                    jg.writeArrayFieldStart(String.valueOf(j+1));
                    jg.writeString(schedule.getEvents().get(i).getWarningList().get(j).getMessage());
                    jg.writeNumber(schedule.getEvents().get(i).getWarningList().get(j).getTime());
                    jg.writeEndArray();
                }
                jg.writeEndObject();
                jg.writeEndObject();
            }
            jg.writeEndObject();  // End of EventModel list;
            jg.writeEndObject();  // End of Schedule Config Options
            jg.writeEndObject();
            jg.close();
        } catch (IOException e) {
            System.out.println("Error saving guiconfig.json");
            return;
        }
    }
}
