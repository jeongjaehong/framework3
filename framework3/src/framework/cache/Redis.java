/** 
 * @(#)Redis.java
 */
package framework.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
		set(_serialize(key), _serialize(value), seconds);
	}

	public void set(byte[] key, byte[] value, int seconds) {
		_client.set(key, value);
		_client.expire(key, seconds);
	}

	@Override
	public Object get(String key) {
		return get(_serialize(key));
	}

	public Object get(byte[] key) {
		return _deserialize(_client.get(key));
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
		return incr(_serialize(key), by);
	}

	public long incr(byte[] key, int by) {
		return _client.incrBy(key, by);
	}

	@Override
	public long decr(String key, int by) {
		return decr(_serialize(key), by);
	}

	public long decr(byte[] key, int by) {
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

	/**
	 * ��ü�� ����Ʈ�迭�� ����ȭ �Ѵ�.
	 * @param obj ����ȭ�� ��ü
	 * @return ����Ʈ�迭
	 */
	public byte[] _serialize(Object obj) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * ����Ʈ�迭�� ��ü�� ������ȭ �Ѵ�.
	 * @param bytes ����Ʈ�迭
	 * @return ������ȭ�� ��ü
	 */
	public Object _deserialize(byte[] bytes) {
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
		}
		return null;
	}
}
