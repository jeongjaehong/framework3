/** 
 * @(#)Redis.java
 */
package framework.cache;

import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import framework.config.Config;

/**
 * Redis ĳ�� ����ü (http://redis.io/)
 */
public class Redis extends AbstractCache {
	/**
	 * �̱��� ��ü
	 */
	private static Redis _uniqueInstance;

	/**
	 * ĳ�� Ŭ���̾�Ʈ
	 */
	private Jedis _client;

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private Redis() {
		String redisHost = null;
		if (_getConfig().containsKey("redis.host")) {
			redisHost = _getConfig().getString("redis.host");
		} else {
			throw new RuntimeException("redis�� ȣ��Ʈ������ �����Ǿ����ϴ�.");
		}
		_client = new Jedis(redisHost);
	}

	/** 
	 * ��ü�� �ν��Ͻ��� �������ش�.
	 * 
	 * @return Memcached ��ü�� �ν��Ͻ�
	 */
	public synchronized static Redis getInstance() {
		if (_uniqueInstance == null) {
			_uniqueInstance = new Redis();
		}
		return _uniqueInstance;
	}

	@Override
	public void add(String key, Object value, int seconds) {
		_client.append(key, value.toString());
		_client.expire(key, seconds);
	}

	@Override
	public void set(String key, Object value, int seconds) {
		_client.set(key, value.toString());
		_client.expire(key, seconds);

	}

	@Override
	public void replace(String key, Object value, int seconds) {
		if (_client.get(key) == null) {
			return;
		}
		_client.set(key, value.toString());
		_client.expire(key, seconds);
	}

	@Override
	public Object get(String key) {
		return _client.get(key);
	}

	@Override
	public Map<String, Object> get(String[] keys) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		for (String key : keys) {
			resultMap.put(key, get(key));
		}
		return resultMap;
	}

	@Override
	public long incr(String key, int by) {
		return _client.incrBy(key, by);
	}

	@Override
	public long decr(String key, int by) {
		return _client.decrBy(key, by);
	}

	@Override
	public void delete(String key) {
		_client.del(key);
	}

	@Override
	public void clear() {
		_client.flushAll();
	}

	@Override
	public void stop() {
		_client.shutdown();
	}

	////////////////////////////////////////////////////////////////////////////////////////Private �޼ҵ�

	/**
	* ��������(config.properties)���� ���� �о���� Ŭ������ �����Ѵ�.
	* @return ������ü
	*/
	private Config _getConfig() {
		return Config.getInstance();
	}
}
