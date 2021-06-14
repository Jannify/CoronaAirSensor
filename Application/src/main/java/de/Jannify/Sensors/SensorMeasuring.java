package de.Jannify.Sensors;

import de.Jannify.IO.Config;
import de.Jannify.Main;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public class SensorMeasuring extends Thread {
    private static final DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("'['yyyy-MM-dd'] ['HH-mm']'");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static final DecimalFormat decimalFormatter = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.GERMANY));

    private static LocalDateTime startTime;
    private SensorValue currentValue = new SensorValue();
    private final List<SensorValue> valuesPerSave = new ArrayList<>();
    private int elapsedMinutes = 0;

    @Override
    public void run() {
        startTime = LocalDateTime.now();
        int timeSync = Config.getTimeSyncMili();
        int factorToSave = Config.getTimeSaveMili() / Config.getTimeSyncMili();

        File file = new File(Config.getPath() + "/" + fileNameFormatter.format(LocalDateTime.now()) + ".csv");
        if (!file.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
                appendTextToFile(file, "Temperatur in °C;Luftfeuchtigkeit in %;eCO2 in bp;PM2 in μg/m³;PM10 in μg/m³ \n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        while (isAlive()) {
            synchronized (this) {
                for (int i = 0; i < factorToSave; i++) {
                    syncData();

                    try {
                        this.wait(timeSync);
                    } catch (InterruptedException ignored) {
                    }
                }
                saveData(file);
            }
        }
    }

    public SensorValue getCurrentValue() {
        return currentValue;
    }

    private void syncData() {
        currentValue = new SensorValue(
                SensorInterface.getTemperature(),
                SensorInterface.getHumidity(),
                SensorInterface.getCO2(),
                SensorInterface.getPM2(),
                SensorInterface.getPM10());
        valuesPerSave.add(currentValue);
    }

    private void saveData(File file) {
        SensorValue average = SensorValue.getAverage(valuesPerSave);

        int tmpMinutes = (int) startTime.until( LocalDateTime.now(), ChronoUnit.MINUTES );
        String timeInSteps = "";
        if (tmpMinutes - elapsedMinutes >= 10) {
            elapsedMinutes = tmpMinutes;
            timeInSteps = String.valueOf(elapsedMinutes);
        }

        appendTextToFile(file, MessageFormat.format("{0};{1};{2};{3};{4};{5};{6}\n",
                decimalFormatter.format(average.getTemperature()), decimalFormatter.format(average.getHumidity()),
                decimalFormatter.format(average.getCO2()/100.0), average.getPM2(), average.getPM10(),
                timeFormatter.format(LocalDateTime.now()), timeInSteps));

        valuesPerSave.clear();
    }

    public void appendTextToFile(File file, String text) {
        try {
            FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
            FileLock lock = null;
            try {
                lock = channel.tryLock();
            } catch (OverlappingFileLockException e) {
                Main.logger.log(Level.SEVERE, "Could not acquire lock on" + file.getPath() );
            }

            FileUtils.writeStringToFile(file, text, Charset.defaultCharset(), true);

            if( lock != null ) {
                lock.release();
            }

            channel.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
