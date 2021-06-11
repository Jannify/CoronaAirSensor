package de.Jannify.Sensors.Nova;

import com.fazecast.jSerialComm.SerialPort;
import java.io.Closeable;

public class NovaPMSensor implements Closeable {
    private final SerialPort sensor;

    public NovaPMSensor() {
        sensor = SerialPort.getCommPort("ttyUSB0");
        sensor.openPort();
        sensor.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        sensor.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 10000, 0);
    }

    public NovaPMValue fetchValue() {
        try {
            byte[] readBuffer = new byte[20];
            sensor.readBytes(readBuffer, readBuffer.length);

            return new NovaPMValue(pmCalculation(readBuffer[2], readBuffer[3]), pmCalculation(readBuffer[4], readBuffer[5]));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new NovaPMValue(-1, -1);
    }

    public void close() {
        sensor.closePort();
    }

    private int pmCalculation(byte low, byte high) {
        return ((high & 0xff) * 256 + (low & 0xff)) / 10;
    }
}

