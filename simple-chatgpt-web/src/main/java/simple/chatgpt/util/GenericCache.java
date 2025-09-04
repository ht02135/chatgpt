package simple.chatgpt.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * A generic, thread-safe cache wrapper using Caffeine.
 * This class is designed to be a regular Spring bean,
 * with instances managed by the Spring container.
 *
 * The underlying Caffeine library is inherently thread-safe,
 * so no manual synchronization is needed for the get/put/invalidate methods.
 */
public class GenericCache<K, V> {

    private final Cache<K, V> cache;

    // Use a public constructor so Spring can create instances
    public GenericCache(long expireAfterMinutes, long maximumSize) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(expireAfterMinutes, TimeUnit.MINUTES)
                .maximumSize(maximumSize)
                .build();
    }

    /**
     * Retrieves a value from the cache. If the value does not exist,
     * the loader function is called to compute it, and the result is
     * atomically stored in the cache before being returned.
     *
     * @param key The key to retrieve.
     * @param loader The function to load the value if it's not in the cache.
     * @return The cached or newly loaded value.
     */
    public V get(K key, Function<K, V> loader) {
        return cache.get(key, loader);
    }

    /**
     * Puts a value into the cache.
     *
     * @param key The key to store.
     * @param value The value to store.
     */
    public void put(K key, V value) {
        cache.put(key, value);
    }

    /**
     * Invalidates a single entry from the cache.
     *
     * @param key The key to invalidate.
     */
    public void invalidate(K key) {
        cache.invalidate(key);
    }

    /**
     * Clears all entries from the cache.
     */
    public void clear() {
        cache.invalidateAll();
    }
}