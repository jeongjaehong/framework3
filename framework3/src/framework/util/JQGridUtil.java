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
 * jqGrid �� �̿��Ͽ� ������ �� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class JQGridUtil {
	protected static final Log logger = LogFactory.getLog(framework.util.JQGridUtil.class);

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private JQGridUtil() {
	}

	/**
	 * RecordSet�� jqGrid �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� jqGrid �������� ����ϴ� ��� : JQGridUtil.render(response, rs, totalCount, currentPage, rowsPerPage)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs jqGrid �������� ��ȯ�� RecordSet ��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, RecordSet rs, int totalCount, int currentPage, int rowsPerPage) {
		if (rs == null) {
			return 0;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0) {
			totalPage += 1;
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
		pw.print("\"rows\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(",");
			}
			pw.print("{");
			pw.print("\"id\":" + rowCount + ",");
			pw.print("\"cell\":" + _jqGridRowStr(rs, colNms));
			pw.print("}");
		}
		pw.print("],");
		pw.print("\"total\":" + totalPage + ",");
		pw.print("\"page\":" + currentPage + ",");
		pw.print("\"records\":" + totalCount);
		pw.print("}");
		return rowCount;
	}

	/**
	 * RecordSet�� jqGrid �������� ����Ѵ�. 
	 * <br>
	 * ex) response�� rs�� jqGrid �������� ����ϴ� ��� : JQGridUtil.render(response, rs, totalCount, currentPage, rowsPerPage, new String[] { "col1", "col2" })
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs jqGrid �������� ��ȯ�� RecordSet ��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @param colNames �÷��̸� �迭
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, RecordSet rs, int totalCount, int currentPage, int rowsPerPage, String[] colNames) {
		if (rs == null) {
			return 0;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0) {
			totalPage += 1;
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
		pw.print("\"rows\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(",");
			}
			pw.print("{");
			pw.print("\"id\":" + rowCount + ",");
			pw.print("\"cell\":" + _jqGridRowStr(rs, colNames));
			pw.print("}");
		}
		pw.print("],");
		pw.print("\"total\":" + totalPage + ",");
		pw.print("\"page\":" + currentPage + ",");
		pw.print("\"records\":" + totalCount);
		pw.print("}");
		return rowCount;
	}

	/**
	 * RecordSet�� jqGrid �������� ��ȯ�Ѵ�. 
	 * <br>
	 * ex) rs�� jqGrid �������� ��ȯ�ϴ� ��� : String json = JQGridUtil.render(rs, totalCount, currentPage, rowsPerPage)
	 * @param rs jqGrid �������� ��ȯ�� RecordSet ��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @return jqGrid �������� ��ȯ�� ���ڿ�
	 */
	public static String render(RecordSet rs, int totalCount, int currentPage, int rowsPerPage) {
		if (rs == null) {
			return "";
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0) {
			totalPage += 1;
		}
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		StringBuilder buf = new StringBuilder();
		buf.append("{");
		int rowCount = 0;
		buf.append("\"rows\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buf.append(",");
			}
			buf.append("{");
			buf.append("\"id\":" + rowCount + ",");
			buf.append("\"cell\":" + _jqGridRowStr(rs, colNms));
			buf.append("}");
		}
		buf.append("],");
		buf.append("\"total\":" + totalPage + ",");
		buf.append("\"page\":" + currentPage + ",");
		buf.append("\"records\":" + totalCount);
		buf.append("}");
		return buf.toString();
	}

	/**
	 * RecordSet�� jqGrid �������� ��ȯ�Ѵ�. 
	 * <br>
	 * ex) rs�� jqGrid �������� ��ȯ�ϴ� ��� : String json = JQGridUtil.render(rs, totalCount, currentPage, rowsPerPage, new String[] { "col1", "col2" })
	 * @param rs jqGrid �������� ��ȯ�� RecordSet ��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @param colNames �÷��̸� �迭
	 * @return jqGrid �������� ��ȯ�� ���ڿ�
	 */
	public static String render(RecordSet rs, int totalCount, int currentPage, int rowsPerPage, String[] colNames) {
		if (rs == null) {
			return "";
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0) {
			totalPage += 1;
		}
		rs.moveRow(0);
		StringBuilder buf = new StringBuilder();
		buf.append("{");
		int rowCount = 0;
		buf.append("\"rows\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buf.append(",");
			}
			buf.append("{");
			buf.append("\"id\":" + rowCount + ",");
			buf.append("\"cell\":" + _jqGridRowStr(rs, colNames));
			buf.append("}");
		}
		buf.append("],");
		buf.append("\"total\":" + totalPage + ",");
		buf.append("\"page\":" + currentPage + ",");
		buf.append("\"records\":" + totalCount);
		buf.append("}");
		return buf.toString();
	}

	/**
	 * ResultSet�� jqGrid �������� ����Ѵ�.
	 * <br>
	 * ex) response�� rs�� jqGrid �������� ����ϴ� ��� : JQGridUtil.render(response, rs, totalCount, currentPage, rowsPerPage)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs jqGrid �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, ResultSet rs, int totalCount, int currentPage, int rowsPerPage) {
		if (rs == null) {
			return 0;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0) {
			totalPage += 1;
		}
		try {
			PrintWriter pw = response.getWriter();
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int cnt = rsmd.getColumnCount();
				String[] colNms = new String[cnt];
				pw.print("{");
				int rowCount = 0;
				pw.print("\"rows\":[");
				while (rs.next()) {
					if (rowCount++ > 0) {
						pw.print(",");
					}
					pw.print("{");
					pw.print("\"id\":" + rowCount + ",");
					pw.print("\"cell\":" + _jqGridRowStr(rs, colNms));
					pw.print("}");
				}
				pw.print("],");
				pw.print("\"total\":" + totalPage + ",");
				pw.print("\"page\":" + currentPage + ",");
				pw.print("\"records\":" + totalCount);
				pw.print("}");
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
	 * ResultSet�� jqGrid �������� ����Ѵ�. 
	 * <br>
	 * ex) response�� rs�� jqGrid �������� ����ϴ� ��� : JQGridUtil.render(response, rs, totalCount, currentPage, rowsPerPage, new String[] { "col1", "col2" })
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs jqGrid �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @param colNames �÷��̸� �迭
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, ResultSet rs, int totalCount, int currentPage, int rowsPerPage, String[] colNames) {
		if (rs == null) {
			return 0;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0) {
			totalPage += 1;
		}
		try {
			PrintWriter pw = response.getWriter();
			try {
				pw.print("{");
				int rowCount = 0;
				pw.print("\"rows\":[");
				while (rs.next()) {
					if (rowCount++ > 0) {
						pw.print(",");
					}
					pw.print("{");
					pw.print("\"id\":" + rowCount + ",");
					pw.print("\"cell\":" + _jqGridRowStr(rs, colNames));
					pw.print("}");
				}
				pw.print("],");
				pw.print("\"total\":" + totalPage + ",");
				pw.print("\"page\":" + currentPage + ",");
				pw.print("\"records\":" + totalCount);
				pw.print("}");
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
	 * ResultSet�� jqGrid �������� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rs�� jqGrid �������� ��ȯ�ϴ� ��� : String json = JQGridUtil.render(rs, totalCount, currentPage, rowsPerPage)
	 * @param rs jqGrid �������� ��ȯ�� ResultSet ��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @return jqGrid �������� ��ȯ�� ���ڿ�
	 */
	public static String render(ResultSet rs, int totalCount, int currentPage, int rowsPerPage) {
		if (rs == null) {
			return "";
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0) {
			totalPage += 1;
		}
		StringBuilder buf = new StringBuilder();
		try {
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int cnt = rsmd.getColumnCount();
				String[] colNms = new String[cnt];
				int rowCount = 0;
				buf.append("{");
				buf.append("\"rows\":[");
				while (rs.next()) {
					if (rowCount++ > 0) {
						buf.append(",");
					}
					buf.append("{");
					buf.append("\"id\":" + rowCount + ",");
					buf.append("\"cell\":" + _jqGridRowStr(rs, colNms));
					buf.append("}");
				}
				buf.append("],");
				buf.append("\"total\":" + totalPage + ",");
				buf.append("\"page\":" + currentPage + ",");
				buf.append("\"records\":" + totalCount);
				buf.append("}");
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
		return buf.toString();
	}

	/**
	 * ResultSet�� jqGrid �������� ��ȯ�Ѵ�. 
	 * <br>
	 * ex) rs�� jqGrid �������� ��ȯ�ϴ� ��� : String json = JQGridUtil.render(rs, totalCount, currentPage, rowsPerPage, new String[] { "col1", "col2" })
	 * @param rs jqGrid �������� ��ȯ�� ResultSet ��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @param colNames �÷��̸� �迭
	 * @return jqGrid �������� ��ȯ�� ���ڿ�
	 */
	public static String render(ResultSet rs, int totalCount, int currentPage, int rowsPerPage, String[] colNames) {
		if (rs == null) {
			return "";
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0) {
			totalPage += 1;
		}
		StringBuilder buf = new StringBuilder();
		try {
			try {
				int rowCount = 0;
				buf.append("{");
				buf.append("\"rows\":[");
				while (rs.next()) {
					if (rowCount++ > 0) {
						buf.append(",");
					}
					buf.append("{");
					buf.append("\"id\":" + rowCount + ",");
					buf.append("\"cell\":" + _jqGridRowStr(rs, colNames));
					buf.append("}");
				}
				buf.append("],");
				buf.append("\"total\":" + totalPage + ",");
				buf.append("\"page\":" + currentPage + ",");
				buf.append("\"records\":" + totalCount);
				buf.append("}");
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
		return buf.toString();
	}

	/**
	 * List��ü�� jqGrid �������� ��ȯ�Ѵ�. 
	 * <br>
	 * ex1) mapList�� jqGrid �������� ��ȯ�ϴ� ��� : String json = JQGridUtil.render(mapList, totalCount, currentPage, rowsPerPage)
	 * @param mapList ��ȯ�� List��ü
	 * @param totalCount ��ü��������
	 * @param currentPage ������������
	 * @param rowsPerPage ���������� ǥ���� �ο��
	 * @return jqGrid �������� ��ȯ�� ���ڿ�
	 */
	public static String render(List<Map<String, Object>> mapList, int totalCount, int currentPage, int rowsPerPage) {
		if (mapList == null) {
			return "";
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0) {
			totalPage += 1;
		}
		StringBuilder buf = new StringBuilder();
		int rowCount = 0;
		buf.append("{");
		buf.append("\"rows\":");
		if (mapList.size() > 0) {
			buf.append("[");
			for (Map<String, Object> map : mapList) {
				rowCount++;
				buf.append("{");
				buf.append("\"id\":" + rowCount + ",");
				buf.append("\"cell\":" + _jqGridRowStr(map));
				buf.append("}");
				buf.append(",");
			}
			buf.delete(buf.length() - 1, buf.length());
			buf.append("],");
		} else {
			buf.append("[],");
		}
		buf.append("\"total\":" + totalPage + ",");
		buf.append("\"page\":" + currentPage + ",");
		buf.append("\"records\":" + totalCount);
		buf.append("}");
		return buf.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// ��ƿ��Ƽ

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
	 * jqGrid �� Row ���ڿ� ����
	 */
	private static String _jqGridRowStr(Map<String, Object> map) {
		StringBuilder buf = new StringBuilder();
		if (map.entrySet().size() > 0) {
			buf.append("[");
			for (Entry<String, Object> entry : map.entrySet()) {
				Object value = entry.getValue();
				if (value == null) {
					buf.append("\"\"");
				} else {
					buf.append("\"" + escapeJS(value.toString()) + "\"");
				}
				buf.append(",");
			}
			buf.delete(buf.length() - 1, buf.length());
			buf.append("]");
		} else {
			buf.append("[]");
		}
		return buf.toString();
	}

	/**
	 * jqGrid �� Row ���ڿ� ����
	 */
	private static String _jqGridRowStr(RecordSet rs, String[] colNms) {
		StringBuilder buf = new StringBuilder();
		if (colNms != null && colNms.length > 0) {
			buf.append("[");
			for (int c = 0; c < colNms.length; c++) {
				Object value = rs.get(colNms[c].toUpperCase());
				if (value == null) {
					buf.append("\"\"");
				} else {
					buf.append("\"" + escapeJS(value.toString()) + "\"");
				}
				buf.append(",");
			}
			buf.delete(buf.length() - 1, buf.length());
			buf.append("]");
		} else {
			buf.append("[]");
		}
		return buf.toString();
	}

	private static String _jqGridRowStr(ResultSet rs, String[] colNms) {
		StringBuilder buf = new StringBuilder();
		if (colNms.length > 0) {
			buf.append("[");
			for (int c = 0; c < colNms.length; c++) {
				Object value;
				try {
					value = rs.getObject(colNms[c].toUpperCase());
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				if (value == null) {
					buf.append("\"\"");
				} else {
					buf.append("\"" + escapeJS(value.toString()) + "\"");
				}
				buf.append(",");
			}
			buf.delete(buf.length() - 1, buf.length());
			buf.append("]");
		} else {
			buf.append("[]");
		}
		return buf.toString();
	}
}