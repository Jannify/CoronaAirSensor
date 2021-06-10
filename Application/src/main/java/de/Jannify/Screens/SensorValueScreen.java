package de.Jannify.Screens;

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
    public void execute(ScreenController controller, Object monitor) {
        try {
            if (Config.ValueDisplayOn) {
                if (firstTime) {
                    ScreenController.setLcdColor(Config.getColor());
                }

                SensorValue sensorValue = sensorMeasuring.getCurrentValue();
                switch (lcdPage) {
                    case 0:
                        ScreenController.setLcdText(MessageFormat.format("Zeit: {0}\nCO2: {1}ppm", timeFormatter.format(LocalDateTime.now()), df.format(sensorValue.CO2)));
                        break;
                    case 1:
                        ScreenController.setLcdText(MessageFormat.format("Temperatur: {0}C°\nLuftfeuchte: {1}%PM", sensorValue.temperature, sensorValue.humidity));
                        break;
                    case 2:
                        ScreenController.setLcdText(MessageFormat.format("PM  2: {0}μg/m3\nPM 10: {1}μg/m3", sensorValue.PM2, sensorValue.PM10));
                        break;
                    default:
                        lcdPage = -1;
                        break;
                }
                lcdPage++;
            } else if (firstTime) {
                ScreenController.setLcdText("");
                ScreenController.setLcdColor(Color.BLACK);
            }
            firstTime = false;

            monitor.wait(2000);
        } catch (InterruptedException ex) {
            Main.logger.severe(ex.getMessage());
        }
    }

    @Override public void buttonDown(ScreenController controller) {
        lcdPage = 0;
        firstTime = true;
        controller.openScreenSelector();
    }
}
