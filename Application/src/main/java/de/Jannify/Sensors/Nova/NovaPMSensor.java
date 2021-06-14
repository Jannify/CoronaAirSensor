package de.Jannify.Sensors.Nova;

import com.fazecast.jSerialComm.SerialPort;

import java.io.Closeable;

public class NovaPMSensor implements Closeable {
    private final SerialPort sensor;

    public NovaPMSensor() {
        sensor = SerialPort.getCommPort("ttyUSB0");
        sensor.openPort();
        sensor.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        sensor.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 10000, 10000);
    }

    public NovaPMValue fetchValue() {
        try {
            byte[] readBuffer = new byte[20];
            sensor.readBytes(readBuffer, readBuffer.length);

            //Getting data start
            int startByte = -1;
            for (int i = 0; i < readBuffer.length - 9; i++) {
                if (readBuffer[i] == (byte) 0xAA && readBuffer[i + 1] == (byte) 0xC0 && readBuffer[i + 9] == (byte) 0xAB) {
                    // Checksum calculation
                    byte checksum = 0;
                    for (int dataIndex = i + 2; dataIndex < i + 8; dataIndex++) {
                        checksum += readBuffer[dataIndex];
                    }
                    if (readBuffer[i + 8] != checksum) {
                        throw new IllegalArgumentException("NovaPmSensor: Checksum is not equal. " +
                                "Calculated: + " + String.format("0x%02X ", checksum) +
                                "Provided: + " + String.format("0x%02X ", readBuffer[i + 8]) +
                                "\n"+ printByteArray(readBuffer));
                    }

                    // Storing found startByte
                    startByte = i;
                    break;
                }
            }
            if (startByte == -1) {
                throw new IllegalArgumentException("NovaPmSensor: Data had no start bit: + " + printByteArray(readBuffer));
            }

            return new NovaPMValue(pmCalculation(readBuffer[startByte + 2], readBuffer[startByte + 3]),
                                   pmCalculation(readBuffer[startByte + 4], readBuffer[startByte + 5]));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new NovaPMValue(-1, -1);
    }

    private int pmCalculation(byte low, byte high) {
        return ((high & 0xff) * 256 + (low & 0xff)) / 10;
    }

    public void close() {
        sensor.closePort();
    }

    private static String printByteArray(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (byte b : bytes) {
            sb.append(String.format("0x%02X ", b));
        }
        sb.append("]");
        return sb.toString();
    }
}

