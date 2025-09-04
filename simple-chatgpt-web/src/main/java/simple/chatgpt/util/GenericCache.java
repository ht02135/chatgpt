package simple.chatgpt.util;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/*
This class is thread-safe not because of anything you did 
explicitly in GenericCache, but because of the underlying 
Caffeine Cache<K, V> implementation.
////////////////////////
1>Caffeine is designed for concurrent access
The Cache from com.github.benmanes.caffeine.cache is implemented 
with highly optimized, lock-striped data structures (similar to 
ConcurrentHashMap), so multiple threads can safely call get, put, 
invalidate, etc. at the same time without corrupting state.
2>Atomic get with loader
The cache.get(key, loader) call is atomic. If multiple threads 
request the same key simultaneously:
Only one thread will run the loader function.
The others will block until that computation is complete, and 
then get the same computed value.
This ensures consistency and avoids race conditions.
3>No shared mutable state in GenericCache itself
Your class only holds a single final reference to the Caffeine 
cache. Since all operations just delegate to the Caffeine instance, 
and that instance is already thread-safe, your wrapper is inherently 
thread-safe as well.
4>Spring bean safety
Since this class is intended to be a singleton Spring bean, it’s 
important that it doesn’t need external synchronization. Any 
number of threads in your app can use the same cache bean 
concurrently without issues.

In short: Caffeine provides the concurrency guarantees, and 
your wrapper doesn’t break them. That’s why GenericCache is 
thread-safe.
*/

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