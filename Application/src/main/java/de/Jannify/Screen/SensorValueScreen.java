package de.Jannify.Screen;

import de.Jannify.IO.Config;
import de.Jannify.Main;
import de.Jannify.Sensors.GroveBridge;
import de.Jannify.Sensors.SensorMeasuring;
import de.Jannify.Sensors.SensorValue;

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
    private final GroveBridge groveBridge;

    private boolean firstTime = true;
    private int lcdPage;

    public SensorValueScreen() {
        sensorMeasuring = Main.sensorMeasuring;
        groveBridge = Main.grove;
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
                    groveBridge.setLcdColor(Config.getColorR(), Config.getColorG(), Config.getColorB());
                }

                SensorValue sensorValue = sensorMeasuring.getCurrent();
                switch (lcdPage) {
                    case 0:
                        groveBridge.setLcdText(MessageFormat.format("Zeit: {0}\nCO2: {1}ppm", timeFormatter.format(LocalDateTime.now()), df.format(sensorValue.CO2)));
                        break;
                    case 1:
                        groveBridge.setLcdText(MessageFormat.format("Temperatur: {0}C°\nLuftfeuchte: {1}%PM", sensorValue.temperature, sensorValue.humidity));
                        break;
                    case 2:
                        groveBridge.setLcdText(MessageFormat.format("PM  2: {0}μg/m3\nPM 10: {1}μg/m3", sensorValue.PM2, sensorValue.PM10));
                        break;
                    default:
                        lcdPage = -1;
                        break;
                }
                lcdPage++;
            } else if (firstTime) {
                groveBridge.setLcdText("");
                groveBridge.setLcdColor(0, 0, 0);
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
