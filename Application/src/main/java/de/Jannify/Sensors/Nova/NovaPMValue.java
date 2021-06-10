package de.Jannify.Sensors.Nova;

public class NovaPMValue {
    private final int pm2;
    private final int pm10;

    public NovaPMValue(int pm2, int pm10) {
        this.pm2 = pm2;
        this.pm10 = pm10;
    }

    public int getPm2() {
        return pm2;
    }

    public int getPm10() {
        return pm10;
    }
}
