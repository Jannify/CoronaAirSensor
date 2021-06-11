package de.Jannify.Screens;

public interface Screen {
    String getName();
    void execute(ScreenController controller);
    void buttonDown(ScreenController controller);
}
