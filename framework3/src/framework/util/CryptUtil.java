package framework.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 * ��/��ȣȭ ���� ����� �ϴ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class CryptUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private CryptUtil() {
	}

	/**
	 * �޽����� MD5 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @return �ؽ���� Hex ���ڿ�
	 */
	public static String hashMD5HexString(String message) {
		return _hashHexString(message, "MD5");
	}

	/**
	 * �޽����� MD5 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @return �ؽ���� Base64 ���ڿ�
	 */
	public static String hashMD5Base64String(String message) {
		return _hashBase64String(message, "MD5");
	}

	/**
	 * salt�� �����Ͽ� �޽����� MD5 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @param salt ��Ʈ��
	 * @return �ؽ���� Hex ���ڿ�
	 */
	public static String hashMD5HexString(String message, String salt) {
		return _hashHexString(message, salt, "MD5");
	}

	/**
	 * salt�� �����Ͽ� �޽����� MD5 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @param salt ��Ʈ��
	 * @return �ؽ���� Base64 ���ڿ�
	 */
	public static String hashMD5Base64String(String message, String salt) {
		return _hashBase64String(message, salt, "MD5");
	}

	/**
	 * �޽����� SHA-1 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @return �ؽ���� Hex ���ڿ�
	 */
	public static String hashSHA1HexString(String message) {
		return _hashHexString(message, "SHA-1");
	}

	/**
	 * �޽����� SHA-1 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @return �ؽ���� Base64 ���ڿ�
	 */
	public static String hashSHA1Base64String(String message) {
		return _hashBase64String(message, "SHA-1");
	}

	/**
	 * salt�� �����Ͽ� �޽����� SHA-1 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @param salt ��Ʈ��
	 * @return �ؽ���� Hex ���ڿ�
	 */
	public static String hashSHA1HexString(String message, String salt) {
		return _hashHexString(message, salt, "SHA-1");
	}

	/**
	 * salt�� �����Ͽ� �޽����� SHA-1 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @param salt ��Ʈ��
	 * @return �ؽ���� Base64 ���ڿ�
	 */
	public static String hashSHA1Base64String(String message, String salt) {
		return _hashBase64String(message, salt, "SHA-1");
	}

	/**
	 * �޽����� SHA-256 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @return �ؽ���� Hex ���ڿ�
	 */
	public static String hashSHA256HexString(String message) {
		return _hashHexString(message, "SHA-256");
	}

	/**
	 * �޽����� SHA-256 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @return �ؽ����  Base64 ���ڿ�
	 */
	public static String hashSHA256Base64String(String message) {
		return _hashBase64String(message, "SHA-256");
	}

	/**
	 * salt�� �����Ͽ� �޽����� SHA-256 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @param salt ��Ʈ��
	 * @return �ؽ���� Hex ���ڿ�
	 */
	public static String hashSHA256HexString(String message, String salt) {
		return _hashHexString(message, salt, "SHA-256");
	}

	/**
	 * salt�� �����Ͽ� �޽����� SHA-256 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @param salt ��Ʈ��
	 * @return �ؽ����  Base64 ���ڿ�
	 */
	public static String hashSHA256Base64String(String message, String salt) {
		return _hashBase64String(message, salt, "SHA-256");
	}

	/**
	 * �޽����� SHA-512 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @return �ؽ���� Hex ���ڿ�
	 */
	public static String hashSHA512HexString(String message) {
		return _hashHexString(message, "SHA-512");
	}

	/**
	 * �޽����� SHA-512 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @return �ؽ���� Base64 ���ڿ�
	 */
	public static String hashSHA512Base64String(String message) {
		return _hashBase64String(message, "SHA-512");
	}

	/**
	 * salt�� �����Ͽ� �޽����� SHA-512 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @param salt ��Ʈ��
	 * @return �ؽ���� Hex ���ڿ�
	 */
	public static String hashSHA512HexString(String message, String salt) {
		return _hashHexString(message, salt, "SHA-512");
	}

	/**
	 * salt�� �����Ͽ� �޽����� SHA-512 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @param salt ��Ʈ��
	 * @return �ؽ���� Base64 ���ڿ�
	 */
	public static String hashSHA512Base64String(String message, String salt) {
		return _hashBase64String(message, salt, "SHA-512");
	}

	/**
	 * �޽����� secretKey�� �̿��Ͽ� HmacMD5 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @param secretKey Ű
	 * @return �ؽ���� Hex ���ڿ�
	 */
	public static String hashHmacMD5HexString(String message, String secretKey) {
		return _hashHmacHexString(message, secretKey, "HmacMD5");
	}

	/**
	 * �޽����� secretKey�� �̿��Ͽ� HmacMD5 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @param secretKey Ű
	 * @return �ؽ���� Base64 ���ڿ�
	 */
	public static String hashHmacMD5Base64String(String message, String secretKey) {
		return _hashHmacBase64String(message, secretKey, "HmacMD5");
	}

	/**
	 * �޽����� secretKey�� �̿��Ͽ� HmacSHA1 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @param secretKey Ű
	 * @return �ؽ���� Hex ���ڿ�
	 */
	public static String hashHmacSHA1HexString(String message, String secretKey) {
		return _hashHmacHexString(message, secretKey, "HmacSHA1");
	}

	/**
	 * �޽����� secretKey�� �̿��Ͽ� HmacSHA1 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @param secretKey Ű
	 * @return �ؽ���� Base64 ���ڿ�
	 */
	public static String hashHmacSHA1Base64String(String message, String secretKey) {
		return _hashHmacBase64String(message, secretKey, "HmacSHA1");
	}

	/**
	 * �޽����� secretKey�� �̿��Ͽ� HmacSHA256 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @param secretKey Ű
	 * @return �ؽ���� Hex ���ڿ�
	 */
	public static String hashHmacSHA256HexString(String message, String secretKey) {
		return _hashHmacHexString(message, secretKey, "HmacSHA256");
	}

	/**
	 * �޽����� secretKey�� �̿��Ͽ� HmacSHA256 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @param secretKey Ű
	 * @return �ؽ���� Base64 ���ڿ�
	 */
	public static String hashHmacSHA256Base64String(String message, String secretKey) {
		return _hashHmacBase64String(message, secretKey, "HmacSHA256");
	}

	/**
	 * �޽����� secretKey�� �̿��Ͽ� HmacSHA512 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @param secretKey Ű
	 * @return �ؽ���� Hex ���ڿ�
	 */
	public static String hashHmacSHA512HexString(String message, String secretKey) {
		return _hashHmacHexString(message, secretKey, "HmacSHA512");
	}

	/**
	 * �޽����� secretKey�� �̿��Ͽ� HmacSHA512 �˰������� �ؽ��Ѵ�.
	 * @param message �����޽���
	 * @param secretKey Ű
	 * @return �ؽ���� Base64 ���ڿ�
	 */
	public static String hashHmacSHA512Base64String(String message, String secretKey) {
		return _hashHmacBase64String(message, secretKey, "HmacSHA512");
	}

	/**
	 * �޽����� BASE64 �˰������� ���ڵ��Ѵ�.
	 * @param message �����޽���
	 * @return ���ڵ��� ���ڿ�
	 */
	public static String encodeBase64String(String message) {
		return Base64.encodeBase64String(message.getBytes());
	}

	/**
	 * �޽����� BASE64 �˰������� ���ڵ��Ѵ�.
	 * @param message ���� �޽���
	 * @return ���ڵ��� ���ڿ�
	 */
	public static String decodeBase64String(String message) {
		return new String(Base64.decodeBase64(message.getBytes()));
	}

	/**
	 * �޽����� ����Ű�� �̿��Ͽ� AES �˰������� ��ȣȭ�Ѵ�.
	 * @param message �����޽���
	 * @param privateKey ����Ű 
	 * @return ��ȣȭ�� ���ڿ�
	 */
	public static String encryptAES(String message, String privateKey) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(privateKey.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			return new String(Hex.encodeHex(cipher.doFinal(message.getBytes())));
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * �޽����� ����Ű�� �̿��Ͽ� AES �˰������� ��ȣȭ�Ѵ�.
	 * @param message �����޽���
	 * @param privateKey ����Ű 
	 * @return ��ȣȭ�� ���ڿ�
	 */
	public static String decryptAES(String message, String privateKey) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(privateKey.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			return new String(cipher.doFinal(Hex.decodeHex(message.toCharArray())));
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * �޽����� ����Ű�� �̿��Ͽ� DES �˰������� ��ȣȭ�Ѵ�.
	 * @param message �����޽���
	 * @param privateKey ����Ű 
	 * @return ��ȣȭ�� ���ڿ�
	 */
	public static String encryptDES(String message, String privateKey) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(privateKey.getBytes(), "DES");
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			return Hex.encodeHexString(cipher.doFinal(message.getBytes()));
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * �޽����� ����Ű�� �̿��Ͽ� DES �˰������� ��ȣȭ�Ѵ�.
	 * @param message �����޽���
	 * @param privateKey ����Ű 
	 * @return ��ȣȭ�� ���ڿ�
	 */
	public static String decryptDES(String message, String privateKey) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(privateKey.getBytes(), "DES");
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			return new String(cipher.doFinal(Hex.decodeHex(message.toCharArray())));
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * �ؽþ˰��򿡼� ����ϱ� ���� ��Ʈ�� �����Ѵ�.
	 * @return �������� ������ 20�ڸ� ��Ʈ ���ڿ�
	 */
	public static String randomSalt() {
		SecureRandom r = new SecureRandom();
		byte[] salt = new byte[10];
		r.nextBytes(salt);
		return Hex.encodeHexString(salt);
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private �޼ҵ�

	/*
	 * �ؽð�� ����Ʈ �迭
	 */
	private static byte[] _hash(String message, String algorithm) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.reset();
			md.update("".getBytes());
			return md.digest(message.getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * �ؽð�� Hex ���ڿ�
	 */
	private static String _hashHexString(String message, String algorithm) {
		return Hex.encodeHexString(_hash(message, algorithm));
	}

	/*
	 * �ؽð�� Base64 ���ڿ�
	 */
	private static String _hashBase64String(String message, String algorithm) {
		return Base64.encodeBase64String((_hash(message, algorithm)));
	}

	/*
	 * Hmac �ؽð�� ����Ʈ �迭
	 */
	private static byte[] _hashHmac(String message, String secretKey, String algorithm) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getBytes(), algorithm);
			Mac mac = Mac.getInstance(algorithm);
			mac.init(skeySpec);
			return mac.doFinal((message.getBytes()));
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * Hmac �ؽð�� Hex ���ڿ�
	 */
	private static String _hashHmacHexString(String message, String secretKey, String algorithm) {
		return Hex.encodeHexString(_hashHmac(message, secretKey, algorithm));
	}

	/*
	 * Hmac �ؽð�� Base64 ���ڿ�
	 */
	private static String _hashHmacBase64String(String message, String secretKey, String algorithm) {
		return Base64.encodeBase64String(_hashHmac(message, secretKey, algorithm));
	}

	/*
	 * salt ���� �ؽð�� ����Ʈ �迭
	 */
	private static byte[] _hash(String message, String salt, String algorithm) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.reset();
			md.update(salt.getBytes());
			return md.digest(message.getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * salt ���� �ؽð�� Hex ���ڿ�
	 */
	private static String _hashHexString(String message, String salt, String algorithm) {
		return Hex.encodeHexString(_hash(message, salt, algorithm));
	}

	/*
	 * salt ���� �ؽð�� Base64 ���ڿ�
	 */
	private static String _hashBase64String(String message, String salt, String algorithm) {
		return Base64.encodeBase64String(_hash(message, salt, algorithm));
	}
}