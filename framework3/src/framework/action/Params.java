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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import framework.config.Config;
import framework.util.StringUtil;

/** 
 * 요청객체, 쿠키객체의 값을 담는 해시테이블 객체이다.
 * 요청객체의 파라미터를 추상화 하여 Params 를 생성해 놓고 파라미터이름을 키로 해당 값을 원하는 데이타 타입으로 반환받는다.
 */
public class Params extends HashMap<String, String[]> {
	private static final long serialVersionUID = 7143941735208780214L;
	protected static final Log logger = LogFactory.getLog(framework.action.Params.class);
	private String _name = null;
	private List<FileItem> _fileItems = new ArrayList<FileItem>();

	/***
	 * Params 생성자
	 * @param name Params 객체의 이름
	 */
	public Params(String name) {
		super();
		_name = name;
	}

	/** 
	 * 요청객체의 파라미터 이름과 값을 저장한 해시테이블을 생성한다.
	 * <br>
	 * ex) request Params 객체를 얻는 경우 : Params params = Params.getParams(request)
	 * @param request HTTP 클라이언트 요청객체
	 * @return 요청Params 객체
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
					logger.error(e);
				}
				try {
					factory.setRepository(new File(_getConfig().getString("fileupload.repository")));
				} catch (IllegalArgumentException e) {
					logger.error(e);
				}
				ServletFileUpload upload = new ServletFileUpload(factory);
				try {
					upload.setSizeMax(_getConfig().getInt("fileupload.sizeMax"));
				} catch (IllegalArgumentException e) {
					logger.error(e);
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
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		return params;
	}

	/** 
	 * 요청객체의 쿠키 이름과 값을 저장한 해시테이블을 생성한다.
	 * <br>
	 * ex) cookie Params 객체를 얻는 경우 : Params params = Params.getParamsFromCookie(request)
	 * @param request HTTP 클라이언트 요청객체
	 * @return 쿠키Params 객체
	 */
	public static Params getParamsFromCookie(HttpServletRequest request) {
		Params cookieParams = new Params("Cookie");
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return cookieParams;
		}
		for (Cookie cookie : cookies) {
			cookieParams.put(cookie.getName(), new String[] { StringUtil.nullToBlankString(cookie.getValue()) });
		}
		return cookieParams;
	}

	/** 
	 * 요청객체의 헤더 이름과 값을 저장한 해시테이블을 생성한다.
	 * <br>
	 * ex) header Params 객체를 얻는 경우 : Params params = Params.getParamsFromHeader(request)
	 * @param request HTTP 클라이언트 요청객체
	 * @return 헤더Params 객체
	 */
	public static Params getParamsFromHeader(HttpServletRequest request) {
		Params headerParams = new Params("Header");
		Enumeration<?> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = (String) headerNames.nextElement();
			headerParams.put(headerName, new String[] { StringUtil.nullToBlankString(request.getHeader(headerName)) });
		}
		return headerParams;
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 오브젝트를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 오브젝트
	 */
	public Object get(String key) {
		Object value = super.get(key);
		if (value == null) {
			return null;
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
	 * 키(key)문자열과 매핑되어 있는 문자열 배열을 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 문자열 배열
	 */
	public String[] getArray(String key) {
		String[] value = super.get(key);
		if (value == null) {
			return new String[] {};
		}
		return value;
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Boolean 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 Boolean 객체
	 */
	public Boolean getBoolean(String key) {
		return Boolean.valueOf(getString(key).trim());
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Double 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 Double 객체
	 */
	public Double getDouble(String key) {
		try {
			return Double.valueOf(getString(key).trim().replaceAll(",", ""));
		} catch (NumberFormatException e) {
			return Double.valueOf(0);
		}
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 BigDecimal 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 BigDecimal 객체
	 */
	public BigDecimal getBigDecimal(String key) {
		try {
			return new BigDecimal(getString(key).trim().replaceAll(",", ""));
		} catch (NumberFormatException e) {
			return BigDecimal.valueOf(0);
		}
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Float 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 Float 객체
	 */
	public Float getFloat(String key) {
		return Float.valueOf(getDouble(key).floatValue());
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Integer 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 Integer 객체
	 */
	public Integer getInteger(String key) {
		return Integer.valueOf(getDouble(key).intValue());
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Long 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 Long 객체
	 */
	public Long getLong(String key) {
		return Long.valueOf(getDouble(key).longValue());
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 String 객체를 리턴한다.
	 * 크로스사이트 스크립팅 공격 방지를 위해 &lt;, &gt; 치환을 수행한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 String 객체
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
	 * 키(key)문자열과 매핑되어 있는 String 객체를 변환없이 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 String 객체
	 */
	public String getRawString(String key) {
		return StringUtil.nullToBlankString((String) get(key));
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Timestamp 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 Timestamp 객체
	 */
	public Timestamp getTimestamp(String key) {
		String str = getString(key);
		if ("".equals(str)) {
			return null;
		}
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.KOREA);
		formatter.format(new java.util.Date());
		return Timestamp.valueOf(str + " " + formatter.format(new java.util.Date()));
	}

	/**
	 * 파일아이템(FileItem)의 리스트 객체를 리턴한다.
	 * @return 파일아이템 리스트 객체
	 */
	public List<FileItem> getFileItems() {
		List<FileItem> list = new ArrayList<FileItem>();
		list.addAll(_fileItems);
		return list;
	}

	/**
	 * 키(key)에 매핑되는 스트링을 셋팅한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @param value 키에 매핑되는 문자열
	 * @return 원래 key에 매핑되어 있는 스트링 배열
	 */
	public String[] putString(String key, String value) {
		return put(key, new String[] { value });
	}

	/** 
	 * Param 객체가 가지고 있는 값들을 화면 출력을 위해 문자열로 변환한다.
	 * @return 화면에 출력하기 위해 변환된 문자열
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
	 * Params 객체가 가지고 있는 값들을 쿼리 스트링으로 변환한다.
	 * @return 쿼리 스트링으로 변환된 문자열
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
	 * Params 객체가 가지고 있는 값들을 Xml로 변환한다.
	 * @return Xml로 변환된 문자열
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
	 * Params 객체가 가지고 있는 값들을 Json 표기법으로 변환한다.
	 * @return Json 표기법으로 변환된 문자열
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

	//////////////////////////////////////////////////////////////////////////////////////////Private 메소드
	/**
	 * 자바스크립트상에 특수하게 인식되는 문자들을 JSON등에 사용하기 위해 변환하여준다.
	 * @param str 변환할 문자열
	 */
	private String _escapeJS(String str) {
		if (str == null) {
			return "";
		}
		return str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\").replaceAll("\r\n", "\\\\n").replaceAll("\n", "\\\\n");
	}

	/**
	 * Multipart 파일업로드시 파일 아이템을 리스트에 추가한다.
	 * @param item 파일을 담고 있는 객체
	 * @return 성공여부
	 */
	private boolean _addFileItem(FileItem item) {
		return _fileItems.add(item);
	}

	/** 
	 * 설정정보를 가지고 있는 객체를 생성하여 리턴한다.
	 * @return config.properties의 설정정보를 가지고 있는 객체
	 */
	private static Config _getConfig() {
		return Config.getInstance();
	}
}