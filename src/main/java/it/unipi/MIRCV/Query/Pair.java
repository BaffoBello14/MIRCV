package it.unipi.MIRCV.Query;

/**
 * A simple class representing a pair of two values.
 *
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 */
public class Pair<K, V> {
    private final K key;
    private final V value;

    /**
     * Constructor for Pair class.
     *
     * @param key   The key of the pair.
     * @param value The value of the pair.
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Getter for the key of the pair.
     *
     * @return The key of the pair.
     */
    public K getKey() {
        return key;
    }

    /**
     * Getter for the value of the pair.
     *
     * @return The value of the pair.
     */
    public V getValue() {
        return value;
    }
}
