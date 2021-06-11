package de.Jannify.Sensors;

import de.Jannify.IO.Config;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class SensorValueTest {
    static SensorValue sensorValue;
    static SensorValue sensorValue2;
    static SensorValue corruptedSensorValue;
@BeforeAll
    @BeforeClass
    public static void initSensorValue() {
        Config.readConfig();
        sensorValue = new SensorValue(29, 42, 425, 19, 115);
        sensorValue2 = new SensorValue(40, 60, 820, 25, 165);
        corruptedSensorValue = new SensorValue(-20, 120, 399, -3, 1234);
    }

    @Test
    public void get() {
        assertEquals(29, sensorValue.get(0));
        assertEquals(42, sensorValue.get(1));
        assertEquals(425, sensorValue.get(2));
        assertEquals(19, sensorValue.get(3));
        assertEquals(115, sensorValue.get(4));

        assertNotEquals(29, corruptedSensorValue.get(0));
        assertNotEquals(42, corruptedSensorValue.get(1));
        assertNotEquals(425, corruptedSensorValue.get(2));
        assertNotEquals(19, corruptedSensorValue.get(3));
        assertNotEquals(115, corruptedSensorValue.get(4));
    }

    @Test
    public void getAverage() {
        SensorValue average = SensorValue.getAverage(Arrays.asList(sensorValue, sensorValue2) );

        assertEquals(34, average.getTemperature());
        assertEquals(51, average.getHumidity());
        assertEquals(622, average.getCO2());
        assertEquals(22, average.getPM2());
        assertEquals(140, average.getPM10());
    }

    @Test
    public void getQuality() {
        int[] quality1 = sensorValue.getQuality();
        int[] quality2 = sensorValue2.getQuality();

        assertEquals(1, quality1[0]); //CO2
        assertEquals(2, quality1[1]); //PM2
        assertEquals(3, quality1[2]); //PM10

        assertEquals(3, quality2[0]); //CO2
        assertEquals(3, quality2[1]); //PM2
        assertEquals(4, quality2[2]); //PM10

    }
}