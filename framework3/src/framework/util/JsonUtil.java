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
import org.stringtree.json.JSONReader;
import org.stringtree.json.JSONWriter;

import framework.db.RecordSet;

/**
 * JSON(JavaScript Object Notation)�� �̿��Ͽ� ������ �� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class JsonUtil {
	protected static final Log logger = LogFactory.getLog(framework.util.JsonUtil.class);

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private JsonUtil() {
	}

	/**
	 * RecordSet�� JSON �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� JSON �������� ����ϴ� ��� : JsonUtil.render(response, rs)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs JSON �������� ��ȯ�� RecordSet ��ü
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, RecordSet rs) {
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
		pw.print("[");
		int rowCount = 0;
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(",");
			}
			pw.print(_jsonRowStr(rs, colNms));
		}
		pw.print("]");
		return rowCount;
	}

	/**
	 * RecordSet�� Json �迭 ���·� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� JSON �������� ��ȯ�ϴ� ��� : String json = JsonUtil.render(rs) 
	 * @param rs JSON �������� ��ȯ�� RecordSet ��ü
	 * @return JSON �������� ��ȯ�� ���ڿ�
	 */
	public static String render(RecordSet rs) {
		if (rs == null) {
			return "";
		}
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		StringBuilder buf = new StringBuilder();
		buf.append("[");
		int rowCount = 0;
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buf.append(",");
			}
			buf.append(_jsonRowStr(rs, colNms));
		}
		buf.append("]");
		return buf.toString();
	}

	/**
	 * ResultSet�� JSON �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� JSON �������� ����ϴ� ��� : JsonUtil.render(response, rs)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs JSON �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, ResultSet rs) {
		if (rs == null) {
			return 0;
		}
		try {
			PrintWriter pw = response.getWriter();
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int cnt = rsmd.getColumnCount();
				String[] colNms = new String[cnt];
				for (int i = 1; i <= cnt; i++) {
					//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
					colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
				}
				pw.print("[");
				int rowCount = 0;
				while (rs.next()) {
					if (rowCount++ > 0) {
						pw.print(",");
					}
					pw.print(_jsonRowStr(rs, colNms));
				}
				pw.print("]");
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
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						if (logger.isErrorEnabled()) {
							logger.error(e);
						}
					}
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						if (logger.isErrorEnabled()) {
							logger.error(e);
						}
					}
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ResultSet�� Json �迭 ���·� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� JSON �������� ��ȯ�ϴ� ��� : String json = JsonUtil.render(rs)
	 * @param rs JSON �������� ��ȯ�� ResultSet ��ü
	 * @return JSON �������� ��ȯ�� ���ڿ�
	 */
	public static String render(ResultSet rs) {
		if (rs == null) {
			return "";
		}
		StringBuilder buffer = new StringBuilder();
		try {
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int cnt = rsmd.getColumnCount();
				String[] colNms = new String[cnt];
				for (int i = 1; i <= cnt; i++) {
					//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
					colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
				}
				buffer.append("[");
				int rowCount = 0;
				while (rs.next()) {
					if (rowCount++ > 0) {
						buffer.append(",");
					}
					buffer.append(_jsonRowStr(rs, colNms));
				}
				buffer.append("]");
			} finally {
				Statement stmt = null;
				try {
					stmt = rs.getStatement();
				} catch (SQLException e) {
					if (logger.isErrorEnabled()) {
						logger.error(e);
					}
				}
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						if (logger.isErrorEnabled()) {
							logger.error(e);
						}
					}
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						if (logger.isErrorEnabled()) {
							logger.error(e);
						}
					}
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		return buffer.toString();
	}

	/**
	 * Map��ü�� JSON �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) map�� JSON �������� ��ȯ�ϴ� ��� : String json = JsonUtil.render(map)
	 * @param map ��ȯ�� Map��ü
	 * @return JSON �������� ��ȯ�� ���ڿ�
	 */
	public static String render(Map<String, Object> map) {
		if (map == null) {
			return "";
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(_jsonRowStr(map));
		return buffer.toString();
	}

	/**
	 * List��ü�� JSON �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex1) mapList�� JSON �������� ��ȯ�ϴ� ��� : String json = JsonUtil.render(mapList)
	 * @param mapList ��ȯ�� List��ü
	 * @return JSON �������� ��ȯ�� ���ڿ�
	 */
	public static String render(List<Map<String, Object>> mapList) {
		if (mapList == null) {
			return "";
		}
		StringBuilder buffer = new StringBuilder();
		if (mapList.size() > 0) {
			buffer.append("[");
			for (Map<String, Object> map : mapList) {
				buffer.append(_jsonRowStr(map));
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("]");
		} else {
			buffer.append("[]");
		}
		return buffer.toString();
	}

	/**
	 * ��ü�� JSON �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex1) obj�� JSON �������� ��ȯ�ϴ� ��� : String json = JsonUtil.stringify(obj)
	 * @param obj ��ȯ�� ��ü
	 * @return JSON �������� ��ȯ�� ���ڿ�
	 */
	public static String stringify(Object obj) {
		JSONWriter writer = new JSONWriter();
		return writer.write(obj);
	}

	/**
	 * JSON ���ڿ��� Object �� ��ȯ�Ѵ�.
	 * <br>
	 * ex1) json�� Object �������� ��ȯ�ϴ� ��� : Object obj = JsonUtil.parse(json)
	 * @param json ��ȯ�� JSON ���ڿ�
	 * @return Object �������� ��ȯ�� ��ü
	 */
	public static Object parse(String json) {
		JSONReader reader = new JSONReader();
		return reader.read(json);
	}

	/**
	 * JSON ���ڿ��� ���ڰ� �鿩���⸦ �����Ͽ� �����Ѵ�.
	 * @param json json ��ȯ�� JSON ���ڿ�
	 * @return Object �������� ��ȯ�� ��ü
	 */
	public static String pretty(String json) {
		return pretty(json, "    ");
	}

	/**
	 * JSON ���ڿ��� ���ڰ� �鿩���⸦ �����Ͽ� �����Ѵ�.
	 * @param json json json ��ȯ�� JSON ���ڿ�
	 * @param indent �鿩���⿡ ����� ���ڿ�
	 * @return Object �������� ��ȯ�� ��ü
	 */
	public static String pretty(String json, String indent) {
		StringBuilder buf = new StringBuilder();
		int level = 0;
		String target = null;
		for (int i = 0; i < json.length(); i++) {
			target = json.substring(i, i + 1);
			if (target.equals("{") || target.equals("[")) {
				buf.append(target).append("\n");
				level++;
				for (int j = 0; j < level; j++) {
					buf.append(indent);
				}
			} else if (target.equals("}") || target.equals("]")) {
				buf.append("\n");
				level--;
				for (int j = 0; j < level; j++) {
					buf.append(indent);
				}
				buf.append(target);
			} else if (target.equals(",")) {
				buf.append(target);
				buf.append("\n");
				for (int j = 0; j < level; j++) {
					buf.append(indent);
				}
			} else {
				buf.append(target);
			}
		}
		return buf.toString();
	}

	/**
	 * �ڹٽ�ũ��Ʈ�� Ư���ϰ� �νĵǴ� ���ڵ��� JSON� ����ϱ� ���� ��ȯ�Ͽ��ش�.
	 * @param str ��ȯ�� ���ڿ�
	 */
	public static String escapeJS(String str) {
		if (str == null) {
			return "";
		}
		return str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"").replaceAll("\r\n", "\\\\n").replaceAll("\n", "\\\\n").replaceAll("\t", "\\\\t");
	}

	////////////////////////////////////////////////////////////////////////////////////////// Private �޼ҵ�

	/**
	 * JSON �� Row ���ڿ� ����
	 */
	@SuppressWarnings("unchecked")
	private static String _jsonRowStr(Map<String, Object> map) {
		StringBuilder buf = new StringBuilder();
		if (map.entrySet().size() > 0) {
			buf.append("{");
			for (Entry<String, Object> entry : map.entrySet()) {
				String key = "\"" + escapeJS(entry.getKey().toLowerCase()) + "\"";
				Object value = entry.getValue();
				if (value == null) {
					buf.append(key + ":" + "\"\"");
				} else {
					if (value instanceof Number || value instanceof Boolean) {
						buf.append(key + ":" + value.toString());
					} else if (value instanceof Map) {
						buf.append(key + ":" + render((Map<String, Object>) value));
					} else if (value instanceof List) {
						buf.append(key + ":" + render((List<Map<String, Object>>) value));
					} else {
						buf.append(key + ":" + "\"" + escapeJS(value.toString()) + "\"");
					}
				}
				buf.append(",");
			}
			buf.delete(buf.length() - 1, buf.length());
			buf.append("}");
		} else {
			buf.append("{}");
		}
		return buf.toString();
	}

	/**
	 * JSON �� Row ���ڿ� ����
	 */
	private static String _jsonRowStr(RecordSet rs, String[] colNms) {
		StringBuilder buf = new StringBuilder();
		if (colNms != null && colNms.length > 0) {
			buf.append("{");
			for (int c = 0; c < colNms.length; c++) {
				Object value = rs.get(colNms[c]);
				String key = "\"" + escapeJS(colNms[c].toLowerCase()) + "\"";

				if (value == null) {
					buf.append(key + ":" + "\"\"");
				} else {
					if (value instanceof Number || value instanceof Boolean) {
						buf.append(key + ":" + value.toString());
					} else {
						buf.append(key + ":" + "\"" + escapeJS(value.toString()) + "\"");
					}
				}
				buf.append(",");
			}
			buf.delete(buf.length() - 1, buf.length());
			buf.append("}");
		} else {
			buf.append("{}");
		}
		return buf.toString();
	}

	private static String _jsonRowStr(ResultSet rs, String[] colNms) {
		StringBuilder buf = new StringBuilder();
		if (colNms.length > 0) {
			buf.append("{");
			for (int c = 0; c < colNms.length; c++) {
				Object value;
				try {
					value = rs.getObject(colNms[c]);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				String key = "\"" + escapeJS(colNms[c].toLowerCase()) + "\"";

				if (value == null) {
					buf.append(key + ":" + "\"\"");
				} else {
					if (value instanceof Number || value instanceof Boolean) {
						buf.append(key + ":" + value.toString());
					} else {
						buf.append(key + ":" + "\"" + escapeJS(value.toString()) + "\"");
					}
				}
				buf.append(",");
			}
			buf.delete(buf.length() - 1, buf.length());
			buf.append("}");
		} else {
			buf.append("{}");
		}
		return buf.toString();
	}
}