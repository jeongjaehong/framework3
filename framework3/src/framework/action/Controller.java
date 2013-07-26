/** 
 * @(#)Controller.java
 */
package framework.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import framework.config.Config;
import framework.db.ConnectionManager;
import framework.db.RecordSet;
import framework.util.JsonUtil;
import framework.util.XmlUtil;

/** 
 * �����Ͻ� ������ ó���ϴ� Ŭ������ ��ӹ޾ƾ� �� �߻�Ŭ�����̴�.
 * ��������(jsp ������)�� ����Ǳ� ���� Ŭ���̾�Ʈ���� ������ ���۵� �����͸� ���ϰ� ���������� �ݿ��ϱ� 
 * ���� ��ó��(Pre-processing)����̴�. �ϳ��� ���񽺿� ���� �������� ���������� ������Ʈ ���·� �����Ͽ� ����� �� �ִ�. 
 * �ۼ��� Controller�� routes.properties�� ����Ѵ�.
 */
public abstract class Controller {
	private Map<String, ConnectionManager> _connMgrMap = new HashMap<String, ConnectionManager>();
	private String _DEFAULT_ENCODING = "utf-8";

	/**
	 * Controller�� ȣ���� ���� ��ü
	 */
	protected HttpServlet servlet = null;

	/**
	 * HTTP Ŭ���̾�Ʈ ��û��ü
	 */
	protected HttpServletRequest request = null;

	/**
	 * ��û�Ķ������ ���� ��� �ؽ����̺�
	 */
	protected Params params = null;

	/**
	 * Ŭ���̾�Ʈ�� ���� ��ü
	 */
	protected HttpSession session = null;

	/**
	 * HTTP Ŭ���̾�Ʈ ���䰴ü
	 */
	protected HttpServletResponse response = null;

	/**
	 * ���䰴ü�� PrintWriter ��ü
	 */
	protected PrintWriter out = null;

	/**
	 * Controller�� �ΰŰ�ü
	 */
	protected static Log logger = LogFactory.getLog(framework.action.Controller.class);

	/** 
	 * Ŭ���̾�Ʈ���� ���񽺸� ȣ���� �� ��û�Ķ���� action�� ������ ���� �����Ͽ� �ش� �޼ҵ带 �����Ѵ�.
	 * ���ǵ��� ���� �޼ҵ带 ȣ���� ��� �α׿� �����޽����� ��ϵǸ� �޼ҵ� ������ ��ģ �� �����ͺ��̽� ������ �ڵ����� �ݾ��ش�.
	 * <br>
	 * ex) action�� search �϶� => search() �޼ҵ尡 ȣ��ȴ�.
	 * @param servlet ���� ��ü
	 * @param request Ŭ���̾�Ʈ���� ��û�� Request��ü
	 * @param response Ŭ���̾�Ʈ�� ������ Response��ü
	 * @throws Exception
	 */
	public void execute(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response) throws Exception {
		_setServlet(servlet);
		_setRequest(request);
		_setParams(request);
		_setSession(request.getSession());
		_setResponse(response);
		_setOut(response);
		try {
			Method method = _getMethod(params.getString("action"));
			if (method == null) {
				throw new NotFoundException("action not found!");
			}
			before();
			method.invoke(this, (Object[]) null);
			after();
		} finally {
			_destroy();
		}
	}

	/**
	 * �׼Ǹ޼ҵ尡 ȣ��Ǳ� ������ ȣ��ȴ�.
	 * ��Ʈ�ѷ� Ŭ�������� �������̵� �ϸ� �ڵ� ȣ��ȴ�.
	 */
	protected void before() {
	}

	/**
	 * �׼Ǹ޼ҵ尡 ȣ��ǰ� ���Ŀ� ȣ��ȴ�.
	 * ��Ʈ�ѷ� Ŭ�������� �������̵� �ϸ� �ڵ� ȣ��ȴ�.
	 */
	protected void after() {
	}

	/**
	 * ��û�� JSP�������� ������(Forward) �Ѵ�.
	 * �ۼ��� JSP�������� routes.properties�� ����Ѵ�.
	 * <br>
	 * ex) Ű�� search-jsp �� JSP�������� ������ �� ��� => render("search-jsp")
	 * @param jsp routes.properties ���Ͽ� ��ϵ� JSP �������� Ű
	 */
	protected void render(String jsp) {
		_route(jsp, true);
	}

