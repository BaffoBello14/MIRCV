//codifica caratteri
/*
package it.unipi.MIRCV;

import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {

        String dataToWrite = "ciao" + "\t" + 10;

        // Scrivi nel file come testo
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream("data.dat"))) {
            dataOutputStream.write(dataToWrite.getBytes("UTF-8"));
        }

        // Leggi dal file come testo
        try (RandomAccessFile randomAccessFile = new RandomAccessFile("data.dat", "r");
             DataInputStream dataInputStream = new DataInputStream(new FileInputStream(randomAccessFile.getFD()))) {

            byte[] readBytes = new byte[(int)randomAccessFile.length()];
            dataInputStream.read(readBytes);

            String readString = new String(readBytes, "UTF-8");
            System.out.print(readString);
        }
    }
}
*/

//codifica binaria

package it.unipi.MIRCV;

import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {

        // Scrivi nel file come dati binari
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream("data.dat"))) {
            dataOutputStream.write("ciao".getBytes("UTF-8"));
            dataOutputStream.writeChar('\t');
            dataOutputStream.writeInt(10);
        }

        // Leggi dal file come dati binari
        try (RandomAccessFile randomAccessFile = new RandomAccessFile("data.dat", "r");
             DataInputStream dataInputStream = new DataInputStream(new FileInputStream(randomAccessFile.getFD()))) {

            byte[] readBytes = new byte[4]; // assumiamo che "ciao" sia sempre di 4 byte
            dataInputStream.read(readBytes);
            String readString = new String(readBytes, "UTF-8");

            char tabChar = dataInputStream.readChar();  // legge il carattere tabulatore
            int number = dataInputStream.readInt();

            System.out.print(readString + tabChar + number);
        }
    }
}
