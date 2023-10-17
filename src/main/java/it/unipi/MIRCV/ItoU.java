package it.unipi.MIRCV;

import java.util.ArrayList;
import java.util.List;

public class ItoU {

    public static byte[] convertiInUnario(int[] numeri) {
        byte[] buffer = new byte[numeri.length * 8]; // Allocazione massima
        int currentIndex = 0;
        int bitCount = 0;
        byte currentByte = 0x00;

        for (int n : numeri) {
            for (int i = 1; i < n; i++) {
                currentByte = (byte) ((currentByte << 1) | 1);
                bitCount++;

                if (bitCount == 8) {
                    buffer[currentIndex++] = currentByte;
                    currentByte = 0x00;
                    bitCount = 0;
                }
            }

            currentByte = (byte) (currentByte << 1);
            bitCount++;
            
            if (bitCount == 8) {
                buffer[currentIndex++] = currentByte;
                currentByte = 0x00;
                bitCount = 0;
            }
        }

        if (bitCount > 0) {
            currentByte = (byte) (currentByte << (8 - bitCount));
            buffer[currentIndex++] = currentByte;
        }

        byte[] result = new byte[currentIndex];
        System.arraycopy(buffer, 0, result, 0, currentIndex);
        return result;
    }

    public static int[] convertiDaUnario(byte[] unario) {
        List<Integer> numeriList = new ArrayList<>();

        int count = 1;  // Poiché stiamo contando '0's, iniziamo da 1
        for (byte b : unario) {
            for (int mask = 0x80; mask != 0; mask >>= 1) {
                if ((b & mask) != 0) {
                    count++;
                } else {
                    numeriList.add(count);
                    count = 1;  // Resetta il contatore per il prossimo numero
                }
            }
        }

        // Converti la lista di Integer in un array di int
        return numeriList.stream().mapToInt(i -> i).toArray();
    }

    public static void main(String[] args) {
        int[] numeri = {8, 3, 1, 4};  // Esempio di input
        byte[] unario = convertiInUnario(numeri);

        System.out.print("La rappresentazione unaria come array di byte è: ");
        for (byte b : unario) {
            String binaryStr = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            System.out.print(binaryStr + " ");
        }

        System.out.println();
        int[] numeriRicavati = convertiDaUnario(unario);
        System.out.print("Convertendo l'array di byte torniamo a: ");
        for (int n : numeriRicavati) {
            System.out.print(n + " ");
        }
    }
}
