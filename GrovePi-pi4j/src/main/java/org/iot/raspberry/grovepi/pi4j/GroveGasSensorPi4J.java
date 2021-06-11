package org.iot.raspberry.grovepi.pi4j;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import org.iot.raspberry.grovepi.devices.GroveGasSensor;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroveGasSensorPi4J extends GroveGasSensor {
    public static final int CHIP_ID_CCS811 = 0x81;
    public static final int DEFAULT_I2C_ADDRESS = 0x5A;

    public static final int MODE_IDLE = 0; // Idle, low current mode
    public static final int MODE_1S = 1; // Constant power mode, IAQ measurement every second
    public static final int MODE_10S = 2; // Pulse heating mode IAQ measurement every 10 seconds
    public static final int MODE_60S = 3; // Low power pulse heating mode IAQ measurement every 60 seconds
    public static final int MODE_250MS = 4; // Constant power mode, sensor measurement every 250ms

    private static final int CCS811_STATUS = 0x00;
    private static final int CCS811_MODE = 0x01;
    private static final int CCS811_ALG_RESULT_DATA = 0x02;
    private static final int CSS811_ENV_DATA = 0x05;
    private static final int CCS811_HW_ID = 0x20;
    private static final int CCS811_FW_BOOT_VERSION = 0x23;
    private static final int CCS811_FW_APP_VERSION = 0x24;
    private static final int CCS811_ERROR_ID = 0xE0;
    private static final int CCS811_START_APP = 0xF4;
    private static final int CCS811_SW_RESET = 0xFF;

    private static final int CCS811_DRIVE_MODE_MASK = 0b00000111;
    private static final int CCS811_STATUS_DATA_READY_BITSHIFT = 3;
    private static final int CCS811_STATUS_APP_VALID_BITSHIFT = 4;
    private static final int CCS811_STATUS_FW_MODE_BITSHIFT = 7;

    private final I2CBus bus;
    private final I2CDevice device;

    private final byte[] mBuffer = new byte[8];
    private final int mChipId;
    private int mMode;

    public GroveGasSensorPi4J() throws IOException, InterruptedException, I2CFactory.UnsupportedBusNumberException {
        this.bus = I2CFactory.getInstance(I2CBus.BUS_1);
        this.device = bus.getDevice(DEFAULT_I2C_ADDRESS);

        softReset();
        Thread.sleep(100);
        mChipId = device.read(CCS811_HW_ID);

        int status = getStatus();
        if ((status & 1) != 0) {
            throw new IOException(getError());
        }
        if ((status & (1 << CCS811_STATUS_APP_VALID_BITSHIFT)) != 0) {
            // Application start. Used to transition the CCS811 state from boot to application mode,
            // a write with no data is required.
            device.write(new byte[]{(byte) CCS811_START_APP}, 0, 1);
            Thread.sleep(100);
        } else {
            throw new IOException("CCS811 app not valid");
        }
    }

    @Override
    public void close() {
        try {
            bus.close();
        } catch (IOException ex) {
            Logger.getLogger("GrovePi").log(Level.SEVERE, null, ex);
        }
    }

    public boolean setEnvironmentalData(double relativeHumidity, double temperature) throws IOException {
        //Check for invalid temperatures
        if ((temperature < -25) || (temperature > 50))
            return false;

        //Check for invalid humidity
        if ((relativeHumidity < 0) || (relativeHumidity > 100))
            return false;

        int rH = (int) (relativeHumidity * 1000); //42.348 becomes 42348
        int temp = (int) (temperature * 1000);    //23.2 becomes 23200

        byte[] envData = new byte[4];

        //Split value into 7-bit integer and 9-bit fractional

        //Incorrect way from datasheet.
        //envData[0] = ((rH % 1000) / 100) > 7 ? (rH / 1000 + 1) << 1 : (rH / 1000) << 1;
        //envData[1] = 0; //CCS811 only supports increments of 0.5 so bits 7-0 will always be zero
        //if (((rH % 1000) / 100) > 2 && (((rH % 1000) / 100) < 8))
        //{
        //	envData[0] |= 1; //Set 9th bit of fractional to indicate 0.5%
        //}

        //Correct rounding. See issue 8: https://github.com/sparkfun/Qwiic_BME280_CCS811_Combo/issues/8
        envData[0] = (byte) ((rH + 250) / 500);
        envData[1] = 0; //CCS811 only supports increments of 0.5 so bits 7-0 will always be zero

        temp += 25000; //Add the 25C offset
        //Split value into 7-bit integer and 9-bit fractional
        //envData[2] = ((temp % 1000) / 100) > 7 ? (temp / 1000 + 1) << 1 : (temp / 1000) << 1;
        //envData[3] = 0;
        //if (((temp % 1000) / 100) > 2 && (((temp % 1000) / 100) < 8))
        //{
        //	envData[2] |= 1;  //Set 9th bit of fractional to indicate 0.5C
        //}

        //Correct rounding
        envData[2] = (byte) ((temp + 250) / 500);
        envData[3] = 0;

        device.write(CSS811_ENV_DATA, envData, 0, envData.length);
        return true;
    }

    /**
     * Issue a software reset of the sensor
     *
     * @throws IOException           if soft reset fails
     * @throws IllegalStateException if I2C device is not open
     */
    private void softReset() throws IOException, IllegalStateException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }

        final byte[] resetSequence = new byte[]{0x11, (byte) 0xE5, 0x72, (byte) 0x8A};

        device.write(CCS811_SW_RESET, resetSequence, 0, resetSequence.length);
    }

    /**
     * Set the measurement mode of the sensor.
     *
     * @param mode measurement mode.
     * @throws IOException           if mode set fails
     * @throws IllegalStateException if I2C device is not open
     */
    public void setMode(int mode) throws IOException, IllegalStateException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }

        int regCtrl = device.read(CCS811_MODE) & 0xff;
        regCtrl &= ~(CCS811_DRIVE_MODE_MASK << 4); // Clear DRIVE_MODE bits
        regCtrl |= (mode << 4); // Mask in mode
        device.write(CCS811_MODE, (byte) (regCtrl));
        mMode = mode;
    }

    /**
     * Read the current mode of the sensor
     *
     * @return mode enum
     */
    public int getMode() {
        return mMode;
    }

    private int getStatus() throws IOException, IllegalStateException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }

        return device.read(CCS811_STATUS) & 0xff;
    }

    private String getError() throws IOException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }

        int error = device.read(CCS811_ERROR_ID) & 0xff;
        String msg = "Error: ";

        if ((error & (1 << 5)) != 0) msg += "HeaterSupply ";
        if ((error & (1 << 4)) != 0) msg += "HeaterFault ";
        if ((error & (1 << 3)) != 0) msg += "MaxResistance ";
        if ((error & (1 << 2)) != 0) msg += "MeasModeInvalid ";
        if ((error & (1 << 1)) != 0) msg += "ReadRegInvalid ";
        if ((error & 1) != 0) msg += "MsgInvalid ";

        return msg;
    }

    /**
     * Reads the bootloader version from the sensor
     *
     * @return String version in semantic format or null if read fails
     */
    private String readBootVersion() {
        try {
            synchronized (mBuffer) {
                device.read(CCS811_FW_BOOT_VERSION, mBuffer, 0, 2);
                final int major = (mBuffer[0] & 0xf0) >> 4;
                final int minor = (mBuffer[0] & 0x0f);
                final int trivial = (mBuffer[1] & 0xff);
                return major + "." + minor + "." + trivial;
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Reads the application version from the sensor
     *
     * @return String version in semantic format or null if read fails
     */
    private String readAppVersion() {
        try {
            synchronized (mBuffer) {
                device.read(CCS811_FW_APP_VERSION, mBuffer, 0, 2);
                final int major = (mBuffer[0] & 0xf0) >> 4;
                final int minor = (mBuffer[0] & 0x0f);
                final int trivial = (mBuffer[1] & 0xff);
                return major + "." + minor + "." + trivial;
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Read the current value of the algorithm result.
     *
     * @return 2-element array. The first element is eCO2 in ppm, and the second is TVOC in ppb.
     * @throws IOException           if read fails
     * @throws IllegalStateException if device is not open
     */
    public int[] readAlgorithmResults() throws IOException, IllegalStateException {
        if (device == null) {
            throw new IllegalStateException("I2C device not open");
        }

        synchronized (mBuffer) {
            device.read(CCS811_ALG_RESULT_DATA, mBuffer, 0, 8);
            final int eCO2 = ((mBuffer[0] & 0xff) << 8) | (mBuffer[1] & 0xff);
            final int tVOC = ((mBuffer[2] & 0xff) << 8) | (mBuffer[3] & 0xff);
            return new int[]{eCO2, tVOC, mBuffer[4], mBuffer[5]};
        }
    }
}
