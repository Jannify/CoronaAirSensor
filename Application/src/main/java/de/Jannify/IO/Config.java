package de.Jannify.IO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.prefs.Preferences;

public class Config {
    private static final Preferences prefs = Preferences.systemNodeForPackage(Config.class);

    private static String path;
    private static int timeSync;
    private static int timeSave;
    private static int colorR;
    private static int colorG;
    private static int colorB;
    public static int mode;
    public static boolean ValueDisplayOn;

    public static void readConfig() {
        Properties props = new Properties();
        try (InputStream inStream = ClassLoader.getSystemResourceAsStream("config.properties")) {
            props.load(inStream);
            path = props.getProperty("Path");
            timeSync = Integer.parseInt(props.getProperty("TimeSync"));
            timeSave = Integer.parseInt(props.getProperty("TimeSave"));
            colorR = Integer.parseInt(props.getProperty("ColorR"));
            colorG = Integer.parseInt(props.getProperty("ColorG"));
            colorB = Integer.parseInt(props.getProperty("ColorB"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        mode = prefs.getInt("Mode", 0);
        ValueDisplayOn = prefs.getBoolean("ValueDisplayOn", true);
    }

    public static void saveConfig() {
        prefs.putInt("Mode", mode);
        prefs.putBoolean("ValueDisplayOn", ValueDisplayOn);
    }

    public static String getPath() {
        return path;
    }

    public static int getTimeSync() {
        return timeSync;
    }

    public static int getTimeSave() {
        return timeSave;
    }

    public static int getColorR() {
        return colorR;
    }

    public static int getColorG() {
        return colorG;
    }

    public static int getColorB() {
        return colorB;
    }
}
