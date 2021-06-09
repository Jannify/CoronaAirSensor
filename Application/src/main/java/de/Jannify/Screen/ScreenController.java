package de.Jannify.Screen;

import de.Jannify.Main;
import org.iot.raspberry.grovepi.GroveDigitalInListener;

public class ScreenController implements Runnable, GroveDigitalInListener {
    private final Object monitor = new Object();

    public final Screen[] screens;
    private final Screen selector;
    private Screen selectedScreen;

    public ScreenController() {
        screens = new Screen[]{new SensorValueScreen(), new OptionsScreen(), new ExitScreen()};
        selectedScreen = selector = new SelectorScreen(screens.length);
    }

    @Override
    public void run() {
        Main.grove.setButtonListener(this);

        if (selectedScreen != null) {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (monitor) {
                    selectedScreen.execute(this, monitor);
                }
            }
        }
    }

    public void openScreen(Screen screen) {
        selectedScreen = screen;
        Main.logger.info("Opening: " + screen.getClass().getSimpleName());
    }

    public void openScreenSelector() {
        openScreen(selector);
    }


    @Override public void onChange(boolean oldValue, boolean newValue) {
        if (selectedScreen != null && newValue) {
            System.out.println("Pressed");
            selectedScreen.buttonDown(this);
        }
    }
}
