package de.Jannify.Sensors;

import com.fazecast.jSerialComm.SerialPort;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NovaPMSensor {
    private ExecutorService runner;
    private SerialPort sensor;

    private boolean isRunning;
    private int pm2;
    private int pm10;


    public void open() {
        runner = Executors.newSingleThreadExecutor();
        sensor = SerialPort.getCommPort("ttyUSB0");
        sensor.openPort();
        sensor.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        sensor.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 10000, 0);
        isRunning = true;

        runner.execute(() -> {
            try {
                while (isRunning) {
                    byte[] readBuffer = new byte[20];
                    sensor.readBytes(readBuffer, readBuffer.length);

                    pm2 = pmCalculation(readBuffer[2], readBuffer[3]);
                    pm10 = pmCalculation(readBuffer[4], readBuffer[5]);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void close() {
        isRunning = false;
        try {
            runner.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sensor.closePort();
    }

    public int getPM2() {
        return pm2;
    }

    public int getPM10() {
        return pm10;
    }

    private int pmCalculation(byte low, byte high) {
        return ((high & 0xff) * 256 + (low & 0xff)) / 10;
    }
}
