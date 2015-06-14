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
 * Memcached 캐시 구현체 (http://memcached.org/)
 */
public class Memcached extends AbstractCache {

	/**
	 * 싱글톤 객체
	 */
	private static Memcached uniqueInstance;

	/**
	 * 캐시 클라이언트
	 */
	private final MemcachedClient client;

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private Memcached() {
		System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.Log4JLogger");
		List<InetSocketAddress> addrList;
		if (getConfig().containsKey("memcached.host")) {
			addrList = AddrUtil.getAddresses(getConfig().getString("memcached.host"));
		} else if (getConfig().containsKey("memcached.1.host")) {
			int count = 1;
			StringBuilder buffer = new StringBuilder();
			while (getConfig().containsKey("memcached." + count + ".host")) {
				buffer.append(getConfig().getString("memcached." + count + ".host") + " ");
				count++;
			}
			addrList = AddrUtil.getAddresses(buffer.toString());
		} else {
			throw new RuntimeException("memcached의 호스트설정이 누락되었습니다.");
		}
		try {
			client = new MemcachedClient(addrList);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/** 
	 * 객체의 인스턴스를 리턴해준다.
	 * 
	 * @return Memcached 객체의 인스턴스
	 */
	public synchronized static Memcached getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new Memcached();
		}
		return uniqueInstance;
	}

	@Override
	public void set(String key, Object value, int seconds) {
		client.set(key, seconds, value);
	}

	@Override
	public Object get(String key) {
		Future<Object> future = client.asyncGet(key);
		try {
			return future.get(1, TimeUnit.SECONDS);
		} catch (Throwable e) {
			future.cancel(false);
		}
		return null;
	}

	@Override
	public Map<String, Object> get(String[] keys) {
		Future<Map<String, Object>> future = client.asyncGetBulk(keys);
		try {
			return future.get(1, TimeUnit.SECONDS);
		} catch (Throwable e) {
			future.cancel(false);
		}
		return Collections.<String, Object> emptyMap();
	}

	@Override
	public long incr(String key, int by) {
		return client.incr(key, by, 0);
	}

	@Override
	public long decr(String key, int by) {
		return client.decr(key, by, 0);
	}

	@Override
	public void delete(String key) {
		client.delete(key);
	}

	@Override
	public void clear() {
		client.flush();
	}

	////////////////////////////////////////////////////////////////////////////////////////Private 메소드

	/**
	 * 설정파일(config.properties)에서 값을 읽어오는 클래스를 리턴한다.
	 * @return 설정객체
	 */
	private Config getConfig() {
		return Config.getInstance();
	}
}