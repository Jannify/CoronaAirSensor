package org.iot.raspberry.grovepi;

import org.iot.raspberry.grovepi.devices.GroveGasSensor;
import org.iot.raspberry.grovepi.devices.GroveRgbLcd;
import java.io.IOException;

public interface GrovePi extends AutoCloseable {

  default GroveDigitalOut getDigitalOut(int digitalPort) throws IOException {
    return new GroveDigitalOut(this, digitalPort);
  }

  default GroveDigitalIn getDigitalIn(int digitalPort) throws IOException {
    return new GroveDigitalIn(this, digitalPort);
  }

  default GroveAnalogOut getAnalogOut(int digitalPort) throws IOException {
    return new GroveAnalogOut(this, digitalPort);
  }

  default GroveAnalogIn getAnalogIn(int digitalPort, int bufferSize) throws IOException {
    return new GroveAnalogIn(this, digitalPort, bufferSize);
  }

  GroveRgbLcd getLCD() throws IOException;

  GroveGasSensor getGasSensor() throws IOException;

  <T> T exec(GrovePiSequence<T> sequence) throws IOException;

  void execVoid(GrovePiSequenceVoid sequence) throws IOException;

  @Override void close();

}
