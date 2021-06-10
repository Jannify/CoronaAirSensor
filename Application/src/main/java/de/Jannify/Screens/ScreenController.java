package de.Jannify.Screens;

import de.Jannify.Main;
import de.Jannify.Sensors.SensorInterface;
import org.iot.raspberry.grovepi.GroveDigitalInListener;
import org.iot.raspberry.grovepi.devices.GroveInputDeviceListener;
import org.iot.raspberry.grovepi.devices.GroveRotaryValue;

import java.awt.*;

public class ScreenController extends Thread implements GroveDigitalInListener, GroveInputDeviceListener<GroveRotaryValue> {
    private final Object monitor = new Object();

    private static SensorInterface sensorInterface;
    public final Screen[] screens;
    private final Screen selector;
    private Screen selectedScreen;

    private static double rotationFactor;

    public ScreenController() {
        sensorInterface = Main.sensorInterface;
        screens = new Screen[]{new SensorValueScreen(), new OptionsScreen(), new ExitScreen()};
        selectedScreen = selector = new SelectorScreen(screens.length);
    }

    @Override
    public void run() {
        sensorInterface.setButtonListener(this);
        sensorInterface.setPotentiometerListener(this);

        if (selectedScreen != null) {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (monitor) {
                    selectedScreen.execute(this, monitor);
                }
            }
        }
    }

    public void openScreenSelector() {
        openScreen(selector);
    }

    public void openScreen(Screen screen) {
        selectedScreen = screen;
        Main.logger.info("Opening: " + screen.getClass().getSimpleName());
    }

    /**
     * Triggers if the button is pressed/released
     */
    @Override
    public void onChange(boolean oldValue, boolean newValue) {
        if (selectedScreen != null && newValue) {
            selectedScreen.buttonDown(this);
        }
    }

    /**
     * Triggers if the potentiometer changes
     */
    @Override
    public void onChange(GroveRotaryValue newValue) {
        rotationFactor = newValue.getFactor();
    }

    public static double getRotationFactor() {
        return rotationFactor;
    }

    static void setLcdText(String text) {
        sensorInterface.setLcdText(text);
    }

    static void setLcdColor(Color color) {
        sensorInterface.setLcdColor(color);
    }
}
