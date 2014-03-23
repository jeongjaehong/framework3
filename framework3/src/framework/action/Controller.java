/** 
 * @(#)Controller.java
 */
package framework.action;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import framework.config.Config;
import framework.db.DB;

/** 
 * �����Ͻ� ������ ó���ϴ� Ŭ������ ��ӹ޾ƾ� �� �߻�Ŭ�����̴�.
 * ��������(jsp ������)�� ����Ǳ� ���� Ŭ���̾�Ʈ���� ������ ���۵� �����͸� ���ϰ� ���������� �ݿ��ϱ� 
 * ���� ��ó��(Pre-processing)����̴�. �ϳ��� ���񽺿� ���� �������� ���������� ������Ʈ ���·� �����Ͽ� ����� �� �ִ�. 
 * �ۼ��� Controller�� routes.properties�� ����Ѵ�.
 */
public abstract class Controller {
	private Map<String, DB> _dbMap = new HashMap<String, DB>();

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
	 * ������� ��� �ؽ����̺�
	 */
	protected Params headers = null;

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
	 * ��Ʈ�ѷ� �̸�
	 */
	protected String controller = null;

	/**
	 * �׼Ǹ޼ҵ� �̸�
	 */
	protected String actionMethod = null;

	/**
	 * �׼� �̸�(��Ʈ�ѷ�.�׼Ǹ޼ҵ�)
	 */
	protected String action = null;

	/**
	 * Controller�� �ΰŰ�ü
	 */
	protected static Log logger = LogFactory.getLog(framework.action.Controller.class);

	/** 
	 * Ŭ���̾�Ʈ���� ���񽺸� ȣ���� �� ��û�Ķ���� action�� ������ ���� �����Ͽ� �ش� �޼ҵ带 �����Ѵ�.
	 * ���ǵ��� ���� �޼ҵ带 ȣ���� ��� �α׿� �����޽����� ��ϵǸ� �޼ҵ� ������ ��ģ �� �����ͺ��̽� ������ �ڵ����� �ݾ��ش�.
	 * <br>
	 * ex) action�� search �϶� : search() �޼ҵ尡 ȣ��ȴ�.
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
		this.headers = Params.getParamsFromHeader(request);
		this.session = request.getSession();
		this.response = response;
		this.out = response.getWriter();
		this.controller = getClass().getName();
		this.actionMethod = method.getName();
		this.action = this.controller + "." + this.actionMethod;
		long currTime = 0;
		if (logger.isDebugEnabled()) {
			currTime = System.currentTimeMillis();
			logger.debug("Start Class : " + this.controller + ", Method : " + this.actionMethod);
			logger.debug(this.params.toString());
			logger.debug(this.cookies.toString());
		}
		try {
			_before();
			method.invoke(this, (Object[]) null);
			_after();
		} catch (Exception e) {
			_catch(e);
			throw e;
		} finally {
			_destroy();
			_finally();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("End | duration : " + (System.currentTimeMillis() - currTime) + " msec");
		}
	}

	/**
	 * ��û�� JSP�������� ������(Forward) �Ѵ�.
	 * �ۼ��� JSP�������� routes.properties�� ����Ѵ�.
	 * <br>
	 * ex) Ű�� search-jsp �� JSP�������� ������ �� ��� : render("search-jsp")
	 * @param key routes.properties ���Ͽ� ��ϵ� JSP �������� Ű
	 */
	protected void render(String key) {
		try {
			ResourceBundle bundle = (ResourceBundle) servlet.getServletContext().getAttribute("routes-mapping");
			String url = ((String) bundle.getObject(key)).trim();
			servlet.getServletContext().getRequestDispatcher(response.encodeURL(url)).forward(request, response);
			if (logger.isDebugEnabled()) {
				logger.debug("�١١� " + request.getRemoteAddr() + " �� ���� \"" + request.getMethod() + " " + request.getRequestURI() + "\" ��û�� \"" + url + "\" �� forward �Ǿ����ϴ�");
			}
		} catch (Exception e) {
			logger.error("Render Error!", e);
		}
	}

	/** 
	 * ��û�� JSP�������� ������(Redirect) �Ѵ�.
	 * �ۼ��� JSP��������  routes.properties�� ����Ѵ�.
	 * <br>
	 * ex) Ű�� search-jsp �� JSP�������� ������ �� ��� : redirect("search-jsp")
	 * @param key routes.properties ���Ͽ� ��ϵ� JSP �������� Ű
	 */
	protected void redirect(String key) {
		try {
			String url = null;
			if (Pattern.matches("^/.+", key)) {
				url = request.getContextPath() + response.encodeRedirectURL(key);
				url = url.replaceAll("/+", "/");
			} else {
				url = response.encodeRedirectURL(key);
			}
			response.sendRedirect(url);
			if (logger.isDebugEnabled()) {
				logger.debug("�١١� " + request.getRemoteAddr() + " �� ���� \"" + request.getMethod() + " " + request.getRequestURI() + "\" ��û�� \"" + url + "\" �� redirect �Ǿ����ϴ�");
			}
		} catch (Exception e) {
			logger.error("Redirect Error!", e);
		}
	}

