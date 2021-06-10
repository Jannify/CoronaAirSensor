package de.Jannify.Sensors;

import com.sun.istack.internal.NotNull;
import de.Jannify.Main;
import de.Jannify.Sensors.Nova.NovaPMSensor;
import de.Jannify.Sensors.Nova.NovaPMValue;
import org.iot.raspberry.grovepi.GroveDigitalIn;
import org.iot.raspberry.grovepi.GroveDigitalInListener;
import org.iot.raspberry.grovepi.GrovePi;
import org.iot.raspberry.grovepi.devices.*;
import org.iot.raspberry.grovepi.pi4j.GrovePi4J;

import java.awt.*;
import java.io.Closeable;
import java.io.IOException;

public class SensorInterface extends Thread implements Closeable {
    // Interfaces/Devices
    private static GrovePi grovePi;
    private static NovaPMSensor novaPMSensor;

    // Grove Devices
    private GroveDigitalIn button;
    private GroveRotarySensor potentiometer;
    private GroveTemperatureAndHumiditySensor tempSensor;
    private GroveRgbLcd lcd;
    private GroveGasSensor gasSensor;

    //Current Sensor values
    private static double temperature;
    private static double humidity;
    private static int pm2;
    private static int pm10;
    private static int co2;

    public SensorInterface() {
        try {
            novaPMSensor = new NovaPMSensor();

            grovePi = new GrovePi4J();
            button = grovePi.getDigitalIn(7);
            potentiometer = new GroveRotarySensor(grovePi, 0);
            tempSensor = new GroveTemperatureAndHumiditySensor(grovePi, 8, GroveTemperatureAndHumiditySensor.Type.DHT11);
            lcd = grovePi.getLCD();
            gasSensor = grovePi.getGasSensor();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            if (lcd != null) {
                lcd.setRGB(0, 0, 0);
                lcd.setText("");
                lcd.close();
            }
            if (gasSensor != null) {
                gasSensor.close();
            }
            grovePi.close();
            novaPMSensor.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                button.run();
                potentiometer.run();

                GroveTemperatureAndHumidityValue tempValue = tempSensor.get();
                if (tempValue != null) {
                    temperature = tempValue.getTemperature();
                    humidity = tempValue.getHumidity();
                    gasSensor.setEnvironmentalData(humidity, temperature);
                } else {
                    temperature = -1;
                    humidity = -1;
                }

                NovaPMValue pmValues = novaPMSensor.getValues();
                pm2 = pmValues.getPm2();
                pm10 = pmValues.getPm10();

                co2 = gasSensor.readAlgorithmResults()[0];

                this.wait(5000);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setLcdText(String text) {
        try {
            lcd.setText(text);
        } catch (IOException ex) {
            Main.logger.info(ex.getMessage());
        }
    }

    public void setLcdColor(Color color) {
        try {
            lcd.setRGB(color.getRed(), color.getGreen(), color.getBlue());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setCO2Mode(int mode) {
        try {
            gasSensor.setMode(mode);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    //Listener register
    public void setButtonListener(@NotNull GroveDigitalInListener listener) {
        button.setListener(listener);
    }

    public void setPotentiometerListener(@NotNull GroveInputDeviceListener<GroveRotaryValue> listener) {
        potentiometer.setListener(listener);
    }


    // GETTER
    public static double getTemperature() {
        return temperature;
    }

    public static double getHumidity() {
        return humidity;
    }

    public static int getPM2() {
        return pm2;
    }

    public static int getPM10() {
        return pm10;
    }

    public static int getCO2() {
        return co2;
    }
}
