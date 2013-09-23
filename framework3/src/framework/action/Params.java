/**
 * @(#)Params.java
 */
package framework.action;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import framework.config.Config;

/** 
 * ��û��ü, ��Ű��ü�� ���� ��� �ؽ����̺� ��ü�̴�.
 * ��û��ü�� �Ķ���͸� �߻�ȭ �Ͽ� Params �� ������ ���� �Ķ�����̸��� Ű�� �ش� ���� ���ϴ� ����Ÿ Ÿ������ ��ȯ�޴´�.
 */
public class Params extends HashMap<String, String[]> {
	private static final long serialVersionUID = 7143941735208780214L;
	private String _name = null;
	private List<FileItem> _fileItems = new ArrayList<FileItem>();

	/***
	 * Params ������
	 * @param name Params ��ü�� �̸�
	 */
	public Params(String name) {
		super();
		_name = name;
	}

	/** 
	 * ��û��ü�� �Ķ���� �̸��� ���� ������ �ؽ����̺��� �����Ѵ�.
	 * <br>
	 * ex) request Params ��ü�� ��� ��� => Params params = Params.getParams(request)
	 * @param request HTTP Ŭ���̾�Ʈ ��û��ü
	 * @return ��ûParams ��ü
	 */
	@SuppressWarnings("unchecked")
	public static Params getParams(HttpServletRequest request) {
		Params params = new Params("Params");
		for (Object obj : request.getParameterMap().keySet()) {
			String key = (String) obj;
			params.put(key, request.getParameterValues(key));
		}
		if (ServletFileUpload.isMultipartContent(request)) {
			try {
				DiskFileItemFactory factory = new DiskFileItemFactory();
				try {
					factory.setSizeThreshold(_getConfig().getInt("fileupload.sizeThreshold"));
				} catch (IllegalArgumentException e) {
				}
				try {
					factory.setRepository(new File(_getConfig().getString("fileupload.repository")));
				} catch (IllegalArgumentException e) {
				}
				ServletFileUpload upload = new ServletFileUpload(factory);
				try {
					upload.setSizeMax(_getConfig().getInt("fileupload.sizeMax"));
				} catch (IllegalArgumentException e) {
				}
				List<FileItem> items = upload.parseRequest(request);
				for (FileItem item : items) {
					if (item.isFormField()) {
						String fieldName = item.getFieldName();
						String fieldValue = item.getString(request.getCharacterEncoding());
						String[] oldValue = params.getArray(fieldName);
						if (oldValue == null) {
							params.put(fieldName, new String[] { fieldValue });
						} else {
							int size = oldValue.length;
							String[] newValue = new String[size + 1];
							for (int i = 0; i < size; i++) {
								newValue[i] = oldValue[i];
							}
							newValue[size] = fieldValue;
							params.put(fieldName, newValue);
						}
					} else {
						params._addFileItem(item);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return params;
	}

	/** 
	 * ��û��ü�� ��Ű �̸��� ���� ������ �ؽ����̺��� �����Ѵ�.
	 * <br>
	 * ex) cookie Params ��ü�� ��� ��� => Params params = Params.getParamsFromCookie(request)
	 * @param request HTTP Ŭ���̾�Ʈ ��û��ü
	 * @return ��ŰParams ��ü
	 */
	public static Params getParamsFromCookie(HttpServletRequest request) {
		Params cookieParams = new Params("Cookie");
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return cookieParams;
		}
		for (Cookie cookie : cookies) {
			cookieParams.put(cookie.getName(), new String[] { cookie.getValue() == null ? "" : cookie.getValue() });
		}
		return cookieParams;
	}

	/** 
	 * ��û��ü�� ��� �̸��� ���� ������ �ؽ����̺��� �����Ѵ�.
	 * <br>
	 * ex) header Params ��ü�� ��� ��� => Params params = Params.getParamsFromHeader(request)
	 * @param request HTTP Ŭ���̾�Ʈ ��û��ü
	 * @return ���Params ��ü
	 */
	public static Params getParamsFromHeader(HttpServletRequest request) {
		Params headerParams = new Params("Header");
		Enumeration<?> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = (String) headerNames.nextElement();
			headerParams.put(headerName, new String[] { request.getHeader(headerName) == null ? "" : request.getHeader(headerName) });
		}
		return headerParams;
	}

	/** 
	 * Ű(key)���ڿ��� ���εǾ� �ִ� ������Ʈ�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� ������Ʈ
	 */
	public Object get(String key) {
		Object value = null;
		value = super.get(key);
		if (value == null) {
			return value;
		}
		if (value.getClass().isArray()) {
			int length = Array.getLength(value);
			if (length == 0) {
				value = null;
			} else {
				value = Array.get(value, 0);
			}
		}
		return value;
	}

	/** 
	 * Ű(key)���ڿ��� ���εǾ� �ִ� ���ڿ� �迭�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� ���ڿ� �迭
	 */
	public String[] getArray(String key) {
		return super.get(key);
	}

	/** 
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Boolean ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� Boolean ��ü
	 */
	public Boolean getBoolean(String key) {
		String value = getString(key);
		Boolean isTrue = Boolean.valueOf(false);
		try {
			isTrue = Boolean.valueOf(value);
		} catch (Exception e) {
		}
		return isTrue;
	}

	/** 
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Double ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� Double ��ü
	 */
	public Double getDouble(String key) {
		String value = getString(key).trim().replaceAll(",", "");
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
	 * Ű(key)���ڿ��� ���εǾ� �ִ� BigDecimal ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� BigDecimal ��ü
	 */
	public BigDecimal getBigDecimal(String key) {
		String value = getString(key).trim().replaceAll(",", "");
		if (value.equals("")) {
			return BigDecimal.valueOf(0);
		}
		try {
			return new BigDecimal(value);
		} catch (Exception e) {
			return BigDecimal.valueOf(0);
		}
	}

	/** 
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Float ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� Float ��ü
	 */
	public Float getFloat(String key) {
		return new Float(getDouble(key).doubleValue());
	}

	/** 
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Integer ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� Integer ��ü
	 */
	public Integer getInteger(String key) {
		Double value = getDouble(key);
		return Integer.valueOf(value.intValue());
	}

	/** 
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Long ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� Long ��ü
	 */
	public Long getLong(String key) {
		Double value = getDouble(key);
		return Long.valueOf(value.longValue());
	}

	/** 
	 * Ű(key)���ڿ��� ���εǾ� �ִ� long ������ �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� long ������
	 */
	public long getlong(String key) {
		Double value = getDouble(key);
		return value.longValue();
	}

	/** 
	 * Ű(key)���ڿ��� ���εǾ� �ִ� String ��ü�� �����Ѵ�.
	 * ũ�ν�����Ʈ ��ũ���� ���� ������ ���� &lt;, &gt; ġȯ�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� String ��ü
	 */
	public String getString(String key) {
		String str = (String) get(key);
		if (str == null) {
			return "";
		}
		StringBuilder result = new StringBuilder(str.length());
		for (int i = 0; i < str.length(); i++) {
			switch (str.charAt(i)) {
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			default:
				result.append(str.charAt(i));
				break;
			}
		}
		return result.toString();
	}

	/** 
	 * Ű(key)���ڿ��� ���εǾ� �ִ� String ��ü�� ��ȯ���� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� String ��ü
	 */
	public String getRawString(String key) {
		String str = (String) get(key);
		if (str == null) {
			return "";
		}
		return str;
	}

	/** 
	 * Ű(key)���ڿ��� ���εǾ� �ִ� ����Ʈ �迭�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� ����Ʈ �迭
	 */
	public byte[] getByte(String key) {
		Object obj = super.get(key);
		if (obj == null) {
			return null;
		}
		return (byte[]) obj;
	}

	/** 
	 * Ű(key)���ڿ��� ���εǾ� �ִ� Timestamp ��ü�� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @return key�� ���εǾ� �ִ� Timestamp ��ü
	 */
	public Timestamp getTimestamp(String key) {
		String str = getString(key);
		if (str == null || "".equals(str)) {
			return null;
		}
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.KOREA);
		formatter.format(new java.util.Date());
		return Timestamp.valueOf(str + " " + formatter.format(new java.util.Date()));
	}

	/**
	 * ���Ͼ�����(FileItem)�� ����Ʈ ��ü�� �����Ѵ�.
	 * @return ���Ͼ����� ����Ʈ ��ü
	 */
	public List<FileItem> getFileItems() {
		return _fileItems;
	}

	/**
	 * Ű(key)�� ���εǴ� ��Ʈ���� �����Ѵ�.
	 * @param key ���� ã�� ���� Ű ���ڿ�
	 * @param value Ű�� ���εǴ� ���ڿ�
	 * @return ���� key�� ���εǾ� �ִ� ��Ʈ�� �迭
	 */
	public String[] put(String key, String value) {
		return put(key, new String[] { value });
	}

	/** 
	 * Param ��ü�� ������ �ִ� ������ ȭ�� ����� ���� ���ڿ��� ��ȯ�Ѵ�.
	 * @return ȭ�鿡 ����ϱ� ���� ��ȯ�� ���ڿ�
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("{ ");
		long currentRow = 0;
		for (String key : keySet()) {
			String value = null;
			Object o = get(key);
			if (o == null) {
				value = "";
			} else {
				if (o.getClass().isArray()) {
					int length = Array.getLength(o);
					if (length == 0) {
						value = "";
					} else if (length == 1) {
						Object item = Array.get(o, 0);
						if (item == null) {
							value = "";
						} else {
							value = item.toString();
						}
					} else {
						StringBuilder valueBuf = new StringBuilder();
						valueBuf.append("[");
						for (int j = 0; j < length; j++) {
							Object item = Array.get(o, j);
							if (item != null) {
								valueBuf.append(item.toString());
							}
							if (j < length - 1) {
								valueBuf.append(",");
							}
						}
						valueBuf.append("]");
						value = valueBuf.toString();
					}
				} else {
					value = o.toString();
				}
			}
			if (currentRow++ > 0) {
				buf.append(", ");
			}
			buf.append(key + "=" + value);
		}
		buf.append(" }");
		return _name + "=" + buf.toString();
	}

	/** 
	 * Params ��ü�� ������ �ִ� ������ ���� ��Ʈ������ ��ȯ�Ѵ�.
	 * @return ���� ��Ʈ������ ��ȯ�� ���ڿ�
	 */
	public String toQueryString() {
		StringBuilder buf = new StringBuilder();
		long currentRow = 0;
		for (String key : keySet()) {
			Object o = get(key);
			if (currentRow++ > 0) {
				buf.append("&");
			}
			if (o == null) {
				buf.append(key + "=" + "");
			} else {
				if (o.getClass().isArray()) {
					StringBuilder valueBuf = new StringBuilder();
					for (int j = 0, length = Array.getLength(o); j < length; j++) {
						Object item = Array.get(o, j);
						if (item != null) {
							valueBuf.append(key + "=" + item.toString());
						}
						if (j < length - 1) {
							valueBuf.append("&");
						}
					}
					buf.append(valueBuf.toString());
				} else {
					buf.append(key + "=" + o.toString());
				}
			}
		}
		return buf.toString();
	}

	/** 
	 * Params ��ü�� ������ �ִ� ������ Xml�� ��ȯ�Ѵ�.
	 * @return Xml�� ��ȯ�� ���ڿ�
	 */
	public String toXml() {
		StringBuilder buf = new StringBuilder();
		buf.append("<items>");
		buf.append("<item>");
		for (String key : keySet()) {
			Object o = get(key);
			if (o == null || "".equals(o)) {
				buf.append("<" + key.toLowerCase() + ">" + "</" + key.toLowerCase() + ">");
			} else {
				if (o.getClass().isArray()) {
					int length = Array.getLength(o);
					if (length == 0) {
						buf.append("<" + key.toLowerCase() + ">" + "</" + key.toLowerCase() + ">");
					} else if (length == 1) {
						Object item = Array.get(o, 0);
						if (item == null || "".equals(item)) {
							buf.append("<" + key.toLowerCase() + ">" + "</" + key.toLowerCase() + ">");
						} else {
							buf.append("<" + key.toLowerCase() + ">" + "<![CDATA[" + item.toString() + "]]>" + "</" + key.toLowerCase() + ">");
						}
					} else {
						for (int j = 0; j < length; j++) {
							Object item = Array.get(o, j);
							if (item == null || "".equals(item)) {
								buf.append("<" + key.toLowerCase() + ">" + "</" + key.toLowerCase() + ">");
							} else {
								buf.append("<" + key.toLowerCase() + ">" + "<![CDATA[" + item.toString() + "]]>" + "</" + key.toLowerCase() + ">");
							}
						}
					}
				} else {
					buf.append("<" + key.toLowerCase() + ">" + "<![CDATA[" + o.toString() + "]]>" + "</" + key.toLowerCase() + ">");
				}
			}
		}
		buf.append("</item>");
		buf.append("</items>");
		return buf.toString();
	}

	/** 
	 * Params ��ü�� ������ �ִ� ������ Json ǥ������� ��ȯ�Ѵ�.
	 * @return Json ǥ������� ��ȯ�� ���ڿ�
	 */
	public String toJson() {
		StringBuilder buf = new StringBuilder();
		buf.append("{ ");
		long currentRow = 0;
		for (String key : keySet()) {
			String value = null;
			Object o = get(key);
			if (o == null) {
				value = "\"\"";
			} else {
				if (o.getClass().isArray()) {
					int length = Array.getLength(o);
					if (length == 0) {
						value = "\"\"";
					} else if (length == 1) {
						Object item = Array.get(o, 0);
						if (item == null) {
							value = "\"\"";
						} else {
							value = "\"" + _escapeJS(item.toString()) + "\"";
						}
					} else {
						StringBuilder valueBuf = new StringBuilder();
						valueBuf.append("[");
						for (int j = 0; j < length; j++) {
							Object item = Array.get(o, j);
							if (item != null) {
								valueBuf.append("\"" + _escapeJS(item.toString()) + "\"");
							}
							if (j < length - 1) {
								valueBuf.append(",");
							}
						}
						valueBuf.append("]");
						value = valueBuf.toString();
					}
				} else {
					value = "\"" + _escapeJS(o.toString()) + "\"";
				}
			}
			if (currentRow++ > 0) {
				buf.append(", ");
			}
			buf.append("\"" + _escapeJS(key) + "\"" + ":" + value);
		}
		buf.append(" }");
		return buf.toString();
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private �޼ҵ�
	/**
	 * �ڹٽ�ũ��Ʈ�� Ư���ϰ� �νĵǴ� ���ڵ��� JSON� ����ϱ� ���� ��ȯ�Ͽ��ش�.
	 * @param str ��ȯ�� ���ڿ�
	 */
	private String _escapeJS(String str) {
		if (str == null) {
			return "";
		}
		return str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\").replaceAll("\r\n", "\\\\n").replaceAll("\n", "\\\\n");
	}

	/**
	 * Multipart ���Ͼ��ε�� ���� �������� ����Ʈ�� �߰��Ѵ�.
	 * @param item ������ ��� �ִ� ��ü
	 * @return ��������
	 */
	private boolean _addFileItem(FileItem item) {
		return _fileItems.add(item);
	}

	/** 
	 * ���������� ������ �ִ� ��ü�� �����Ͽ� �����Ѵ�.
	 * @return config.properties�� ���������� ������ �ִ� ��ü
	 */
	private static Config _getConfig() {
		return Config.getInstance();
	}
}