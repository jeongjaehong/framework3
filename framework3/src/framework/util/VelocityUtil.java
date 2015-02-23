package framework.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import framework.action.Params;

/**
 * Velocity�� �̿��� ���ø� ó�� ���̺귯��
 */
public class VelocityUtil {
	protected static final Log logger = LogFactory.getLog(framework.util.VelocityUtil.class);

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private VelocityUtil() {
	}

	/**
	 * mapping.properties ���Ͽ� ������ key�� ����� ���ø� ���Ͽ��� statement�� ���ǵ� COMMAND�� ���ڿ��� �Ķ���͸� 
	 * ������ ���ڿ��� �����Ѵ�. VelocityUtil.evalutate�� ����
	 * <br>
	 * Sql ������� �� �̸��� �߼��� ���� ���ø� �����Ҷ� ������ �� �ִ�.
	 * @param servlet ���� ��ü
	 * @param key routes.properties�� ����� ���ø��� Ű ���ڿ� 
	 * @param statement ����ĺ� ���ڿ�
	 * @param param �Ķ���� Param ��ü
	 * @return ���ø��� ����� ���ڿ�
	 */
	public static String render(HttpServlet servlet, String key, String statement, Params param) {
		return _evaluate(servlet, key, statement, param);
	}

	/**
	 * routes.properties ���Ͽ� ������ key�� ����� ���ø� ���Ͽ��� statement�� ���ǵ� COMMAND�� ���ڿ��� �Ķ���͸� 
	 * ������ ���ڿ��� �����Ѵ�.
	 */
	private static String _evaluate(HttpServlet servlet, String key, String statement, Params param) {
		StringWriter writer = new StringWriter();
		try {
			Velocity.init();
			VelocityContext context = new VelocityContext();
			context.put("COMMAND", statement);
			context.put("PARAM", param);
			context.put("UTIL", StringUtil.class);

			ResourceBundle bundle = (ResourceBundle) servlet.getServletContext().getAttribute("routes-mapping");
			String fileName = ((String) bundle.getObject(key)).trim();

			String template = _readTemplate(servlet, fileName);
			StringReader reader = new StringReader(template);
			Velocity.evaluate(context, writer, "framework.util.VelocityUtil", reader);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		return writer.toString();
	}

	/**
	 * ���ø������� �о���δ�.
	 */
	private static String _readTemplate(HttpServlet servlet, String fileName) {
		String pathFile = servlet.getServletContext().getRealPath(fileName);
		return _read(pathFile);
	}

	/** 
	 * ������ path�� ���� ���ϸ����� ���� ���� �о String���� �����Ѵ� 
	 */
	private static String _read(String pathFile) {
		StringBuilder ta = new StringBuilder();
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(pathFile);
			br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				ta.append(line + "\n");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error(e);
					}
				}
			}
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error(e);
					}
				}
			}
		}
		return ta.toString();
	}
}