package de.Jannify.Sensors;

import java.util.List;

public class SensorValue {
    public double temperature;
    public double humidity;
    public int PM2;
    public int PM10;
    public int CO2;

    public SensorValue() {
        this.temperature = 0;
        this.humidity = 0;
        this.PM2 = 0;
        this.PM10 = 0;
        this.CO2 = 0;
    }

    public SensorValue(double temperature, double humidity, int PM2, int PM10, int CO2) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.PM2 = PM2;
        this.PM10 = PM10;
        this.CO2 = CO2;
    }

    public static SensorValue getAverage(List<SensorValue> sensorValues) {
        return new SensorValue(
                (int) sensorValues.stream().mapToDouble(d -> d.temperature).average().orElse(Double.NaN),
                (int) sensorValues.stream().mapToDouble(d -> d.humidity).average().orElse(Double.NaN),
                (int) sensorValues.stream().mapToInt(d -> d.PM2).average().orElse(Double.MIN_VALUE),
                (int) sensorValues.stream().mapToInt(d -> d.PM10).average().orElse(Double.MIN_VALUE),
                (int) sensorValues.stream().mapToInt(d -> d.CO2).average().orElse(Double.MIN_VALUE));
    }
}
