package framework.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import framework.db.RecordSet;

/**
 * XML�� �̿��Ͽ� ������ �� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class XmlUtil {
	protected static final Log logger = LogFactory.getLog(framework.util.XmlUtil.class);
	
	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private XmlUtil() {
	}

	/**
	 * RecordSet�� xml �������� ����Ѵ�. (xml �������). 
	 * <br>
	 * ex) response�� rs�� xml �������� ����ϴ� ��� : XmlUtil.render(response, rs, "utf-8")
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs xml �������� ��ȯ�� RecordSet ��ü
	 * @param encoding ����� ���Ե� ���ڵ�
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, RecordSet rs, String encoding) {
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
		pw.print(_xmlHeaderStr(encoding));
		pw.print("<items>");
		int rowCount = 0;
		while (rs.nextRow()) {
			rowCount++;
			pw.print(_xmlItemStr(rs, colNms));
		}
		pw.print("</items>");
		return rowCount;
	}

	/**
	 * RecordSet�� xml �������� ��ȯ�Ѵ�. (xml ��� ������).
	 * <br>
	 * ex) rs�� xml �������� ��ȯ�ϴ� ��� : String xml = XmlUtil.render(rs)
	 * @param rs xml �������� ��ȯ�� RecordSet ��ü
	 * @return xml �������� ��ȯ�� ���ڿ�
	 */
	public static String render(RecordSet rs) {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		buffer.append("<items>");
		while (rs.nextRow()) {
			buffer.append(_xmlItemStr(rs, colNms));
		}
		buffer.append("</items>");
		return buffer.toString();
	}

	/**
	 * RecordSet�� xml �������� ��ȯ�Ѵ�. (xml �������). 
	 * <br>
	 * ex) rs�� xml �������� ��ȯ�ϴ� ��� : String xml = XmlUtil.render(rs, "utf-8")
	 * @param rs xml �������� ��ȯ�� RecordSet ��ü
	 * @param encoding ����� ���Ե� ���ڵ�
	 * @return xml �������� ��ȯ�� ���ڿ�
	 */
	public static String render(RecordSet rs, String encoding) {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(_xmlHeaderStr(encoding));
		buffer.append(render(rs));
		return buffer.toString();
	}

	/**
	 * ResultSet�� xml �������� ����Ѵ� (xml �������). 
	 * <br>
	 * ex) response�� rs�� xml �������� ����ϴ� ��� : XmlUtil.render(response, rs, "utf-8")
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs xml �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param encoding ����� ���Ե� ���ڵ�
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, ResultSet rs, String encoding) {
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
				pw.print(_xmlHeaderStr(encoding));
				pw.print("<items>");
				int rowCount = 0;
				while (rs.next()) {
					rowCount++;
					pw.print(_xmlItemStr(rs, colNms));
				}
				pw.print("</items>");
				return rowCount;
			} finally {
				Statement stmt = null;
				try {
					stmt = rs.getStatement();
				} catch (SQLException e) {
					if (logger.isErrorEnabled()) {
						logger.error(e);
					}
				}
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
	 * ResultSet�� xml �������� ��ȯ�Ѵ� (xml ��� ������). 
	 * <br>
	 * ex) rs�� xml �������� ��ȯ�ϴ� ��� : String xml = XmlUtil.render(rs)
	 * @param rs xml �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 */
	public static String render(ResultSet rs) {
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
				buffer.append("<items>");
				while (rs.next()) {
					buffer.append(_xmlItemStr(rs, colNms));
				}
				buffer.append("</items>");
			} finally {
				Statement stmt = null;
				try {
					stmt = rs.getStatement();
				} catch (SQLException e) {
					if (logger.isErrorEnabled()) {
						logger.error(e);
					}
				}
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
	 * ResultSet�� xml �������� ��ȯ�Ѵ� (xml �������). 
	 * <br>
	 * ex) rs�� xml �������� ��ȯ�ϴ� ��� : String xml = XmlUtil.render(rs, "utf-8")
	 * @param rs xml �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param encoding ����� ���Ե� ���ڵ�
	 */
	public static String render(ResultSet rs, String encoding) {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		try {
			buffer.append(_xmlHeaderStr(encoding));
			buffer.append(render(rs));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return buffer.toString();
	}

	/**
	 * Map��ü�� xml �������� ��ȯ�Ѵ� (xml ��� ������). 
	 * <br>
	 * ex) map�� xml �������� ��ȯ�ϴ� ��� : String xml = XmlUtil.render(map)
	 * @param map ��ȯ�� Map��ü
	 * @return xml �������� ��ȯ�� ���ڿ�
	 */
	public static String render(Map<String, Object> map) {
		if (map == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append("<items>");
		buffer.append(_xmlItemStr(map));
		buffer.append("</items>");
		return buffer.toString();
	}

	/**
	 * Map��ü�� xml �������� ��ȯ�Ѵ� (xml �������). 
	 * <br>
	 * ex) map�� xml �������� ��ȯ�ϴ� ���  : String xml = XmlUtil.render(map, "utf-8")
	 * @param map ��ȯ�� Map��ü
	 * @param encoding ����� ���Ե� ���ڵ�
	 * @return xml �������� ��ȯ�� ���ڿ�
	 */
	public static String render(Map<String, Object> map, String encoding) {
		if (map == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(_xmlHeaderStr(encoding));
		buffer.append(render(map));
		return buffer.toString();
	}

	/**
	 * List��ü�� xml ���·� ��ȯ�Ѵ� (xml ��� ������). 
	 * <br>
	 * ex) mapList�� xml���� ��ȯ�ϴ� ��� : String xml = XmlUtil.render(mapList)
	 * @param mapList ��ȯ�� List��ü
	 * @return xml�������� ��ȯ�� ���ڿ�
	 */
	public static String render(List<Map<String, Object>> mapList) {
		if (mapList == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append("<items>");
		for (Map<String, Object> map : mapList) {
			buffer.append(_xmlItemStr(map));
		}
		buffer.append("</items>");
		return buffer.toString();
	}

	/**
	 * List��ü�� xml ���·� ��ȯ�Ѵ� (xml �������).
	 * <br>
	 * ex) mapList�� xml���� ��ȯ�ϴ� ���  : String xml = XmlUtil.render(mapList, "utf-8")
	 * @param mapList ��ȯ�� List��ü
	 * @param encoding ����� ���Ե� ���ڵ�
	 * @return xml�������� ��ȯ�� ���ڿ�
	 */
	public static String render(List<Map<String, Object>> mapList, String encoding) {
		if (mapList == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(_xmlHeaderStr(encoding));
		buffer.append(render(mapList));
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// Private �޼ҵ�

	/**
	 *  xml ��� ���ڿ� ����
	 */
	private static String _xmlHeaderStr(String encoding) {
		return "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>";
	}

	/**
	 * xml item ���ڿ� ����
	 */
	@SuppressWarnings("unchecked")
	private static String _xmlItemStr(Map<String, Object> map) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("<item>");
		for (Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value == null) {
				buffer.append("<" + key.toLowerCase() + ">" + "</" + key.toLowerCase() + ">");
			} else {
				if (value instanceof Number) {
					buffer.append("<" + key.toLowerCase() + ">" + value.toString() + "</" + key.toLowerCase() + ">");
				} else if (value instanceof Map) {
					buffer.append("<" + key.toLowerCase() + ">" + render((Map<String, Object>) value) + "</" + key.toLowerCase() + ">");
				} else if (value instanceof List) {
					buffer.append("<" + key.toLowerCase() + ">" + render((List<Map<String, Object>>) value) + "</" + key.toLowerCase() + ">");
				} else {
					buffer.append("<" + key.toLowerCase() + ">" + "<![CDATA[" + value.toString() + "]]>" + "</" + key.toLowerCase() + ">");
				}
			}
		}
		buffer.append("</item>");
		return buffer.toString();
	}

	/**
	 * xml item ���ڿ� ����
	 */
	private static String _xmlItemStr(RecordSet rs, String[] colNms) {
		if (colNms == null) {
			return "<item></item>";
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append("<item>");
		for (int c = 0; c < colNms.length; c++) {
			Object value = rs.get(colNms[c]);
			if (value == null) {
				buffer.append("<" + colNms[c].toLowerCase() + ">" + "</" + colNms[c].toLowerCase() + ">");
			} else {
				if (value instanceof Number) {
					buffer.append("<" + colNms[c].toLowerCase() + ">" + value.toString() + "</" + colNms[c].toLowerCase() + ">");
				} else {
					buffer.append("<" + colNms[c].toLowerCase() + ">" + "<![CDATA[" + value.toString() + "]]>" + "</" + colNms[c].toLowerCase() + ">");
				}
			}
		}
		buffer.append("</item>");
		return buffer.toString();
	}

	private static String _xmlItemStr(ResultSet rs, String[] colNms) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("<item>");
		for (int c = 0; c < colNms.length; c++) {
			Object value;
			try {
				value = rs.getObject(colNms[c]);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			if (value == null) {
				buffer.append("<" + colNms[c].toLowerCase() + ">" + "</" + colNms[c].toLowerCase() + ">");
			} else {
				if (value instanceof Number) {
					buffer.append("<" + colNms[c].toLowerCase() + ">" + value.toString() + "</" + colNms[c].toLowerCase() + ">");
				} else {
					buffer.append("<" + colNms[c].toLowerCase() + ">" + "<![CDATA[" + value.toString() + "]]>" + "</" + colNms[c].toLowerCase() + ">");
				}
			}
		}
		buffer.append("</item>");
		return buffer.toString();
	}
}