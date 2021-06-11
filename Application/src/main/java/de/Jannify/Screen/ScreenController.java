package de.Jannify.Screen;

import de.Jannify.Main;
import de.Jannify.Sensors.SensorInterface;
import org.iot.raspberry.grovepi.GroveDigitalInListener;
import org.iot.raspberry.grovepi.devices.GroveInputDeviceListener;
import org.iot.raspberry.grovepi.devices.GroveRotaryValue;

import java.awt.*;

public class ScreenController extends Thread implements GroveDigitalInListener, GroveInputDeviceListener<GroveRotaryValue> {
    private static SensorInterface sensorInterface;
    public final Screen[] screens;
    private final Screen selector;
    private Screen selectedScreen;

    private static double rotationFactor;

    public ScreenController() {
        sensorInterface = Main.sensorInterface;
        screens = new Screen[]{new SensorValueScreen(), new MonitoringScreen(), new OptionsScreen(), new ExitScreen(), new RestartScreen()};
        selector = selectedScreen = new SelectorScreen(screens.length);
    }

    @Override
    public void run() {
        sensorInterface.registerButtonListener(this);
        sensorInterface.registerPotentiometerListener(this);

        if (selectedScreen != null) {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (this) {
                    selectedScreen.execute(this);
                }
            }
        }
    }

    public void openScreenSelector() {
        openScreen(selector);
    }

    public void openScreen(Screen screen) {
        selectedScreen.reset(this);
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

    double getRotationFactor() {
        return rotationFactor;
    }

    void updateLcdText(String text) {
        sensorInterface.updateLcdText(text);
    }

    void updateLcdColor(Color color) {
        sensorInterface.updateLcdColor(color);
    }
}
