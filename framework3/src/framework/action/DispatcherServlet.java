/** 
 * @(#)DispatcherServlet.java
 */
package framework.action;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import framework.cache.Cache;
import framework.util.StringUtil;

/** 
 * ��Ʈ�ѷ� ������ �ϴ� �������� ��� Ŭ���̾�Ʈ�� ��û�� �޾� �ش� �׼��� �����Ѵ�.
 * Ȯ���ڰ� (.do)�� ����Ǵ� ��� ��û�� �� ������ ó���ϱ� ���Ͽ� web.xml ���Ͽ��� ������ �����Ͽ��� �ϸ�
 * ���� ���ý� �Ѱ��� ��ü�� ������ ���´�.  
 * ��û���� ������ �׼�Ű�� routes.properties���� ControllerŬ������ ã�� ��ü�� �����Ͽ� �����Ͻ� ���μ����� �����Ѵ�. 
 */
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = -6478697606075642071L;
	private static Log _logger = LogFactory.getLog(framework.action.DispatcherServlet.class);
	private static String _defaultServletName = null;
	private static final String[] _DEFAULT_SERVLET_NAMES = new String[] { "default", "WorkerServlet", "FileServlet", "resin-file", "SimpleFileServlet", "_ah_default" };

	/**
	 * ���� ��ü�� �ʱ�ȭ �Ѵ�.
	 * web.xml�� �ʱ�ȭ �Ķ���ͷ� ��ϵǾ� �ִ� routes-mapping ���� ã�� ���ҽ� ������ �����ϴ� ������ �Ѵ�.
	 * @param config ServletConfig ��ü
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ResourceBundle bundle = null;
		try {
			bundle = ResourceBundle.getBundle(config.getInitParameter("routes-mapping"));
			_defaultServletName = StringUtil.nullToBlankString(config.getInitParameter("default-servlet-name"));
			if ("".equals(_defaultServletName)) {
				for (String servletName : _DEFAULT_SERVLET_NAMES) {
					if (getServletContext().getNamedDispatcher(servletName) != null) {
						_defaultServletName = servletName;
						break;
					}
				}
			}
			if (getServletContext().getNamedDispatcher(_defaultServletName) == null) {
				throw new IllegalStateException("defaultServletName Error!");
			}
		} catch (MissingResourceException e) {
			throw new ServletException(e);
		}
		getServletContext().setAttribute("routes-mapping", bundle);
		// Cache
		Cache.init();
	}

	/**
	 * Ŭ���̾�Ʈ�� Get ������� ��û�� ��� processRequest�� ó���� �̰��Ѵ�.
	 * @param request HTTP Ŭ���̾�Ʈ ��û��ü
	 * @param response HTTP Ŭ���̾�Ʈ ���䰴ü
	 * @exception java.io.IOException DispatcherServlet���� IO�� ���õ� ������ �߻��� ��� 
	 * @exception javax.servlet.ServletException ������ ���õ� ������ �߻��� ���
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		_processRequest(request, response);
	}

	/**
	 * Ŭ���̾�Ʈ�� Post ������� ��û�� ��� processRequest�� ó���� �̰��Ѵ�.
	 * @param request HTTP Ŭ���̾�Ʈ ��û��ü
	 * @param response HTTP Ŭ���̾�Ʈ ���䰴ü
	 * @exception java.io.IOException Servlet���� IO�� ���õ� ������ �߻��� ��� 
	 * @exception javax.servlet.ServletException ������ ���õ� ������ �߻��� ���
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		_processRequest(request, response);
	}

	/**
	 * Ŭ���̾�Ʈ�� Put ������� ��û�� ��� processRequest�� ó���� �̰��Ѵ�.
	 * @param request HTTP Ŭ���̾�Ʈ ��û��ü
	 * @param response HTTP Ŭ���̾�Ʈ ���䰴ü
	 * @exception java.io.IOException DispatcherServlet���� IO�� ���õ� ������ �߻��� ��� 
	 * @exception javax.servlet.ServletException ������ ���õ� ������ �߻��� ���
	 */
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		_processRequest(request, response);
	}

	/**
	 * Ŭ���̾�Ʈ�� Delete ������� ��û�� ��� processRequest�� ó���� �̰��Ѵ�.
	 * @param request HTTP Ŭ���̾�Ʈ ��û��ü
	 * @param response HTTP Ŭ���̾�Ʈ ���䰴ü
	 * @exception java.io.IOException DispatcherServlet���� IO�� ���õ� ������ �߻��� ��� 
	 * @exception javax.servlet.ServletException ������ ���õ� ������ �߻��� ���
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		_processRequest(request, response);
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private �޼ҵ�

	private void _processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String controllerKey = _getControllerKey(request);
			String controllerClassName = _getControllerClassName(controllerKey);
			if (controllerClassName == null) {
				throw new PageNotFoundExeption("controller class");
			} else {
				Class<?> controllerClass = Class.forName(controllerClassName);
				Controller controller = (Controller) controllerClass.newInstance();
				long currTime = 0;
				if (_getLogger().isDebugEnabled()) {
					currTime = System.currentTimeMillis();
					_getLogger().debug("Start [ Controller : " + controllerKey + " | ClassName : " + controllerClassName + " ]");
				}
				controller.execute(this, request, response);
				if (_getLogger().isDebugEnabled()) {
					_getLogger().debug("End | duration : " + (System.currentTimeMillis() - currTime) + " msec");
				}
			}
		} catch (PageNotFoundExeption e) {
			this.getServletContext().getNamedDispatcher(_defaultServletName).forward(request, response);
		} catch (Exception e) {
			_getLogger().error(e);
			throw new RuntimeException(e);
		}
	}

	private String _getControllerKey(HttpServletRequest request) {
		String path = request.getServletPath() + StringUtil.nullToBlankString(request.getPathInfo());
		return path;
	}

	private String _getControllerClassName(String controllerKey) {
		try {
			ResourceBundle bundle = (ResourceBundle) getServletContext().getAttribute("routes-mapping");
			return ((String) bundle.getObject(controllerKey)).trim();
		} catch (MissingResourceException e) {
			return null;
		}
	}

	private Log _getLogger() {
		return DispatcherServlet._logger;
	}
}