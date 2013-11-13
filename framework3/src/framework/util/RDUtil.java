/* 
 * @(#)RDUtil.java
 */
package framework.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import framework.db.RecordSet;

/**
 * RD(Report Designer)�� �̿��Ͽ� ������ �� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class RDUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private RDUtil() {
	}

	/**
	 * ����Ʈ �� ������
	 */
	public static final String DEFAULT_COLSEP = "##";

	/**
	 * ����Ʈ �� ������
	 */
	public static final String DEFAULT_LINESEP = "\n";

	/**
	 * RecordSet�� RD ���� �������� ����Ѵ�.
	 * ��, �� �����ڷ� ����Ʈ �����ڸ� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� RD ���� �������� ����ϴ� ��� : RDUtil.render(response, rs)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs RD ���� �������� ��ȯ�� RecordSet ��ü
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, RecordSet rs) {
		return render(response, rs, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * RecordSet�� RD ���� �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� RD ���� �������� ����ϴ� ��� : RDUtil.render(response, rs)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs RD ���� �������� ��ȯ�� RecordSet ��ü
	 * @param colSep �� �����ڷ� ���� ���ڿ�
	 * @param lineSep �� �����ڷ� ���� ���ڿ�
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, RecordSet rs, String colSep, String lineSep) {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw;
		try {
			pw = response.getWriter();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		int rowCount = 0;
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(lineSep);
			}
			pw.print(_rdRowStr(rs, colNms, colSep));
		}
		return rowCount;
	}

	/**
	 * RecordSet�� RD ���� �������� ��ȯ�Ѵ�.
	 * ��, �� �����ڷ� ����Ʈ �����ڸ� ����Ѵ�.
	 * <br>
	 * ex) rs�� RD ���� �������� ��ȯ�ϴ� ��� : String rd = RDUtil.render(rs)
	 * @param rs ��ȯ�� RecordSet ��ü
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 */
	public static String render(RecordSet rs) {
		return render(rs, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * RecordSet�� RD ���� �������� ��ȯ�Ѵ�. 
	 * <br>
	 * ex) rs�� �������� ##, �౸���� !! �� RD ���� �������� ��ȯ�ϴ� ��� : String rd = RDUtil.render(rs, "##", "!!")
	 * @param rs ��ȯ�� RecordSet ��ü
	 * @param colSep �� �����ڷ� ���� ���ڿ�
	 * @param lineSep �� �����ڷ� ���� ���ڿ�
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 */
	public static String render(RecordSet rs, String colSep, String lineSep) {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		int rowCount = 0;
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append(lineSep);
			}
			buffer.append(_rdRowStr(rs, colNms, colSep));
		}
		return buffer.toString();
	}

	/**
	 * ResultSet�� RD ���� �������� ����Ѵ�.
	 * ��, �� �����ڷ� ����Ʈ �����ڸ� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� RD ���� �������� ����ϴ� ��� : RDUtil.render(response, rs)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs RD ���� �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @return ó���Ǽ� 
	 */
	public static int render(HttpServletResponse response, ResultSet rs) {
		return render(response, rs, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * ResultSet�� RD ���� �������� ����Ѵ�. 
	 * <br>
	 * ex) response�� rs�� RD ���� �������� ����ϴ� ��� : RDUtil.render(response, rs)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs RD ���� �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param colSep �� �����ڷ� ���� ���ڿ�
	 * @param lineSep �� �����ڷ� ���� ���ڿ�
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, ResultSet rs, String colSep, String lineSep) {
		if (rs == null) {
			return 0;
		}
		try {
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
						pw.print(lineSep);
					}
					pw.print(_rdRowStr(rs, colNms, colSep));
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
	 * ResultSet�� RD ���� �������� ��ȯ�Ѵ�.
	 * ��, �� �����ڷ� ����Ʈ �����ڸ� ����Ѵ�. 
	 * <br>
	 * ex) rs�� RD ���� �������� ��ȯ�ϴ� ��� : String rd = RDUtil.render(rs)
	 * @param rs ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 */
	public static String render(ResultSet rs) {
		return render(rs, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * ResultSet�� RD ���� �������� ��ȯ�Ѵ�. 
	 * <br>
	 * ex) rs�� �������� ##, �౸���� !! �� RD ���� �������� ��ȯ�ϴ� ��� : String rd = RDUtil.render(rs, "##", "!!")
	 * @param rs ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param colSep �� �����ڷ� ���� ���ڿ�
	 * @param lineSep �� �����ڷ� ���� ���ڿ�
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 */
	public static String render(ResultSet rs, String colSep, String lineSep) {
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
						buffer.append(lineSep);
					}
					buffer.append(_rdRowStr(rs, colNms, colSep));
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
	 * Map��ü�� RD ���� �������� ��ȯ�Ѵ�.
	 * �� �����ڷ� ����Ʈ �����ڸ� ����Ѵ�. 
	 * <br>
	 * ex) map�� RD ���� �������� ��ȯ�ϴ� ��� : String rd = RDUtil.render(map)
	 * @param map ��ȯ�� Map��ü
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 */
	public static String render(Map<String, Object> map) {
		return render(map, DEFAULT_COLSEP);
	}

	/**
	 * Map��ü�� RD ���� �������� ��ȯ�Ѵ�. 
	 * <br>
	 * ex) map�� �������� ## �� RD ���� �������� ��ȯ�ϴ� ��� : String rd = RDUtil.render(map, "##")
	 * @param map ��ȯ�� Map��ü
	 * @param colSep �� �����ڷ� ���� ���ڿ�
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 */
	public static String render(Map<String, Object> map, String colSep) {
		if (map == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(_rdRowStr(map, colSep));
		return buffer.toString();
	}

	/**
	 * List��ü�� RD ���� �������� ��ȯ�Ѵ�.
	 * ��, �� �����ڷ� ����Ʈ �����ڸ� ����Ѵ�.
	 * <br>
	 * ex1) mapList�� RD ���� �������� ��ȯ�ϴ� ��� : String rd = RDUtil.render(mapList)
	 * @param mapList ��ȯ�� List��ü
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 */
	public static String render(List<Map<String, Object>> mapList) {
		return render(mapList, DEFAULT_COLSEP, DEFAULT_LINESEP);
	}

	/**
	 * List��ü�� RD ���� �������� ��ȯ�Ѵ�. 
	 * <br>
	 * ex1) mapList�� �������� ##, �౸���� !! �� RD ���� �������� ��ȯ�ϴ� ��� : String rd = RDUtil.render(mapList, "##", "!!")
	 * @param mapList ��ȯ�� List��ü
	 * @param colSep �� �����ڷ� ���� ���ڿ�
	 * @param lineSep �� �����ڷ� ���� ���ڿ�
	 * @return RD ���� �������� ��ȯ�� ���ڿ�
	 */
	public static String render(List<Map<String, Object>> mapList, String colSep, String lineSep) {
		if (mapList == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		if (mapList.size() > 0) {
			for (Map<String, Object> map : mapList) {
				buffer.append(_rdRowStr(map, colSep));
				buffer.append(lineSep);
			}
			buffer.delete(buffer.length() - lineSep.length(), buffer.length());
		}
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// Private �޼ҵ�

	/**
	 * ĳ��������, �����ǵ� ���ڵ��� ��ȯ�Ͽ��ش�.
	 * 
	 * @param str ��ȯ�� ���ڿ�
	 */
	private static String _escapeRD(String str) {
		if (str == null) {
			return "";
		}
		return str.replaceAll("\r\n", "\\\\n").replaceAll("\n", "\\\\n");
	}

	/**
	 * RD(����Ʈ�����̳�) �� Row ���ڿ� ����
	 */
	private static String _rdRowStr(Map<String, Object> map, String colSep) {
		StringBuilder buffer = new StringBuilder();
		for (Entry<String, Object> entry : map.entrySet()) {
			Object value = entry.getValue();
			if (value != null) {
				buffer.append(_escapeRD(value.toString()));
			}
			buffer.append(colSep);
		}
		return buffer.toString();
	}

	/**
	 * RD(����Ʈ�����̳�) �� Row ���ڿ� ����
	 */
	private static String _rdRowStr(RecordSet rs, String[] colNms, String colSep) {
		StringBuilder buffer = new StringBuilder();
		for (int c = 0; c < colNms.length; c++) {
			if (rs.get(colNms[c]) != null) {
				buffer.append(_escapeRD(rs.getString(colNms[c])));
			}
			buffer.append(colSep);
		}
		return buffer.toString();
	}

	private static String _rdRowStr(ResultSet rs, String[] colNms, String colSep) {
		StringBuilder buffer = new StringBuilder();
		try {
			for (int c = 0; c < colNms.length; c++) {
				if (rs.getObject(colNms[c]) != null) {
					buffer.append(_escapeRD(rs.getString(colNms[c])));
				}
				buffer.append(colSep);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return buffer.toString();
	}
}
