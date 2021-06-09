package de.Jannify;

import de.Jannify.IO.Config;
import de.Jannify.Screen.ScreenController;
import de.Jannify.Sensors.GroveBridge;
import de.Jannify.Sensors.NovaPMSensor;
import de.Jannify.Sensors.SensorMeasuring;
import org.iot.raspberry.grovepi.pi4j.GroveGasSensorPi4J;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static Logger logger = Logger.getLogger("CoronaAirSensor");
    public static NovaPMSensor novaPMSensor = new NovaPMSensor();
    public static GroveBridge grove = new GroveBridge();
    public static SensorMeasuring sensorMeasuring = new SensorMeasuring();
    public static ScreenController screenController = new ScreenController();

    public static void main(String[] args) {
        Logger.getLogger("GrovePi").setLevel(Level.WARNING);
        Logger.getLogger("RaspberryPi").setLevel(Level.WARNING);
        Logger.getLogger("org.iot.raspberry.grovepi.pi4j").setLevel(Level.WARNING);
        Logger.getLogger("org.iot.raspberry.grovepi.pi4j.IO").setLevel(Level.WARNING);
        System.out.println("Starting CoronaAirSensor");

        Config.readConfig();
        novaPMSensor.open();
        grove.start();
        grove.setLcdColor(Config.getColorR(), Config.getColorG(), Config.getColorB());
        grove.setCO2Mode(GroveGasSensorPi4J.MODE_10S);

        Thread groveThread = new Thread(grove);
        groveThread.start();

        Thread measuringThread = new Thread(sensorMeasuring);
        measuringThread.start();

        Thread screenThread = new Thread(screenController);
        screenThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            sensorMeasuring.close();
            measuringThread.interrupt();
            screenThread.interrupt();
            grove.shutdown();
            novaPMSensor.close();
            Config.saveConfig();
        }));
    }
}
