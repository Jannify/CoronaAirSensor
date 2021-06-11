package de.Jannify.IO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.prefs.Preferences;

public class Config {
    private static final Preferences prefs = Preferences.systemNodeForPackage(Config.class);

    private static String path;
    private static int timeSync;
    private static int timeSave;
    private static Color color;

    private static int[] qualityGateCO2;
    private static int[] qualityGatePM2;
    private static int[] qualityGatePM10;

    public static void readConfig() {
        Properties props = new Properties();
        try {
            File propsFile = new File("defaultConfig.properties");
            if (propsFile.createNewFile()) {
                URL defaultConfigPath = Config.class.getClassLoader().getResource("defaultConfig.properties");
                if (defaultConfigPath == null)
                    throw new FileNotFoundException("Could not find \"defaultConfig.properties\" in resources.");
                FileUtils.writeStringToFile(propsFile, IOUtils.toString(defaultConfigPath, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            }

            try (InputStream inStream = new FileInputStream(propsFile)) {
                props.load(inStream);
                path = props.getProperty("Path");
                timeSync = Integer.parseInt(props.getProperty("TimeSync"));
                timeSave = Integer.parseInt(props.getProperty("TimeSave"));
                color = new Color(
                        Integer.parseInt(props.getProperty("ColorR")),
                        Integer.parseInt(props.getProperty("ColorG")),
                        Integer.parseInt(props.getProperty("ColorB"))
                );
                qualityGateCO2 = new int[]{
                        Integer.parseInt(props.getProperty("QualityCO2Good")),
                        Integer.parseInt(props.getProperty("QualityCO2Medium")),
                        Integer.parseInt(props.getProperty("QualityCO2Bad")),
                        Integer.MAX_VALUE
                };
                qualityGatePM2 = new int[]{
                        Integer.parseInt(props.getProperty("QualityPM2Good")),
                        Integer.parseInt(props.getProperty("QualityPM2Medium")),
                        Integer.parseInt(props.getProperty("QualityPM2Bad")),
                        Integer.MAX_VALUE
                };
                qualityGatePM10 = new int[]{
                        Integer.parseInt(props.getProperty("QualityPM10Good")),
                        Integer.parseInt(props.getProperty("QualityPM10Medium")),
                        Integer.parseInt(props.getProperty("QualityPM10Bad")),
                        Integer.MAX_VALUE
                };
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getPath() {
        return path;
    }

    public static int getTimeSyncMili() {

        return timeSync * 1000;
    }

    public static int getTimeSaveMili() {
        return timeSave * 1000;
    }

    public static Color getColor() {
        return color;
    }

    public static int[][] getQualityGates() {
        return new int[][]{qualityGateCO2, qualityGatePM2, qualityGatePM10};
    }


    public static boolean getDisplayOn() {
        return prefs.getBoolean("DisplayOn", true);
    }

    public static void putIntValue(String key, int value) {
        prefs.putInt(key, value);
    }

    public static void putBooleanValue(String key, boolean value) {
        prefs.putBoolean(key, value);
    }
}
