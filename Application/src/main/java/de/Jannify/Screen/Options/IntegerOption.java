package de.Jannify.Screen.Options;

import de.Jannify.IO.Config;

public class IntegerOption implements Option<Integer> {
    private final String name;
    private final String configName;
    private final int minValue;
    private final int maxValue;

    private int value;

    public IntegerOption(String name,String configName, int currentValue, int minValue, int maxValue) {
        this.name = name;
        this.configName = configName;
        this.value = currentValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }

    public String getValueText() {
        return String.valueOf(value);
    }

    public void changeValue(double rotaryFactor) {
        value = (int) (minValue + (rotaryFactor * (maxValue - minValue)));
    }

    public void saveValue() {
        Config.putIntValue(configName, value);
    }
}