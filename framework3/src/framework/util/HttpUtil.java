/* 
 * @(#)HttpUtil.java
 */
package framework.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * HTTP Ŭ���̾�Ʈ�� ����� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class HttpUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private HttpUtil() {
	}

	/**
	 * Result ��ü
	 */
	public static class Result {
		private int _statusCode;
		private String _content;

		public Result() {
			super();
		}

		public Result(int statusCode, String content) {
			super();
			this._statusCode = statusCode;
			this._content = content;
		}

		public int getStatusCode() {
			return _statusCode;
		}

		public String getContent() {
			return _content;
		}

		public String toString() {
			return String.format("Result={ statusCode : %d, content : %s }", getStatusCode(), getContent());
		}
	}

	/**
	 * url �� Get ������� ȣ���ϰ� ����� �����Ѵ�.
	 * @param url
	 * @return Result ��ü
	 */
	public static Result get(String url) {
		return get(url, null);
	}

	/**
	 * url �� Get ������� ȣ���ϰ� ����� �����Ѵ�.
	 * @param url
	 * @param headerMap
	 * @return Result ��ü
	 */
	public static Result get(String url, Map<String, String> headerMap) {
		int statusCode = 0;
		String content = "";
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			if (headerMap != null) {
				for (Entry<String, String> entry : headerMap.entrySet()) {
					get.addHeader(entry.getKey(), entry.getValue());
				}
			}
			HttpResponse responseGet = client.execute(get);
			statusCode = responseGet.getStatusLine().getStatusCode();
			HttpEntity resEntityGet = responseGet.getEntity();
			if (resEntityGet != null) {
				content = EntityUtils.toString(resEntityGet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Result(statusCode, content);
	}

	/**
	 * url �� Post ������� ȣ���ϰ� ����� �����Ѵ�.
	 * @param url
	 * @return Result ��ü
	 */
	public static Result post(String url) {
		return post(url, null, (Map<String, String>) null);
	}

	/**
	 * url �� Post ������� ȣ���ϰ� ����� �����Ѵ�.
	 * @param url
	 * @param paramMap
	 * @return Result ��ü
	 */
	public static Result post(String url, Map<String, String> paramMap) {
		return post(url, paramMap, (Map<String, String>) null);
	}

	/**
	 * url �� Post ������� ȣ���ϰ� ����� �����Ѵ�.
	 * @param url
	 * @param paramMap
	 * @param headerMap
	 * @return Result ��ü
	 */
	public static Result post(String url, Map<String, String> paramMap, Map<String, String> headerMap) {
		int statusCode = 0;
		String content = "";
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			if (headerMap != null) {
				for (Entry<String, String> entry : headerMap.entrySet()) {
					post.addHeader(entry.getKey(), entry.getValue());
				}
			}
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if (paramMap != null) {
				for (Entry<String, String> entry : paramMap.entrySet()) {
					params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, "UTF-8");
			post.setEntity(ent);
			HttpResponse responsePOST = client.execute(post);
			statusCode = responsePOST.getStatusLine().getStatusCode();
			HttpEntity resEntity = responsePOST.getEntity();
			if (resEntity != null) {
				content = EntityUtils.toString(resEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Result(statusCode, content);
	}

	/**
	 * url �� Post ������� ȣ���ϰ� ����� �����Ѵ�. (÷������ ����)
	 * @param url
	 * @param paramMap
	 * @param fileList
	 * @return Result ��ü
	 */
	public static Result post(String url, Map<String, String> paramMap, List<File> fileList) {
		return post(url, paramMap, fileList, null);
	}

	/**
	 * url �� Post ������� ȣ���ϰ� ����� �����Ѵ�. (÷������ ����)
	 * @param url
	 * @param paramMap
	 * @param fileList
	 * @param headerMap
	 * @return Result ��ü
	 */
	public static Result post(String url, Map<String, String> paramMap, List<File> fileList, Map<String, String> headerMap) {
		int statusCode = 0;
		String content = "";
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			if (headerMap != null) {
				for (Entry<String, String> entry : headerMap.entrySet()) {
					post.addHeader(entry.getKey(), entry.getValue());
				}
			}
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			if (paramMap != null) {
				for (Entry<String, String> entry : paramMap.entrySet()) {
					reqEntity.addPart(entry.getKey(), new StringBody(entry.getValue()));
				}
			}
			if (fileList != null) {
				for (File file : fileList) {
					ContentBody contentBody = new FileBody(file);
					reqEntity.addPart("userfile", contentBody);
				}
			}
			post.setEntity(reqEntity);
			HttpResponse response = client.execute(post);
			statusCode = response.getStatusLine().getStatusCode();
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				content = EntityUtils.toString(resEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Result(statusCode, content);
	}
}
