package de.Jannify.Screen.Options;

public interface Option<T> {
    String getName();

    T getValue();

    String getValueText();

    void changeValue(double rotaryFactor);

    void saveValue();
}