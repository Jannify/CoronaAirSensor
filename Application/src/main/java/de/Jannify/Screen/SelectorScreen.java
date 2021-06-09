package de.Jannify.Screen;

import de.Jannify.Main;
import de.Jannify.Sensors.GroveBridge;

public class SelectorScreen implements Screen {
    private final GroveBridge groveBridge;
    private final double potentialPerScreen;
    private final int screenLenght;

    private int selectedScreen = -1;

    public SelectorScreen(int screenLenght) {
        groveBridge = Main.grove;
        this.screenLenght = screenLenght;
        potentialPerScreen = 1.0 / screenLenght;
    }

    @Override public String getName() {
        return "Selector";
    }

    @Override
    public void execute(ScreenController controller, Object monitor) {
        int newSelected = (int) (groveBridge.getRotation() / potentialPerScreen);
        if (newSelected > screenLenght- 1) {
            newSelected = screenLenght - 1;
        } else if (newSelected < 0) {
            newSelected = 0;
        }

        if(selectedScreen != newSelected) {
            selectedScreen = newSelected;

            String text = controller.screens[selectedScreen].getName();
            if (selectedScreen + 1 < screenLenght) {
                text += "\n" + controller.screens[selectedScreen + 1].getName();
            }

            groveBridge.setLcdText("->" + text);
        }

        try {
            monitor.wait(200);
        } catch (InterruptedException ex) {
            Main.logger.severe(ex.getMessage());
        }
    }

    @Override public void buttonDown(ScreenController controller) {
        controller.openScreen(controller.screens[selectedScreen]);
        selectedScreen = -1;
    }
}
