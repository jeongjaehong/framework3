/* 
 * @(#)AccessLogFilter.java
 * Ŭ���̾�Ʈ ��û ���۰� ���Ḧ �α��ϴ� ����
 */
package framework.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AccessLogFilter implements Filter {
	private Log _logger = LogFactory.getLog(framework.filter.AccessLogFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		long currTime = 0;
		if (_getLogger().isDebugEnabled()) {
			currTime = System.currentTimeMillis();
			_getLogger().debug("�ڡڡ� " + httpReq.getRemoteAddr() + " �� ���� \"" + httpReq.getMethod() + " " + httpReq.getRequestURI() + "\" ��û�� ���۵Ǿ����ϴ�");
			_getLogger().debug("ContentLength : " + httpReq.getContentLength() + "bytes");
		}
		filterChain.doFilter(request, response);
		if (_getLogger().isDebugEnabled()) {
			_getLogger().debug("�١١� " + httpReq.getRemoteAddr() + " �� ���� \"" + httpReq.getMethod() + " " + httpReq.getRequestURI() + "\" ��û�� ����Ǿ����ϴ� | duration : " + (System.currentTimeMillis() - currTime) + " msec\n");
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	private Log _getLogger() {
		return this._logger;
	}
}