/** 
 * @(#)GauceUtil.java
 */
package framework.util;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gauce.GauceDataColumn;
import com.gauce.GauceDataRow;
import com.gauce.GauceDataSet;
import com.gauce.GauceException;
import com.gauce.http.HttpGauceRequest;
import com.gauce.http.HttpGauceResponse;
import com.gauce.io.GauceInputStream;
import com.gauce.io.GauceOutputStream;

import framework.action.Params;
import framework.db.RecordSet;

/**
 * ���콺�� �̿��Ͽ� ������ �� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class GauceUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private GauceUtil() {
	}

	/**
	 * RecordSet�� ���콺 ����Ÿ������ ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * <br>
	 * ex) rs�� ���콺 �����ͼ����� ��ȯ�Ͽ� response�� �����ϴ� ��� => GauceUtil.render(response, rs)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs ���콺 ����Ÿ������ ��ȯ�� RecordSet ��ü
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, RecordSet rs) {
		return _setRecordSet(response, rs);
	}

	/**
	 * RecordSet�� ���콺 ����Ÿ��(��Ī�� datasetName ���� ��)���� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * <br>
	 * ex) rs�� ���콺 �����ͼ�(��Ī�� result)���� ��ȯ�Ͽ� response�� �����ϴ� ��� => GauceUtil.render(response, "result", rs)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param datasetName ����Ÿ�� �̸�
	 * @param rs ���콺 ����Ÿ������ ��ȯ�� RecordSet ��ü
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, String datasetName, RecordSet rs) {
		return _setRecordSet(response, datasetName, rs);
	}

	/**
	 * RecordSet�� ���콺 ����Ÿ��(��Ī�� datasetNameArray ���� ��)���� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * <br>
	 * ex) rs1�� rs2�� ���콺 �����ͼ����� ��ȯ�Ͽ� response�� �����ϴ� ��� => GauceUtil.render(response, new String[] { "result1", "result2" }, new RecordSet[] { rs1, rs2 })
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param datasetNameArray ����Ÿ�� �̸� �迭
	 * @param rsArray ���콺 ����Ÿ������ ��ȯ�� RecordSet ��ü �迭
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, String[] datasetNameArray, RecordSet[] rsArray) {
		return _setRecordSet(response, datasetNameArray, rsArray);
	}

	/**
	 * RecordSet�� ���ڷ� �Ѿ�� ���콺 ����Ÿ������ ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. 
	 * <br>
	 * ex) rs�� ���콺 �����ͼ����� ��ȯ�Ͽ� response�� �����ϴ� ��� => GauceUtil.render(response, dSet, rs)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param dSet ����Ÿ��
	 * @param rs ���콺 ����Ÿ������ ��ȯ�� RecordSet ��ü
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, GauceDataSet dSet, RecordSet rs) {
		return _setRecordSet(response, dSet, rs);
	}

	/**
	 * RecordSet�� ���ڷ� �Ѿ�� ���콺 ����Ÿ������ ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. 
	 * <br>
	 * ex) rs1�� rs2�� ���콺 �����ͼ����� ��ȯ�Ͽ� response�� �����ϴ� ��� => GauceUtil.render(response, new GauceDataSet[] { dSet1, dSet2 }, new RecordSet[] { rs1, rs2 })
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param dSetArray ����Ÿ�� �迭
	 * @param rsArray ���콺 ����Ÿ������ ��ȯ�� RecordSet ��ü �迭
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, GauceDataSet[] dSetArray, RecordSet[] rsArray) {
		return _setRecordSet(response, dSetArray, rsArray);
	}

	/**
	 * ResultSet�� ���콺 ����Ÿ������ ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. 
	 * <br>
	 * ex) rs�� ���콺 �����ͼ����� ��ȯ�Ͽ� response�� �����ϴ� ��� => GauceUtil.render(response, rs)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs ���콺 ����Ÿ������ ��ȯ�� RecordSet ��ü
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, ResultSet rs) {
		return _setResultSet(response, rs);
	}

	/**
	 * ResultSet�� ���콺 ����Ÿ��(��Ī�� datasetName ���� ��)���� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. 
	 * <br>
	 * ex) rs�� ���콺 �����ͼ�(��Ī�� result)���� ��ȯ�Ͽ� response�� �����ϴ� ��� => GauceUtil.render(response, "result", rs)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param datasetName ����Ÿ�� �̸�
	 * @param rs ���콺 ����Ÿ������ ��ȯ�� RecordSet ��ü
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, String datasetName, ResultSet rs) {
		return _setResultSet(response, datasetName, rs);
	}

	/**
	 * ResultSet�� ���콺 ����Ÿ��(��Ī�� datasetNameArray ���� ��)���� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. 
	 * <br>
	 * ex) rs1�� rs2�� ���콺 �����ͼ����� ��ȯ�Ͽ� response�� �����ϴ� ��� => GauceUtil.render(response, new String[] { "result1", "result2" }, new ResultSet[] { rs1, rs2 })
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param datasetNameArray ����Ÿ�� �̸� �迭
	 * @param rsArray ���콺 ����Ÿ������ ��ȯ�� ResultSet ��ü �迭, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, String[] datasetNameArray, ResultSet[] rsArray) {
		return _setResultSet(response, datasetNameArray, rsArray);
	}

	/**
	 * ResultSet�� ���ڷ� �Ѿ�� ���콺 ����Ÿ������ ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. 
	 * <br>
	 * ex) rs�� ���콺 �����ͼ����� ��ȯ�Ͽ� response�� �����ϴ� ��� => GauceUtil.render(response, dSet, rs)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param dSet ����Ÿ��
	 * @param rs ���콺 ����Ÿ������ ��ȯ�� RecordSet ��ü
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, GauceDataSet dSet, ResultSet rs) {
		return _setResultSet(response, dSet, rs);
	}

	/**
	 * ResultSet�� ���ڷ� �Ѿ�� ���콺 ����Ÿ������ ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. 
	 * <br>
	 * ex) rs1�� rs2�� ���콺 �����ͼ����� ��ȯ�Ͽ� response�� �����ϴ� ��� => GauceUtil.render(response, new GauceDataSet[] { dSet1, dSet2 }, new ResultSet[] { rs1, rs2 })
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param dSetArray ����Ÿ�� �̸� �迭
	 * @param rsArray ���콺 ����Ÿ������ ��ȯ�� ResultSet ��ü �迭, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, GauceDataSet[] dSetArray, ResultSet[] rsArray) {
		return _setResultSet(response, dSetArray, rsArray);
	}

	/**
	 * �ش� HttpServletRequest�� ���� GauceInputStream�� ��ȯ�޴´�
	 * <br>
	 * ex) ��û��ü�� ���� ���콺 �Է½�Ʈ���� ���ϴ� ��� => GauceInputStream gis = GauceUtil.getGIS(request)
	 * @param request Ŭ���̾�Ʈ���� ��û�� Request ��ü
	 * @return ��û��ü���� ���� GauceInputStream ��ü
	 */
	public static GauceInputStream getGIS(HttpServletRequest request) {
		GauceInputStream inputGis = null;
		try {
			inputGis = ((HttpGauceRequest) request).getGauceInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return inputGis;
	}

	/**
	 * �ش� HttpServletResponse�� ���� GauceOutputStream�� ��ȯ�޴´�
	 * <br>
	 * ex) ���䰴ü�� ���� ���콺 ��½�Ʈ���� ���ϴ� ��� => GauceOutputStream gos = GauceUtil.getGOS(response)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @return ���䰴ü���� ���� GauceOutputStream ��ü
	 */
	public static GauceOutputStream getGOS(HttpServletResponse response) {
		GauceOutputStream inputGos = null;
		try {
			inputGos = ((HttpGauceResponse) response).getGauceOutputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return inputGos;
	}

	/**
	 * ���ǰ�ü�� null �� ��� Ŭ���̾�Ʈ���� ������ ������ �˸��� ���� ���ܸ� �����Ѵ�.
	 * <br>
	 * ex) GauceUtil.setSessionException(getResponse())
	 * @param response response Ŭ���̾�Ʈ�� ������ Response ��ü
	 */
	public static void setSessionException(HttpServletResponse response) {
		try {
			((HttpGauceResponse) response).addException(new GauceException("SESSION", "0000", "OUT"));
			((HttpGauceResponse) response).getGauceOutputStream().close();
		} catch (IOException e) {
		}
	}

	/**
	 * Ŭ���̾�Ʈ���� ���콺 ���ܸ� �����Ѵ�.
	 * <br>
	 * ex) GauceUtil.setException(new GauceException("Native", "9999", e.toString()), getResponse())
	 * @param exception Ŭ���̾�Ʈ�� ������ GauceException ��ü
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 */
	public static void setException(GauceException exception, HttpServletResponse response) {
		try {
			((HttpGauceResponse) response).addException(exception);
			((HttpGauceResponse) response).getGauceOutputStream().close();
		} catch (IOException e) {
		}
	}

	/**
	 * �ش� GauceDataSet�� ���� Box�� ��ȯ�޴´�
	 * <br>
	 * ex) GauceDataSet���� ���� Box�� ���ϴ� ��� => Box box = GauceUtil.getBox(dSet)
	 * @param dSet Box�� ��ȯ�� GauceDataSet ��ü
	 * @return GauceDataSet���� ���� Box ��ü
	 */
	public static Params getParam(GauceDataSet dSet) {
		if (dSet.getDataRowCnt() != 1) { // row ���� 1���� �ƴϸ� �߸��� ����
			throw new IllegalArgumentException("row ���� 1�� �̾�� �մϴ�.");
		}
		Params box = new Params("gauce");
		GauceDataRow dRow = dSet.getDataRow(0);
		for (GauceDataColumn column : dSet.getDataColumns()) {
			String key = column.getColName();
			box.put(key, new String[] { dRow.getColumnValue(dSet.indexOfColumn(key)).toString() });
		}
		return box;
	}

	//////////////////////////////////////////////////////////////////////////////////////// Private �޼ҵ�
	/**
	 * RecordSet�� ���콺 ����Ÿ������ ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 */
	private static int _setRecordSet(HttpServletResponse response, RecordSet rs) {
		return _setRecordSet(response, "", rs);
	}

	/**
	 * RecordSet�� ���콺 ����Ÿ��(��Ī�� datasetName ���� ��)���� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 */
	private static int _setRecordSet(HttpServletResponse response, String datasetName, RecordSet rs) {
		return _setRecordSet(response, new String[] { datasetName }, new RecordSet[] { rs });
	}

	/**
	 * RecordSet�� ���콺 ����Ÿ��(��Ī�� datasetNameArray ���� ��)���� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 */
	private static int _setRecordSet(HttpServletResponse response, String[] datasetNameArray, RecordSet[] rsArray) {
		if (datasetNameArray.length != rsArray.length)
			throw new IllegalArgumentException("DataSet�̸� ������ RecordSet������ ��ġ���� �ʽ��ϴ�.");
		int rowCount = 0;
		try {
			GauceOutputStream gos = getGOS(response);
			for (int i = 0, len = rsArray.length; i < len; i++) {
				GauceDataSet dSet = new GauceDataSet(datasetNameArray[i]);
				gos.fragment(dSet);
				rowCount += _appendDataSet(dSet, rsArray[i]);
				gos.write(dSet);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return rowCount;
	}

	/**
	 * RecordSet�� ���ڷ� �Ѿ�� ���콺 ����Ÿ������ ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 */
	private static int _setRecordSet(HttpServletResponse response, GauceDataSet dSet, RecordSet rs) {
		return _setRecordSet(response, new GauceDataSet[] { dSet }, new RecordSet[] { rs });
	}

	/**
	 * RecordSet�� ���ڷ� �Ѿ�� ���콺 ����Ÿ������ ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 */
	private static int _setRecordSet(HttpServletResponse response, GauceDataSet[] dSetArray, RecordSet[] rsArray) {
		if (dSetArray.length != rsArray.length)
			throw new IllegalArgumentException("DataSet ������ RecordSet������ ��ġ���� �ʽ��ϴ�.");
		int rowCount = 0;
		try {
			GauceOutputStream gos = getGOS(response);
			for (int i = 0, len = rsArray.length; i < len; i++) {
				GauceDataSet dSet = dSetArray[i];
				gos.fragment(dSet);
				rowCount += _appendDataSet(dSet, rsArray[i]);
				gos.write(dSet);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return rowCount;
	}

	/**
	 * RecordSet�� ���콺 ����Ÿ������ ��ȯ�Ѵ�.
	 */
	private static int _appendDataSet(GauceDataSet dSet, RecordSet rs) {
		if (rs == null) {
			return 0;
		}
		String[] colNms = rs.getColumns();
		String[] colInfo = rs.getColumnsInfo();
		int[] colSize = rs.getColumnsSize();
		int[] colSizeReal = rs.getColumnsSizeReal();
		int[] colScale = rs.getColumnsScale();
		rs.moveRow(0); // rs�� ��ġ�� 1��°�� �̵� 
		int rowCount = 0;
		while (rs.nextRow()) {
			rowCount++;
			_appendRow(dSet, rs, colNms, colInfo, colSize, colSizeReal, colScale);
		}
		return rowCount;
	}

	/**
	 * ResultSet�� ���콺 ����Ÿ������ ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 */
	private static int _setResultSet(HttpServletResponse response, ResultSet rs) {
		return _setResultSet(response, "", rs);
	}

	/**
	 * ResultSet�� ���콺 ����Ÿ��(��Ī�� datasetName ���� ��)���� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 */
	private static int _setResultSet(HttpServletResponse response, String datasetName, ResultSet rs) {
		return _setResultSet(response, new String[] { datasetName }, new ResultSet[] { rs });
	}

	/**
	 * ResultSet�� ���콺 ����Ÿ��(��Ī�� datasetNameArray ���� ��)���� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 */
	private static int _setResultSet(HttpServletResponse response, String[] datasetNameArray, ResultSet[] rsArray) {
		if (datasetNameArray.length != rsArray.length)
			throw new IllegalArgumentException("DataSet�̸� ������ RecordSet������ ��ġ���� �ʽ��ϴ�.");
		int rowCount = 0;
		try {
			GauceOutputStream gos = getGOS(response);
			for (int i = 0, len = rsArray.length; i < len; i++) {
				GauceDataSet dSet = new GauceDataSet(datasetNameArray[i]);
				gos.fragment(dSet);
				rowCount += _appendDataSet(dSet, rsArray[i]);
				gos.write(dSet);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return rowCount;
	}

	/**
	 * ResultSet�� ���ڷ� �Ѿ�� ���콺 ����Ÿ������ ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 */
	private static int _setResultSet(HttpServletResponse response, GauceDataSet dSet, ResultSet rs) {
		return _setResultSet(response, new GauceDataSet[] { dSet }, new ResultSet[] { rs });
	}

	/**
	 * ResultSet�� ���ڷ� �Ѿ�� ���콺 ����Ÿ������ ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 */
	private static int _setResultSet(HttpServletResponse response, GauceDataSet[] dSetArray, ResultSet[] rsArray) {
		if (dSetArray.length != rsArray.length)
			throw new IllegalArgumentException("DataSet ������ RecordSet������ ��ġ���� �ʽ��ϴ�.");
		int rowCount = 0;
		try {
			GauceOutputStream gos = getGOS(response);
			for (int i = 0, len = rsArray.length; i < len; i++) {
				GauceDataSet dSet = dSetArray[i];
				gos.fragment(dSet);
				rowCount += _appendDataSet(dSet, rsArray[i]);
				gos.write(dSet);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return rowCount;
	}

	/**
	 * ResultSet�� ���콺 ����Ÿ������ ��ȯ�Ѵ�.
	 */
	private static int _appendDataSet(GauceDataSet dSet, ResultSet rs) {
		if (rs == null) {
			return 0;
		}
		try {
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] colNms = new String[count];
				String[] colInfo = new String[count];
				int[] colSize = new int[count];
				int[] colSizeReal = new int[count];
				int[] colScale = new int[count];
				for (int i = 1; i <= count; i++) {
					//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
					colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
					//Fiels �� ���� �� Size �߰�
					colSize[i - 1] = rsmd.getColumnDisplaySize(i);
					colSizeReal[i - 1] = rsmd.getPrecision(i);
					colScale[i - 1] = rsmd.getScale(i);
					colInfo[i - 1] = rsmd.getColumnTypeName(i);
				}
				int rowCount = 0;
				while (rs.next()) {
					rowCount++;
					_appendRow(dSet, rs, colNms, colInfo, colSize, colSizeReal, colScale);
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
	 * ���콺 ����Ÿ�¿� RecordSet ���� �߰�
	 */
	private static void _appendRow(GauceDataSet dSet, RecordSet rs, String[] colNms, String[] colInfo, int[] colSize, int[] colSizeReal, int[] colScale) {
		for (int c = 0; c < colNms.length; c++) {
			Object value = rs.get(colNms[c]);
			if (value == null) {
				dSet.put(colNms[c], "", colSize[c], GauceDataColumn.TB_NORMAL);
			} else {
				if (value instanceof Number) {
					double dblSize = colSize[c];
					if (colSizeReal[c] > 0) {
						if (colScale[c] > 0) {
							dblSize = Double.parseDouble("" + colSizeReal[c] + "." + colScale[c]);
						} else {
							dblSize = colSizeReal[c];
						}
					}
					dSet.put(colNms[c], rs.getDouble(colNms[c]), dblSize, GauceDataColumn.TB_DECIMAL);
				} else {
					// �ѱ۱������� ���� colSize[c] �� 2�� ���Ͽ� �ذ�
					dSet.put(colNms[c], (rs.getString(colNms[c])), colSize[c] * 2, GauceDataColumn.TB_NORMAL);
				}
			}
		}
		dSet.heap();
	}

	/**
	 * ���콺 ����Ÿ�¿� ResultSet ���� �߰�
	 */
	private static void _appendRow(GauceDataSet dSet, ResultSet rs, String[] colNms, String[] colInfo, int[] colSize, int[] colSizeReal, int[] colScale) {
		try {
			for (int c = 0; c < colNms.length; c++) {
				Object value = rs.getObject(colNms[c]);
				if (value == null) {
					dSet.put(colNms[c], "", colSize[c], GauceDataColumn.TB_NORMAL);
				} else {
					if (value instanceof Number) {
						double dblSize = colSize[c];
						if (colSizeReal[c] > 0) {
							if (colScale[c] > 0) {
								dblSize = Double.parseDouble("" + colSizeReal[c] + "." + colScale[c]);
							} else {
								dblSize = colSizeReal[c];
							}
						}
						dSet.put(colNms[c], rs.getDouble(colNms[c]), dblSize, GauceDataColumn.TB_DECIMAL);
					} else {
						// �ѱ۱������� ���� colSize[c] �� 2�� ���Ͽ� �ذ�
						dSet.put(colNms[c], (rs.getString(colNms[c])), colSize[c] * 2, GauceDataColumn.TB_NORMAL);
					}
				}
			}
			dSet.heap();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}