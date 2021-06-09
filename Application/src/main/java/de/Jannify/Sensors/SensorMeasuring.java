package de.Jannify.Sensors;

import de.Jannify.IO.Config;
import de.Jannify.IO.FileIO;
import de.Jannify.Main;
import org.iot.raspberry.grovepi.devices.GroveTemperatureAndHumidityValue;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SensorMeasuring implements Runnable, Closeable {
    private static final DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("'['yyyy-MM-dd'] ['HH-mm']'");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static final DecimalFormat decimalFormatter = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.GERMANY));
    private final Object monitor = new Object();
    private final Object monitor2 = new Object();

    private GroveBridge groveBridge;
    private NovaPMSensor novaPMSensor;
    private ExecutorService syncData;
    private ExecutorService saveData;
    private int timeSync;
    private int timeSave;

    private SensorValue currentValue = new SensorValue();
    private final List<SensorValue> valuesPerMinute = new ArrayList<>();
    private int elapsedMinutes;

    @Override
    public void run() {
        groveBridge = Main.grove;
        novaPMSensor = Main.novaPMSensor;
        timeSync = Config.getTimeSync() * 1000;
        timeSave = Config.getTimeSave() * 1000;

        File database = new File(Config.getPath() + "/" + fileNameFormatter.format(LocalDateTime.now()) + ".csv");
        if (!database.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                database.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileIO.setFile(database);

        syncData = Executors.newSingleThreadExecutor();
        syncData.execute(this::syncData);

        saveData = Executors.newSingleThreadExecutor();
        saveData.execute(this::saveData);
    }


    @Override
    public void close() {
        syncData.shutdown();
        saveData.shutdown();
    }

    public SensorValue getCurrent() {
        return currentValue;
    }

    public void syncData() {
        while (!syncData.isShutdown()) {
            synchronized (monitor) {
                try {
                    GroveTemperatureAndHumidityValue value = groveBridge.getTemperatureAndHumidity();
                    currentValue = new SensorValue(value.getTemperature(), value.getHumidity(), novaPMSensor.getPM2(), novaPMSensor.getPM10(), groveBridge.getCO2());
                    valuesPerMinute.add(currentValue);

                    monitor.wait(timeSync);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void saveData() {
        while (!syncData.isShutdown()) {
            synchronized (monitor2) {
                try {
                    monitor2.wait(timeSave);

                    String time = "";
                    if (elapsedMinutes % 10 == 0) {
                        time = String.valueOf(elapsedMinutes);
                    }
                    SensorValue average = SensorValue.getAverage(valuesPerMinute);

                    Thread io = new Thread(new FileIO(MessageFormat.format("{0};{1};{2};{3};{4};{5};{6}\n",
                            time, decimalFormatter.format(average.temperature), decimalFormatter.format(average.humidity), average.PM2,
                            average.PM10, decimalFormatter.format(average.CO2/100.0), timeFormatter.format(LocalDateTime.now()))));
                    io.start();
                    valuesPerMinute.clear();
                    elapsedMinutes += 1;
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
