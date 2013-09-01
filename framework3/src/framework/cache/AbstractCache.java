/** 
 * @(#)AbstractCache.java
 */
package framework.cache;

import java.util.Map;

/**
 * ĳ�ñ���ü�� ��ӹ޾ƾ� �ϴ� �߻�Ŭ����
 */
public abstract class AbstractCache {

	/**
	 * Ű�� ���� ĳ�ÿ� �����Ѵ�.
	 * @param key Ű
	 * @param value ��
	 * @param seconds ĳ�ýð�(�ʴ���)
	 */
	public abstract void set(String key, Object value, int seconds);

	/**
	 * ĳ�ÿ��� Ű�� ���� ���´�.
	 * @param key Ű
	 * @return ��
	 */
	public abstract Object get(String key);

	/**
	 * ĳ�ÿ��� Ű�� �迭�� ������ ���´�.
	 * @param keys Ű
	 * @return ��
	 */
	public abstract Map<String, Object> get(String[] keys);

	/**
	 * Ű�� ���� by ��ŭ ������Ų��.
	 * @param key Ű
	 * @param by ������ų ��
	 * @return ������ �� ��
	 */
	public abstract long incr(String key, int by);

	/**
	 * Ű�� ���� by ��ŭ ���ҽ�Ų��.
	 * @param key Ű
	 * @param by ���ҽ�ų ��
	 * @return ���ҵ� �� ��
	 */
	public abstract long decr(String key, int by);

	/**
	 * Ű�� ���� ĳ�ÿ��� �����Ѵ�.
	 * @param key Ű
	 */
	public abstract void delete(String key);

	/**
	 * ĳ�ø� ��� ����.
	 */
	public abstract void clear();
}
