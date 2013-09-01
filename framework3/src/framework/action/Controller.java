/** 
 * @(#)Controller.java
 */
package framework.action;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import framework.config.Config;
import framework.db.ConnectionManager;

/** 
 * �����Ͻ� ������ ó���ϴ� Ŭ������ ��ӹ޾ƾ� �� �߻�Ŭ�����̴�.
 * ��������(jsp ������)�� ����Ǳ� ���� Ŭ���̾�Ʈ���� ������ ���۵� �����͸� ���ϰ� ���������� �ݿ��ϱ� 
 * ���� ��ó��(Pre-processing)����̴�. �ϳ��� ���񽺿� ���� �������� ���������� ������Ʈ ���·� �����Ͽ� ����� �� �ִ�. 
 * �ۼ��� Controller�� routes.properties�� ����Ѵ�.
 */
public abstract class Controller {
	private Map<String, ConnectionManager> _connMgrMap = new HashMap<String, ConnectionManager>();

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
	 * ��Ű���� ��� �ؽ����̺�
	 */
	protected Params cookies = null;

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
	 * @param method �޼ҵ�
	 * @throws Exception
	 */
	public void execute(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response, Method method) throws Exception {
		this.servlet = servlet;
		this.request = request;
		this.params = Params.getParams(request);
		this.cookies = Params.getParamsFromCookie(request);
		this.session = request.getSession();
		this.response = response;
		this.out = response.getWriter();
		long currTime = 0;
		if (logger.isDebugEnabled()) {
			currTime = System.currentTimeMillis();
			logger.debug("Start");
			logger.debug(this.params.toString());
			logger.debug(this.cookies.toString());
		}
		try {
			before();
			method.invoke(this, (Object[]) null);
			after();
		} finally {
			_destroy();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("End | duration : " + (System.currentTimeMillis() - currTime) + " msec");
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
		try {
			Router router = new Router(jsp, true);
			router.route(servlet, request, response);
		} catch (Exception e) {
			logger.error("Render Error!", e);
		}
	}

	/** 
	 * ��û�� JSP�������� ������(Redirect) �Ѵ�.
	 * �ۼ��� JSP��������  routes.properties�� ����Ѵ�.
	 * <br>
	 * ex) Ű�� search-jsp �� JSP�������� ������ �� ��� => redirect("search-jsp")
	 * @param key routes.properties ���Ͽ� ��ϵ� JSP �������� Ű
	 */
	protected void redirect(String key) {
		try {
			Router router = new Router(key, false);
			router.route(servlet, request, response);
		} catch (Exception e) {
			logger.error("Redirect Error!", e);
		}
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
}