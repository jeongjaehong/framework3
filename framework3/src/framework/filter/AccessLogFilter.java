/* 
 * @(#)AccessLogFilter.java
 * Ŭ���̾�Ʈ ��û ���۰� ���Ḧ �α��ϴ� ����
 */
package framework.filter;

import java.io.IOException;
import java.lang.reflect.Array;

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
		if (_getLogger().isDebugEnabled()) {
			_getLogger().debug("�ڡڡ� " + httpReq.getRemoteAddr() + " �� ���� \"" + httpReq.getMethod() + " " + httpReq.getRequestURI() + "\" ��û�� ���۵Ǿ����ϴ�");
			_getLogger().debug(_getParamString(httpReq));
			_getLogger().debug("ContentLength : " + httpReq.getContentLength() + "bytes");
		}
		filterChain.doFilter(request, response);
		if (_getLogger().isDebugEnabled()) {
			_getLogger().debug("�ڡڡ� " + httpReq.getRemoteAddr() + " �� ���� \"" + httpReq.getMethod() + " " + httpReq.getRequestURI() + "\" ��û�� ����Ǿ����ϴ�\n");
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	private String _getParamString(HttpServletRequest request) {
		StringBuilder buf = new StringBuilder();
		buf.append("{ ");
		long currentRow = 0;
		for (Object obj : request.getParameterMap().keySet()) {
			String key = (String) obj;
			String value = null;
			Object o = request.getParameterValues(key);
			if (o == null) {
				value = "";
			} else {
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
			}
			if (currentRow++ > 0) {
				buf.append(", ");
			}
			buf.append(key + "=" + value);
		}
		buf.append(" }");
		return "Param[request]=" + buf.toString();
	}

	private Log _getLogger() {
		return this._logger;
	}
}