package framework.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import framework.db.RecordSet;

/**
 * Excel ����� ���� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class ExcelUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private ExcelUtil() {
	}

	/**
	 * Ȯ���ڿ� ���ؼ� ���������� �Ľ��Ѵ�.
	 * @param fileItem ���Ͼ�����
	 * @return �������� ����Ʈ
	 */
	public static List<Map<String, String>> parse(FileItem fileItem) {
		String ext = FileUtil.getFileExtension(fileItem.getName());
		InputStream is;
		try {
			is = fileItem.getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if ("csv".equalsIgnoreCase(ext)) {
			return _parseCSV(is);
		} else if ("tsv".equalsIgnoreCase(ext)) {
			return _parseTSV(is);
		} else if ("xls".equalsIgnoreCase(ext)) {
			return _parseExcel2003(is);
		} else if ("xlsx".equalsIgnoreCase(ext)) {
			return _parseExcel2007(is);
		} else {
			throw new RuntimeException("�������� �ʴ� ���������Դϴ�.");
		}
	}

	/**
	 * ��ȣȭ�� ���������� �Ľ��Ѵ�.
	 * @param fileItem ���Ͼ�����
	 * @param password ��й�ȣ
	 * @return �������� ����Ʈ
	 */
	public static List<Map<String, String>> parse(FileItem fileItem, String password) {
		String ext = FileUtil.getFileExtension(fileItem.getName());
		InputStream is;
		try {
			is = fileItem.getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if ("xls".equalsIgnoreCase(ext)) {
			return _parseExcel2003(is, password);
		} else if ("xlsx".equalsIgnoreCase(ext)) {
			return _parseExcel2007(is, password);
		} else {
			throw new RuntimeException("�������� �ʴ� ���������Դϴ�.");
		}
	}

	/**
	 * Ȯ���ڿ� ���ؼ� ���������� �Ľ��Ѵ�.
	 * @param file ����
	 * @return �������� ����Ʈ
	 */
	public static List<Map<String, String>> parse(File file) {
		FileInputStream fis = null;
		try {
			try {
				String ext = FileUtil.getFileExtension(file);
				fis = new FileInputStream(file);
				if ("csv".equalsIgnoreCase(ext)) {
					return _parseCSV(fis);
				} else if ("tsv".equalsIgnoreCase(ext)) {
					return _parseTSV(fis);
				} else if ("xls".equalsIgnoreCase(ext)) {
					return _parseExcel2003(fis);
				} else if ("xlsx".equalsIgnoreCase(ext)) {
					return _parseExcel2007(fis);
				} else {
					throw new RuntimeException("�������� �ʴ� ���������Դϴ�.");
				}
			} finally {
				if (fis != null) {
					fis.close();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ��ȣȭ�� ���������� �Ľ��Ѵ�.
	 * @param file ����
	 * @return �������� ����Ʈ
	 */
	public static List<Map<String, String>> parse(File file, String password) {
		FileInputStream fis = null;
		try {
			try {
				String ext = FileUtil.getFileExtension(file);
				fis = new FileInputStream(file);
				if ("xls".equalsIgnoreCase(ext)) {
					return _parseExcel2003(fis, password);
				} else if ("xlsx".equalsIgnoreCase(ext)) {
					return _parseExcel2007(fis, password);
				} else {
					throw new RuntimeException("�������� �ʴ� ���������Դϴ�.");
				}
			} finally {
				if (fis != null) {
					fis.close();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * RecordSet�� ����2003 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param response
	 * @param rs
	 * @param fileName
	 * @return ó���Ǽ�
	 */
	public static int renderExcel2003(HttpServletResponse response, RecordSet rs, String fileName) {
		return renderExcel2003(response, rs, fileName, null);
	}

	/**
	 * RecordSet�� ����2003 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param response
	 * @param rs
	 * @param fileName
	 * @param header
	 * @return ó���Ǽ�
	 */
	public static int renderExcel2003(HttpServletResponse response, RecordSet rs, String fileName, String[] header) {
		if (rs == null) {
			return 0;
		}
		int rowCount = 0;
		try {
			response.setContentType("application/octet-stream;");
			response.setHeader("Content-Disposition", (new StringBuilder("attachment; filename=\"")).append(new String(fileName.getBytes(), "ISO-8859-1")).append("\"").toString());
			response.setHeader("Pragma", "no-cache;");
			response.setHeader("Expires", "-1;");
			Workbook workbook = new HSSFWorkbook();
			Sheet sheet = workbook.createSheet();
			OutputStream os = response.getOutputStream();
			String[] colNms = rs.getColumns();
			if (header != null) {
				Row row = sheet.createRow(rowCount);
				_appendHeader(row, header);
				rowCount++;
			}
			rs.moveRow(0);
			while (rs.nextRow()) {
				Row row = sheet.createRow(rowCount);
				_appendRow(row, rs, colNms);
				rowCount++;
			}
			for (int i = 0; i < colNms.length; i++) {
				sheet.autoSizeColumn(i);
			}
			workbook.write(os);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return rowCount;
	}

	/**
	 * RecordSet�� ����2003 �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file
	 * @param rs
	 * @return ó���Ǽ�
	 */
	public static int writeExcel2003(File file, RecordSet rs) {
		return writeExcel2003(file, rs, null);
	}

	/**
	 * RecordSet�� ����2003 �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file
	 * @param rs
	 * @param header
	 * @return ó���Ǽ�
	 */
	public static int writeExcel2003(File file, RecordSet rs, String[] header) {
		if (rs == null) {
			return 0;
		}
		int rowCount = 0;
		try {
			Workbook workbook = new HSSFWorkbook();
			Sheet sheet = workbook.createSheet();
			FileOutputStream fos = new FileOutputStream(file);
			String[] colNms = rs.getColumns();
			if (header != null) {
				Row row = sheet.createRow(rowCount);
				_appendHeader(row, header);
				rowCount++;
			}
			rs.moveRow(0);
			while (rs.nextRow()) {
				Row row = sheet.createRow(rowCount);
				_appendRow(row, rs, colNms);
				rowCount++;
			}
			for (int i = 0; i < colNms.length; i++) {
				sheet.autoSizeColumn(i);
			}
			workbook.write(fos);
			fos.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return rowCount;
	}

	/**
	 * RecordSet�� ����2007 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. 
	 * @param response
	 * @param rs
	 * @param fileName
	 * @return ó���Ǽ�
	 */
	public static int renderExcel2007(HttpServletResponse response, RecordSet rs, String fileName) {
		return renderExcel2007(response, rs, fileName, null);
	}

	/**
	 * RecordSet�� ����2007 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. 
	 * @param response
	 * @param rs
	 * @param fileName
	 * @param header
	 * @return ó���Ǽ�
	 */
	public static int renderExcel2007(HttpServletResponse response, RecordSet rs, String fileName, String[] header) {
		if (rs == null) {
			return 0;
		}
		int rowCount = 0;
		try {
			response.setContentType("application/octet-stream;");
			response.setHeader("Content-Disposition", (new StringBuilder("attachment; filename=\"")).append(new String(fileName.getBytes(), "ISO-8859-1")).append("\"").toString());
			response.setHeader("Pragma", "no-cache;");
			response.setHeader("Expires", "-1;");
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet();
			OutputStream os = response.getOutputStream();
			String[] colNms = rs.getColumns();
			if (header != null) {
				Row row = sheet.createRow(rowCount);
				_appendHeader(row, header);
				rowCount++;
			}
			rs.moveRow(0);
			while (rs.nextRow()) {
				Row row = sheet.createRow(rowCount);
				_appendRow(row, rs, colNms);
				rowCount++;
			}
			for (int i = 0; i < colNms.length; i++) {
				sheet.autoSizeColumn(i);
			}
			workbook.write(os);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return rowCount;
	}

	/**
	 * RecordSet�� ����2007 �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file
	 * @param rs
	 * @return ó���Ǽ�
	 */
	public static int writeExcel2007(File file, RecordSet rs) {
		return writeExcel2007(file, rs, null);
	}

	/**
	 * RecordSet�� ����2007 �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file
	 * @param rs
	 * @param header
	 * @return ó���Ǽ�
	 */
	public static int writeExcel2007(File file, RecordSet rs, String[] header) {
		if (rs == null) {
			return 0;
		}
		int rowCount = 0;
		try {
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet();
			FileOutputStream fos = new FileOutputStream(file);
			String[] colNms = rs.getColumns();
			if (header != null) {
				Row row = sheet.createRow(rowCount);
				_appendHeader(row, header);
				rowCount++;
			}
			rs.moveRow(0);
			while (rs.nextRow()) {
				Row row = sheet.createRow(rowCount);
				_appendRow(row, rs, colNms);
				rowCount++;
			}
			for (int i = 0; i < colNms.length; i++) {
				sheet.autoSizeColumn(i);
			}
			workbook.write(fos);
			fos.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return rowCount;
	}

	/**
	 * RecordSet�� CSV �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. 
	 * @param response
	 * @param rs
	 * @param fileName
	 * @return ó���Ǽ�
	 */
	public static int renderCSV(HttpServletResponse response, RecordSet rs, String fileName) {
		return renderSep(response, rs, fileName, ",");
	}

	/**
	 * RecordSet�� CSV �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file
	 * @param rs
	 * @return ó���Ǽ�
	 */
	public static int writeCSV(File file, RecordSet rs) {
		return writeSep(file, rs, ",");
	}

	/**
	 * RecordSet�� TSV �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. 
	 * @param response
	 * @param rs
	 * @param fileName
	 * @return ó���Ǽ�
	 */
	public static int renderTSV(HttpServletResponse response, RecordSet rs, String fileName) {
		return renderSep(response, rs, fileName, "\t");
	}

	/**
	 * RecordSet�� TSV �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file
	 * @param rs
	 * @return ó���Ǽ�
	 */
	public static int writeTSV(File file, RecordSet rs) {
		return writeSep(file, rs, "\t");
	}

	/**
	 * RecordSet�� ������(CSV, TSV ��)���� �������� ����Ѵ�. 
	 * <br>
	 * ex) response�� rs�� �������� �޸�(,) �� ������(CSV, TSV ��)���� �������� ����ϴ� ��� => ExcelUtil.renderSep(response, rs, ",")
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs ������(CSV, TSV ��)���� �������� ��ȯ�� RecordSet ��ü
	 * @param fileName
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 * @return ó���Ǽ�
	 */
	public static int renderSep(HttpServletResponse response, RecordSet rs, String fileName, String sep) {
		if (rs == null) {
			return 0;
		}
		int rowCount = 0;
		try {
			response.setContentType("application/octet-stream;");
			response.setHeader("Content-Disposition", (new StringBuilder("attachment; filename=\"")).append(new String(fileName.getBytes(), "ISO-8859-1")).append("\"").toString());
			response.setHeader("Pragma", "no-cache;");
			response.setHeader("Expires", "-1;");
			PrintWriter pw = response.getWriter();
			String[] colNms = rs.getColumns();
			rs.moveRow(0);
			while (rs.nextRow()) {
				if (rowCount++ > 0) {
					pw.print("\n");
				}
				pw.print(_sepRowStr(rs, colNms, sep));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return rowCount;
	}

	/**
	 * RecordSet�� ������(CSV, TSV ��)���� �������� ���Ϸ� �����Ѵ�.
	 * @param file
	 * @param rs
	 * @param sep
	 * @return ó���Ǽ�
	 */
	public static int writeSep(File file, RecordSet rs, String sep) {
		if (rs == null) {
			return 0;
		}
		int rowCount = 0;
		try {
			FileWriter fw = new FileWriter(file);
			String[] colNms = rs.getColumns();
			rs.moveRow(0);

			while (rs.nextRow()) {
				if (rowCount++ > 0) {
					fw.write("\n");
				}
				fw.write(_sepRowStr(rs, colNms, sep));
			}
			fw.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return rowCount;
	}

	/**
	 * RecordSet�� ������(CSV, TSV ��)���� �������� ��ȯ�Ѵ�. 
	 * <br>
	 * ex) rs�� �������� �޸�(,) �� ������(CSV, TSV ��)���� �������� ��ȯ�ϴ� ��� : String csv = ExcelUtil.renderSep(rs, ",")
	 * @param rs ��ȯ�� RecordSet ��ü
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 * @return ������(CSV, TSV ��)���� �������� ��ȯ�� ���ڿ�
	 */
	public static String renderSep(RecordSet rs, String sep) {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		int rowCount = 0;
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append("\n");
			}
			buffer.append(_sepRowStr(rs, colNms, sep));
		}
		return buffer.toString();
	}

	/**
	 * ResultSet�� ����2003 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param response
	 * @param rs
	 * @param fileName
	 * @return ó���Ǽ�
	 */
	public static int renderExcel2003(HttpServletResponse response, ResultSet rs, String fileName) {
		return renderExcel2003(response, rs, fileName, null);
	}

	/**
	 * ResultSet�� ����2003 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param response
	 * @param rs
	 * @param fileName
	 * @param header
	 * @return ó���Ǽ�
	 */
	public static int renderExcel2003(HttpServletResponse response, ResultSet rs, String fileName, String[] header) {
		if (rs == null) {
			return 0;
		}
		try {
			response.setContentType("application/octet-stream;");
			response.setHeader("Content-Disposition", (new StringBuilder("attachment; filename=\"")).append(new String(fileName.getBytes(), "ISO-8859-1")).append("\"").toString());
			response.setHeader("Pragma", "no-cache;");
			response.setHeader("Expires", "-1;");
			Workbook workbook = new HSSFWorkbook();
			Sheet sheet = workbook.createSheet();
			OutputStream os = response.getOutputStream();
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] colNms = new String[count];
				for (int i = 1; i <= count; i++) {
					//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
					colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
				}
				int rowCount = 0;
				if (header != null) {
					Row row = sheet.createRow(rowCount);
					_appendHeader(row, header);
					rowCount++;
				}
				while (rs.next()) {
					Row row = sheet.createRow(rowCount);
					_appendRow(row, rs, colNms);
					rowCount++;
				}
				for (int i = 0; i < colNms.length; i++) {
					sheet.autoSizeColumn(i);
				}
				workbook.write(os);
				return rowCount;
			} finally {
				Statement stmt = rs.getStatement();
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ResultSet�� ����2003 �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file
	 * @param rs
	 * @return ó���Ǽ�
	 */
	public static int writeExcel2003(File file, ResultSet rs) {
		return writeExcel2003(file, rs, null);
	}

	/**
	 * ResultSet�� ����2003 �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file
	 * @param rs
	 * @param header
	 * @return ó���Ǽ�
	 */
	public static int writeExcel2003(File file, ResultSet rs, String[] header) {
		if (rs == null) {
			return 0;
		}
		try {
			Workbook workbook = new HSSFWorkbook();
			Sheet sheet = workbook.createSheet();
			FileOutputStream fos = new FileOutputStream(file);
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] colNms = new String[count];
				for (int i = 1; i <= count; i++) {
					//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
					colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
				}
				int rowCount = 0;
				if (header != null) {
					Row row = sheet.createRow(rowCount);
					_appendHeader(row, header);
					rowCount++;
				}
				while (rs.next()) {
					Row row = sheet.createRow(rowCount);
					_appendRow(row, rs, colNms);
					rowCount++;
				}
				for (int i = 0; i < colNms.length; i++) {
					sheet.autoSizeColumn(i);
				}
				workbook.write(fos);
				return rowCount;
			} finally {
				Statement stmt = rs.getStatement();
				if (fos != null)
					fos.close();
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ResultSet�� ����2007 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param response
	 * @param rs
	 * @param fileName
	 * @return ó���Ǽ�
	 */
	public static int renderExcel2007(HttpServletResponse response, ResultSet rs, String fileName) {
		return renderExcel2007(response, rs, fileName, null);
	}

	/**
	 * ResultSet�� ����2007 �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * @param response
	 * @param rs
	 * @param fileName
	 * @param header
	 * @return ó���Ǽ�
	 */
	public static int renderExcel2007(HttpServletResponse response, ResultSet rs, String fileName, String[] header) {
		if (rs == null) {
			return 0;
		}
		try {
			response.setContentType("application/octet-stream;");
			response.setHeader("Content-Disposition", (new StringBuilder("attachment; filename=\"")).append(new String(fileName.getBytes(), "ISO-8859-1")).append("\"").toString());
			response.setHeader("Pragma", "no-cache;");
			response.setHeader("Expires", "-1;");
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet();
			OutputStream os = response.getOutputStream();
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] colNms = new String[count];
				for (int i = 1; i <= count; i++) {
					//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
					colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
				}
				int rowCount = 0;
				if (header != null) {
					Row row = sheet.createRow(rowCount);
					_appendHeader(row, header);
					rowCount++;
				}
				while (rs.next()) {
					Row row = sheet.createRow(rowCount);
					_appendRow(row, rs, colNms);
					rowCount++;
				}
				for (int i = 0; i < colNms.length; i++) {
					sheet.autoSizeColumn(i);
				}
				workbook.write(os);
				return rowCount;
			} finally {
				Statement stmt = rs.getStatement();
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ResultSet�� ����2007 �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file
	 * @param rs
	 * @return ó���Ǽ�
	 */
	public static int writeExcel2007(File file, ResultSet rs) {
		return writeExcel2007(file, rs, null);
	}

	/**
	 * ResultSet�� ����2007 �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file
	 * @param rs
	 * @param header
	 * @return ó���Ǽ�
	 */
	public static int writeExcel2007(File file, ResultSet rs, String[] header) {
		if (rs == null) {
			return 0;
		}
		try {
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet();
			FileOutputStream fos = new FileOutputStream(file);
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] colNms = new String[count];
				for (int i = 1; i <= count; i++) {
					//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
					colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
				}
				int rowCount = 0;
				if (header != null) {
					Row row = sheet.createRow(rowCount);
					_appendHeader(row, header);
					rowCount++;
				}
				while (rs.next()) {
					Row row = sheet.createRow(rowCount);
					_appendRow(row, rs, colNms);
					rowCount++;
				}
				for (int i = 0; i < colNms.length; i++) {
					sheet.autoSizeColumn(i);
				}
				workbook.write(fos);
				return rowCount;
			} finally {
				Statement stmt = rs.getStatement();
				if (fos != null)
					fos.close();
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ResultSet�� CSV �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. 
	 * @param response
	 * @param rs
	 * @param fileName
	 * @return ó���Ǽ�
	 */
	public static int renderCSV(HttpServletResponse response, ResultSet rs, String fileName) {
		return renderSep(response, rs, fileName, ",");
	}

	/**
	 * ResultSet�� CSV �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file
	 * @param rs
	 * @return ó���Ǽ�
	 */
	public static int writeCSV(File file, ResultSet rs) {
		return writeSep(file, rs, ",");
	}

	/**
	 * ResultSet�� TSV �������� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. 
	 * @param response
	 * @param rs
	 * @param fileName
	 * @return ó���Ǽ�
	 */
	public static int renderTSV(HttpServletResponse response, ResultSet rs, String fileName) {
		return renderSep(response, rs, fileName, "\t");
	}

	/**
	 * ResultSet�� TSV �������� ��ȯ�Ͽ� ���Ϸ� �����Ѵ�.
	 * @param file
	 * @param rs
	 * @return ó���Ǽ�
	 */
	public static int writeTSV(File file, ResultSet rs) {
		return writeSep(file, rs, "\t");
	}

	/**
	 * ResultSet�� ������(CSV, TSV ��)���� �������� ����Ѵ�. 
	 * <br>
	 * ex) response�� rs�� �������� �޸�(,) �� ������(CSV, TSV ��)���� �������� ����ϴ� ��� => ExcelUtil.renderSep(response, rs, ",")
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs ������(CSV, TSV ��)���� �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param fileName
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 * @return ó���Ǽ�
	 */
	public static int renderSep(HttpServletResponse response, ResultSet rs, String fileName, String sep) {
		if (rs == null) {
			return 0;
		}
		try {
			response.setContentType("application/octet-stream;");
			response.setHeader("Content-Disposition", (new StringBuilder("attachment; filename=\"")).append(new String(fileName.getBytes(), "ISO-8859-1")).append("\"").toString());
			response.setHeader("Pragma", "no-cache;");
			response.setHeader("Expires", "-1;");
			PrintWriter pw = response.getWriter();
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] colNms = new String[count];
				for (int i = 1; i <= count; i++) {
					//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
					colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
				}
				int rowCount = 0;
				while (rs.next()) {
					if (rowCount++ > 0) {
						pw.print("\n");
					}
					pw.print(_sepRowStr(rs, colNms, sep));
				}
				return rowCount;
			} finally {
				Statement stmt = rs.getStatement();
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ResultSet�� ������(CSV, TSV ��)���� �������� ���Ϸ� �����Ѵ�.
	 * @param file
	 * @param rs
	 * @param sep
	 * @return ó���Ǽ�
	 */
	public static int writeSep(File file, ResultSet rs, String sep) {
		if (rs == null) {
			return 0;
		}
		try {
			FileWriter fw = new FileWriter(file);
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] colNms = new String[count];
				for (int i = 1; i <= count; i++) {
					//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
					colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
				}
				int rowCount = 0;
				while (rs.next()) {
					if (rowCount++ > 0) {
						fw.write("\n");
					}
					fw.write(_sepRowStr(rs, colNms, sep));
				}
				return rowCount;
			} finally {
				Statement stmt = rs.getStatement();
				if (fw != null)
					fw.close();
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ResultSet�� ������(CSV, TSV ��)���� �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� �������� �޸�(,) �� ������(CSV, TSV ��)���� �������� ��ȯ�ϴ� ��� : String csv = ExcelUtil.renderSep(rs, ",")
	 * @param rs ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 * @return ������(CSV, TSV ��)���� �������� ��ȯ�� ���ڿ�
	 */
	public static String renderSep(ResultSet rs, String sep) {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		try {
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] colNms = new String[count];
				for (int i = 1; i <= count; i++) {
					//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
					colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
				}
				int rowCount = 0;
				while (rs.next()) {
					if (rowCount++ > 0) {
						buffer.append("\n");
					}
					buffer.append(_sepRowStr(rs, colNms, sep));
				}
			} finally {
				Statement stmt = rs.getStatement();
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return buffer.toString();
	}

	/**
	 * Map��ü�� ������(CSV, TSV ��)���� �������� ��ȯ�Ѵ�. 
	 * <br>
	 * ex) map�� �������� �޸�(,) �� ������(CSV, TSV ��)���� �������� ��ȯ�ϴ� ��� : String csv = ExcelUtil.renderSep(map, ",")
	 * @param map ��ȯ�� Map��ü
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 * @return ������(CSV, TSV ��)���� �������� ��ȯ�� ���ڿ�
	 */
	public static String renderSep(Map<String, Object> map, String sep) {
		if (map == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(_sepRowStr(map, sep));
		return buffer.toString();
	}

	/**
	 * List��ü�� ������(CSV, TSV ��)���� �������� ��ȯ�Ѵ�. 
	 * <br>
	 * ex1) mapList�� �������� �޸�(,) �� ������(CSV, TSV ��)���� �������� ��ȯ�ϴ� ��� : String csv = ExcelUtil.renderSep(mapList, ",")
	 * @param mapList ��ȯ�� List��ü
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 * @return ������(CSV, TSV ��)���� �������� ��ȯ�� ���ڿ�
	 */
	public static String renderSep(List<Map<String, Object>> mapList, String sep) {
		if (mapList == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		int rowCount = 0;
		for (Map<String, Object> map : mapList) {
			if (rowCount++ > 0) {
				buffer.append("\n");
			}
			buffer.append(_sepRowStr(map, sep));
		}
		return buffer.toString();
	}

	/**
	 * �����ڷ� ���̴� ���ڿ� �Ǵ� ���๮�ڰ� ���� ���ԵǾ� ���� ��� ���� �ֵ���ǥ�� �ѷ��ε��� ��ȯ�Ѵ�.
	 * @param str ��ȯ�� ���ڿ�
	 * @param sep �� �����ڷ� ���� ���ڿ�
	 */
	public static String escapeSep(String str, String sep) {
		if (str == null) {
			return "";
		}
		return (str.contains(sep) || str.contains("\n")) ? "\"" + str + "\"" : str;
	}

	////////////////////////////////////////////////////////////////////////////////////////// Private �޼ҵ�

	/**
	 * ������(CSV, TSV ��)���� ������ Row ���ڿ� ����
	 * ����Ÿ�� ���ڰ� �ƴҶ����� �����ڷ� ���� ���ڿ� �Ǵ� ���๮�ڸ� escape �ϱ� ���� ���� �ֵ���ǥ�� �ѷ��Ѵ�.
	 */
	private static String _sepRowStr(Map<String, Object> map, String sep) {
		StringBuilder buffer = new StringBuilder();
		Set<String> keys = map.keySet();
		int rowCount = 0;
		for (String key : keys) {
			Object value = map.get(key);
			if (rowCount++ > 0) {
				buffer.append(sep);
			}
			if (value == null) {
				buffer.append("");
			} else {
				if (value instanceof Number) {
					buffer.append(value.toString());
				} else {
					buffer.append(escapeSep(value.toString(), sep));
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * ������(CSV, TSV ��)���� ������ Row ���ڿ� ����
	 * ����Ÿ�� ���ڰ� �ƴҶ����� �����ڷ� ���� ���ڿ� �Ǵ� ���๮�ڸ� escape �ϱ� ���� ���� �ֵ���ǥ�� �ѷ��Ѵ�.
	 */
	private static String _sepRowStr(RecordSet rs, String[] colNms, String sep) {
		StringBuilder buffer = new StringBuilder();
		int rowCount = 0;
		for (int c = 0; c < colNms.length; c++) {
			Object value = rs.get(colNms[c]);
			if (rowCount++ > 0) {
				buffer.append(sep);
			}
			if (value == null) {
				buffer.append("");
			} else {
				if (value instanceof Number) {
					buffer.append(value.toString());
				} else {
					buffer.append(escapeSep(value.toString(), sep));
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * ������(CSV, TSV ��)���� ������ Row ���ڿ� ����
	 * ����Ÿ�� ���ڰ� �ƴҶ����� �����ڷ� ���� ���ڿ� �Ǵ� ���๮�ڸ� escape �ϱ� ���� ���� �ֵ���ǥ�� �ѷ��Ѵ�.
	 */
	private static String _sepRowStr(ResultSet rs, String[] colNms, String sep) {
		StringBuilder buffer = new StringBuilder();
		int rowCount = 0;
		for (int c = 0; c < colNms.length; c++) {
			Object value;
			try {
				value = rs.getObject(colNms[c]);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			if (rowCount++ > 0) {
				buffer.append(sep);
			}
			if (value == null) {
				buffer.append("");
			} else {
				if (value instanceof Number) {
					buffer.append(value.toString());
				} else {
					buffer.append(escapeSep(value.toString(), sep));
				}
			}
		}
		return buffer.toString();
	}

	private static void _appendHeader(Row row, String[] header) {
		if (header == null)
			return;
		for (int c = 0; c < header.length; c++) {
			Cell cell = row.createCell(c);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(header[0]);
		}
	}

	private static void _appendRow(Row row, RecordSet rs, String[] colNms) {
		if (rs.getRowCount() == 0)
			return;
		for (int c = 0; c < colNms.length; c++) {
			Cell cell = row.createCell(c);
			Object value = rs.get(colNms[c]);
			if (value == null) {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue("");
			} else {
				if (value instanceof Number) {
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellValue(Double.valueOf(value.toString()));
				} else {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(value.toString());
				}
			}
		}
	}

	private static void _appendRow(Row row, ResultSet rs, String[] colNms) {
		try {
			if (rs.getRow() == 0)
				return;
			for (int c = 0; c < colNms.length; c++) {
				Cell cell = row.createCell(c);
				Object value = rs.getObject(colNms[c]);
				if (value == null) {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue("");
				} else {
					if (value instanceof Number) {
						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell.setCellValue(Double.valueOf(value.toString()));
					} else {
						cell.setCellType(Cell.CELL_TYPE_STRING);
						cell.setCellValue(value.toString());
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static List<Map<String, String>> _parseExcel2003(InputStream is) {
		POIFSFileSystem poiFileSystem;
		HSSFSheet sheet;
		try {
			poiFileSystem = new POIFSFileSystem(is);
			HSSFWorkbook workbook = new HSSFWorkbook(poiFileSystem);
			sheet = workbook.getSheetAt(0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return _parseSheet(sheet);
	}

	private static List<Map<String, String>> _parseExcel2003(InputStream is, String password) {
		POIFSFileSystem poiFileSystem;
		HSSFSheet sheet;
		try {
			poiFileSystem = new POIFSFileSystem(is);
			Biff8EncryptionKey.setCurrentUserPassword(password);
			HSSFWorkbook workbook = new HSSFWorkbook(poiFileSystem);
			Biff8EncryptionKey.setCurrentUserPassword(null);
			sheet = workbook.getSheetAt(0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return _parseSheet(sheet);
	}

	private static List<Map<String, String>> _parseExcel2007(InputStream is) {
		XSSFWorkbook workbook;
		try {
			workbook = new XSSFWorkbook(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return _parseSheet(workbook.getSheetAt(0));
	}

	private static List<Map<String, String>> _parseExcel2007(InputStream is, String password) {
		XSSFWorkbook workbook;
		try {
			POIFSFileSystem fs = new POIFSFileSystem(is);
			EncryptionInfo info = new EncryptionInfo(fs);
			Decryptor d = new Decryptor(info);
			d.verifyPassword(password);
			workbook = new XSSFWorkbook(d.getDataStream(fs));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return _parseSheet(workbook.getSheetAt(0));
	}

	private static List<Map<String, String>> _parseCSV(InputStream is) {
		return _parseSep(is, ",");
	}

	private static List<Map<String, String>> _parseTSV(InputStream is) {
		return _parseSep(is, "\t");
	}

	private static List<Map<String, String>> _parseSep(InputStream is, String sep) {
		BufferedReader br = null;
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		try {
			try {
				br = new BufferedReader(new InputStreamReader(is));
				String line = null;
				while ((line = br.readLine()) != null) {
					String[] items = line.split(sep);
					Map<String, String> map = new HashMap<String, String>();
					for (int i = 0; i < items.length; i++) {
						map.put(String.valueOf(i), items[i]);
					}
					mapList.add(map);
				}
			} finally {
				if (br != null)
					br.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return mapList;
	}

	/**
	 * ���� ��Ʈ�� ������ �Ľ��Ͽ� ���� ����Ʈ�� ����
	 */
	private static List<Map<String, String>> _parseSheet(Sheet sheet) {
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		int rowCount = sheet.getPhysicalNumberOfRows();
		int colCount = sheet.getRow(0).getPhysicalNumberOfCells();
		for (int i = 0; i < rowCount; i++) {
			Row row = sheet.getRow(i);
			Map<String, String> map = new HashMap<String, String>();
			for (int j = 0; j < colCount; j++) {
				Cell cell = row.getCell(j);
				String item = "";
				if (cell == null) {
					item = "";
				} else {
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_ERROR:
						throw new RuntimeException("EXCEL�� ���� ������ ���ԵǾ� �־� �м��� �����Ͽ����ϴ�.");
					case Cell.CELL_TYPE_FORMULA:
						throw new RuntimeException("EXCEL�� ������ ���ԵǾ� �־� �м��� �����Ͽ����ϴ�.");
					case Cell.CELL_TYPE_NUMERIC:
						cell.setCellType(Cell.CELL_TYPE_STRING);
						item = cell.getStringCellValue();
						break;
					case Cell.CELL_TYPE_STRING:
						item = cell.getStringCellValue();
						break;
					}
				}
				map.put(String.valueOf(j), item);
			}
			mapList.add(map);
		}
		return mapList;
	}
}
