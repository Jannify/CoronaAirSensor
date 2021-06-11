package de.Jannify.Screen;

import de.Jannify.Main;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class RestartScreen implements Screen {
    private double time = 3;

   public String getName() {
        return "Neustarten";
    }

    public void execute(ScreenController controller) {
        try {
            controller.updateLcdText("Abbrechen? \n" + String.format("%.1f", time) + " Sekunden");

            if (time < 0) {
                restartApplication();
            }
            time -= 0.2;
            controller.wait(50); //plus 150 ms from setLcdText()
        } catch (Exception ex) {
            Main.logger.severe(ex.getMessage());
        }
    }

    public void buttonDown(ScreenController controller) {
        controller.openScreenSelector();
    }

    public void reset(ScreenController controller) {
        time = 3;
    }

    //Copied from https://stackoverflow.com/questions/4159802/how-can-i-restart-a-java-application
    public void restartApplication() throws IOException, URISyntaxException {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(RestartScreen.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        /* is it a jar file? */
        if (!currentJar.getName().endsWith(".jar"))
            return;

        /* Build command: java -jar application.jar */
        final ArrayList<String> command = new ArrayList<>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.getPath());

        Main.close();
        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        System.exit(0);
    }
}
