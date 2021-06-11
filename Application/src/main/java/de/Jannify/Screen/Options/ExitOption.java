package de.Jannify.Screen.Options;

import de.Jannify.IO.Config;
import de.Jannify.Main;

public class ExitOption implements Option<String> {
    private final String name;

    public ExitOption(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return null;
    }

    public String getValueText() {
        return "";
    }

    public void changeValue(double rotaryFactor) {}

    public void saveValue() {
        Main.screenController.openScreenSelector();
    }
}