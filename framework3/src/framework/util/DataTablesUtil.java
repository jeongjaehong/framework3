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
 * DataTables �� �̿��Ͽ� ������ �� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class DataTablesUtil {
	protected static final Log logger = LogFactory.getLog(framework.util.DataTablesUtil.class);

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private DataTablesUtil() {
	}

	/**
	 * RecordSet�� DataTables �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� DataTables �������� ����ϴ� ��� : DataTablesUtil.render(response, rs)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs DataTables �������� ��ȯ�� RecordSet ��ü
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
		pw.print("{");
		int rowCount = 0;
		pw.print("\"aaData\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(",");
			}
			pw.print(_dataTablesRowStr(rs, colNms));
		}
		pw.print("]");
		pw.print("}");
		return rowCount;
	}

	/**
	 * RecordSet�� DataTables �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� DataTables �������� ����ϴ� ��� : DataTablesUtil.render(response, rs, new String[] { "col1", "col2" })
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs DataTables �������� ��ȯ�� RecordSet ��ü
	 * @param colNames �÷��̸� �迭
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, RecordSet rs, String[] colNames) {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw;
		try {
			pw = response.getWriter();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		rs.moveRow(0);
		pw.print("{");
		int rowCount = 0;
		pw.print("\"aaData\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(",");
			}
			pw.print(_dataTablesRowStr(rs, colNames));
		}
		pw.print("]");
		pw.print("}");
		return rowCount;
	}

	/**
	 * RecordSet�� DataTables �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� DataTables �������� ��ȯ�ϴ� ��� : String json = DataTablesUtil.render(rs)
	 * @param rs DataTables �������� ��ȯ�� RecordSet ��ü
	 * @return DataTables �������� ��ȯ�� ���ڿ�
	 */
	public static String render(RecordSet rs) {
		StringBuilder buffer = new StringBuilder();
		if (rs == null) {
			return "";
		}
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		buffer.append("{");
		int rowCount = 0;
		buffer.append("\"aaData\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append(",");
			}
			buffer.append(_dataTablesRowStr(rs, colNms));
		}
		buffer.append("]");
		buffer.append("}");
		return buffer.toString();
	}

	/**
	 * RecordSet�� DataTables �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� DataTables �������� ��ȯ�ϴ� ��� : String json = DataTablesUtil.render(rs, new String[] { "col1", "col2" })
	 * @param rs DataTables �������� ��ȯ�� RecordSet ��ü
	 * @param colNames �÷��̸� �迭
	 * @return DataTables �������� ��ȯ�� ���ڿ�
	 */
	public static String render(RecordSet rs, String[] colNames) {
		StringBuilder buffer = new StringBuilder();
		if (rs == null) {
			return "";
		}
		rs.moveRow(0);
		buffer.append("{");
		int rowCount = 0;
		buffer.append("\"aaData\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append(",");
			}
			buffer.append(_dataTablesRowStr(rs, colNames));
		}
		buffer.append("]");
		buffer.append("}");
		return buffer.toString();
	}

	/**
	 * ResultSet�� DataTables �������� ����Ѵ�. 
	 * <br>
	 * ex) response�� rs�� DataTables �������� ����ϴ� ��� : DataTablesUtil.render(response, rs)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs DataTables �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
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
				pw.print("{");
				int rowCount = 0;
				pw.print("\"aaData\":[");
				while (rs.next()) {
					if (rowCount++ > 0) {
						pw.print(",");
					}
					pw.print(_dataTablesRowStr(rs, colNms));
				}
				pw.print("]");
				pw.print("}");
				return rowCount;
			} finally {
				Statement stmt = null;
				try {
					stmt = rs.getStatement();
				} catch (SQLException e) {
					logger.error(e);
				}
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						logger.error(e);
					}
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						logger.error(e);
					}
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ResultSet�� DataTables �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� DataTables �������� ����ϴ� ��� : DataTablesUtil.render(response, rs, new String[] { "col1", "col2" })
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs DataTables �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param colNames �÷��̸� �迭
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, ResultSet rs, String[] colNames) {
		if (rs == null) {
			return 0;
		}
		try {
			PrintWriter pw = response.getWriter();
			try {
				pw.print("{");
				int rowCount = 0;
				pw.print("\"aaData\":[");
				while (rs.next()) {
					if (rowCount++ > 0) {
						pw.print(",");
					}
					pw.print(_dataTablesRowStr(rs, colNames));
				}
				pw.print("]");
				pw.print("}");
				return rowCount;
			} finally {
				Statement stmt = null;
				try {
					stmt = rs.getStatement();
				} catch (SQLException e) {
					logger.error(e);
				}
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						logger.error(e);
					}
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						logger.error(e);
					}
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ResultSet�� DataTables �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� DataTables �������� ��ȯ�ϴ� ��� : String json = DataTablesUtil.render(rs)
	 * @param rs DataTables �������� ��ȯ�� ResultSet ��ü
	 * @return DataTables �������� ��ȯ�� ���ڿ�
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
				int rowCount = 0;
				buffer.append("{");
				buffer.append("\"aaData\":[");
				while (rs.next()) {
					if (rowCount++ > 0) {
						buffer.append(",");
					}
					buffer.append(_dataTablesRowStr(rs, colNms));
				}
				buffer.append("]");
				buffer.append("}");
			} finally {
				Statement stmt = null;
				try {
					stmt = rs.getStatement();
				} catch (SQLException e) {
					logger.error(e);
				}
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						logger.error(e);
					}
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						logger.error(e);
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return buffer.toString();
	}

	/**
	 * ResultSet�� DataTables �������� ��ȯ�Ѵ�. 
	 * <br>
	 * ex) rs�� DataTables �������� ��ȯ�ϴ� ��� : String json = DataTablesUtil.render(rs, new String[] { "col1", "col2" })
	 * @param rs DataTables �������� ��ȯ�� ResultSet ��ü
	 * @param colNames �÷��̸� �迭
	 * @return DataTables �������� ��ȯ�� ���ڿ�
	 */
	public static String render(ResultSet rs, String[] colNames) {
		if (rs == null) {
			return "";
		}
		StringBuilder buffer = new StringBuilder();
		try {
			try {
				int rowCount = 0;
				buffer.append("{");
				buffer.append("\"aaData\":[");
				while (rs.next()) {
					if (rowCount++ > 0) {
						buffer.append(",");
					}
					buffer.append(_dataTablesRowStr(rs, colNames));
				}
				buffer.append("]");
				buffer.append("}");
			} finally {
				Statement stmt = null;
				try {
					stmt = rs.getStatement();
				} catch (SQLException e) {
					logger.error(e);
				}
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						logger.error(e);
					}
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						logger.error(e);
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return buffer.toString();
	}

	/**
	 * List��ü�� DataTables �������� ��ȯ�Ѵ�. DataTablesUtil.format�� ����
	 * <br>
	 * ex1) mapList�� DataTables �������� ��ȯ�ϴ� ��� : String json = DataTablesUtil.render(mapList)
	 * @param mapList ��ȯ�� List��ü
	 * @return DataTables �������� ��ȯ�� ���ڿ�
	 */
	public static String render(List<Map<String, Object>> mapList) {
		if (mapList == null) {
			return "";
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append("{");
		buffer.append("\"aaData\":");
		if (mapList.size() > 0) {
			buffer.append("[");
			for (Map<String, Object> map : mapList) {
				buffer.append(_dataTablesRowStr(map));
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("]");
		} else {
			buffer.append("[]");
		}
		buffer.append("}");
		return buffer.toString();
	}

	/**
	 * �ڹٽ�ũ��Ʈ�� Ư���ϰ� �νĵǴ� ���ڵ��� JSON� ����ϱ� ���� ��ȯ�Ͽ��ش�.
	 * @param str ��ȯ�� ���ڿ�
	 */
	public static String escapeJS(String str) {
		if (str == null) {
			return "";
		}
		return str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"").replaceAll("\r\n", "\\\\n").replaceAll("\n", "\\\\n");
	}

	////////////////////////////////////////////////////////////////////////////////////////// Private �޼ҵ�

	/**
	 * DataTables �� Row ���ڿ� ����
	 */
	private static String _dataTablesRowStr(Map<String, Object> map) {
		StringBuilder buffer = new StringBuilder();
		if (map.entrySet().size() > 0) {
			buffer.append("[");
			for (Entry<String, Object> entry : map.entrySet()) {
				Object value = entry.getValue();
				if (value == null) {
					buffer.append("\"\"");
				} else {
					buffer.append("\"" + escapeJS(value.toString()) + "\"");
				}
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
	 * DataTables �� Row ���ڿ� ����
	 */
	private static String _dataTablesRowStr(RecordSet rs, String[] colNms) {
		StringBuilder buffer = new StringBuilder();
		if (colNms != null && colNms.length > 0) {
			buffer.append("[");
			for (int c = 0; c < colNms.length; c++) {
				Object value = rs.get(colNms[c].toUpperCase());
				if (value == null) {
					buffer.append("\"\"");
				} else {
					buffer.append("\"" + escapeJS(value.toString()) + "\"");
				}
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("]");
		} else {
			buffer.append("[]");
		}
		return buffer.toString();
	}

	private static String _dataTablesRowStr(ResultSet rs, String[] colNms) {
		StringBuilder buffer = new StringBuilder();
		if (colNms.length > 0) {
			buffer.append("[");
			for (int c = 0; c < colNms.length; c++) {
				Object value;
				try {
					value = rs.getObject(colNms[c].toUpperCase());
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				if (value == null) {
					buffer.append("\"\"");
				} else {
					buffer.append("\"" + escapeJS(value.toString()) + "\"");
				}
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("]");
		} else {
			buffer.append("[]");
		}
		return buffer.toString();
	}
}