package it.unipi;

import org.junit.jupiter.api.Test;

import it.unipi.MIRCV.Converters.*;

import static org.junit.jupiter.api.Assertions.*;

public class ConvertersTest {
    @Test
    public void testConvertToUnary() {
        int[] input = {1, 5, 10, 15, 20};
        byte[] expected = {(byte) 0b01111011, (byte) 0b11111110, (byte) 0b11111111,
            (byte) 0b11111101, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11000000};
        byte[] result = UnaryConverter.convertToUnary(input);
        assertArrayEquals(expected, result);
    }


    @Test
    public void testConvertFromUnary() {
        byte[] input = {(byte) 0b01111011, (byte) 0b11111110, (byte) 0b11111111,
            (byte) 0b11111101, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111110};
        int[] expected = {1, 5, 10, 15, 25};
        int len = 5;

        int[] result = UnaryConverter.convertFromUnary(input, len);

        assertArrayEquals(expected, result);
    }


    @Test
    public void testEncode() {
        int[] input = {1, 127, 128, 255, 256, 16383, 16384};
        byte[][] expected = {
                {(byte) 0b00000001},
                {(byte) 0b01111111},
                {(byte) 0b00000001, (byte) 0b10000000},
                {(byte) 0b00000001, (byte) 0b11111111},
                {(byte) 0b00000010, (byte) 0b10000000},
                {(byte) 0b01111111, (byte) 0b11111111},
                {(byte) 0b00000001, (byte) 0b10000000, (byte) 0b10000000}
        };
        for (int i = 0; i < input.length; i++) {
            byte[] result = VariableByteEncoder.encode(input[i]);
            assertArrayEquals(expected[i], result);
        }
    }


    @Test
    public void testDecode() {
        byte[][] input = {
                {(byte) 0b00000001},
                {(byte) 0b01111111},
                {(byte) 0b00000001, (byte) 0b10000000},
                {(byte) 0b00000001, (byte) 0b11111111},
                {(byte) 0b00000010, (byte) 0b10000000},
                {(byte) 0b01111111, (byte) 0b11111111},
                {(byte) 0b00000001, (byte) 0b10000000, (byte) 0b10000000}
        };
        int[] expected = {1, 127, 128, 255, 256, 16383, 16384};

        for (int i = 0; i < input.length; i++) {
            int result = VariableByteEncoder.decode(input[i]);
            assertEquals(expected[i], result);
        }
    }
    @Test
    public void testEncodeArray() {
        int[] originalValues = {42, 123, 999, 4567};
        byte[] expectedEncodedBytes = {(byte)0x2A, (byte) 0x7B, (byte) 0x07,(byte)0xE7, (byte) 0x23, (byte)0xD7};

        // Encode the original values.
        byte[] encodedBytes = VariableByteEncoder.encodeArray(originalValues);

        // Check if the encoded bytes match the expected encoded bytes.
        assertArrayEquals(expectedEncodedBytes, encodedBytes);
    }
    @Test
    public void testDecodeArray() {
        byte[] encodedBytes = {(byte)0x2A, (byte) 0x7B, (byte) 0x07,(byte)0xE7, (byte) 0x23, (byte)0xD7};
        int[] expectedDecodedValues = {42, 123, 999, 4567};

        // Decode the encoded bytes.
        int[] decodedValues = VariableByteEncoder.decodeArray(encodedBytes);

        // Check if the decoded values match the expected decoded values.
        assertArrayEquals(expectedDecodedValues, decodedValues);
    }
}
