/** 
 * @(#)Router.java
 */
package framework.action;

import java.util.ResourceBundle;

import javax.servlet.GenericServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * Ŭ���̾�Ʈ ��û�� �����(������ �Ǵ� ������) ���ִ� Ŭ�����̴�.
 * routes.properties�� ��ϵǾ��� Ű ���� ���εǾ� �ִ� JSP �������� ã�� �Ķ���ͷ� �Է¹��� �÷��׸� ���� ����������
 * ������ ������ �����ϰ� �ȴ�.
 */
public class Router {
	private final String _key;
	private final boolean _isForward;
	private static Log _logger = LogFactory.getLog(framework.action.Router.class);

	/**
	 * ��û�� JSP�������� ������(Forward) �ϱ����� ��ü�� �����ȴ�.
	 * 
	 * @param key routes.properties ���Ͽ� ��ϵ� JSP �������� Ű
	 */
	public Router(String key) {
		this(key, true);
	}

	/**
	 * ��û�� JSP�������� ������(Forward) �Ǵ� ������(Redirect) �ϱ����� ��ü�� �����ȴ�.
	 * 
	 * @param key routes.properties ���Ͽ� ��ϵ� JSP �������� Ű
	 * @param isForward true �̸� ������, false �̸� ������ �ϱ����� �÷���
	 */
	public Router(String key, boolean isForward) {
		_key = key;
		_isForward = isForward;
	}

	/**
	 * ���� ��û�� ����� �ϰ� �ȴ�.
	 * 
	 * @param servlet ��ü�� ȣ���� ����
	 * @param request Ŭ���̾�Ʈ���� ��û�� Request��ü
	 * @param response Ŭ���̾�Ʈ�� ������ Response��ü
	 */
	public synchronized void route(GenericServlet servlet, HttpServletRequest request, HttpServletResponse response) {
		try {
			if (_isForward) {
				ResourceBundle bundle = (ResourceBundle) servlet.getServletContext().getAttribute("routes-mapping");
				String url = ((String) bundle.getObject(_key)).trim();
				servlet.getServletContext().getRequestDispatcher(response.encodeURL(url)).forward(request, response);
				if (getLogger().isDebugEnabled()) {
					getLogger().debug("�١١� " + request.getRemoteAddr() + " �� ���� \"" + request.getMethod() + " " + request.getRequestURI() + "\" ��û�� \"" + url + "\" �� forward �Ǿ����ϴ�");
				}
			} else {
				String url = request.getContextPath() + "/" + response.encodeRedirectURL(_key);
				String normalizeURL = url.replaceAll("/+", "/");
				response.sendRedirect(normalizeURL);
				if (getLogger().isDebugEnabled()) {
					getLogger().debug("�١١� " + request.getRemoteAddr() + " �� ���� \"" + request.getMethod() + " " + request.getRequestURI() + "\" ��û�� \"" + normalizeURL + "\" �� redirect �Ǿ����ϴ�");
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 
	 * Router�� �ΰŰ�ü�� �����Ѵ�.
	 * ��� �α״� �ش� �ΰŸ� �̿��ؼ� ����Ͽ��� �Ѵ�.
	 * <br>
	 * ex1) ���� ������ ����� ��� : getLogger().error("...�����޽�������")
	 * <br>
	 * ex2) ����� ������ ����� ��� : getLogger().debug("...����׸޽�������")
	 *
	 * @return Router�� �ΰŰ�ü
	 */
	protected Log getLogger() {
		return Router._logger;
	}
}