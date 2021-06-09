package de.Jannify.Screen;

public interface Screen {
    String getName();
    void execute(ScreenController controller, Object monitor);
    void buttonDown(ScreenController controller);
}
