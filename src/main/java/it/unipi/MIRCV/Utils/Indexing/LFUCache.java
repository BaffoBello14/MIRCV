package it.unipi.MIRCV.Utils.Indexing;
import java.util.*;

public class LFUCache<K, V> {
    private final int capacity;
    private final Map<K, V> cache;
    private final Map<K, Integer> usageCounts;
    private final Map<Integer, LinkedHashSet<K>> frequencyLists;
    private final TreeSet<LFUItem> lfuItems;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.usageCounts = new HashMap<>();
        this.frequencyLists = new HashMap<>();
        this.lfuItems = new TreeSet<>();
    }

    public V get(K key) {
        if (!cache.containsKey(key)) {
            return null;
        }

        int frequency = usageCounts.get(key);
        LFUItem item = new LFUItem(key, frequency);
        lfuItems.remove(item);

        usageCounts.put(key, frequency + 1);

        if (!frequencyLists.containsKey(frequency + 1)) {
            frequencyLists.put(frequency + 1, new LinkedHashSet<>());
        }

        frequencyLists.get(frequency + 1).add(key);

        lfuItems.add(new LFUItem(key, frequency + 1));

        return cache.get(key);
    }

    public void put(K key, V value) {
        if (capacity == 0) {
            return;
        }

        if (cache.size() >= capacity && !cache.containsKey(key)) {
            removeLFU();
        }

        if (!cache.containsKey(key)) {
            usageCounts.put(key, 1);
        }

        cache.put(key, value);
        lfuItems.add(new LFUItem(key, 1));
    }
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    private void removeLFU() {
        LFUItem lfuItem = lfuItems.first();
        K keyToRemove = lfuItem.key;
        lfuItems.remove(lfuItem);
        cache.remove(keyToRemove);
        usageCounts.remove(keyToRemove);
    }

    private class LFUItem implements Comparable<LFUItem> {
        K key;
        int frequency;

        public LFUItem(K key, int frequency) {
            this.key = key;
            this.frequency = frequency;
        }

        @Override
        public int compareTo(LFUItem other) {
            int frequencyComparison = Integer.compare(this.frequency, other.frequency);
            if (frequencyComparison == 0) {
                // If frequencies are equal, use key comparison
                return key.hashCode() - other.key.hashCode();
            }
            return frequencyComparison;
        }
    }
}
