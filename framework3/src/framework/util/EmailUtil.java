/*
 * @(#)EmailUtil.java
 */
package framework.util;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * JavaMail�� �̿��� ������ �߼��ϴ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class EmailUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private EmailUtil() {
	}

	/**
	 * �⺻ ���ڵ� ��
	 */
	private static final String _DEFAULT_CHARSET = "utf-8";

	//////////////////////////////////////////////////////////////////////////////////////////SMTP������ ������ �ʿ��� ���

	/**
	 * ������ SMTP ���� ������ ���Ͽ� ���ڸ����� �߼��Ѵ�.
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ��� => EmailUtil.sendMailAuth("mail.xxx.co.kr", "25", "id", "password", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿");
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param smtpUser ������ SMTP ���� �������̵�
	 * @param smtpPassword ������ SMTP ���� ������й�ȣ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 */
	public static void sendMailAuth(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName) {
		sendMailAuth(smtpHost, smtpPort, smtpUser, smtpPassword, subject, content, toEmail, fromEmail, fromName, _DEFAULT_CHARSET, null);
	}

	/**
	 * ������ SMTP ���� ������ ���Ͽ� ���ڸ����� �߼��Ѵ�. (���ȿ���-SSL �� �ʿ��Ҷ�)
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ��� => EmailUtil.sendMailAuthSSL("mail.xxx.co.kr", "465", "id", "password", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿");
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param smtpUser ������ SMTP ���� �������̵�
	 * @param smtpPassword ������ SMTP ���� ������й�ȣ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 */
	public static void sendMailAuthSSL(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName) {
		sendMailAuthSSL(smtpHost, smtpPort, smtpUser, smtpPassword, subject, content, toEmail, fromEmail, fromName, _DEFAULT_CHARSET, null);
	}

	/**
	 * ������ SMTP ���� ������ ���Ͽ� ���ڸ����� �߼��Ѵ�.
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ��� => EmailUtil.sendMailAuth("mail.xxx.co.kr", "25", "id", "password", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿", "euc-kr");
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param smtpUser ������ SMTP ���� �������̵�
	 * @param smtpPassword ������ SMTP ���� ������й�ȣ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 * @param charset ���ڵ� ĳ���ͼ�
	 */
	public static void sendMailAuth(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName, String charset) {
		sendMailAuth(smtpHost, smtpPort, smtpUser, smtpPassword, subject, content, toEmail, fromEmail, fromName, charset, null);
	}

	/**
	 * ������ SMTP ���� ������ ���Ͽ� ���ڸ����� �߼��Ѵ�. (���ȿ���-SSL �� �ʿ��Ҷ�)
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ��� => EmailUtil.sendMailAuthSSL("mail.xxx.co.kr", "465", "id", "password", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿", "euc-kr");
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param smtpUser ������ SMTP ���� �������̵�
	 * @param smtpPassword ������ SMTP ���� ������й�ȣ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 * @param charset ���ڵ� ĳ���ͼ�
	 */
	public static void sendMailAuthSSL(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName, String charset) {
		sendMailAuthSSL(smtpHost, smtpPort, smtpUser, smtpPassword, subject, content, toEmail, fromEmail, fromName, charset, null);
	}

	/**
	 * ������ SMTP ���� ������ ���Ͽ� ���ڸ����� �߼��Ѵ�.
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ��� => EmailUtil.sendMailAuth("mail.xxx.co.kr", "25", "id", "password", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿", "euc-kr", new File[] { f1, f2 });
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param smtpUser ������ SMTP ���� �������̵�
	 * @param smtpPassword ������ SMTP ���� ������й�ȣ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 * @param charset ���ڵ� ĳ���ͼ�
	 * @param attachFiles ÷������ �迭
	 */
	public static void sendMailAuth(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles) {
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);
		props.put("mail.smtp.user", smtpUser);
		props.put("mail.smtp.auth", true);
		MyAuthenticator auth = new MyAuthenticator(smtpUser, smtpPassword);
		Session session = Session.getDefaultInstance(props, auth);
		_sendMail(subject, content, toEmail, fromEmail, fromName, charset, attachFiles, session);
	}

	/**
	 * ������ SMTP ���� ������ ���Ͽ� ���ڸ����� �߼��Ѵ�. (���ȿ���-SSL �� �ʿ��Ҷ�)
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ��� => EmailUtil.sendMailAuthSSL("mail.xxx.co.kr", "465", "id", "password", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿", "euc-kr", new File[] { f1, f2 });
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param smtpUser ������ SMTP ���� �������̵�
	 * @param smtpPassword ������ SMTP ���� ������й�ȣ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 * @param charset ���ڵ� ĳ���ͼ�
	 * @param attachFiles ÷������ �迭
	 */
	public static void sendMailAuthSSL(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles) {
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);
		props.put("mail.smtp.user", smtpUser);
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		MyAuthenticator auth = new MyAuthenticator(smtpUser, smtpPassword);
		Session session = Session.getDefaultInstance(props, auth);
		_sendMail(subject, content, toEmail, fromEmail, fromName, charset, attachFiles, session);
	}

	//////////////////////////////////////////////////////////////////////////////////////////SMTP������ ������ �ʿ���� ���

	/**
	 * ������ SMTP ���� �������� ���ڸ����� �߼��Ѵ�.
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ��� => EmailUtil.sendMailNoAuth("mail.xxx.co.kr", "25", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿");
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 */
	public static void sendMailNoAuth(String smtpHost, String smtpPort, String subject, String content, String toEmail, String fromEmail, String fromName) {
		sendMailNoAuth(smtpHost, smtpPort, subject, content, toEmail, fromEmail, fromName, _DEFAULT_CHARSET, null);
	}

	/**
	 * ������ SMTP ���� �������� ���ڸ����� �߼��Ѵ�. (���ȿ���-SSL �� �ʿ��Ҷ�)
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ��� => EmailUtil.sendMailNoAuthSSL("mail.xxx.co.kr", "465", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿");
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 */
	public static void sendMailNoAuthSSL(String smtpHost, String smtpPort, String subject, String content, String toEmail, String fromEmail, String fromName) {
		sendMailNoAuthSSL(smtpHost, smtpPort, subject, content, toEmail, fromEmail, fromName, _DEFAULT_CHARSET, null);
	}

	/**
	 * ������ SMTP ���� �������� ���ڸ����� �߼��Ѵ�.
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ��� => EmailUtil.sendMailNoAuth("mail.xxx.co.kr", "25", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿", "euc-kr");
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 * @param charset ���ڵ� ĳ���ͼ�
	 */
	public static void sendMailNoAuth(String smtpHost, String smtpPort, String subject, String content, String toEmail, String fromEmail, String fromName, String charset) {
		sendMailNoAuth(smtpHost, smtpPort, subject, content, toEmail, fromEmail, fromName, charset, null);
	}

	/**
	 * ������ SMTP ���� �������� ���ڸ����� �߼��Ѵ�. (���ȿ���-SSL �� �ʿ��Ҷ�)
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ��� => EmailUtil.sendMailNoAuthSSL("mail.xxx.co.kr", "465", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿", "euc-kr");	
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 * @param charset ���ڵ� ĳ���ͼ�
	 */
	public static void sendMailNoAuthSSL(String smtpHost, String smtpPort, String subject, String content, String toEmail, String fromEmail, String fromName, String charset) {
		sendMailNoAuthSSL(smtpHost, smtpPort, subject, content, toEmail, fromEmail, fromName, charset, null);
	}

	/**
	 * ������ SMTP ���� �������� ���ڸ����� �߼��Ѵ�.
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ��� => EmailUtil.sendMailNoAuth("mail.xxx.co.kr", "25", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿", "euc-kr", new File[] { f1, f2 });
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 * @param charset ���ڵ� ĳ���ͼ�
	 * @param attachFiles ÷������ �迭
	 */
	public static void sendMailNoAuth(String smtpHost, String smtpPort, String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles) {
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);
		Session session = Session.getDefaultInstance(props, null);
		_sendMail(subject, content, toEmail, fromEmail, fromName, charset, attachFiles, session);
	}

	/**
	 * ������ SMTP ���� �������� ���ڸ����� �߼��Ѵ�. (���ȿ���-SSL �� �ʿ��Ҷ�)
	 * <br>
	 * ex) receiver@xxx.co.kr �� sender@xxx.co.kr ���� ������ ������ ��� => EmailUtil.sendMailNoAuthSSL("mail.xxx.co.kr", "465", "����", "����", "receiver@xxx.co.kr", "sender@xxx.co.kr", "ȫ�浿", "euc-kr", new File[] { f1, f2 });
	 * @param smtpHost ������ SMTP �����ּ�
	 * @param smtpPort ������ SMTP ��Ʈ
	 * @param subject ��������
	 * @param content ���ϳ���
	 * @param toEmail �޴»�� �����ּ�
	 * @param fromEmail �����»�� �����ּ�
	 * @param fromName �����»�� �̸�
	 * @param charset ���ڵ� ĳ���ͼ�
	 * @param attachFiles ÷������ �迭
	 */
	public static void sendMailNoAuthSSL(String smtpHost, String smtpPort, String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles) {
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		Session session = Session.getDefaultInstance(props, null);
		_sendMail(subject, content, toEmail, fromEmail, fromName, charset, attachFiles, session);
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private �޼ҵ� �� ��ü

	/**
	 * ���Ϲ߼� �� ÷������ ó��
	 */
	private static void _sendMail(String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles, Session session) {
		MimeMessage message = new MimeMessage(session);
		try {
			InternetAddress addr = new InternetAddress(fromEmail, fromName, charset);
			message.setFrom(addr);
			message.setSubject(subject);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			if (attachFiles == null) {
				message.setContent(content, "text/html; charset=" + charset);
			} else {
				Multipart multipart = new MimeMultipart();
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText(content);
				multipart.addBodyPart(messageBodyPart);
				for (File f : attachFiles) {
					BodyPart fileBodyPart = new MimeBodyPart();
					FileDataSource fds = new FileDataSource(f);
					fileBodyPart.setDataHandler(new DataHandler(fds));
					fileBodyPart.setFileName(MimeUtility.encodeText(f.getName(), charset, "B"));
					multipart.addBodyPart(fileBodyPart);
				}
				message.setContent(multipart);
			}
			Transport.send(message);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ���������� ���� ��ü
	 */
	private static class MyAuthenticator extends Authenticator {
		private String _id;
		private String _pw;

		public MyAuthenticator(String id, String pw) {
			_id = id;
			_pw = pw;
		}

		protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
			return new javax.mail.PasswordAuthentication(_id, _pw);
		}
	}
}