package it.unipi.MIRCV.Converters;

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

    
}
