package framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.servlet.http.HttpServletResponse;

import MarkAny.MaSaferJava.Madec;
import MarkAny.MaSaferJava.Madn;

/**
 * Markany DocSafer(DRM)�� �̿��Ͽ� ������ �� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class MaSaferUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private MaSaferUtil() {
	}

	/**
	 * ���������� DRM ��ȣȭ�� �����Ͽ� ������Ϸ� �����Ѵ�.
	 * @param configpath
	 * @param srcFile
	 * @param destFile
	 * @param systemName
	 * @param companyId
	 * @param companyName
	 * @param orgCode
	 * @param orgName
	 * @param userid
	 * @param userName
	 * @param ipAddr
	 */
	public static void encrypt(String configpath, File srcFile, File destFile, String systemName, String companyId, String companyName, String orgCode, String orgName, String userid, String userName, String ipAddr) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			try {
				Madn madn = new Madn(configpath);
				fis = new FileInputStream(srcFile);
				long outfileSize = madn.lGetEncryptFileSize(0, 1, userid, srcFile.getName(), srcFile.length(), userid, companyId, orgCode, "", "", srcFile.getName(), 1, 1, 0, -99, -99, -99, 1, 1, 1, 1, 1, 0, "", companyName, orgName, userid, userName, ipAddr, systemName, 1, 0, fis);
				if (outfileSize > 0) {
					fos = new FileOutputStream(destFile);
					madn.strMadn(fos);
				}
			} catch (Exception e) {
				throw e;
			} finally {
				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ���������� DRM ��ȣȭ�� �����Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param configpath
	 * @param srcFile
	 * @param response
	 * @param systemName
	 * @param companyId
	 * @param companyName
	 * @param orgCode
	 * @param orgName
	 * @param userid
	 * @param userName
	 * @param ipAddr
	 */
	public static void encrypt(String configpath, File srcFile, HttpServletResponse response, String systemName, String companyId, String companyName, String orgCode, String orgName, String userid, String userName, String ipAddr) {
		FileInputStream fis = null;
		try {
			try {
				response.setContentType("application/octet-stream;");
				response.setHeader("Content-Disposition", (new StringBuilder("attachment; filename=\"")).append(new String(srcFile.getName().getBytes(), "ISO-8859-1")).append("\"").toString());
				response.setHeader("Pragma", "no-cache;");
				response.setHeader("Expires", "-1;");
				Madn madn = new Madn(configpath);
				fis = new FileInputStream(srcFile);
				long outfileSize = madn.lGetEncryptFileSize(0, 1, userid, srcFile.getName(), srcFile.length(), userid, companyId, orgCode, "", "", srcFile.getName(), 1, 1, 0, -99, -99, -99, 1, 1, 1, 1, 1, 0, "", companyName, orgName, userid, userName, ipAddr, systemName, 1, 0, fis);
				if (outfileSize > 0) {
					madn.strMadn(response.getOutputStream());
				}
			} catch (Exception e) {
				throw e;
			} finally {
				if (fis != null)
					fis.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ���������� DRM ��ȣȭ�� �����Ͽ� ������Ϸ� �����Ѵ�.
	 * @param configpath
	 * @param srcFile
	 * @param destFile
	 * @param userId
	 */
	public static void decrypt(String configpath, File srcFile, File destFile, String userId) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			try {
				Madec madec = new Madec(configpath);
				fis = new FileInputStream(srcFile);
				long outfileSize = madec.lGetDecryptFileSize(srcFile.getCanonicalPath(), srcFile.length(), userId, fis);
				if (outfileSize > 0) {
					fos = new FileOutputStream(destFile);
					madec.strMadec(fos);
				}
			} catch (Exception e) {
				throw e;
			} finally {
				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ���������� DRM ��ȣȭ�� �����Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param configpath
	 * @param srcFile
	 * @param response
	 * @param userId
	 */
	public static void decrypt(String configpath, File srcFile, HttpServletResponse response, String userId) {
		FileInputStream fis = null;
		try {
			try {
				response.setContentType("application/octet-stream;");
				response.setHeader("Content-Disposition", (new StringBuilder("attachment; filename=\"")).append(new String(srcFile.getName().getBytes(), "ISO-8859-1")).append("\"").toString());
				response.setHeader("Pragma", "no-cache;");
				response.setHeader("Expires", "-1;");
				Madec madec = new Madec(configpath);
				fis = new FileInputStream(srcFile);
				long outfileSize = madec.lGetDecryptFileSize(srcFile.getCanonicalPath(), srcFile.length(), userId, fis);
				if (outfileSize > 0) {
					madec.strMadec(response.getOutputStream());
				}
			} catch (Exception e) {
				throw e;
			} finally {
				if (fis != null)
					fis.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
