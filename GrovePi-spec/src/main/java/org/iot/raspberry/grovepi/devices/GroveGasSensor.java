package org.iot.raspberry.grovepi.devices;

import org.iot.raspberry.grovepi.GroveI2CPin;
import org.iot.raspberry.grovepi.GrovePiSequenceVoid;

import java.io.IOException;

@GroveI2CPin
public abstract class GroveGasSensor implements AutoCloseable {

    public abstract void setMode(int mode) throws IOException, IllegalStateException;

    public abstract int getMode();

    public abstract boolean setEnvironmentalData(double relativeHumidity, double temperature) throws IOException;

    public abstract int[] readAlgorithmResults() throws IOException, IllegalStateException;

    @Override
    public abstract void close();
}
