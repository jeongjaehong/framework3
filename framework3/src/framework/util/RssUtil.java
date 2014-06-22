package framework.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import framework.db.RecordSet;

/**
 * RSS�� �̿��Ͽ� ������ �� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class RssUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private RssUtil() {
	}

	private static final String _BR = System.getProperty("line.separator");

	/**
	 * RssItem ��ü
	 */
	public static class RssItem {
		private String _title = null;
		private String _link = null;
		private String _description = null;
		private String _author = null;
		private String _category = null;
		private Date _pubDate = null;

		public RssItem() {
		}

		public RssItem(String title, String link, String description, String author, String category, Date pubDate) {
			setTitle(title);
			setLink(link);
			setDescription(description);
			setAuthor(author);
			setCategory(category);
			setPubDate(pubDate);
		}

		public String getTitle() {
			return _title;
		}

		public String getLink() {
			return _link;
		}

		public String getDescription() {
			return _description;
		}

		public String getAuthor() {
			return _author;
		}

		public String getCategory() {
			return _category;
		}

		public Date getPubDate() {
			return _pubDate;
		}

		public void setTitle(String title) {
			_title = title;
		}

		public void setLink(String link) {
			_link = link;
		}

		public void setDescription(String description) {
			_description = description;
		}

		public void setAuthor(String author) {
			_author = author;
		}

		public void setCategory(String category) {
			_category = category;
		}

		public void setPubDate(Date pubDate) {
			_pubDate = pubDate;
		}
	}

	/**
	 * �Է��� ������ RssItem�� �����Ѵ�.
	 * <br>
	 * ex) titie, link, description, author, category, pubDate�� RssItem��ü�� �����ϴ� ��� : RssUtil.makeRssItem(title, link, description, author, category, pubDate) 
	 * @param title ����
	 * @param link ��ũ(validator�� ����ϱ� ���ؼ��� url�� ���ۼ������ ����Ƽǥ�⸦ ����Ͽ��� ��)
	 * @param description ����
	 * @param author �ۼ���(validator�� ����ϱ� ���ؼ��� "�̸����ּ�(�̸�)" �������� ǥ���Ͽ��� ��)
	 * @param category �з�
	 * @param pubDate �ۼ���
	 * @return RssItem ��ü
	 */
	public static RssItem makeRssItem(String title, String link, String description, String author, String category, Date pubDate) {
		return new RssItem(title, link, description, author, category, pubDate);
	}

	/**
	 * RecordSet�� RSS 2.0 �������� ����Ѵ�. RecordSet���� �����÷��� �ݵ�� ���ԵǾ�� �Ѵ�.(title, link, description, author, category, pubDate). 
	 * <br>
	 * ex) response�� rs�� RSS �������� ����ϴ� ��� : RssUtil.render(response, rs, "utf-8", "����", "http://www.xxx.com", "����", "admin@xxx.com")
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs RSS �������� ��ȯ�� RecordSet ��ü
	 * @param encoding ����� ���Ե� ���ڵ�
	 * @param title ���� : �ʼ�
	 * @param link ��ũ(validator�� ����ϱ� ���ؼ��� url�� ���ۼ������ ����Ƽǥ�⸦ ����Ͽ��� ��) : �ʼ�
	 * @param description ���� : �ʼ�
	 * @param webMaster �������� e-mail �ּ�(validator�� ����ϱ� ���ؼ��� "�̸����ּ�(�̸�)" �������� ǥ���Ͽ��� ��) : �ɼ�
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, RecordSet rs, String encoding, String title, String link, String description, String webMaster) {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw;
		try {
			pw = response.getWriter();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		rs.moveRow(0);
		pw.println(_xmlHeaderStr(encoding));
		pw.println("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">");
		pw.println("  <channel>");
		pw.println("    <title>" + "<![CDATA[" + title + "]]>" + "</title>");
		pw.println("    <link>" + link + "</link>");
		pw.println("    <description>" + "<![CDATA[" + description + "]]>" + "</description>");
		pw.println("    <language>ko</language>");
		pw.println("    <atom:link href=\"" + link + "\" rel=\"self\" type=\"application/rss+xml\"/>");
		pw.println("    <pubDate>" + _toRfc822DateFormat(new Date()) + "</pubDate>");
		if (webMaster != null && !"".equals(webMaster)) {
			pw.println("    <webMaster>" + webMaster + "</webMaster>");
		}
		int rowCount = 0;
		while (rs.nextRow()) {
			rowCount++;
			pw.println(_rssItemStr(rs));
		}
		pw.println("  </channel>");
		pw.println("</rss>");
		return rowCount;
	}

	/**
	 * RecordSet�� RSS 2.0 �������� ��ȯ�Ѵ�. RecordSet���� �����÷��� �ݵ�� ���ԵǾ�� �Ѵ�.(title, link, description, author, category, pubDate). 
	 * <br>
	 * ex) rs�� RSS �������� ��ȯ�ϴ� ��� : String rss = RssUtil.render(rs, "utf-8", "����", "http://www.xxx.com", "����", "admin@xxx.com")
	 * @param rs RSS �������� ��ȯ�� RecordSet ��ü
	 * @param encoding ����� ���Ե� ���ڵ�
	 * @param title ���� : �ʼ�
	 * @param link ��ũ(validator�� ����ϱ� ���ؼ��� url�� ���ۼ������ ����Ƽǥ�⸦ ����Ͽ��� ��) : �ʼ�
	 * @param description ���� : �ʼ�
	 * @param webMaster �������� e-mail �ּ�(validator�� ����ϱ� ���ؼ��� "�̸����ּ�(�̸�)" �������� ǥ���Ͽ��� ��) : �ɼ�
	 * @return RSS �������� ��ȯ�� ���ڿ�
	 */
	public static String render(RecordSet rs, String encoding, String title, String link, String description, String webMaster) {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		rs.moveRow(0);
		buffer.append(_xmlHeaderStr(encoding) + _BR);
		buffer.append("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">" + _BR);
		buffer.append("  <channel>" + _BR);
		buffer.append("    <title>" + "<![CDATA[" + title + "]]>" + "</title>" + _BR);
		buffer.append("    <link>" + link + "</link>" + _BR);
		buffer.append("    <description>" + "<![CDATA[" + description + "]]>" + "</description>" + _BR);
		buffer.append("    <language>ko</language>" + _BR);
		buffer.append("    <atom:link href=\"" + link + "\" rel=\"self\" type=\"application/rss+xml\"/>" + _BR);
		buffer.append("    <pubDate>" + _toRfc822DateFormat(new Date()) + "</pubDate>" + _BR);
		if (webMaster != null && !"".equals(webMaster)) {
			buffer.append("    <webMaster>" + webMaster + "</webMaster>" + _BR);
		}
		while (rs.nextRow()) {
			buffer.append(_rssItemStr(rs) + _BR);
		}
		buffer.append("  </channel>" + _BR);
		buffer.append("</rss>" + _BR);
		return buffer.toString();
	}

	/**
	 * ResultSet�� RSS 2.0 �������� ����Ѵ�. ResultSet���� �����÷��� �ݵ�� ���ԵǾ�� �Ѵ�.(title, link, description, author, category, pubDate).
	 * <br>
	 * ex) response�� rs�� RSS �������� ����ϴ� ��� : RssUtil.render(response, rs, "utf-8", "����", "http://www.xxx.com", "����", "admin@xxx.com")
	 * @param response Ŭ���̾�Ʈ�� ������ Response ��ü
	 * @param rs RSS �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param encoding ����� ���Ե� ���ڵ�
	 * @param title ���� : �ʼ�
	 * @param link ��ũ(validator�� ����ϱ� ���ؼ��� url�� ���ۼ������ ����Ƽǥ�⸦ ����Ͽ��� ��) : �ʼ�
	 * @param description ���� : �ʼ�
	 * @param webMaster �������� e-mail �ּ�(validator�� ����ϱ� ���ؼ��� "�̸����ּ�(�̸�)" �������� ǥ���Ͽ��� ��) : �ɼ�
	 * @return ó���Ǽ�
	 */
	public static int render(HttpServletResponse response, ResultSet rs, String encoding, String title, String link, String description, String webMaster) {
		if (rs == null) {
			return 0;
		}
		try {
			PrintWriter pw = response.getWriter();
			try {
				pw.println(_xmlHeaderStr(encoding));
				pw.println("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">");
				pw.println("  <channel>");
				pw.println("    <title>" + "<![CDATA[" + title + "]]>" + "</title>");
				pw.println("    <link>" + link + "</link>");
				pw.println("    <description>" + "<![CDATA[" + description + "]]>" + "</description>");
				pw.println("    <language>ko</language>");
				pw.println("    <atom:link href=\"" + link + "\" rel=\"self\" type=\"application/rss+xml\"/>");
				pw.println("    <pubDate>" + _toRfc822DateFormat(new Date()) + "</pubDate>");
				if (webMaster != null && !"".equals(webMaster)) {
					pw.println("    <webMaster>" + webMaster + "</webMaster>");
				}
				int rowCount = 0;
				while (rs.next()) {
					rowCount++;
					pw.println(_rssItemStr(rs));
				}
				pw.println("  </channel>");
				pw.println("</rss>");
				return rowCount;
			} finally {
				Statement stmt = rs.getStatement();
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ResultSet�� RSS 2.0 �������� ��ȯ�Ѵ�. ResultSet���� �����÷��� �ݵ�� ���ԵǾ�� �Ѵ�.(title, link, description, author, category, pubDate).
	 * <br>
	 * ex) rs�� RSS �������� ��ȯ�ϴ� ��� : String rss = RssUtil.render(rs, "utf-8", "����", "http://www.xxx.com", "����", "admin@xxx.com")
	 * @param rs RSS �������� ��ȯ�� ResultSet ��ü, ResultSet ��ü�� �ڵ����� close �ȴ�.
	 * @param encoding ����� ���Ե� ���ڵ�
	 * @param title ���� : �ʼ�
	 * @param link ��ũ(validator�� ����ϱ� ���ؼ��� url�� ���ۼ������ ����Ƽǥ�⸦ ����Ͽ��� ��) : �ʼ�
	 * @param description ���� : �ʼ�
	 * @param webMaster �������� e-mail �ּ�(validator�� ����ϱ� ���ؼ��� "�̸����ּ�(�̸�)" �������� ǥ���Ͽ��� ��) : �ɼ�
	 */
	public static String render(ResultSet rs, String encoding, String title, String link, String description, String webMaster) {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		try {
			try {
				buffer.append(_xmlHeaderStr(encoding) + _BR);
				buffer.append("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">" + _BR);
				buffer.append("  <channel>" + _BR);
				buffer.append("    <title>" + "<![CDATA[" + title + "]]>" + "</title>" + _BR);
				buffer.append("    <link>" + link + "</link>" + _BR);
				buffer.append("    <description>" + "<![CDATA[" + description + "]]>" + "</description>" + _BR);
				buffer.append("    <language>ko</language>" + _BR);
				buffer.append("    <atom:link href=\"" + link + "\" rel=\"self\" type=\"application/rss+xml\"/>" + _BR);
				buffer.append("    <pubDate>" + _toRfc822DateFormat(new Date()) + "</pubDate>" + _BR);
				if (webMaster != null && !"".equals(webMaster)) {
					buffer.append("    <webMaster>" + webMaster + "</webMaster>" + _BR);
				}
				while (rs.next()) {
					buffer.append(_rssItemStr(rs) + _BR);
				}
				buffer.append("  </channel>" + _BR);
				buffer.append("</rss>" + _BR);
			} finally {
				Statement stmt = rs.getStatement();
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return buffer.toString();
	}

	/**
	 * List��ü�� RSS 2.0 ���·� ��ȯ�Ѵ�.
	 * <br>
	 * ex) rssItemList�� RSS �� ��ȯ�ϴ� ���  : String rss = RssUtil.render(rssItemList, "utf-8", "����", "http://www.xxx.com", "����", "admin@xxx.com")
	 * @param rssItemList ��ȯ�� List��ü
	 * @param encoding ����� ���Ե� ���ڵ�
	 * @param title ���� : �ʼ�
	 * @param link ��ũ(validator�� ����ϱ� ���ؼ��� url�� ���ۼ������ ����Ƽǥ�⸦ ����Ͽ��� ��) : �ʼ�
	 * @param description ���� : �ʼ�
	 * @param webMaster �������� e-mail �ּ�(validator�� ����ϱ� ���ؼ��� "�̸����ּ�(�̸�)" �������� ǥ���Ͽ��� ��) : �ɼ�
	 * @return RSS �������� ��ȯ�� ���ڿ�
	 */
	public static String render(List<RssItem> rssItemList, String encoding, String title, String link, String description, String webMaster) {
		if (rssItemList == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(_xmlHeaderStr(encoding) + _BR);
		buffer.append("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">" + _BR);
		buffer.append("  <channel>" + _BR);
		buffer.append("    <title>" + "<![CDATA[" + title + "]]>" + "</title>" + _BR);
		buffer.append("    <link>" + link + "</link>" + _BR);
		buffer.append("    <description>" + "<![CDATA[" + description + "]]>" + "</description>" + _BR);
		buffer.append("    <language>ko</language>" + _BR);
		buffer.append("    <atom:link href=\"" + link + "\" rel=\"self\" type=\"application/rss+xml\"/>" + _BR);
		buffer.append("    <pubDate>" + _toRfc822DateFormat(new Date()) + "</pubDate>" + _BR);
		if (webMaster != null && !"".equals(webMaster)) {
			buffer.append("    <webMaster>" + webMaster + "</webMaster>" + _BR);
		}
		for (RssItem rssItem : rssItemList) {
			buffer.append(_rssItemStr(rssItem) + _BR);
		}
		buffer.append("  </channel>" + _BR);
		buffer.append("</rss>" + _BR);
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// Private �޼ҵ�

	/**
	 *  xml ��� ���ڿ� ����
	 */
	private static String _xmlHeaderStr(String encoding) {
		return "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>";
	}

	/**
	 * rss item ���ڿ� ����
	 */
	private static String _rssItemStr(RssItem item) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("    "); // �鿩�����
		buffer.append("<item>");
		if (item.getTitle() != null && !"".equals(item.getTitle()))
			buffer.append("<title>" + "<![CDATA[" + item.getTitle() + "]]>" + "</title>");
		if (item.getLink() != null && !"".equals(item.getLink()))
			buffer.append("<link>" + item.getLink() + "</link>");
		if (item.getDescription() != null && !"".equals(item.getDescription()))
			buffer.append("<description>" + "<![CDATA[" + item.getDescription().replaceAll(_BR, "") + "]]>" + "</description>");
		if (item.getAuthor() != null && !"".equals(item.getAuthor()))
			buffer.append("<author>" + item.getAuthor() + "</author>");
		if (item.getCategory() != null && !"".equals(item.getCategory()))
			buffer.append("<category>" + "<![CDATA[" + item.getCategory() + "]]>" + "</category>");
		if (item.getLink() != null && !"".equals(item.getLink()))
			buffer.append("<guid>" + item.getLink() + "</guid>");
		if (item.getPubDate() != null)
			buffer.append("<pubDate>" + _toRfc822DateFormat(item.getPubDate()) + "</pubDate>");
		buffer.append("</item>");
		return buffer.toString();
	}

	/**
	 * rss item ���ڿ� ����
	 */
	private static String _rssItemStr(RecordSet rs) {
		return _rssItemStr(makeRssItem(rs.getString("TITLE"), rs.getString("LINK"), rs.getString("DESCRIPTION"), rs.getString("AUTHOR"), rs.getString("CATEGORY"), rs.getTimestamp("PUBDATE")));
	}

	/**
	 * rss item ���ڿ� ����
	 */
	private static String _rssItemStr(ResultSet rs) {
		String title = null;
		String link = null;
		String description = null;
		String author = null;
		String category = null;
		Date pubDate = null;
		try {
			title = rs.getString("TITLE");
			link = rs.getString("LINK");
			description = rs.getString("DESCRIPTION");
			author = rs.getString("AUTHOR");
			category = rs.getString("CATEGORY");
			pubDate = rs.getTimestamp("PUBDATE");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return _rssItemStr(makeRssItem(title, link, description, author, category, pubDate));
	}

	/**
	 * ��¥�� Rfc822 ��¥�������� ��ȯ
	 * @param date ��ȯ�� ��¥
	 * @return Rfc822 ������ ��¥ ���ڿ�
	 */
	private static String _toRfc822DateFormat(Date date) {
		SimpleDateFormat rfc822DateFormat = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
		return rfc822DateFormat.format(date);
	}
}
