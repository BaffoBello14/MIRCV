package it.unipi.MIRCV.Utils.Indexing;
import com.google.common.cache.*;
public class LRUCache<K,V> {
    private final Cache<K,V>cache;
    public LRUCache(int maxSize){
        cache=CacheBuilder.newBuilder().maximumSize(maxSize).build();
    }
    public void put(K key,V value){
        cache.put(key, value);
    }
    public boolean containsKey(K term){
        V element=cache.getIfPresent(term);
        return element != null;
    }
    public V get(K key){
        return cache.getIfPresent(key);
    }
    public void remove(K key){
        cache.invalidate(key);
    }
}