	/** 
	 * ����Ÿ���̽� ��ü�� �����Ѵ�.
	 * <br>
	 * config.properties�� datasource�� ��ϵǾ� ������ JNDI�� ��ϵǾ��ִ� ����Ÿ�ҽ����� ���ؼ��� �����Ѵ�.
	 * datasource�� ��ϵǾ� ���� �ʴ� ��� ���������� �������� jdbc ���ؼ��� �����Ѵ�.
	 * �������� default�� �ش��ϴ� �������� ������ �̿��Ͽ� ���ؼ��� �����Ѵ�.
	 * ������ ���ؼ��� autoCommit �Ӽ��� false �� ���õȴ�.
	 * @return DB ��ü
	 */
	protected DB getDB() {
		return getDB("default");
	}

	/** 
	 * ����Ÿ���̽� ��ü�� �����Ѵ�.
	 * <br>
	 * config.properties�� datasource�� ��ϵǾ� ������ JNDI�� ��ϵǾ��ִ� ����Ÿ�ҽ����� ���ؼ��� �����Ѵ�.
	 * datasource�� ��ϵǾ� ���� �ʴ� ��� ���������� �������� jdbc ���ؼ��� �����Ѵ�.
	 * �Ķ���ͷ� �Ѱ��� ������ �ش��ϴ� �������� ������ �̿��Ͽ� ���ؼ��� �����Ѵ�.
	 * ������ ���ؼ��� autoCommit �Ӽ��� false �� ���õȴ�.
	 * @param serviceName ���񽺸�(������)
	 * @return DB ��ü
	 */
	protected DB getDB(String serviceName) {
		if (!_dbMap.containsKey(serviceName)) {
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
				DB db = new DB(dsName, this);
				if (dsName != null) {
					db.connect();
				} else {
					db.connect(jdbcDriver, jdbcUrl, jdbcUid, jdbcPw);
				}
				db.setAutoCommit(false);
				_dbMap.put(serviceName, db);
			} catch (Exception e) {
				logger.error("DB Connection Error!", e);
			}
		}
		return _dbMap.get(serviceName);
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
	 * ex) ���ǿ��� result��� Ű�� ������Ʈ�� ���Ϲ޴� ��� : Object obj = getSessionAttribute("result")
	 * @param key ���ǰ�ü�� ��ȸŰ
	 * @return ���ǰ�ü���� ���� ������Ʈ
	 */
	protected Object getSessionAttribute(String key) {
		return session.getAttribute(key);
	}

	/**
	 * ���䰴ü�� Ŭ���̾�Ʈ���� �����ϱ� ���� ������Ÿ���� �����Ѵ�. 
	 * <br>
	 * ex1) xml������ ���� �ϴ� ��� : setContentType("text/xml; charset=utf-8")
	 * <br>
	 * ex2) �ؽ�Ʈ ������ �����ϴ� ��� : setContentType("text/plain; charset=euc-kr")
	 * @param contentType ���䰴ü�� ������ ������ Ÿ��
	 */
	protected void setContentType(String contentType) {
		response.setContentType(contentType);
	}

	/** 
	 * ��û��ü�� Ű,�� �Ӽ��� �����Ѵ�.
	 * Controller���� ó���� ����� �� �� �ѱ涧 ��û��ü�� �Ӽ��� �����Ͽ� ������Ѵ�.
	 * <br>
	 * ex) rs��� RecordSet ��ü�� result ��� Ű�� ��û��ü�� �����ϴ� ��� : setAttribute("result", re) 
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
	 * ex) userinfo ��� �����������ü�� userinfo ��� Ű�� ���ǰ�ü�� �����ϴ� ��� : setSessionAttribute("userinfo", userinfo)
	 * @param key �Ӽ��� Ű ���ڿ�
	 * @param value �Ӽ��� �� ��ü
	 */
	protected void setSessionAttribute(String key, Object value) {
		session.setAttribute(key, value);
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private �޼ҵ�

	/*
	 * Play framework ����
	 */
	private void _before() throws Exception {
		List<Method> beforeMethods = _getAnnotationMethods(Before.class);
		Collections.sort(beforeMethods, new Comparator<Method>() {
			public int compare(Method m1, Method m2) {
				Before before1 = m1.getAnnotation(Before.class);
				Before before2 = m2.getAnnotation(Before.class);
				return before1.priority() - before2.priority();
			}
		});
		for (Method beforeMethod : beforeMethods) {
			String[] only = beforeMethod.getAnnotation(Before.class).only();
			String[] unless = beforeMethod.getAnnotation(Before.class).unless();
			boolean skip = false;
			for (String o : only) {
				if (!o.contains(".")) {
					o = getClass().getName() + "." + o;
				}
				if (o.equals(this.action)) {
					skip = false;
					break;
				} else {
					skip = true;
				}
			}
			for (String u : unless) {
				if (!u.contains(".")) {
					u = getClass().getName() + "." + u;
				}
				if (u.equals(this.action)) {
					skip = true;
					break;
				}
			}
			if (!skip) {
				if (logger.isDebugEnabled()) {
					logger.debug("@Before Class : " + beforeMethod.getDeclaringClass().getName() + ", Method : " + beforeMethod.getName());
				}
				beforeMethod.setAccessible(true);
				beforeMethod.invoke(this, (Object[]) null);
			}
		}
	}

	/*
	 * Play framework ����
	 */
	private void _after() throws Exception {
		List<Method> afterMethods = _getAnnotationMethods(After.class);
		Collections.sort(afterMethods, new Comparator<Method>() {
			public int compare(Method m1, Method m2) {
				After after1 = m1.getAnnotation(After.class);
				After after2 = m2.getAnnotation(After.class);
				return after1.priority() - after2.priority();
			}
		});
		for (Method afterMethod : afterMethods) {
			String[] only = afterMethod.getAnnotation(After.class).only();
			String[] unless = afterMethod.getAnnotation(After.class).unless();
			boolean skip = false;
			for (String o : only) {
				if (!o.contains(".")) {
					o = getClass().getName() + "." + o;
				}
				if (o.equals(this.action)) {
					skip = false;
					break;
				} else {
					skip = true;
				}
			}
			for (String u : unless) {
				if (!u.contains(".")) {
					u = getClass().getName() + "." + u;
				}
				if (u.equals(this.action)) {
					skip = true;
					break;
				}
			}
			if (!skip) {
				if (logger.isDebugEnabled()) {
					logger.debug("@After Class : " + afterMethod.getDeclaringClass().getName() + ", Method : " + afterMethod.getName());
				}
				afterMethod.setAccessible(true);
				afterMethod.invoke(this, (Object[]) null);
			}
		}
	}

	/*
	 * Play framework ����
	 */
	private void _catch(Exception e) throws Exception {
		List<Method> catchMethods = _getAnnotationMethods(Catch.class);
		Collections.sort(catchMethods, new Comparator<Method>() {
			public int compare(Method m1, Method m2) {
				Catch catch1 = m1.getAnnotation(Catch.class);
				Catch catch2 = m2.getAnnotation(Catch.class);
				return catch1.priority() - catch2.priority();
			}
		});
		for (Method catchMethod : catchMethods) {
			Class<?>[] exceptionClasses = catchMethod.getAnnotation(Catch.class).value();
			if (exceptionClasses.length == 0) {
				exceptionClasses = new Class<?>[] { Exception.class };
			}
			for (Class<?> exceptionClass : exceptionClasses) {
				if (exceptionClass.isInstance(e)) {
					if (logger.isDebugEnabled()) {
						logger.debug("@Catch Class : " + catchMethod.getDeclaringClass().getName() + ", Method : " + catchMethod.getName());
					}
					catchMethod.setAccessible(true);
					catchMethod.invoke(this, (Object[]) null);
					break;
				}
			}
		}
	}

	/*
	 * Play framework ����
	 */
	private void _finally() throws Exception {
		List<Method> finallyMethods = _getAnnotationMethods(Finally.class);
		Collections.sort(finallyMethods, new Comparator<Method>() {
			public int compare(Method m1, Method m2) {
				Finally finally1 = m1.getAnnotation(Finally.class);
				Finally finally2 = m2.getAnnotation(Finally.class);
				return finally1.priority() - finally2.priority();
			}
		});
		for (Method finallyMethod : finallyMethods) {
			String[] only = finallyMethod.getAnnotation(Finally.class).only();
			String[] unless = finallyMethod.getAnnotation(Finally.class).unless();
			boolean skip = false;
			for (String o : only) {
				if (!o.contains(".")) {
					o = getClass().getName() + "." + o;
				}
				if (o.equals(this.action)) {
					skip = false;
					break;
				} else {
					skip = true;
				}
			}
			for (String u : unless) {
				if (!u.contains(".")) {
					u = getClass().getName() + "." + u;
				}
				if (u.equals(this.action)) {
					skip = true;
					break;
				}
			}
			if (!skip) {
				if (logger.isDebugEnabled()) {
					logger.debug("@Finally Class : " + finallyMethod.getDeclaringClass().getName() + ", Method : " + finallyMethod.getName());
				}
				finallyMethod.setAccessible(true);
				finallyMethod.invoke(this, (Object[]) null);
			}
		}
	}

	private List<Method> _getAnnotationMethods(Class<? extends Annotation> annotation) {
		List<Method> methods = new ArrayList<Method>();
		for (Method method : getClass().getMethods()) {
			if (method.isAnnotationPresent(annotation)) {
				methods.add(method);
			}
		}
		return methods;
	}

	/*
	 * DB ���ؼ� ����
	 */
	private void _destroy() {
		try {
			DB db = null;
			for (String key : _dbMap.keySet()) {
				db = _dbMap.get(key);
				if (db != null) {
					db.release();
					db = null;
				}
			}
			_dbMap.clear();
			params = null;
			out = null;
		} catch (Exception e) {
		}
	}
}