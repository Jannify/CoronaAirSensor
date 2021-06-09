package de.Jannify.Screen;

import de.Jannify.IO.Config;
import de.Jannify.Main;
import de.Jannify.Sensors.GroveBridge;

public class OptionsScreen implements Screen {
    private final GroveBridge groveBridge;

    private final String[] optionEntries = new String[] {"ValueDisplayOn", "Exit"};

    private final double potentialPerScreen;
    private int selectedScreen;

    public OptionsScreen() {
        groveBridge = Main.grove;
        potentialPerScreen = 1.0 / optionEntries.length;
    }

    @Override public String getName() {
        return "Einstellungen";
    }

    @Override
    public void execute(ScreenController controller, Object monitor) {
        try {
            int newSelected = (int) (groveBridge.getRotation() / potentialPerScreen);
            if (newSelected >= optionEntries.length) {
                newSelected = optionEntries.length - 1;
            } else if (newSelected < 0) {
                newSelected = 0;
            }

            if (selectedScreen != newSelected) {
                selectedScreen = newSelected;


                switch (optionEntries[selectedScreen]) {
                    case "ValueDisplayOn":
                        groveBridge.setLcdText("Display Aktiv\n"+ Config.ValueDisplayOn);
                        break;
                    case "Exit":
                        groveBridge.setLcdText("Speichern und Verlassen");
                        break;
                }
            }
                monitor.wait(200);
            } catch(InterruptedException ex){
                Main.logger.severe(ex.getMessage());
            }
        }

    @Override public void buttonDown(ScreenController controller) {
        switch (optionEntries[selectedScreen]) {
            case "ValueDisplayOn":
                Config.ValueDisplayOn = !Config.ValueDisplayOn;
                if(Config.ValueDisplayOn) {
                    groveBridge.setLcdColor(Config.getColorR(), Config.getColorG(), Config.getColorB());
                }
                break;
            case "Exit":
                Config.saveConfig();
                controller.openScreenSelector();
                break;
            default:
                break;
        }
        selectedScreen = -1;
    }
}
