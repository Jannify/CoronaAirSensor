package de.Jannify.Screen;

import de.Jannify.Main;

public class SelectorScreen implements Screen {
    private final double potentialPerScreen;
    private final int screenLength;

    private int selectedScreen = -1;

    public SelectorScreen(int screenLength) {
        this.screenLength = screenLength;
        potentialPerScreen = 1.0 / screenLength;
        reset(null);
    }

    public String getName() {
        return "Selector";
    }

    public void execute(ScreenController controller) {
        int newSelected = (int) (controller.getRotationFactor() / potentialPerScreen);
        newSelected = Integer.min(newSelected, screenLength - 1);
        newSelected = Integer.max(newSelected, 0);

        if (selectedScreen != newSelected) {
            selectedScreen = newSelected;

            String text = controller.screens[selectedScreen].getName();
            if (selectedScreen + 1 < screenLength) {
                text += "\n" + controller.screens[selectedScreen + 1].getName();
            }

            controller.updateLcdText("->" + text);
        }

        try {
            controller.wait(100);
        } catch (InterruptedException ex) {
            Main.logger.severe(ex.getMessage());
        }
    }

    public void buttonDown(ScreenController controller) {
        controller.openScreen(controller.screens[selectedScreen]);
    }

    public void reset(ScreenController controller) {
        selectedScreen = -1;
    }
}
