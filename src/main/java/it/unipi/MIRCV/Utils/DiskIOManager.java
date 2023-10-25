package it.unipi.MIRCV.Utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class DiskIOManager {

    public static MappedByteBuffer readFromDisk(String path, long offset, long size) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(path);
             FileChannel fileChannel = fileInputStream.getChannel()) {

            return fileChannel.map(FileChannel.MapMode.READ_ONLY, offset, size);

        } catch (IOException e) {
            throw new IOException("Problems with reading from file at " + path + ": " + e.getMessage());
        }
    }

    public static boolean writeToDisk(String path, MappedByteBuffer dataBuffer) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path);
             FileChannel fileChannel = fileOutputStream.getChannel()) {

            fileChannel.write(dataBuffer);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
