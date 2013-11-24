/** 
 * @(#)MiPlatformUtil.java
 */
package framework.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tobesoft.platform.PlatformRequest;
import com.tobesoft.platform.PlatformResponse;
import com.tobesoft.platform.data.ColumnInfo;
import com.tobesoft.platform.data.Dataset;
import com.tobesoft.platform.data.DatasetList;
import com.tobesoft.platform.data.VariableList;

import framework.action.Params;
import framework.db.RecordSet;

/**
 * �����÷����� �̿��Ͽ� ������ �� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class MiPlatformUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private MiPlatformUtil() {
	}

	/**
	 * �̾��� ����� ���̳ʸ� �ۼ��� ����
	 */
	public static int BIN = PlatformRequest.BIN;

	/**
	 * �̾��� ����� XML �ۼ��� ����
	 */
	public static int XML = PlatformRequest.XML;

	/**
	 * Zlib ���� ����� ���̳ʸ� �ۼ��� ����
	 */
	public static int ZLIB_COMP = PlatformRequest.ZLIB_COMP;

	/**
	 * RecordSet�� �����÷��� ����Ÿ��(��Ī�� datasetName ���� ��)���� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * <br>
	 * ex) rs�� �����÷��� �����ͼ�(��Ī�� result)���� ��ȯ�Ͽ� response�� XML �������� �����ϴ� ��� : MiPlatformUtil.render(response, "result", rs, MiPlatformUtil.XML)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param datasetName ����Ÿ�� �̸�
	 * @param rs �����÷��� ����Ÿ������ ��ȯ�� RecordSet ��ü
	 * @param dataFormat �ۼ��� ���� (MiPlatformUtil.BIN, MiPlatformUtil.ZLIB_COMP, MiPlatformUtil.XML)
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, String datasetName, RecordSet rs, int dataFormat) {
		return render(response, new String[] { datasetName }, new RecordSet[] { rs }, dataFormat);
	}

	/**
	 * RecordSet�� �����÷��� ����Ÿ��(��Ī�� datasetNameArray ���� ��)���� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�.
	 * <br>
	 * ex) rs1�� rs2�� �����÷��� �����ͼ����� ��ȯ�Ͽ� response�� XML �������� �����ϴ� ��� : MiPlatformUtil.render(response, new String[] { "result1", "result2" }, new RecordSet[] { rs1, rs2 }, MiPlatformUtil.XML)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param datasetNameArray ����Ÿ�� �̸� �迭
	 * @param rsArray �����÷��� ����Ÿ������ ��ȯ�� RecordSet ��ü �迭
	 * @param dataFormat �ۼ��� ���� (MiPlatformUtil.BIN, MiPlatformUtil.ZLIB_COMP, MiPlatformUtil.XML)
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, String[] datasetNameArray, RecordSet[] rsArray, int dataFormat) {
		if (datasetNameArray.length != rsArray.length)
			throw new IllegalArgumentException("Dataset�̸� ������ RecordSet������ ��ġ���� �ʽ��ϴ�.");
		int rowCount = 0;
		VariableList vl = new VariableList();
		DatasetList dl = new DatasetList();
		try {
			for (int i = 0, len = rsArray.length; i < len; i++) {
				Dataset dSet = new Dataset(datasetNameArray[i], "utf-8", false, false);
				rowCount += _appendDataset(dSet, rsArray[i]);
				dl.addDataset(dSet);
			}
			vl.addStr("ErrorCode", "0");
			vl.addStr("ErrorMsg", "SUCC");
			sendData(response, vl, dl, dataFormat);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return rowCount;
	}

	/**
	 * ResultSet�� �����÷��� ����Ÿ��(��Ī�� datasetName ���� ��)���� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. 
	 * <br>
	 * ex) rs�� �����÷��� �����ͼ�(��Ī�� result)���� ��ȯ�Ͽ� response�� XML �������� �����ϴ� ��� : MiPlatformUtil.render(response, "result", rs, MiPlatformUtil.XML)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param datasetName ����Ÿ�� �̸�
	 * @param rs �����÷��� ����Ÿ������ ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param dataFormat �ۼ��� ���� (MiPlatformUtil.BIN, MiPlatformUtil.ZLIB_COMP, MiPlatformUtil.XML)
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, String datasetName, ResultSet rs, int dataFormat) {
		return render(response, new String[] { datasetName }, new ResultSet[] { rs }, dataFormat);
	}

	/**
	 * ResultSet�� �����÷��� ����Ÿ��(��Ī�� datasetNameArray ���� ��)���� ��ȯ�Ͽ� ���䰴ü�� �����Ѵ�. 
	 * <br>
	 * ex) rs1�� rs2�� �����÷��� �����ͼ����� ��ȯ�Ͽ� response�� XML �������� �����ϴ� ��� : MiPlatformUtil.render(response, new String[] { "result1", "result2" }, new ResultSet[] { rs1, rs2 }, MiPlatformUtil.XML)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param datasetNameArray ����Ÿ�� �̸� �迭
	 * @param rsArray �����÷��� ����Ÿ������ ��ȯ�� ResultSet ��ü �迭, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param dataFormat �ۼ��� ���� (MiPlatformUtil.BIN, MiPlatformUtil.ZLIB_COMP, MiPlatformUtil.XML)
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, String[] datasetNameArray, ResultSet[] rsArray, int dataFormat) {
		if (datasetNameArray.length != rsArray.length)
			throw new IllegalArgumentException("Dataset�̸� ������ ResultSet������ ��ġ���� �ʽ��ϴ�.");
		int rowCount = 0;
		try {
			VariableList vl = new VariableList();
			DatasetList dl = new DatasetList();
			for (int i = 0, len = rsArray.length; i < len; i++) {
				Dataset dSet = new Dataset(datasetNameArray[i], "utf-8", false, false);
				rowCount += _appendDataset(dSet, rsArray[i]);
				dl.addDataset(dSet);
			}
			vl.addStr("ErrorCode", "0");
			vl.addStr("ErrorMsg", "SUCC");
			sendData(response, vl, dl, dataFormat);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return rowCount;
	}

	/**
	 * �ش� HttpServletRequest�� ���� PlatformRequest ��ȯ�޴´�
	 * <br>
	 * ex) ��û��ü�� ���� �����÷��� ��û��ü�� ���ϴ� ��� : PlatformRequest pReq = MiPlatformUtil.getPReq(request)
	 * @param request Ŭ���̾�Ʈ���� ��û�� Request ��ü
	 * @return ��û��ü���� ���� PlatformRequest ��ü
	 */
	public static PlatformRequest getPReq(HttpServletRequest request) {
		PlatformRequest inputPR = null;
		try {
			inputPR = new PlatformRequest(request);
			inputPR.receiveData();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return inputPR;
	}

	/**
	 * �ش� HttpServletRequest�� ���� encoding ������ PlatformRequest ��ȯ�޴´�
	 * <br>
	 * ex) ��û��ü�� ���� utf-8 ������ �����÷��� ��û��ü�� ���ϴ� ��� : PlatformRequest pReq = MiPlatformUtil.getPReq(request, "utf-8")
	 * @param request Ŭ���̾�Ʈ���� ��û�� Request ��ü
	 * @param encoding ���ڵ��� ����
	 * @return ��û��ü���� ���� PlatformRequest ��ü
	 */
	public static PlatformRequest getPReq(HttpServletRequest request, String encoding) {
		PlatformRequest inputPR = null;
		try {
			inputPR = new PlatformRequest(request, encoding);
			inputPR.receiveData();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return inputPR;
	}

	/**
	 * �ش� HttpServletResponse�� ���� PlatformResponse ��ȯ�޴´�
	 * <br>
	 * ex) ���䰴ü�� ���� XML �ۼ��� ������ �����÷��� ���䰴ü�� ���ϴ� ��� : PlatformResponse pRes = MiPlatformUtil.getPRes(response, MiPlatformUtil.XML)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param dataFormat �ۼ��� ���� (MiPlatformUtil.BIN, MiPlatformUtil.ZLIB_COMP, MiPlatformUtil.XML)
	 * @return ���䰴ü���� ���� PlatformResponse ��ü
	 */
	public static PlatformResponse getPRes(HttpServletResponse response, int dataFormat) {
		PlatformResponse inputPRes = null;
		try {
			inputPRes = new PlatformResponse(response, dataFormat);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return inputPRes;
	}

	/**
	 * �ش� HttpServletResponse�� ���� encoding ������ PlatformResponse ��ȯ�޴´�
	 * <br>
	 * ex) ���䰴ü�� ���� utf-8 ������ XML �ۼ��� ������ �����÷��� ���䰴ü�� ���ϴ� ��� : PlatformResponse pRes = MiPlatformUtil.getPRes(response, MiPlatformUtil.XML, "utf-8")
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param dataFormat �ۼ��� ���� (MiPlatformUtil.BIN, MiPlatformUtil.ZLIB_COMP, MiPlatformUtil.XML)
	 * @param encoding ���ڵ��� ����
	 * @return ���䰴ü���� ���� PlatformResponse ��ü 
	 */
	public static PlatformResponse getPRes(HttpServletResponse response, int dataFormat, String encoding) {
		PlatformResponse inputPRes = null;
		try {
			inputPRes = new PlatformResponse(response, dataFormat, encoding);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return inputPRes;
	}

	/**
	 * �ش� DataSet�� ���� Param�� ��ȯ�޴´�
	 * <br>
	 * ex) DataSet���� ���� Param�� ���ϴ� ��� : Param box = MiPlatformUtil.getParam(dSet)
	 * @param dSet Box�� ��ȯ�� DataSet ��ü
	 * @return DataSet���� ���� Box ��ü
	 */
	public static Params getParam(Dataset dSet) {
		if (dSet.getRowCount() != 1) { // row ���� 1���� �ƴϸ� �߸��� ����
			throw new IllegalArgumentException("row ���� 1�� �̾�� �մϴ�.");
		}
		Params box = new Params("miplatform");
		for (int i = 0, col = dSet.getColumnCount(); i < col; i++) {
			String key = dSet.getColumnId(i);
			box.put(key, new String[] { dSet.getColumn(0, i).toString() });
		}
		return box;
	}

	/**
	 * VariableList�� DatasetList�� ���䰴ü�� �����Ѵ�.
	 * <br>
	 * ex) vl�� dl�� response�� XML �������� �����ϴ� ��� : MiPlatformUtil.sendData(response, vl, dl, MiPlatformUtil.XML)
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param vl �����÷��� VariableList ��ü
	 * @param dl �����÷��� DatasetList ��ü
	 * @param dataFormat �ۼ��� ���� (MiPlatformUtil.BIN, MiPlatformUtil.ZLIB_COMP, MiPlatformUtil.XML)
	 */
	public static void sendData(HttpServletResponse response, VariableList vl, DatasetList dl, int dataFormat) {
		try {
			PlatformResponse pResponse = getPRes(response, dataFormat, "utf-8");
			pResponse.sendData(vl, dl);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/** 
	 * Dataset�� ���� �����Ͽ� String ��ü�� �����Ѵ�.
	 * @param dSet ���� ������ Dataset
	 * @param row ������ ���ȣ
	 * @param colName ������ ���̸�
	 * @return ����� ���� ��� �ִ� String ��ü
	 */
	public static String getString(Dataset dSet, int row, String colName) {
		String str = dSet.getColumnAsString(row, colName);
		if (str == null) {
			return "";
		}
		return str;
	}

	/** 
	 * Dataset�� ���� �����Ͽ� Double ��ü�� �����Ѵ�.
	 * @param dSet ���� ������ Dataset
	 * @param row ������ ���ȣ
	 * @param colName ������ ���̸�
	 * @return ����� ���� ��� �ִ� Double ��ü
	 */
	public static Double getDouble(Dataset dSet, int row, String colName) {
		String value = getString(dSet, row, colName).trim().replaceAll(",", "");
		if (value.equals("")) {
			return Double.valueOf(0);
		}
		Double num = null;
		try {
			num = Double.valueOf(value);
		} catch (Exception e) {
			num = Double.valueOf(0);
		}
		return num;
	}

	/** 
	 * Dataset�� ���� �����Ͽ� Long ��ü�� �����Ѵ�.
	 * @param dSet ���� ������ Dataset
	 * @param row ������ ���ȣ
	 * @param colName ������ ���̸�
	 * @return ����� ���� ��� �ִ� Long ��ü
	 */
	public static Long getLong(Dataset dSet, int row, String colName) {
		Double value = getDouble(dSet, row, colName);
		return Long.valueOf(value.longValue());
	}

	/** 
	 * Dataset�� ���� �����Ͽ� Integer ��ü�� �����Ѵ�.
	 * @param dSet ���� ������ Dataset
	 * @param row ������ ���ȣ
	 * @param colName ������ ���̸�
	 * @return ����� ���� ��� �ִ� Integer ��ü
	 */
	public static Integer getInteger(Dataset dSet, int row, String colName) {
		Double value = getDouble(dSet, row, colName);
		return Integer.valueOf(value.intValue());
	}

	/** 
	 * Dataset�� ���� �����Ͽ� Float ��ü�� �����Ѵ�.
	 * @param dSet ���� ������ Dataset
	 * @param row ������ ���ȣ
	 * @param colName ������ ���̸�
	 * @return ����� ���� ��� �ִ� Float ��ü
	 */
	public static Float getFloat(Dataset dSet, int row, String colName) {
		return new Float(getDouble(dSet, row, colName).doubleValue());
	}

	/** 
	 * Dataset�� ���� �����Ͽ� BigDecimal ��ü�� �����Ѵ�.
	 * @param dSet ���� ������ Dataset
	 * @param row ������ ���ȣ
	 * @param colName ������ ���̸�
	 * @return ����� ���� ��� �ִ� BigDecimal ��ü
	 */
	public static BigDecimal getBigDecimal(Dataset dSet, int row, String colName) {
		String value = getString(dSet, row, colName).trim().replaceAll(",", "");
		if (value.equals("")) {
			return BigDecimal.valueOf(0);
		}
		try {
			return new BigDecimal(value);
		} catch (Exception e) {
			return BigDecimal.valueOf(0);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////// Private �޼ҵ�

	/**
	 * RecordSet�� �����÷��� ����Ÿ������ ��ȯ�Ѵ�.
	 */
	private static int _appendDataset(Dataset dSet, RecordSet rs) {
		if (rs == null) {
			return 0;
		}
		String[] colNms = rs.getColumns();
		int[] colSize = rs.getColumnsSize();
		int[] colType = rs.getColumnsType();
		// �÷� ���̾ƿ� ����
		for (int c = 0; c < colNms.length; c++) {
			switch (colType[c]) {
			case Types.BIGINT:
			case Types.DECIMAL:
			case Types.DOUBLE:
			case Types.FLOAT:
			case Types.INTEGER:
			case Types.NUMERIC:
			case Types.REAL:
			case Types.SMALLINT:
			case Types.TINYINT:
				dSet.addColumn(colNms[c].toLowerCase(), ColumnInfo.COLUMN_TYPE_DECIMAL, colSize[c]);
				break;
			default:
				dSet.addColumn(colNms[c].toLowerCase(), ColumnInfo.COLUMN_TYPE_STRING, colSize[c]);
				break;
			}
		}
		rs.moveRow(0); // rs�� ��ġ�� 1��°�� �̵� 
		int rowCount = 0;
		while (rs.nextRow()) {
			rowCount++;
			_appendRow(dSet, rs, colNms);
		}
		return rowCount;
	}

	/**
	 * ResultSet�� �����÷��� ����Ÿ������ ��ȯ�Ѵ�.
	 */
	private static int _appendDataset(Dataset dSet, ResultSet rs) {
		if (rs == null) {
			return 0;
		}
		try {
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] colNms = new String[count];
				int[] colSize = new int[count];
				int[] colType = new int[count];
				for (int i = 1; i <= count; i++) {
					//Table�� Field �� �ҹ��� �ΰ��� �빮�ڷ� ����ó��
					colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
					//Field �� ���� �� Size �߰�
					colSize[i - 1] = rsmd.getColumnDisplaySize(i);
					// Field �� Ÿ�� �߰�
					colType[i - 1] = rsmd.getColumnType(i);
				}
				// �÷� ���̾ƿ� ����
				for (int c = 0; c < colNms.length; c++) {
					switch (colType[c]) {
					case Types.BIGINT:
					case Types.DECIMAL:
					case Types.DOUBLE:
					case Types.FLOAT:
					case Types.INTEGER:
					case Types.NUMERIC:
					case Types.REAL:
					case Types.SMALLINT:
					case Types.TINYINT:
						dSet.addColumn(colNms[c].toLowerCase(), ColumnInfo.COLUMN_TYPE_DECIMAL, colSize[c]);
						break;
					default:
						dSet.addColumn(colNms[c].toLowerCase(), ColumnInfo.COLUMN_TYPE_STRING, colSize[c]);
						break;
					}
				}
				int rowCount = 0;
				while (rs.next()) {
					rowCount++;
					_appendRow(dSet, rs, colNms);
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
	 * �����÷��� ����Ÿ�¿� RecordSet ���� �߰�
	 */
	private static void _appendRow(Dataset dSet, RecordSet rs, String[] colNms) {
		if (rs.getRowCount() == 0)
			return;
		int row = dSet.appendRow();
		for (int c = 0; c < colNms.length; c++) {
			Object value = rs.get(colNms[c]);
			if (value == null) {
				dSet.setColumn(row, colNms[c].toLowerCase(), "");
			} else {
				if (value instanceof Number) {
					dSet.setColumn(row, colNms[c].toLowerCase(), rs.getDouble(colNms[c]));
				} else {
					dSet.setColumn(row, colNms[c].toLowerCase(), rs.getString(colNms[c]));
				}
			}
		}
	}

	/**
	 * �����÷��� ����Ÿ�¿� ResultSet ���� �߰�
	 */
	private static void _appendRow(Dataset dSet, ResultSet rs, String[] colNms) {
		try {
			if (rs.getRow() == 0)
				return;
			int row = dSet.appendRow();
			for (int c = 0; c < colNms.length; c++) {
				Object value = rs.getObject(colNms[c]);
				if (value == null) {
					dSet.setColumn(row, colNms[c].toLowerCase(), "");
				} else {
					if (value instanceof Number) {
						dSet.setColumn(row, colNms[c].toLowerCase(), rs.getDouble(colNms[c]));
					} else {
						dSet.setColumn(row, colNms[c].toLowerCase(), rs.getString(colNms[c]));
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}