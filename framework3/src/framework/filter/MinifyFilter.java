package framework.filter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

import framework.util.StringUtil;

/**
 * HTML, JavaScript, CSS Minify filter
 */
public class MinifyFilter implements Filter {
	private HtmlCompressor _compressor;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		MyResponseWrapper resWrapper = null;
		try {
			resWrapper = new MyResponseWrapper((HttpServletResponse) response);
			filterChain.doFilter(request, resWrapper);
			String contentType = StringUtil.nullToBlankString(resWrapper.getContentType()).toLowerCase();
			if ("".equals(contentType) || contentType.contains("text") || contentType.contains("json") || contentType.contains("xml")) {
				PrintWriter writer = response.getWriter();
				String content = resWrapper.toString();
				if (contentType.contains("html") || contentType.contains("xml") || contentType.contains("javascript") || contentType.contains("css")) {
					writer.print(_compressor.compress(content));
				} else {
					writer.print(content);
				}
				writer.flush();
			} else {
				resWrapper.writeTo(response.getOutputStream());
			}
		} finally {
			if (resWrapper != null) {
				resWrapper.close();
				resWrapper = null;
			}
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		_compressor = new HtmlCompressor();
		_compressor.setCompressCss(true);
		_compressor.setCompressJavaScript(true);
	}

	@Override
	public void destroy() {
	}

	class MyResponseWrapper extends HttpServletResponseWrapper {
		private ByteArrayOutputStream _bytes;
		private PrintWriter _writer;

		public MyResponseWrapper(HttpServletResponse p_res) {
			super(p_res);
			_bytes = new ByteArrayOutputStream(8192);
			_writer = new PrintWriter(new BufferedOutputStream(_bytes));
		}

		@Override
		public PrintWriter getWriter() {
			return _writer;
		}

		@Override
		public ServletOutputStream getOutputStream() {
			return new MyOutputStream(new BufferedOutputStream(_bytes));
		}

		@Override
		public String toString() {
			_writer.flush();
			return _bytes.toString();
		}

		public void writeTo(OutputStream os) throws IOException {
			BufferedOutputStream bos = new BufferedOutputStream(os);
			_bytes.writeTo(bos);
			bos.flush();
		}

		public void close() throws IOException {
			_bytes.close();
			_writer.close();
			_bytes = null;
			_writer = null;
		}
	}

	class MyOutputStream extends ServletOutputStream {
		private OutputStream _bytes;

		public MyOutputStream(OutputStream p_bytes) {
			_bytes = p_bytes;
		}

		@Override
		public void write(int p_c) throws IOException {
			_bytes.write(p_c);
		}

		@Override
		public void write(byte[] b) throws IOException {
			_bytes.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			_bytes.write(b, off, len);
		}

		@Override
		public void close() throws IOException {
			_bytes.close();
			super.close();
			_bytes = null;
		}
	}
}