	/**
	 * �ؽ�Ʈ�� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) hello world!�� ����� ��� => renderText("hello world!")
	 * @param text ����� �ؽ�Ʈ(�⺻ ���ڵ����� utf-8 ���)
	 */
	protected void renderText(String text) {
		renderText(text, _DEFAULT_ENCODING);
	}

	/**
	 * �ؽ�Ʈ�� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) hello world!�� ����� ��� => renderText("hello world!", "utf-8")
	 * @param text ����� �ؽ�Ʈ
	 * @param encoding ���ڵ�
	 */
	protected void renderText(String text, String encoding) {
		setContentType("text/plain; charset=" + encoding);
		out.write(text);
	}

	/**
	 * HTML������ �ؽ�Ʈ�� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) hello world!�� ����� ��� => renderHTML("<h1>hello world!<h1>")
	 * @param html ����� HTML������ �ؽ�Ʈ(�⺻ ���ڵ����� utf-8 ���)
	 */
	protected void renderHTML(String html) {
		renderHTML(html, _DEFAULT_ENCODING);
	}

	/**
	 * HTML������ �ؽ�Ʈ�� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) hello world!�� ����� ��� => renderHTML("<h1>hello world!<h1>", "utf-8")
	 * @param html ����� HTML������ �ؽ�Ʈ
	 * @param encoding ���ڵ�
	 */
	protected void renderHTML(String html, String encoding) {
		setContentType("text/html; charset=" + encoding);
		out.write(html);
	}

	/**
	 * JSON������ �ؽ�Ʈ�� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) hello world!�� ����� ��� => renderJSON("{ msg: \"hello world!\" }")
	 * @param json ����� JSON������ �ؽ�Ʈ(�⺻ ���ڵ����� utf-8 ���)
	 */
	protected void renderJSON(String json) {
		renderJSON(json, _DEFAULT_ENCODING);
	}

	/**
	 * JSON������ �ؽ�Ʈ�� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) hello world!�� ����� ��� => renderJSON("{ msg: \"hello world!\" }", "utf-8")
	 * @param json ����� JSON������ �ؽ�Ʈ
	 * @param encoding ���ڵ�
	 */
	protected void renderJSON(String json, String encoding) {
		setContentType("application/json; charset=" + encoding);
		out.write(json);
	}

	/**
	 * RecordSet �����͸� JSON �������� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) rs�� ����� ��� => renderJSON(rs)
	 * @param rs ����� RecordSet ������(�⺻ ���ڵ����� utf-8 ���)
	 */
	protected void renderJSON(RecordSet rs) {
		renderJSON(rs);
	}

	/**
	 * RecordSet �����͸� JSON �������� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) rs�� ����� ��� => renderJSON(rs, "utf-8")
	 * @param rs ����� RecordSet ������
	 * @param encoding ���ڵ�
	 */
	protected void renderJSON(RecordSet rs, String encoding) {
		setContentType("application/json; charset=" + encoding);
		out.write(JsonUtil.render(rs));
	}

	/**
	 * ResultSet �����͸� JSON �������� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) rs�� ����� ��� => renderJSON(rs)
	 * @param rs ����� ResultSet ������(�⺻ ���ڵ����� utf-8 ���)
	 */
	protected void renderJSON(ResultSet rs) {
		renderJSON(rs);
	}

	/**
	 * ResultSet �����͸� JSON �������� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) rs�� ����� ��� => renderJSON(rs, "utf-8")
	 * @param rs ����� ResultSet ������
	 * @param encoding ���ڵ�
	 */
	protected void renderJSON(ResultSet rs, String encoding) {
		setContentType("application/json; charset=" + encoding);
		out.write(JsonUtil.render(rs));
	}

	/**
	 * Map �����͸� JSON �������� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) map�� ����� ��� => renderJSON(map)
	 * @param map ����� Map ������(�⺻ ���ڵ����� utf-8 ���)
	 */
	protected void renderJSON(Map<String, Object> map) {
		renderJSON(map);
	}

