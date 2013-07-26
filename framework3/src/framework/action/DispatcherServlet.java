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

/** 
 * ��Ʈ�ѷ� ������ �ϴ� �������� ��� Ŭ���̾�Ʈ�� ��û�� �޾� �ش� �׼��� �����Ѵ�.
 * Ȯ���ڰ� (.do)�� ����Ǵ� ��� ��û�� �� ������ ó���ϱ� ���Ͽ� web.xml ���Ͽ��� ������ �����Ͽ��� �ϸ�
 * ���� ���ý� �Ѱ��� ��ü�� ������ ���´�.  
 * ��û���� ������ �׼�Ű�� routes.properties���� ControllerŬ������ ã�� ��ü�� �����Ͽ� �����Ͻ� ���μ����� �����Ѵ�. 
 */
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = -6478697606075642071L;
	private static Log _logger = LogFactory.getLog(framework.action.DispatcherServlet.class);
	private String _404Page = null;
	private String _500Page = null;

	/**
	 * ���� ��ü�� �ʱ�ȭ �Ѵ�.
	 * web.xml�� �ʱ�ȭ �Ķ���ͷ� ��ϵǾ� �ִ� routes-mapping ���� ã�� ���ҽ� ������ �����ϴ� ������ �Ѵ�.
	 * @param config ServletConfig ��ü
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ResourceBundle bundle = null;
		try {
			bundle = ResourceBundle.getBundle(config.getInitParameter("routes-mapping"));
			_404Page = config.getInitParameter("404-page");
			_500Page = config.getInitParameter("500-page");
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
			if (controllerKey == null) {
				throw new NotFoundException();
			}
			String controllerClassName = _getControllerClassName(controllerKey);
			if (controllerClassName == null) {
				throw new NotFoundException();
			} else {
				Class<?> controllerClass = Class.forName(controllerClassName);
				Controller controller = (Controller) controllerClass.newInstance();
				long currTime = 0;
				if (_getLogger().isDebugEnabled()) {
					currTime = System.currentTimeMillis();
					_getLogger().debug("Start [ Pgm : " + controllerKey + " | Controller : " + controller + " ]");
				}
				controller.execute(this, request, response);
				if (_getLogger().isDebugEnabled()) {
					_getLogger().debug("End [ Pgm : " + controllerKey + " | Controller : " + controller + " ] TIME : " + (System.currentTimeMillis() - currTime) + "msec");
				}
			}
		} catch (NotFoundException e) {
			if (_404Page != null && !"".equals(_404Page)) {
				getServletContext().getRequestDispatcher(response.encodeURL(_404Page)).forward(request, response);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			_getLogger().error(e.getMessage());
			if (_500Page != null && !"".equals(_500Page)) {
				getServletContext().getRequestDispatcher(response.encodeURL(_500Page)).forward(request, response);
			} else {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
	}

	private String _getControllerKey(HttpServletRequest request) {
		String path = request.getServletPath();
		int slash = path.lastIndexOf("/");
		int period = path.lastIndexOf(".");
		if (period > 0 && period > slash) {
			path = path.substring(0, period);
			return path;
		}
		return null;
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