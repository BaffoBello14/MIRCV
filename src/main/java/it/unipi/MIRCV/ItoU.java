package it.unipi.MIRCV;

import java.util.ArrayList;
import java.util.List;

public class ItoU {

    public static byte[] convertiInUnario(int[] numeri) {
        List<Byte> unarioList = new ArrayList<>();

        int bitCount = 0;
        byte currentByte = 0x00;

        for (int n : numeri) {
            for (int i = 0; i < n; i++) {
                currentByte = (byte) ((currentByte << 1) | 1);
                bitCount++;

                if (bitCount == 8) {
                    unarioList.add(currentByte);
                    currentByte = 0x00;
                    bitCount = 0;
                }
            }

            if (bitCount != 0) {
                currentByte = (byte) (currentByte << 1);
                bitCount++;

                if (bitCount == 8) {
                    unarioList.add(currentByte);
                    currentByte = 0x00;
                    bitCount = 0;
                }
            }
        }

        if (bitCount > 0) {
            currentByte = (byte) (currentByte << (8 - bitCount));
            unarioList.add(currentByte);
        }

        byte[] unarioArray = new byte[unarioList.size()];
        for (int i = 0; i < unarioList.size(); i++) {
            unarioArray[i] = unarioList.get(i);
        }
        return unarioArray;
    }

    public static void main(String[] args) {
        int[] numeri = {9, 10}; 
        byte[] unario = convertiInUnario(numeri);

        System.out.print("La rappresentazione unaria come array di byte è: ");
        
        for (byte b : unario) {
            String binaryStr = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            System.out.print(binaryStr + " ");
        }
    }
}
