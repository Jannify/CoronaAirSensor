package org.iot.raspberry.grovepi;

import java.io.IOException;

public interface GroveIO {

  void write(int... command) throws IOException;

  int read() throws IOException;

  byte[] read(byte[] buffer) throws IOException;

  default void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException ex) {
    }
  }
}
