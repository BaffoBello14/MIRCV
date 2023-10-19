package it.unipi.MIRCV;

public class VariableByteEncoder {

    /**
     * Codifica un valore intero utilizzando la codifica "Variable Byte".
     *
     * @param value L'intero da codificare.
     * @return Un array di byte che rappresenta il valore in codifica "Variable Byte".
     */
    public static byte[] encode(int value) {
        // Calcola il numero di byte necessari per rappresentare il valore dato.
        // Ogni byte può rappresentare 7 bit del valore, più 1 bit come segnalatore di continuazione.
        int numBytes = (int) Math.ceil((Math.log(value + 1) / Math.log(128)));
        byte[] encodedBytes = new byte[numBytes];

        // Inizia dalla posizione del byte meno significativo e lavora all'indietro.
        for (int i = numBytes - 1; i >= 0; i--) {
            // Estrai i 7 bit meno significativi del valore.
            byte currentByte = (byte) (value % 128);
            
            // Riduci il valore, rimuovendo i bit che abbiamo appena estratto.
            value /= 128;
            
            // Se non siamo sull'ultimo byte, imposta il bit più significativo (segnalatore di continuazione) su 1.
            if (i > 0) {
                currentByte |= (byte) 128;
            }
            
            // Assegna il byte codificato all'array di output nella posizione corrente.
            encodedBytes[i] = currentByte;
        }

        return encodedBytes;
    }

    /**
     * Decodifica un valore intero da una rappresentazione "Variable Byte".
     *
     * @param encodedBytes L'array di byte in codifica "Variable Byte".
     * @return L'intero decodificato.
     */
    public static int decode(byte[] encodedBytes) {
        int decodedValue = 0;

        // Per ogni byte nell'array di input...
        for (byte b : encodedBytes) {
            // Estrai i 7 bit meno significativi dal byte corrente.
            int value = b & 0x7f;

            // Aggiungi questi bit al valore decodificato, spostandoli nella posizione corretta.
            decodedValue = (decodedValue << 7) | value;
        }

        return decodedValue;
    }

    public static void main(String[] args) {
        int[] testValues = {1, 127, 128, 255, 256, 16383, 16384, 2097151};
    
        System.out.println("Test NewVariableByteEncoder:");
    
        for (int value : testValues) {
            // Inizia a misurare il tempo per la codifica
            long startTimeEncode = System.nanoTime();
            byte[] encoded = VariableByteEncoder.encode(value);
            long endTimeEncode = System.nanoTime();
    
            // Inizia a misurare il tempo per la decodifica
            long startTimeDecode = System.nanoTime();
            int decoded = VariableByteEncoder.decode(encoded);
            long endTimeDecode = System.nanoTime();
    
            // Calcola il tempo trascorso per la codifica e la decodifica
            long durationEncode = endTimeEncode - startTimeEncode;
            long durationDecode = endTimeDecode - startTimeDecode;
    
            System.out.print("Valore originale: " + value + " -> Codificato: ");
            for (byte b : encoded) {
                System.out.print(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0') + " ");
            }
            System.out.println("-> Decodificato: " + decoded);
            System.out.println("Tempo di codifica: " + durationEncode + " nanosecondi");
            System.out.println("Tempo di decodifica: " + durationDecode + " nanosecondi");
            System.out.println("-------------------------------------------------");
        }
    }
}
