package de.Jannify.Screen;

import de.Jannify.IO.Config;
import de.Jannify.Main;
import de.Jannify.Screen.Options.BooleanOption;
import de.Jannify.Screen.Options.ExitOption;
import de.Jannify.Screen.Options.Option;

public class OptionsScreen implements Screen {
    private final double potentialPerScreen;
    private final Option[] optionEntries = new Option[]{
            new BooleanOption("Display An/Aus", "DisplayOn", Config.getDisplayOn(), "An", "Aus"),
            new ExitOption("Speichern und Verlassen")
    };

    private int selectedOptionIndex;
    private Option selectedOption;
    private boolean isInSetMode;

    public OptionsScreen() {
        potentialPerScreen = 1.0 / optionEntries.length;
        reset(null);
    }

    public String getName() {
        return "Einstellungen";
    }

    public void execute(ScreenController controller) {
        try {
            double rotation = controller.getRotationFactor();

            if (isInSetMode) {
                selectedOption.changeValue(rotation);
                controller.updateLcdText(selectedOption.getName() + "\n-> " + selectedOption.getValueText());
            } else {
                int newOptionIndex = (int) (rotation / potentialPerScreen);
                newOptionIndex = Integer.min(newOptionIndex, optionEntries.length - 1);
                newOptionIndex = Integer.max(newOptionIndex, 0);

                if (selectedOptionIndex != newOptionIndex) {
                    selectedOptionIndex = newOptionIndex;
                    selectedOption = optionEntries[selectedOptionIndex];

                    controller.updateLcdText(selectedOption.getName() + "\n" + selectedOption.getValueText());
                    controller.wait(100); //plus 100 ms from getRotation()
                }
            }

        } catch (InterruptedException ex) {
            Main.logger.severe(ex.getMessage());
        }
    }

    public void buttonDown(ScreenController controller) {
        if (isInSetMode) {
            selectedOption.saveValue();
            isInSetMode = false;
        } else {
            selectedOption.saveValue();
            isInSetMode = true;
            selectedOptionIndex = -1;
        }
    }

    public void reset(ScreenController controller) {
        selectedOptionIndex = -1;
        selectedOption = null;
        isInSetMode = false;
    }
}
