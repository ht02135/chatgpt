package simple.chatgpt.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class GenericCache<K, V> {
    private final Cache<K, V> cache;
    private static GenericCache<?, ?> INSTANCE;

    private GenericCache(long expireAfterMinutes, long maximumSize) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(expireAfterMinutes, TimeUnit.MINUTES)
                .maximumSize(maximumSize)
                .build();
    }

    @SuppressWarnings("unchecked")
    public static synchronized <K, V> GenericCache<K, V> getInstance(long expireAfterMinutes, long maximumSize) {
        if (INSTANCE == null) {
            INSTANCE = new GenericCache<>(expireAfterMinutes, maximumSize);
        }
        return (GenericCache<K, V>) INSTANCE;
    }

    public V get(K key, Function<K, V> loader) {
        return cache.get(key, loader);
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public void invalidate(K key) {
        cache.invalidate(key);
    }

    public void clear() {
        cache.invalidateAll();
    }
}
