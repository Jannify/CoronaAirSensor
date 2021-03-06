package de.Jannify.Screen;

public interface Screen {
    String getName();
    void execute(ScreenController controller);
    void buttonDown(ScreenController controller);
    void reset(ScreenController controller);
}
