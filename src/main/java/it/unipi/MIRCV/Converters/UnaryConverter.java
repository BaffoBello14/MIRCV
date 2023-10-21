package it.unipi.MIRCV.Converters;

import java.util.ArrayList;
import java.util.List;

public class UnaryConverter {

    /**
     * Converts an array of integers into its unary representation as an array of bytes.
     * Unary representation for a number n is n-1 '1's followed by a '0'. 
     * For example, 3 in unary is "110".
     *
     * @param numbers An array of integers to convert.
     * @return The unary representation as an array of bytes.
     */
    public static byte[] convertToUnary(int[] numbers) {
        // Calculate the total number of bits required for the unary representation of all numbers.
        int totalBits = 0;
        for (int n : numbers) {
            totalBits += n;
        }

        // Calculate buffer size in bytes. We're adding 7 to round up to the nearest byte.
        int bufferSize = (totalBits + 7) / 8;
        byte[] buffer = new byte[bufferSize];

        int currentIndex = 0;  // Track current index in the buffer.
        int bitCount = 0;      // Track number of bits set in the current byte.
        byte currentByte = 0x00;

        // Process each number in the input array.
        for (int n : numbers) {
            // Set n-1 bits to '1'.
            for (int i = 1; i < n; i++) {
                currentByte = (byte) ((currentByte << 1) | 1);
                bitCount++;

                // If the byte is full, store it and reset counters.
                if (bitCount == 8) {
                    buffer[currentIndex++] = currentByte;
                    currentByte = 0x00;
                    bitCount = 0;
                }
            }

            // Set the nth bit to '0' (end of unary representation for the number).
            currentByte = (byte) (currentByte << 1);
            bitCount++;

            // If the byte is full, store it and reset counters.
            if (bitCount == 8) {
                buffer[currentIndex++] = currentByte;
                currentByte = 0x00;
                bitCount = 0;
            }
        }

        // Handle remaining bits if any.
        if (bitCount > 0) {
            currentByte = (byte) (currentByte << (8 - bitCount));
            buffer[currentIndex++] = currentByte;
        }

        return buffer;
    }

    /**
     * Converts a unary representation (given as an array of bytes) back into its integer array representation.
     * It identifies a sequence of 1s followed by a 0 as a unary encoded number.
     *
     * @param unary An array of bytes in unary representation.
     * @param len The number of numbers to convert from the unary array.
     * @return An array of integers.
     */
    public static int[] convertFromUnary(byte[] unary, int len) {
        List<Integer> numbersList = new ArrayList<>();
        int count = 1;  // Start at 1 as we count the '0' at the end of each unary number.
        int conversions = 0;  // Track how many numbers have been converted.

        // Process each byte in the input array.
        for (byte b : unary) {
            // Check each bit in the byte.
            for (int mask = 0x80; mask != 0; mask >>= 1) {
                if ((b & mask) != 0) {  // If bit is '1', increase the count.
                    count++;
                } else {  // If bit is '0', it marks the end of a unary number.
                    numbersList.add(count);
                    conversions++;
                    count = 1;  // Reset for the next number.
                }

                // Break if we've converted the specified number of numbers.
                if (conversions == len)
                    break;
            }
        }

        // Convert the list of integers to an array and return.
        return numbersList.stream().mapToInt(i -> i).toArray();
    }


    public static void main(String[] args) {
        int[] numbers = {1, 127, 128, 255, 256, 16383, 16384, 2097151}; 
        int len = numbers.length;
        long startConversionToUnaryTime = System.nanoTime();
        byte[] unary = convertToUnary(numbers);
        long endConversionToUnaryTime = System.nanoTime();
        long durationConversionToUnary = endConversionToUnaryTime - startConversionToUnaryTime;

        System.out.print("Unary representation as an array of bytes is: ");
        for (byte b : unary) {
            String binaryStr = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            System.out.print(binaryStr + " ");
        }

        System.out.println();
        System.out.println("Conversion to unary time: " + durationConversionToUnary + " nanoseconds");

        long startConversionFromUnaryTime = System.nanoTime();
        int[] retrievedNumbers = convertFromUnary(unary, len);
        long endConversionFromUnaryTime = System.nanoTime();
        long durationConversionFromUnary = endConversionFromUnaryTime - startConversionFromUnaryTime;

        System.out.print("Converting the byte array back to: ");
        for (int n : retrievedNumbers) {
            System.out.print(n + " ");
        }

        System.out.println();
        System.out.println("Conversion from unary time: " + durationConversionFromUnary + " nanoseconds");
    }    
}
