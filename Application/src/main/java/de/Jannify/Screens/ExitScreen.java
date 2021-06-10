package de.Jannify.Screens;

import de.Jannify.Main;

public class ExitScreen implements Screen {
    private double time = 3;

    @Override public String getName() {
        return "Herunterfahren";
    }

    @Override
    public void execute(ScreenController controller, Object monitor) {
        try {
            Main.sensorInterface.setLcdText("Abbrechen? \n" + String.format("%.1f", time) + " Sekunden");

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
