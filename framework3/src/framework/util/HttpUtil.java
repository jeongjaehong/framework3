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
	 * url �� Get ������� ȣ���ϰ� ����� �����Ѵ�.
	 * @param url
	 * @return �������ڿ�
	 */
	public static String GET(String url) {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();
			if (resEntityGet != null) {
				return EntityUtils.toString(resEntityGet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * url �� Post ������� ȣ���ϰ� ����� �����Ѵ�.
	 * @param url
	 * @return �������ڿ�
	 */
	public static String POST(String url) {
		return POST(url, null);
	}

	public static String POST(String url, Map<String, String> paramMap) {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if (paramMap != null) {
				for (Entry<String, String> entry : paramMap.entrySet()) {
					params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, "UTF-8");
			post.setEntity(ent);
			HttpResponse responsePOST = client.execute(post);
			HttpEntity resEntity = responsePOST.getEntity();
			if (resEntity != null) {
				return EntityUtils.toString(resEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * url �� Post ������� ȣ���ϰ� ����� �����Ѵ�. (÷������ ����)
	 * @param url
	 * @param paramMap
	 * @param fileList
	 * @return �������ڿ�
	 */
	public static String POST(String url, Map<String, String> paramMap, List<File> fileList) {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
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
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				return EntityUtils.toString(resEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
