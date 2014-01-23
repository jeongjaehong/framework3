/** 
 * @(#)Cache.java
 */
package framework.cache;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Cache {

	/**
	 * �ΰŰ�ü ����
	 */
	private static Log _logger = LogFactory.getLog(framework.cache.Cache.class);

	/**
	 * ĳ�ñ���ü
	 */
	public static AbstractCache cache = null;

	/**
	 * ĳ�ñ���ü �̸�
	 */
	public static String cacheName = null;

	/**
	 * �⺻ ĳ�� �ð� (30��)
	 */
	private final static int DEFAULT_DURATION = 60 * 60 * 24 * 30;

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ���� 
	 */
	private Cache() {
	}

	/**
	 * ĳ�� �ʱ�ȭ, ���������� �о� ĳ�� ����ü�� �����Ѵ�.
	 */
	public synchronized static void init() {
		if (cache == null) {
			try {
				cache = Memcached.getInstance();
				cacheName = "Memcached";
			} catch (Exception e) {
				try {
					cache = Redis.getInstance();
					cacheName = "Redis";
				} catch (Exception e2) {
					cache = EhCache.getInstance();
					cacheName = "EhCache";
				}
			}
			_getLogger().info(String.format("[ %s ] init : �ʱ�ȭ ����", cacheName));
		}
	}

	/**
	 * Ű�� ���� ĳ�ÿ� �����Ѵ�.
	 * @param key Ű
	 * @param value ��
	 */
	public static void set(String key, Object value) {
		_isSerializable(value);
		cache.set(key, value, DEFAULT_DURATION);
		_getLogger().debug(String.format("[ %s ] set : { key=%s, value=%s, seconds=%d }", cacheName, key, value, DEFAULT_DURATION));
	}

	/**
	 * Ű�� ���� ĳ�ÿ� �����Ѵ�.
	 * @param key Ű
	 * @param value ��
	 * @param seconds ĳ�ýð�(�ʴ���)
	 */
	public static void set(String key, Object value, int seconds) {
		_isSerializable(value);
		cache.set(key, value, seconds);
		_getLogger().debug(String.format("[ %s ] set : { key=%s, value=%s, seconds=%d }", cacheName, key, value, seconds));
	}

	/**
	 * Ű�� ���� 1��ŭ ������Ų��.
	 * @param key Ű
	 * @return ������ �� ��
	 */
	public static long incr(String key) {
		long result = cache.incr(key, 1);
		_getLogger().debug(String.format("[ %s ] incr : { key=%s, by=%d }", cacheName, key, 1));
		return result;
	}

	/**
	 * Ű�� ���� by ��ŭ ������Ų��.
	 * @param key Ű
	 * @param by ������ų ��
	 * @return ������ �� ��
	 */
	public static long incr(String key, int by) {
		long result = cache.incr(key, by);
		_getLogger().debug(String.format("[ %s ] incr : { key=%s, by=%d }", cacheName, key, by));
		return result;
	}

	/**
	 * Ű�� ���� 1��ŭ ���ҽ�Ų��.
	 * @param key Ű
	 * @return ���ҵ� �� ��
	 */
	public static long decr(String key) {
		long result = cache.decr(key, 1);
		_getLogger().debug(String.format("[ %s ] decr : { key=%s, by=%d }", cacheName, key, 1));
		return result;
	}

	/**Ű�� ���� by ��ŭ ���ҽ�Ų��.
	 * @param key Ű
	 * @param by ���ҽ�ų ��
	 * @return ���ҵ� �� ��
	 */
	public static long decr(String key, int by) {
		long result = cache.decr(key, by);
		_getLogger().debug(String.format("[ %s ] decr : { key=%s, by=%d }", cacheName, key, by));
		return result;
	}

	/**
	 * ĳ�ÿ��� Ű�� ���� ���´�.
	 * @param key Ű
	 * @return ��
	 */
	public static Object get(String key) {
		Object value = cache.get(key);
		_getLogger().debug(String.format("[ %s ] get : { key=%s, value=%s }", cacheName, key, value));
		return value;
	}

	/**
	 * ĳ�ÿ��� Ű�� �迭�� ������ ���´�.
	 * @param keys Ű
	 * @return ��
	 */
	public static Map<String, Object> get(String... keys) {
		Map<String, Object> valueMap = cache.get(keys);
		_getLogger().debug(String.format("[ %s ] get : { key=%s, value=%s }", cacheName, Arrays.asList(keys), valueMap));
		return valueMap;
	}

	/**
	 * Ű�� ���� ĳ�ÿ��� �����Ѵ�.
	 * @param key Ű
	 */
	public static void delete(String key) {
		cache.delete(key);
		_getLogger().debug(String.format("[ %s ] delete : { key=%s }", cacheName, key));
	}

	/**
	 * ĳ�ø� ��� ����.
	 */
	public static void clear() {
		cache.clear();
		_getLogger().debug(String.format("[ %s ] clear : ĳ�� Ŭ���� ����", cacheName));
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private �޼ҵ�

	/**
	 * ����ȭ ���� ��ü���� �Ǻ��Ѵ�.
	 */
	private static void _isSerializable(Object value) {
		if (value != null && !(value instanceof Serializable)) {
			throw new RuntimeException(new NotSerializableException(value.getClass().getName()));
		}
	}

	private static Log _getLogger() {
		return Cache._logger;
	}
}
