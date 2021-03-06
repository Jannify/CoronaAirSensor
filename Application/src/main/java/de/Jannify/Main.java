package de.Jannify;

import de.Jannify.IO.Config;
import de.Jannify.Screen.ScreenController;
import de.Jannify.Sensors.SensorInterface;
import de.Jannify.Sensors.SensorMeasuring;
import de.Jannify.Utils.LogColorFormatter;
import org.iot.raspberry.grovepi.pi4j.GroveGasSensorPi4J;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static Logger logger;
    public static SensorInterface sensorInterface;
    public static SensorMeasuring sensorMeasuring;
    public static ScreenController screenController;

    private static boolean isClosed;

    public static void main(String[] args) {
        setupLogger();
        logger.info("Starting CoronaAirSensor");
        Config.readConfig();

        sensorInterface = new SensorInterface();
        sensorInterface.start();
        sensorInterface.updateLcdColor(Config.getColor());
        sensorInterface.updateCO2Mode(GroveGasSensorPi4J.MODE_10S);

        sensorMeasuring = new SensorMeasuring();
        sensorMeasuring.start();

        screenController = new ScreenController();
        screenController.start();
        logger.info("CoronaAirSensor is running");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping CoronaAirSensor");
            if(!isClosed) {
                close();
            }
            logger.info("CoronaAirSensor was stopped");
        }));
    }

    public static void close() {
        sensorInterface.interrupt();
        sensorMeasuring.interrupt();
        screenController.interrupt();

        sensorInterface.close();
        isClosed = true;
    }

    private static void setupLogger() {
        logger = Logger.getLogger("CoronaAirSensor");
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LogColorFormatter());
        logger.addHandler(handler);

        Logger.getLogger("GrovePi").setLevel(Level.WARNING);
        Logger.getLogger("RaspberryPi").setLevel(Level.WARNING);
        Logger.getLogger("org.iot.raspberry.grovepi.pi4j").setLevel(Level.WARNING);
        Logger.getLogger("org.iot.raspberry.grovepi.pi4j.IO").setLevel(Level.WARNING);
    }
}
