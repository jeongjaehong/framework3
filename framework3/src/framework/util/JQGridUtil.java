/* 
 * @(#)JQGridUtil.java
 */
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

import framework.db.RecordSet;

/**
 * jqGrid �� �̿��Ͽ� ������ �� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class JQGridUtil {

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
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
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
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
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
		StringBuilder buffer = new StringBuilder();
		if (rs == null) {
			return null;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		buffer.append("{");
		int rowCount = 0;
		buffer.append("\"rows\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append(",");
			}
			buffer.append("{");
			buffer.append("\"id\":" + rowCount + ",");
			buffer.append("\"cell\":" + _jqGridRowStr(rs, colNms));
			buffer.append("}");
		}
		buffer.append("],");
		buffer.append("\"total\":" + totalPage + ",");
		buffer.append("\"page\":" + currentPage + ",");
		buffer.append("\"records\":" + totalCount);
		buffer.append("}");
		return buffer.toString();
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
		StringBuilder buffer = new StringBuilder();
		if (rs == null) {
			return null;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		rs.moveRow(0);
		buffer.append("{");
		int rowCount = 0;
		buffer.append("\"rows\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append(",");
			}
			buffer.append("{");
			buffer.append("\"id\":" + rowCount + ",");
			buffer.append("\"cell\":" + _jqGridRowStr(rs, colNames));
			buffer.append("}");
		}
		buffer.append("],");
		buffer.append("\"total\":" + totalPage + ",");
		buffer.append("\"page\":" + currentPage + ",");
		buffer.append("\"records\":" + totalCount);
		buffer.append("}");
		return buffer.toString();
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
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		try {
			PrintWriter pw = response.getWriter();
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] colNms = new String[count];
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
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
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
			return null;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		StringBuilder buffer = new StringBuilder();
		try {
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] colNms = new String[count];
				int rowCount = 0;
				buffer.append("{");
				buffer.append("\"rows\":[");
				while (rs.next()) {
					if (rowCount++ > 0) {
						buffer.append(",");
					}
					buffer.append("{");
					buffer.append("\"id\":" + rowCount + ",");
					buffer.append("\"cell\":" + _jqGridRowStr(rs, colNms));
					buffer.append("}");
				}
				buffer.append("],");
				buffer.append("\"total\":" + totalPage + ",");
				buffer.append("\"page\":" + currentPage + ",");
				buffer.append("\"records\":" + totalCount);
				buffer.append("}");
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
			return null;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		StringBuilder buffer = new StringBuilder();
		try {
			try {
				int rowCount = 0;
				buffer.append("{");
				buffer.append("\"rows\":[");
				while (rs.next()) {
					if (rowCount++ > 0) {
						buffer.append(",");
					}
					buffer.append("{");
					buffer.append("\"id\":" + rowCount + ",");
					buffer.append("\"cell\":" + _jqGridRowStr(rs, colNames));
					buffer.append("}");
				}
				buffer.append("],");
				buffer.append("\"total\":" + totalPage + ",");
				buffer.append("\"page\":" + currentPage + ",");
				buffer.append("\"records\":" + totalCount);
				buffer.append("}");
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
			return null;
		}
		rowsPerPage = ((rowsPerPage == 0) ? 1 : rowsPerPage);
		int totalPage = totalCount / rowsPerPage;
		if (totalCount % rowsPerPage != 0)
			totalPage += 1;
		StringBuilder buffer = new StringBuilder();
		int rowCount = 0;
		buffer.append("{");
		buffer.append("\"rows\":");
		if (mapList.size() > 0) {
			buffer.append("[");
			for (Map<String, Object> map : mapList) {
				rowCount++;
				buffer.append("{");
				buffer.append("\"id\":" + rowCount + ",");
				buffer.append("\"cell\":" + _jqGridRowStr(map));
				buffer.append("}");
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("],");
		} else {
			buffer.append("[],");
		}
		buffer.append("\"total\":" + totalPage + ",");
		buffer.append("\"page\":" + currentPage + ",");
		buffer.append("\"records\":" + totalCount);
		buffer.append("}");
		return buffer.toString();
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
	 * jqGrid �� Row ���ڿ� ����
	 */
	private static String _jqGridRowStr(RecordSet rs, String[] colNms) {
		StringBuilder buffer = new StringBuilder();
		if (colNms.length > 0) {
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

	private static String _jqGridRowStr(ResultSet rs, String[] colNms) {
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
