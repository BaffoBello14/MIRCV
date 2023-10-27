package it.unipi.MIRCV.Converters;

import java.util.ArrayList;
import java.util.List;

public class VariableByteEncoder {

    /**
     * Encodes an integer value using Variable Byte encoding.
     * The method involves representing an integer in base-128 where the most significant bit 
     * of each byte determines if there's another byte after it.
     *
     * @param value The integer to encode.
     * @return An array of bytes representing the encoded value.
     */
    public static byte[] encode(int value) {
        // Calculate the number of bytes required to represent the value in base-128.
        int numBytes = (int) Math.ceil((Math.log(value + 1) / Math.log(128)));
        byte[] encodedBytes = new byte[numBytes];

        // Process each byte of the encoded value, starting from the least significant.
        for (int i = numBytes - 1; i >= 0; i--) {
            byte currentByte = (byte) (value % 128);
            value /= 128;
            
            // Set the most significant bit if there's another byte after the current one.
            if (i > 0) {
                currentByte |= (byte) 128;
            }
            encodedBytes[i] = currentByte;
        }

        return encodedBytes;
    }

    public static byte[] encodeArray(int[] values) {
        // Calculate the total number of bytes required to represent all the values in base-128.
        int totalBytes = 0;
        for (int value : values) {
            totalBytes += (int) Math.ceil((Math.log(value + 1) / Math.log(128)));
        }

        byte[] encodedBytes = new byte[totalBytes];
        int currentIndex = 0;

        // Process each integer in the input array.
        for (int value : values) {
            int numBytes = (int) Math.ceil((Math.log(value + 1) / Math.log(128)));
            
            // Process each byte of the encoded value, starting from the least significant.
            for (int i = numBytes - 1; i >= 0; i--) {
                byte currentByte = (byte) (value % 128);
                value /= 128;

                // Set the most significant bit if there's another byte after the current one.
                if (i > 0) {
                    currentByte |= (byte) 128;
                }
                encodedBytes[currentIndex] = currentByte;
                currentIndex++;
            }
        }

        return encodedBytes;
    }

    /**
     * Decodes a value encoded using Variable Byte encoding back into its integer representation.
     *
     * @param encodedBytes An array of bytes representing the encoded value.
     * @return The decoded integer value.
     */
    public static int decode(byte[] encodedBytes) {
        int decodedValue = 0;

        // Process each byte of the encoded array.
        for (byte b : encodedBytes) {
            int value = b & 0x7f; // Retrieve the 7 least significant bits.
            decodedValue = (decodedValue << 7) | value;
        }

        return decodedValue;
    }

    public static int[] decodeArray(byte[] encodedBytes) {
        List<Integer> decodedValues = new ArrayList<>();
        int currentIndex = 0;

        // Process each byte until we've processed the entire encoded array.
        while (currentIndex < encodedBytes.length) {
            int decodedValue = 0;
            int shift = 0;

            byte currentByte;
            do {
                currentByte = encodedBytes[currentIndex];
                int value = currentByte & 0x7f;
                decodedValue |= (value << shift);
                shift += 7;
                currentIndex++;
            } while ((currentByte & 0x80) != 0); // Check for continuation byte.

            decodedValues.add(decodedValue);
        }

        // Convert the ArrayList of decoded values to an array.
        int[] result = new int[decodedValues.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = decodedValues.get(i);
        }

        return result;
    }
}
