package de.Jannify.IO;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileIO implements Runnable {
    private static File File;
    private final String Text;

    public FileIO(String text) {
        Text = text;
    }

    public static void setFile(File file){
        File = file;
    }

    @Override
    public void run() {

        try {
            FileChannel channel = new RandomAccessFile(File, "rw").getChannel();
            FileLock lock = null;
            try {
                lock = channel.tryLock();
            } catch (OverlappingFileLockException e) {
                Logger.getLogger("RaspberryPi").log(Level.WARNING, "Could not acquire lock on" + File.getPath() );
            }

            FileUtils.writeStringToFile(File, Text, Charset.defaultCharset(), true);

            if( lock != null ) {
                lock.release();
            }

            channel.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