	/**
	 * Map �����͸� JSON �������� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) map�� ����� ��� => renderJSON(map, "utf-8")
	 * @param map ����� Map ������
	 * @param encoding ���ڵ�
	 */
	protected void renderJSON(Map<String, Object> map, String encoding) {
		setContentType("application/json; charset=" + encoding);
		out.write(JsonUtil.render(map));
	}

	/**
	 * List �����͸� JSON �������� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) list�� ����� ��� => renderJSON(list)
	 * @param list ����� List ������(�⺻ ���ڵ����� utf-8 ���)
	 */
	protected void renderJSON(List<Map<String, Object>> list) {
		renderJSON(list);
	}

	/**
	 * List �����͸� JSON �������� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) list�� ����� ��� => renderJSON(list, "utf-8")
	 * @param list ����� List ������
	 * @param encoding ���ڵ�
	 */
	protected void renderJSON(List<Map<String, Object>> list, String encoding) {
		setContentType("application/json; charset=" + encoding);
		out.write(JsonUtil.render(list));
	}

	/**
	 * XML������ �ؽ�Ʈ�� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) hello world!�� ����� ��� => renderXML("<?xml version=\"1.0\" encoding=\"utf-8\"?><msg>hello world!</msg>")
	 * @param xml ����� XML������ �ؽ�Ʈ(�⺻ ���ڵ����� utf-8 ���)
	 */
	protected void renderXML(String xml) {
		renderXML(xml, _DEFAULT_ENCODING);
	}

	/**
	 * XML������ �ؽ�Ʈ�� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) hello world!�� ����� ��� => renderXML("<?xml version=\"1.0\" encoding=\"utf-8\"?><msg>hello world!</msg>", "utf-8")
	 * @param xml ����� XML������ �ؽ�Ʈ
	 * @param encoding ���ڵ�
	 */
	protected void renderXML(String xml, String encoding) {
		setContentType("text/xml; charset=" + encoding);
		out.write(xml);
	}

	/**
	 * RecordSet �����͸� XML �������� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) rs�� ����� ��� => renderXML(rs)
	 * @param rs ����� RecordSet ������(�⺻ ���ڵ����� utf-8 ���)
	 */
	protected void renderXML(RecordSet rs) {
		renderXML(rs, _DEFAULT_ENCODING);
	}

	/**
	 * RecordSet �����͸� XML �������� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) rs�� ����� ��� => renderXML(rs, "utf-8")
	 * @param rs ����� RecordSet ������
	 * @param encoding ���ڵ�
	 */
	protected void renderXML(RecordSet rs, String encoding) {
		setContentType("text/xml; charset=" + encoding);
		out.write(XmlUtil.render(rs, encoding));
	}

	/**
	 * ResultSet �����͸� XML �������� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) rs�� ����� ��� => renderXML(rs)
	 * @param rs ����� ResultSet ������(�⺻ ���ڵ����� utf-8 ���)
	 */
	protected void renderXML(ResultSet rs) {
		renderXML(rs, _DEFAULT_ENCODING);
	}

	/**
	 * ResultSet �����͸� XML �������� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) rs�� ����� ��� => renderXML(rs, "utf-8")
	 * @param rs ����� ResultSet ������
	 * @param encoding ���ڵ�
	 */
	protected void renderXML(ResultSet rs, String encoding) {
		setContentType("text/xml; charset=" + encoding);
		out.write(XmlUtil.render(rs, encoding));
	}

	/**
	 * Map �����͸� XML �������� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) map�� ����� ��� => renderXML(map)
	 * @param map ����� Map ������(�⺻ ���ڵ����� utf-8 ���)
	 */
	protected void renderXML(Map<String, Object> map) {
		renderXML(map, _DEFAULT_ENCODING);
	}

	/**
	 * Map �����͸� XML �������� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) map�� ����� ��� => renderXML(map, "utf-8")
	 * @param map ����� Map ������
	 * @param encoding ���ڵ�
	 */
	protected void renderXML(Map<String, Object> map, String encoding) {
		setContentType("text/xml; charset=" + encoding);
		out.write(XmlUtil.render(map, encoding));
	}

	/**
	 * List �����͸� XML �������� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) list�� ����� ��� => renderXML(list)
	 * @param list ����� List ������(�⺻ ���ڵ����� utf-8 ���)
	 */
	protected void renderXML(List<Map<String, Object>> list) {
		renderXML(list, _DEFAULT_ENCODING);
	}

