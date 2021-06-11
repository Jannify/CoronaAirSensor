package de.Jannify.Screen.Options;

import de.Jannify.IO.Config;

public class BooleanOption implements Option<Boolean> {
    private final String name;
    private final String configName;
    private final String trueText;
    private final String falseText;

    private boolean value;

    public BooleanOption(String name, String configName, boolean currentValue, String trueText, String falseText) {
        this.name = name;
        this.configName = configName;
        this.trueText = trueText;
        this.falseText = falseText;
        this.value = currentValue;
    }

    public String getName() {
        return name;
    }

    public Boolean getValue() {
        return value;
    }

    public String getValueText() {
        return value ? trueText : falseText;
    }

    public void changeValue(double rotaryFactor) {
        value = rotaryFactor > 0.5;
    }

    public void saveValue() {
        Config.putBooleanValue(configName, value);
    }
}