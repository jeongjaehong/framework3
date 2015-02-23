package framework.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ����ó��, ���ε�, �ٿ�ε�� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class FileUtil {
	protected static final Log logger = LogFactory.getLog(framework.util.FileUtil.class);

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private FileUtil() {
	}

	/**
	 * ���ڷ� ���޵� ��ο� �ش��ϴ� ���丮�� ũ�⸦ byte ������ ��ȯ�ϴ� �޼ҵ�
	 * @param directoryPath ���丮 ���
	 * @return ���丮�� byte ������ ũ��
	 */
	public static long getDirSizeToByteUnit(String directoryPath) {
		return getDirSizeToByteUnit(new File(directoryPath));
	}

	/**
	 * ���ڷ� ���޵� ���丮�� ũ�⸦ byte ������ ��ȯ�ϴ� �޼ҵ�
	 * @param directory ���丮 ���ϰ�ü
	 * @return ���丮�� byte ������ ũ��
	 */
	public static long getDirSizeToByteUnit(File directory) {
		long totalSum = 0;
		if (directory != null && directory.isDirectory()) {
			File[] fileItems = directory.listFiles();
			for (File item : fileItems) {
				if (item.isFile()) {
					totalSum += item.length();
				} else {
					totalSum += FileUtil.getDirSizeToByteUnit(item);
				}
			}
		}
		return totalSum;
	}

	/**
	 * ���ڷ� ���޵� ������ Ȯ���ڸ� ��ȯ�ϴ� �޼ҵ�
	 * @param file Ȯ���ڸ� �˰��� ���ϴ� ���ϸ�
	 * @return Ȯ���ڸ�
	 */
	public static String getFileExtension(File file) {
		return FileUtil.getFileExtension(file.toString());
	}

	/**
	 * ���ڷ� ���޵� ���ϸ��� Ȯ���ڸ� ��ȯ�ϴ� �޼ҵ�
	 * @param filePath Ȯ���ڸ� �˰��� ���ϴ� ���ϸ�
	 * @return Ȯ���ڸ�
	 */
	public static String getFileExtension(String filePath) {
		return filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
	}

	/**
	 * ���ڷ� ���޵� ���ϰ�ο��� ���ϸ� ����(��δ� ����)�ϴ� �޼ҵ�
	 * @param filePath
	 * @return ��ΰ� ���ŵ� ���ϸ�
	 */
	public static String getFileName(String filePath) {
		return filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length()).substring(filePath.lastIndexOf("\\") + 1, filePath.length());
	}

	/**
	 * ���ڷ� ���޵� ���ϰ�ü���� ���ϸ� ����(��δ� ����)�ϴ� �޼ҵ�
	 * @param file
	 * @return ��ΰ� ���ŵ� ���ϸ�
	 */
	public static String getFileName(File file) {
		return getFileName(file.getPath());
	}

	/**
	 * ������ �����ϴ� �޼ҵ�
	 * @param src ���� ���� ��ü
	 * @param dest ��� ���� ��ü
	 */
	public static void copyFile(java.io.File src, java.io.File dest) {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dest);
			copy(in, out);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error(e);
					}
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error(e);
					}
				}
			}
		}
	}

	/**
	 * �Է� stream �����͸� ��� stream ���� �����ϴ� �޼ҵ�
	 * @param in �Է½�Ʈ��
	 * @param out ��½�Ʈ��
	 */
	public static void copy(InputStream in, OutputStream out) {
		int size = 1024;
		byte[] buffer = new byte[size];
		int read;
		try {
			while ((read = in.read(buffer)) > 0) {
				out.write(buffer, 0, read);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ���� ���� �޼ҵ�
	 * @param fileName ���� ���
	 * @return ��������
	 */
	public static boolean deleteFile(String fileName) {
		return deleteFile(new File(fileName));
	}

	/**
	 * ���� ���� �޼ҵ�
	 * @param file ���� ��ü
	 * @return ��������
	 */
	public static boolean deleteFile(File file) {
		return file.canWrite() ? file.delete() : false;
	}

	/**
	 * ���丮 ���� �޼ҵ�
	 * @param directoryPath ���丮 ���
	 * @return ��������
	 */
	public static boolean deleteDirectory(String directoryPath) {
		return deleteDirectory(new File(directoryPath));
	}

	/**
	 * ���丮 ���� �޼ҵ�
	 * @param directory ���丮 ��ü
	 * @return ��������
	 */
	public static boolean deleteDirectory(File directory) {
		if (directory != null && directory.isDirectory() && directory.exists()) {
			for (File item : directory.listFiles()) {
				if (!item.delete())
					return false;
			}
			return directory.delete();
		} else {
			return false;
		}
	}

	/**
	 * �̹��� �����͸� stream ���� �����ϴ� �޼ҵ�
	 * @param response
	 * @param file
	 */
	public static void displayImage(HttpServletResponse response, File file) {
		if (file != null && file.isFile() && file.length() != 0) {
			long fileLen = file.length();
			response.setContentLength((int) fileLen);
			response.setContentType("image/pjpeg");
			response.setHeader("Content-Disposition", "inline; filename=\"\"");
			response.setHeader("Pragma", "no-cache;");
			response.setHeader("Expires", "-1;");
			_download(response, file);
		}
	}

	/**
	 * ���� �����͸� stream ���� �����ϴ� �޼ҵ�
	 * @param response
	 * @param file
	 */
	public static void displayVideo(HttpServletResponse response, File file) {
		if (file != null && file.isFile() && file.length() != 0) {
			long fileLen = file.length();
			response.setContentLength((int) fileLen);
			response.setContentType("video/x-ms-wmv");
			response.setHeader("Content-Disposition", "inline; filename=\"\"");
			response.setHeader("Pragma", "no-cache;");
			response.setHeader("Expires", "-1;");
			_download(response, file);
		}
	}

	/**
	 * ������ stream ���� �����ϴ� �޼ҵ�
	 * @param response
	 * @param displayName
	 * @param file
	 */
	public static void download(HttpServletResponse response, String displayName, File file) {
		if (file != null && file.isFile() && file.length() != 0) {
			long fileLen = file.length();
			response.setContentLength((int) fileLen);
			response.setContentType("application/octet-stream;");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + displayName + "\"");
			response.setHeader("Pragma", "no-cache;");
			response.setHeader("Expires", "-1;");
			_download(response, file);
		}
	}

	private static void _download(HttpServletResponse response, File file) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			int readBytes = 0;
			int available = 1024;
			byte b[] = new byte[available];
			bis = new BufferedInputStream(new FileInputStream(file));
			bos = new BufferedOutputStream(response.getOutputStream());
			while ((readBytes = bis.read(b, 0, available)) != -1) {
				bos.write(b, 0, readBytes);
			}
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error(e);
			}
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error(e);
					}
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error(e);
					}
				}
			}
		}
	}
}