	/**
	 * List �����͸� XML �������� Ŭ���̾�Ʈ�� ����Ѵ�.
	 * <br>
	 * ex) list�� ����� ��� => renderXML(list, "utf-8")
	 * @param list ����� List ������
	 * @param encoding ���ڵ�
	 */
	protected void renderXML(List<Map<String, Object>> list, String encoding) {
		setContentType("text/xml; charset=" + encoding);
		out.write(XmlUtil.render(list, encoding));
	}

	/** 
	 * ��û�� JSP�������� ������(Redirect) �Ѵ�.
	 * �ۼ��� JSP��������  routes.properties�� ����Ѵ�.
	 * <br>
	 * ex) Ű�� search-jsp �� JSP�������� ������ �� ��� => redirect("search-jsp")
	 * @param key routes.properties ���Ͽ� ��ϵ� JSP �������� Ű
	 */
	protected void redirect(String key) {
		_route(key, false);
	}

	/** 
	 * ����Ÿ���̽� ���������(���ؼ� �Ŵ���) ��ü�� �����Ѵ�.
	 * <br>
	 * config.properties�� datasource�� ��ϵǾ� ������ JNDI�� ��ϵǾ��ִ� ����Ÿ�ҽ����� ���ؼ��� �����Ѵ�.
	 * datasource�� ��ϵǾ� ���� �ʴ� ��� ���������� �������� jdbc ���ؼ��� �����Ѵ�.
	 * �������� default�� �ش��ϴ� �������� ������ �̿��Ͽ� ���ؼ��� �����Ѵ�.
	 * ������ ���ؼ��� autoCommit �Ӽ��� false �� ���õȴ�.
	 * @return ���������(���ؼ� �Ŵ���) ��ü
	 */
	protected ConnectionManager getConnectionManager() {
		return getConnectionManager("default");
	}

	/** 
	 * ����Ÿ���̽� ���������(���ؼ� �Ŵ���) ��ü�� �����Ѵ�.
	 * <br>
	 * config.properties�� datasource�� ��ϵǾ� ������ JNDI�� ��ϵǾ��ִ� ����Ÿ�ҽ����� ���ؼ��� �����Ѵ�.
	 * datasource�� ��ϵǾ� ���� �ʴ� ��� ���������� �������� jdbc ���ؼ��� �����Ѵ�.
	 * �Ķ���ͷ� �Ѱ��� ������ �ش��ϴ� �������� ������ �̿��Ͽ� ���ؼ��� �����Ѵ�.
	 * ������ ���ؼ��� autoCommit �Ӽ��� false �� ���õȴ�.
	 * @param serviceName ���񽺸�(������)
	 * @return ���������(���ؼ� �Ŵ���) ��ü
	 */
	protected ConnectionManager getConnectionManager(String serviceName) {
		if (!_connMgrMap.containsKey(serviceName)) {
			String dsName = null;
			String jdbcDriver = null;
			String jdbcUrl = null;
			String jdbcUid = null;
			String jdbcPw = null;
			try {
				dsName = getConfig().getString("jdbc." + serviceName + ".datasource");
			} catch (Exception e) {
				// �������Ͽ� ����Ÿ�ҽ��� ���ǵǾ����� ������ ����
				jdbcDriver = getConfig().getString("jdbc." + serviceName + ".driver");
				jdbcUrl = getConfig().getString("jdbc." + serviceName + ".url");
				jdbcUid = getConfig().getString("jdbc." + serviceName + ".uid");
				jdbcPw = getConfig().getString("jdbc." + serviceName + ".pwd");
			}
			try {
				ConnectionManager connMgr = new ConnectionManager(dsName, this);
				if (dsName != null) {
					connMgr.connect();
				} else {
					connMgr.connect(jdbcDriver, jdbcUrl, jdbcUid, jdbcPw);
				}
				connMgr.setAutoCommit(false);
				_connMgrMap.put(serviceName, connMgr);
			} catch (Exception e) {
				logger.error("DB Connection Error!", e);
			}
		}
		return _connMgrMap.get(serviceName);
	}

