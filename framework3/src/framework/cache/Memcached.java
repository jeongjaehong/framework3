package framework.cache;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;
import framework.config.Config;

/**
 * Memcached ĳ�� ����ü (http://memcached.org/)
 */
public class Memcached extends AbstractCache {

	/**
	 * �̱��� ��ü
	 */
	private static Memcached _uniqueInstance;

	/**
	 * ĳ�� Ŭ���̾�Ʈ
	 */
	private MemcachedClient _client;

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private Memcached() {
		System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.Log4JLogger");
		List<InetSocketAddress> addrList;
		if (_getConfig().containsKey("memcached.host")) {
			addrList = AddrUtil.getAddresses(_getConfig().getString("memcached.host"));
		} else if (_getConfig().containsKey("memcached.1.host")) {
			int count = 1;
			StringBuilder buffer = new StringBuilder();
			while (_getConfig().containsKey("memcached." + count + ".host")) {
				buffer.append(_getConfig().getString("memcached." + count + ".host") + " ");
				count++;
			}
			addrList = AddrUtil.getAddresses(buffer.toString());
		} else {
			throw new RuntimeException("memcached�� ȣ��Ʈ������ �����Ǿ����ϴ�.");
		}
		try {
			_client = new MemcachedClient(addrList);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/** 
	 * ��ü�� �ν��Ͻ��� �������ش�.
	 * 
	 * @return Memcached ��ü�� �ν��Ͻ�
	 */
	public synchronized static Memcached getInstance() {
		if (_uniqueInstance == null) {
			_uniqueInstance = new Memcached();
		}
		return _uniqueInstance;
	}

	@Override
	public void set(String key, Object value, int seconds) {
		_client.set(key, seconds, value);
	}

	@Override
	public Object get(String key) {
		Future<Object> future = _client.asyncGet(key);
		try {
			return future.get(1, TimeUnit.SECONDS);
		} catch (Exception e) {
			future.cancel(false);
		}
		return null;
	}

	@Override
	public Map<String, Object> get(String[] keys) {
		Future<Map<String, Object>> future = _client.asyncGetBulk(keys);
		try {
			return future.get(1, TimeUnit.SECONDS);
		} catch (Exception e) {
			future.cancel(false);
		}
		return Collections.<String, Object> emptyMap();
	}

	@Override
	public long incr(String key, int by) {
		return _client.incr(key, by, 0);
	}

	@Override
	public long decr(String key, int by) {
		return _client.decr(key, by, 0);
	}

	@Override
	public void delete(String key) {
		_client.delete(key);
	}

	@Override
	public void clear() {
		_client.flush();
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
