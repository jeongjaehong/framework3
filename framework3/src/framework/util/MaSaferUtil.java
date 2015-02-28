package framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import MarkAny.MaSaferJava.Madec;
import MarkAny.MaSaferJava.Madn;

/**
 * Markany DocSafer(DRM)를 이용하여 개발할 때 이용할 수 있는 유틸리티 클래스이다.
 */
public class MaSaferUtil {
	protected static final Log logger = LogFactory.getLog(framework.util.MaSaferUtil.class);

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private MaSaferUtil() {
	}

	/**
	 * 원본파일을 DRM 암호화를 적용하여 대상파일로 저장한다.
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
			Madn madn = new Madn(configpath);
			fis = new FileInputStream(srcFile);
			long outfileSize = madn.lGetEncryptFileSize(0, 1, userid, srcFile.getName(), srcFile.length(), userid, companyId, orgCode, "", "", srcFile.getName(), 1, 1, 0, -99, -99, -99, 1, 1, 1, 1, 1, 0, "", companyName, orgName, userid, userName, ipAddr, systemName, 1, 0, fis);
			if (outfileSize > 0) {
				fos = new FileOutputStream(destFile);
				madn.strMadn(fos);
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
	}

	/**
	 * 원본파일을 DRM 암호화를 적용하여 응답객체로 전송한다.
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
			response.reset();
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
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
	}

	/**
	 * 원본파일을 DRM 복호화를 적용하여 대상파일로 저장한다.
	 * @param configpath
	 * @param srcFile
	 * @param destFile
	 * @param userId
	 */
	public static void decrypt(String configpath, File srcFile, File destFile, String userId) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			Madec madec = new Madec(configpath);
			fis = new FileInputStream(srcFile);
			long outfileSize = madec.lGetDecryptFileSize(srcFile.getCanonicalPath(), srcFile.length(), userId, fis);
			if (outfileSize > 0) {
				fos = new FileOutputStream(destFile);
				madec.strMadec(fos);
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
	}

	/**
	 * 원본파일을 DRM 복호화를 적용하여 응답객체로 전송한다.
	 * @param configpath
	 * @param srcFile
	 * @param response
	 * @param userId
	 */
	public static void decrypt(String configpath, File srcFile, HttpServletResponse response, String userId) {
		FileInputStream fis = null;
		try {
			response.reset();
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
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
	}
}