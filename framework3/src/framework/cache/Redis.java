/** 
 * @(#)Redis.java
 */
package framework.cache;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

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
		List<JedisShardInfo> shards;
		if (_getConfig().containsKey("redis.host")) {
			shards = _getAddresses(_getConfig().getString("redis.host"));
		} else if (_getConfig().containsKey("redis.1.host")) {
			int count = 1;
			StringBuilder buffer = new StringBuilder();
			while (_getConfig().containsKey("redis." + count + ".host")) {
				buffer.append(_getConfig().getString("redis." + count + ".host") + " ");
				count++;
			}
			shards = _getAddresses(buffer.toString());
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

	/**
	 * ���ڿ����� redis ȣ��Ʈ �ּҸ� �Ľ��Ͽ� �����Ѵ�.
	 * @param str �����̽��� ���е� �ּҹ��ڿ�
	 * @return �����ּҰ�ü
	 */
	private List<JedisShardInfo> _getAddresses(String str) {
		if (str == null || "".equals(str.trim())) {
			throw new IllegalArgumentException("redis�� ȣ��Ʈ������ �����Ǿ����ϴ�.");
		}
		ArrayList<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		for (String addr : str.split("(?:\\s|,)+")) {
			if ("".equals(addr)) {
				continue;
			}
			int sep = addr.lastIndexOf(':');
			if (sep < 1) {
				throw new IllegalArgumentException("���������� �߸��Ǿ����ϴ�. ����=>ȣ��Ʈ:��Ʈ");
			}
			shards.add(new JedisShardInfo(addr.substring(0, sep), Integer.valueOf(addr.substring(sep + 1))));
		}
		assert !shards.isEmpty() : "redis�� ȣ��Ʈ������ �����Ǿ����ϴ�.";
		return shards;
	}
}
