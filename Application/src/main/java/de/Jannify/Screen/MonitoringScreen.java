package de.Jannify.Screen;

import de.Jannify.IO.Config;
import de.Jannify.Main;
import de.Jannify.Sensors.SensorMeasuring;
import de.Jannify.Sensors.SensorValue;

import java.awt.*;
import java.util.Arrays;

public class MonitoringScreen implements Screen {
    private final SensorMeasuring sensorMeasuring;
    private final String[] qualityTypes = new String[] {"CO2", "PM2", "PM10"};
    private int stage;

    public MonitoringScreen() {
        this.sensorMeasuring = Main.sensorMeasuring;
        stage = -1;
    }

    public String getName() {
        return "Ueberwachung";
    }

    public void execute(ScreenController controller) {
        SensorValue sensorValue = sensorMeasuring.getCurrentValue();
        System.out.println(sensorValue);
        int[] quality = sensorValue.getQuality();
        int newStage = (int) Arrays.stream(quality).max().orElse(-1);
        if (stage != newStage) {
            stage = newStage;

            switch (stage) {
                case 1:
                    controller.updateLcdColor(Color.GREEN);
                    break;
                case 2:
                    controller.updateLcdColor(Color.YELLOW);
                    break;
                case 3:
                    controller.updateLcdColor(Color.ORANGE);
                    break;
                case 4:
                    controller.updateLcdColor(Color.RED);
                    break;
                default:
                    controller.updateLcdColor(Color.CYAN);
                    break;
            }

            if (newStage != 0) {
                StringBuilder text = new StringBuilder();
                for (int i = 0; i < qualityTypes.length; i++) {
                    if (quality[i] == 4) {
                        text.append(qualityTypes[i]).append(" sehr hoch\n");
                    } else if (quality[i] == 3) {
                        text.append(qualityTypes[i]).append(" hoch\n");
                    } else if (quality[i] == 2) {
                        text.append(qualityTypes[i]).append(" steigt\n");
                    }
                }

                controller.updateLcdText(text.toString());
            }
        } else if (stage == -1) {
            controller.updateLcdText("");
        }

        try {
            controller.wait(Config.getTimeSyncMili());
        } catch (InterruptedException ex) {
            Main.logger.severe(ex.getMessage());
        }
    }

    public void buttonDown(ScreenController controller) {
        controller.openScreenSelector();
    }

    public void reset(ScreenController controller) {
        stage = -1;
        controller.updateLcdColor(Config.getColor());
    }
}
