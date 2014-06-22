package framework.config;

import java.util.ResourceBundle;

/** 
 * ��������(config.properties)���� ���� �о���� Ŭ�����̴�. 
 * �̱��� �������� �������Ͽ� �����ϴ� ��ü�� �ν��Ͻ��� ���� �Ѱ��� ������ �ȴ�.
 */
public class Config {
	private static Config _instance = new Config();
	private static final String _NAME = "config";
	private ResourceBundle _bundle = null;

	private Config() {
		_bundle = ResourceBundle.getBundle(_NAME);
	}

	/** 
	 * ��ü�� �ν��Ͻ��� �������ش�.
	 * @return Configuration ��ü�� �ν��Ͻ�
	 */
	public static Config getInstance() {
		return _instance;
	}

	/** 
	 * Ű(key)���ڿ��� ���εǾ� �ִ� String �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� String ��ü
	 */
	public String get(String key) {
		return getString(key);
	}

	/** 
	 * Ű(key)���ڿ��� ���εǾ� �ִ� boolean�� ������ �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� boolean�� ����
	 */
	public boolean getBoolean(String key) {
		boolean value = false;
		value = (Boolean.valueOf(_bundle.getString(key).trim())).booleanValue();
		return value;
	}

	/** 
	 * Ű(key)���ڿ��� ���εǾ� �ִ� int�� ������ �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� int�� ����
	 */
	public int getInt(String key) {
		int value = -1;
		value = Integer.parseInt(_bundle.getString(key).trim());
		return value;
	}

	/** 
	 * Ű(key)���ڿ��� ���εǾ� �ִ� String �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� String ��ü
	 */
	public String getString(String key) {
		String value = null;
		value = _bundle.getString(key).trim();
		return value;
	}

	/**
	 * Ű(key)�� ���ԵǾ��ִ��� ���θ� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�s
	 * @return key�� ���Կ���
	 */
	public boolean containsKey(String key) {
		return _bundle.containsKey(key);
	}
}