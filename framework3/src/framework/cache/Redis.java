/** 
 * @(#)Redis.java
 */
package framework.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
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
	private ShardedJedis _client;

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private Redis() {
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		if (_getConfig().containsKey("redis.host")) {
			shards.add(new JedisShardInfo(_getConfig().getString("redis.host")));
		} else if (_getConfig().containsKey("redis.1.host")) {
			int count = 1;
			while (_getConfig().containsKey("redis." + count + ".host")) {
				shards.add(new JedisShardInfo(_getConfig().getString("redis." + count + ".host")));
				count++;
			}
		} else {
			throw new RuntimeException("redis�� ȣ��Ʈ������ �����Ǿ����ϴ�.");
		}
		_client = new ShardedJedis(shards);
	}

	/** 
	 * ��ü�� �ν��Ͻ��� �������ش�.
	 * 
	 * @return Redis ��ü�� �ν��Ͻ�
	 */
	public synchronized static Redis getInstance() {
		if (_uniqueInstance == null) {
			_uniqueInstance = new Redis();
		}
		return _uniqueInstance;
	}

	@Override
	public void set(String key, Object value, int seconds) {
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
		for (Jedis jedis : _client.getAllShards()) {
			jedis.flushAll();
		}
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
