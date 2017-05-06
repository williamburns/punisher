package uk.co.williamburns.punisher.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;

/**
 * A map protected by a read-write lock to provide thread safety.
 *
 * @param <K> The key type of the map.
 * @param <V> The value type of the map.
 */
public class ReadWriteLockMap<K, V>
{
	private final ReentrantReadWriteLock lock;
	private final Map<K, V> map;

	public ReadWriteLockMap()
	{
		lock = new ReentrantReadWriteLock();
		map = new HashMap<>();
	}

	/**
	 * Allows temporary access to write to the map.
	 *
	 * @param consumer A consumer which accepts the map to be written to.
	 */
	public void write(Consumer<Map<K, V>> consumer)
	{
		try
		{
			lock.writeLock().lock();
			consumer.accept(map);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	/**
	 * Gets a value by key.
	 *
	 * @param key The key of the map.
	 * @return The value retrieved by the key.
	 */
	public V read(K key)
	{
		try
		{
			lock.readLock().lock();
			return map.get(key);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	/**
	 * Checks whether the map contains a value by a certain key.
	 *
	 * @param key The key of the map.
	 * @return Whether or not the map contains the key.
	 */
	public boolean containsKey(K key)
	{
		return read(key) != null;
	}

	/**
	 * Gets an immutable copy of the entire map.
	 *
	 * @return An immutable copy of the entire map.
	 */
	public Map<K, V> readAll()
	{
		try
		{
			lock.readLock().lock();
			return ImmutableMap.copyOf(map);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
}
