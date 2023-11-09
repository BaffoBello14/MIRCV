package it.unipi.MIRCV.Converters;

import java.util.ArrayList;
import java.util.List;

/**
 * The VariableByteEncoder class provides methods for encoding and decoding integers using Variable Byte encoding.
 * This encoding represents an integer in base-128, where the most significant bit of each byte indicates 
 * if there's another byte after it.
 */
public class VariableByteEncoder {

    /**
     * Encodes an integer value using Variable Byte encoding.
     *
     * @param value The integer to encode.
     * @return An array of bytes representing the encoded value.
     */
    public static byte[] encode(int value) {
        // Calculate the number of bytes required to represent the value in base-128.
        int numBytes = (int) Math.ceil((Math.log(value + 1) / Math.log(128)));
        byte[] encodedBytes = new byte[numBytes];

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

    /**
     * Encodes an array of integers using Variable Byte encoding.
     *
     * @param values The array of integers to encode.
     * @return An array of bytes representing the encoded values.
     */
    public static byte[] encodeArray(int[] values) {
        // Calculate the total number of bytes required to represent all the values in base-128.
        int totalBytes = 0;
        for (int value : values) {
            totalBytes += (int) Math.ceil((Math.log(value + 1) / Math.log(128)));
        }

        byte[] encodedBytes = new byte[totalBytes];
        int currentIndex = 0;
        for (int value : values) {
            byte[] encode = encode(value);
            System.arraycopy(encode, 0, encodedBytes, currentIndex, encode.length);
            currentIndex += encode.length;
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

        for (byte b : encodedBytes) {
            int value = b & 0x7f; // Retrieve the 7 least significant bits.
            decodedValue = (decodedValue << 7) | value;
        }

        return decodedValue;
    }

    /**
     * Decodes an array of values encoded using Variable Byte encoding back into an array of integers.
     *
     * @param encodedBytes An array of bytes representing the encoded values.
     * @return An array of integers containing the decoded values.
     */
    public static int[] decodeArray(byte[] encodedBytes) {
        List<Integer> decodedValues = new ArrayList<>();
        int currentIndex = encodedBytes.length - 1;
        int backwardIndex = currentIndex;

        while (backwardIndex >= 0) {
            int value;
            while ((encodedBytes[backwardIndex] & 0x80) != 0x00) {
                backwardIndex--;
            }
            byte[] valueByte = new byte[currentIndex - backwardIndex + 1];
            for (int i = backwardIndex, j = 0; j < currentIndex - backwardIndex + 1; i++, j++) {
                valueByte[j] = encodedBytes[i];
            }
            value = decode(valueByte);
            backwardIndex--;
            currentIndex = backwardIndex;

            decodedValues.add(value);
        }

        // Convert the List to an array.
        int[] result = new int[decodedValues.size()];
        for (int i = decodedValues.size() - 1; i >= 0; i--) {
            result[i] = decodedValues.get(decodedValues.size() - i - 1);
        }

        return result;
    }
}
