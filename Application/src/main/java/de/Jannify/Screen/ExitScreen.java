package de.Jannify.Screen;

import de.Jannify.Main;
import de.Jannify.Sensors.GroveBridge;

public class ExitScreen implements Screen {
    private final GroveBridge groveBridge;

    private double time = 3;

    public ExitScreen() {
        groveBridge = Main.grove;
    }

    @Override public String getName() {
        return "Herunterfahren";
    }

    @Override
    public void execute(ScreenController controller, Object monitor) {
        try {
            groveBridge.setLcdText("Abbrechen? \n" + String.format("%.1f", time) + " Sekunden");

            if (time < 0) {
                System.exit(202);
            }
            time -= 0.1;
            monitor.wait(100);
        } catch (InterruptedException ex) {
            Main.logger.severe(ex.getMessage());
        }
    }

    @Override public void buttonDown(ScreenController controller) {
        time = 3;
        controller.openScreenSelector();
    }
}
