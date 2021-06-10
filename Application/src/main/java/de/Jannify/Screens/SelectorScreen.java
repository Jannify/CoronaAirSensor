package de.Jannify.Screens;

import de.Jannify.Main;

public class SelectorScreen implements Screen {
    private final double potentialPerScreen;
    private final int screenLength;

    private int selectedScreen = -1;

    public SelectorScreen(int screenLength) {
        this.screenLength = screenLength;
        potentialPerScreen = 1.0 / screenLength;
    }

    @Override public String getName() {
        return "Selector";
    }

    @Override
    public void execute(ScreenController controller, Object monitor) {
        int newSelected = (int) (ScreenController.getRotationFactor() / potentialPerScreen);
        if (newSelected > screenLength - 1) {
            newSelected = screenLength - 1;
        } else if (newSelected < 0) {
            newSelected = 0;
        }

        if(selectedScreen != newSelected) {
            selectedScreen = newSelected;

            String text = controller.screens[selectedScreen].getName();
            if (selectedScreen + 1 < screenLength) {
                text += "\n" + controller.screens[selectedScreen + 1].getName();
            }

            ScreenController.setLcdText("->" + text);
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
