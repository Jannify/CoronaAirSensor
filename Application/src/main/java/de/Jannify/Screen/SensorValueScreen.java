package de.Jannify.Screen;

import de.Jannify.IO.Config;
import de.Jannify.Main;
import de.Jannify.Sensors.SensorMeasuring;
import de.Jannify.Sensors.SensorValue;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SensorValueScreen implements Screen {
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final DecimalFormat df = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.GERMANY));
    private final SensorMeasuring sensorMeasuring;

    private boolean firstTime = true;
    private int lcdPage;

    public SensorValueScreen() {
        sensorMeasuring = Main.sensorMeasuring;
        lcdPage = 0;
    }

    @Override public String getName() {
        return "Live Daten";
    }

    @Override
    public void execute(ScreenController controller) {
        try {
            if (Config.getDisplayOn()) {
                if (firstTime) {
                    controller.updateLcdColor(Config.getColor());
                }

                SensorValue sensorValue = sensorMeasuring.getCurrentValue();
                switch (lcdPage) {
                    case 0:
                        controller.updateLcdText(MessageFormat.format("Zeit: {0}\neCO2: {1}ppm", timeFormatter.format(LocalDateTime.now()), df.format(sensorValue.getCO2())));
                        break;
                    case 1:
                        controller.updateLcdText(MessageFormat.format("Temperatur: {0}C°\nLuftfeuchte: {1}%PM", sensorValue.getTemperature(), sensorValue.getHumidity()));
                        break;
                    case 2:
                        controller.updateLcdText(MessageFormat.format("PM  2: {0}μg/m3\nPM 10: {1}μg/m3", df.format(sensorValue.getPM2()), df.format(sensorValue.getPM10())));
                        break;
                    default:
                        lcdPage = -1;
                        break;
                }
                lcdPage++;
            } else if (firstTime) {
                controller.updateLcdText("");
                controller.updateLcdColor(Color.BLACK);
            }
            firstTime = false;

            controller.wait(1850); //plus 150 ms from setLcdText()
        } catch (InterruptedException ex) {
            Main.logger.severe(ex.getMessage());
        }
    }

    @Override public void buttonDown(ScreenController controller) {
        controller.openScreenSelector();
    }

    public void reset(ScreenController controller) {
        lcdPage = 0;
        firstTime = true;

        if (!Config.getDisplayOn()) {
            controller.updateLcdColor(Config.getColor());
        }
    }
}
