package it.unipi.MIRCV;

import java.util.ArrayList;
import java.util.List;

public class ItoU {

    public static byte[] convertiInUnario(int[] numeri) {
        // Calcola la dimensione totale in bit. 
        // Ogni numero n sarà rappresentato da n bit: n-1 bit settati su 1 seguiti da un bit 0.
        int totalBits = 0;
        for (int n : numeri) {
            totalBits += n;
        }

        // Calcola la dimensione necessaria in byte. 
        // L'arrotondamento verso l'alto assicura che ci sia abbastanza spazio per tutti i bit.
        int bufferSize = (totalBits + 7) / 8; 
        byte[] buffer = new byte[bufferSize];
    
        int currentIndex = 0; // Indice corrente nel buffer. Utilizzato per tracciare dove inserire il prossimo byte.
        int bitCount = 0;     // Conta quanti bit sono stati inseriti nell'attuale byte.
        byte currentByte = 0x00;  // L'attuale byte che viene riempito.

        // Per ogni numero nel nostro array di input...
        for (int n : numeri) {
            // Aggiungi n-1 bit settati su 1.
            for (int i = 1; i < n; i++) {
                // Sposta il byte corrente a sinistra e imposta l'ultimo bit su 1.
                currentByte = (byte) ((currentByte << 1) | 1);
                bitCount++;

                // Se abbiamo riempito un byte completo, aggiungilo al buffer.
                if (bitCount == 8) {
                    buffer[currentIndex++] = currentByte;
                    currentByte = 0x00; // Resetta il byte corrente.
                    bitCount = 0;       // Resetta il contatore dei bit.
                }
            }

            // Dopo aver aggiunto n-1 bit 1, aggiungi un bit 0.
            currentByte = (byte) (currentByte << 1);
            bitCount++;

            // Ancora una volta, se abbiamo riempito un byte completo, aggiungilo al buffer.
            if (bitCount == 8) {
                buffer[currentIndex++] = currentByte;
                currentByte = 0x00; // Resetta il byte corrente.
                bitCount = 0;       // Resetta il contatore dei bit.
            }
        }

        // Se ci sono bit rimanenti nell'ultimo byte, assicuriamoci che siano spostati nella posizione corretta.
        if (bitCount > 0) {
            currentByte = (byte) (currentByte << (8 - bitCount));
            buffer[currentIndex++] = currentByte;
        }

        // Il buffer è pronto e contiene la rappresentazione unaria dell'input.
        return buffer;
    }

    public static int[] convertiDaUnario(byte[] unario) {
        // Crea una lista per conservare i numeri estratti dall'array di byte in codifica unaria.
        List<Integer> numeriList = new ArrayList<>();
    
        // Inizializza il contatore. Iniziamo da 1 poiché contiamo i '0's per determinare 
        // la fine della rappresentazione unaria di un numero.
        int count = 1;  
    
        // Itera attraverso ogni byte nell'array di byte in input.
        for (byte b : unario) {
            // Utilizza una maschera per controllare ogni singolo bit del byte.
            // Inizia dalla maschera 0x80 (cioè 10000000 in binario) per controllare 
            // il bit più significativo prima.
            for (int mask = 0x80; mask != 0; mask >>= 1) {
                
                // Controlla se il bit corrente è 1.
                if ((b & mask) != 0) {
                    count++;  // Incrementa il contatore.
                }
                // Se il bit corrente è 0, abbiamo raggiunto la fine della rappresentazione
                // unaria per il numero corrente.
                else {
                    numeriList.add(count);   // Aggiungi il numero estratto alla lista.
                    count = 1;  // Resetta il contatore per il prossimo numero.
                }
            }
        }
    
        // Alla fine dell'elaborazione, converti la lista di numeri estratti in un array di interi.
        // Utilizziamo le funzionalità di streaming Java per effettuare questa conversione.
        return numeriList.stream().mapToInt(i -> i).toArray();
    }

    public static void main(String[] args) {
        int[] numeri = {1, 127, 128, 255, 256, 16383, 16384, 2097151};  // Esempio di input
        
        // Inizia a misurare il tempo per la conversione in unario
        long startTimeConvertiInUnario = System.nanoTime();
        byte[] unario = convertiInUnario(numeri);
        long endTimeConvertiInUnario = System.nanoTime();
    
        // Calcola il tempo trascorso per la conversione in unario
        long durationConvertiInUnario = endTimeConvertiInUnario - startTimeConvertiInUnario;
    
        System.out.print("La rappresentazione unaria come array di byte è: ");
        for (byte b : unario) {
            String binaryStr = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            System.out.print(binaryStr + " ");
        }
    
        System.out.println();
        System.out.println("Tempo di conversione in unario: " + durationConvertiInUnario + " nanosecondi");
    
        // Inizia a misurare il tempo per la conversione da unario
        long startTimeConvertiDaUnario = System.nanoTime();
        int[] numeriRicavati = convertiDaUnario(unario);
        long endTimeConvertiDaUnario = System.nanoTime();
    
        // Calcola il tempo trascorso per la conversione da unario
        long durationConvertiDaUnario = endTimeConvertiDaUnario - startTimeConvertiDaUnario;
    
        System.out.print("Convertendo l'array di byte torniamo a: ");
        for (int n : numeriRicavati) {
            System.out.print(n + " ");
        }
    
        System.out.println();
        System.out.println("Tempo di conversione da unario: " + durationConvertiDaUnario + " nanosecondi");
    }    
}
