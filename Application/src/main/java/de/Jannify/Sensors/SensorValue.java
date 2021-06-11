package de.Jannify.Sensors;

import de.Jannify.IO.Config;

import java.util.List;

public class SensorValue {
    private final double temperature;
    private final double humidity;
    private final int co2;
    private final int pm2;
    private final int pm10;

    public SensorValue(double temperature, double humidity, int co2, int pm2, int pm10) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.co2 = Math.max(co2, 399);
        this.pm2 = Math.min(pm2, 1000);
        this.pm10 = Math.min(pm10, 1000);
    }

    public SensorValue() {
        this(0, 0, 0, 0, 0);
    }

    public double get(int index) {
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
                sensorValues.stream().mapToDouble(d -> d.temperature).average().orElse(Double.NaN),
                sensorValues.stream().mapToDouble(d -> d.humidity).average().orElse(Double.NaN),
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
