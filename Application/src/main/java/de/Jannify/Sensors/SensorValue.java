package de.Jannify.Sensors;

import de.Jannify.IO.Config;

import java.util.List;

public class SensorValue {
    private final int temperature;
    private final int humidity;
    private final int co2;
    private final int pm2;
    private final int pm10;

    public SensorValue(int temperature, int humidity, int co2, int pm2, int pm10) {
        this.temperature = Math.min(Math.max(temperature, 0), 50);
        this.humidity = Math.min(Math.max(humidity, 20), 90);
        this.co2 = Math.min(Math.max(co2, 399), 29300); //It goes from 400 to 29206, these values are just to see it's corrupted and not just low.
        this.pm2 = Math.min(Math.max(pm2, 0), 1000);
        this.pm10 = Math.min(Math.max(pm10, 0), 1000);
    }

    public SensorValue() {
        this(0, 0, 0, 0, 0);
    }

    public int get(int index) {
        switch (index) {
            case 0:
                return temperature;
            case 1:
                return humidity;
            case 2:
                return co2;
            case 3:
                return pm2;
            case 4:
                return pm10;
            default:
                throw new IndexOutOfBoundsException("Excepted values from 0 to 4. Index was: " + index);
        }
    }

    public static SensorValue getAverage(List<SensorValue> sensorValues) {
        return new SensorValue(
                (int) sensorValues.stream().mapToInt(d -> d.temperature).average().orElse(Double.NaN),
                (int) sensorValues.stream().mapToInt(d -> d.humidity).average().orElse(Double.NaN),
                (int) sensorValues.stream().mapToInt(d -> d.co2).average().orElse(Double.NaN),
                (int) sensorValues.stream().mapToInt(d -> d.pm2).average().orElse(Double.NaN),
                (int) sensorValues.stream().mapToInt(d -> d.pm10).average().orElse(Double.NaN));
    }

    /**
     * @return Airquality of eCO2, PM2 and PM10 (in this order) from 1 to 4
     */
    public int[] getQuality() {
        int[][] qualityGates = Config.getQualityGates();
        int[] index = new int[3];

        for (int gate = 0; gate < qualityGates.length; gate++) {
            for (int level = 0; level < qualityGates[gate].length; level++) {
                if (get(gate + 2) < qualityGates[gate][level]) {
                    index[gate] = level + 1;
                    break;
                }
            }
        }

        return index;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public int getCO2() {
        return co2;
    }

    public int getPM2() {
        return pm2;
    }

    public int getPM10() {
        return pm10;
    }

    @Override
    public String toString() {
        return "SensorValue[" + "Temperature=" + temperature + ", Humidity=" + humidity + ", CO2=" + co2 + ", PM2=" + pm2 + ", PM10=" + pm10 + ']';
    }
}