	/** 
	 * ���������� ������ �ִ� ��ü�� �����Ͽ� �����Ѵ�.
	 * @return config.properties�� ���������� ������ �ִ� ��ü
	 */
	protected Config getConfig() {
		return Config.getInstance();
	}

	/** 
	 * ���ǰ�ü���� �ش� Ű�� �ش��ϴ� ������Ʈ�� �����Ѵ�.
	 * <br>
	 * ex) ���ǿ��� result��� Ű�� ������Ʈ�� ���Ϲ޴� ��� => Object obj = getSessionAttribute("result")
	 * @param key ���ǰ�ü�� ��ȸŰ
	 * @return ���ǰ�ü���� ���� ������Ʈ
	 */
	protected Object getSessionAttribute(String key) {
		return session.getAttribute(key);
	}

	/**
	 * ���䰴ü�� Ŭ���̾�Ʈ���� �����ϱ� ���� ������Ÿ���� �����Ѵ�. 
	 * <br>
	 * ex1) xml������ ���� �ϴ� ��� => setContentType("text/xml; charset=utf-8")
	 * <br>
	 * ex2) �ؽ�Ʈ ������ �����ϴ� ��� => setContentType("text/plain; charset=euc-kr")
	 * @param contentType ���䰴ü�� ������ ������ Ÿ��
	 */
	protected void setContentType(String contentType) {
		response.setContentType(contentType);
	}

	/** 
	 * ��û��ü�� Ű,�� �Ӽ��� �����Ѵ�.
	 * Controller���� ó���� ����� �� �� �ѱ涧 ��û��ü�� �Ӽ��� �����Ͽ� ������Ѵ�.
	 * <br>
	 * ex) rs��� RecordSet ��ü�� result ��� Ű�� ��û��ü�� �����ϴ� ��� => setAttribute("result", re) 
	 * @param key �Ӽ��� Ű ���ڿ�
	 * @param value �Ӽ��� �� ��ü
	 */
	protected void setAttribute(String key, Object value) {
		request.setAttribute(key, value);
	}

	/** 
	 * ���ǰ�ü�� Ű,�� �Ӽ��� �����Ѵ�.
	 * Controller���� ó���� ����� ���ǿ� �����Ѵ�.
	 * <br>
	 * ex) userinfo ��� �����������ü�� userinfo ��� Ű�� ���ǰ�ü�� �����ϴ� ��� => setSessionAttribute("userinfo", userinfo)
	 * @param key �Ӽ��� Ű ���ڿ�
	 * @param value �Ӽ��� �� ��ü
	 */
	protected void setSessionAttribute(String key, Object value) {
		session.setAttribute(key, value);
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private �޼ҵ�
	private void _setServlet(HttpServlet ser) {
		servlet = ser;
	}

	private void _setRequest(HttpServletRequest req) {
		request = req;
	}

	private void _setSession(HttpSession ses) {
		session = ses;
	}

	private void _setParams(HttpServletRequest req) {
		params = Params.getParams(request);
	}

	private void _route(String key, boolean isForward) {
		try {
			Router router = new Router(key, isForward);
			router.route(servlet, request, response);
		} catch (Exception e) {
			logger.error("Router Error!", e);
		}
	}

	private void _setResponse(HttpServletResponse res) {
		response = res;
	}

	private void _setOut(HttpServletResponse response) {
		try {
			out = response.getWriter();
		} catch (IOException e) {
		}
	}

	private void _destroy() {
		ConnectionManager connMgr = null;
		for (String key : _connMgrMap.keySet()) {
			connMgr = _connMgrMap.get(key);
			if (connMgr != null) {
				connMgr.release();
				connMgr = null;
			}
		}
		_connMgrMap.clear();
		params = null;
		out = null;
	}

	private Method _getMethod(String methodName) {
		if (methodName == null || "".equals(methodName.trim())) {
			methodName = "index";
		}
		if (!methodName.startsWith("_")) { // ����ٷ� �����ϴ� �Լ��� ȣ�� �Ұ�
			Method method[] = getClass().getMethods();
			for (int i = 0; i < method.length; i++) {
				if (method[i].getName().equals(methodName)) {
					return method[i];
				}
			}
		}
		return null;
	}
}