package de.Jannify.Screen;

import de.Jannify.Main;

public class ExitScreen implements Screen {
    private double time = 3;

    public String getName() {
        return "Herunterfahren";
    }

    public void execute(ScreenController controller) {
        try {
            Main.sensorInterface.updateLcdText("Abbrechen? \n" + String.format("%.1f", time) + " Sekunden");

            if (time < 0) {
                System.exit(202);
            }
            time -= 0.2;
            controller.wait(50); //plus 150 ms from setLcdText()
        } catch (InterruptedException ex) {
            Main.logger.severe(ex.getMessage());
        }
    }

    public void buttonDown(ScreenController controller) {
        controller.openScreenSelector();
    }

    public void reset(ScreenController controller) {
        time = 3;
    }
}
