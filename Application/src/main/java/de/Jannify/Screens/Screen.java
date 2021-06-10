package de.Jannify.Screens;

public interface Screen {
    String getName();
    void execute(ScreenController controller, Object monitor);
    void buttonDown(ScreenController controller);
}
