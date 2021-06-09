package de.Jannify.Sensors;

import de.Jannify.Main;
import org.iot.raspberry.grovepi.GroveDigitalIn;
import org.iot.raspberry.grovepi.GroveDigitalInListener;
import org.iot.raspberry.grovepi.GrovePi;
import org.iot.raspberry.grovepi.devices.*;
import org.iot.raspberry.grovepi.pi4j.GrovePi4J;

import java.io.IOException;

public class GroveBridge implements Runnable {
    private final Object monitor = new Object();
    private GrovePi grovePi;

    private GroveLed led;
    private GroveDigitalIn button;
    private GroveRotarySensor rotarySensor;
    private GroveTemperatureAndHumiditySensor tempSensor;
    private GroveRgbLcd lcd;
    private GroveGasSensor gasSensor;

    private GroveTemperatureAndHumidityValue lastTempHumValue;

    public void start() {
        try {
            grovePi = new GrovePi4J();
            //led = new GroveLed(grovePi, 4);
            button = grovePi.getDigitalIn(7);
            rotarySensor = new GroveRotarySensor(grovePi, 0);
            tempSensor = new GroveTemperatureAndHumiditySensor(grovePi, 8, GroveTemperatureAndHumiditySensor.Type.DHT11);
            lcd = grovePi.getLCD();
            gasSensor = grovePi.getGasSensor();

            Thread measuringThread = new Thread(button);
            measuringThread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            if (led != null) {
                led.set(false);
            }
            if (lcd != null) {
                lcd.setRGB(0, 0, 0);
                lcd.setText("");
                lcd.close();
            }
            if (gasSensor != null) {
                gasSensor.close();
            }
            grovePi.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setLcdText(String text) {
        try {
            lcd.setText(text);
        } catch (IOException ex) {
            Main.logger.info(ex.getMessage());
        }
    }

    public void setCO2Mode(int mode) {
        try {
            gasSensor.setMode(mode);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setLcdColor(int r, int g, int b) {
        try {
            lcd.setRGB(r, g, b);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public GroveTemperatureAndHumidityValue getTemperatureAndHumidity() {
        try {
            GroveTemperatureAndHumidityValue tmp = tempSensor.get();
            if (tmp != null) {
                lastTempHumValue = tmp;
                gasSensor.setEnvironmentalData(tmp.getHumidity(), tmp.getTemperature());
                return tmp;
            } else {
                return lastTempHumValue;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return lastTempHumValue;
        }
    }

    public int getCO2() {
        try {
            return gasSensor.readAlgorithmResults()[0];
        } catch (IOException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public void setButtonListener(GroveDigitalInListener listener) {
        if (listener != null) {
            button.setListener(listener);
        }
    }


    @Override public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (monitor) {
                button.run();
                //try {
               //     monitor.wait(100);
               // } catch (InterruptedException e) {
              //      e.printStackTrace();
              //  }
            }
        }
    }

    /**
     * @return Returns factor of RotarySensor (0-1)
     */
    public double getRotation() {
        try {
            GroveRotaryValue value = rotarySensor.get();
            return value.getFactor();
        } catch (IOException ex) {
            ex.printStackTrace();
            return 0;
        }
    }
}
