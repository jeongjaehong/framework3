/* 
 * @(#)ImageUtil.java
 */
package framework.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import nl.captcha.Captcha;
import nl.captcha.gimpy.RippleGimpyRenderer;
import nl.captcha.servlet.CaptchaServletUtil;

/**
 * �̹��� ���� ����, ũ�� ����� �̿��� �� �ִ� ��ƿ��Ƽ Ŭ�����̴�.
 */
public class ImageUtil {

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private ImageUtil() {
	}

	/**
	 * �̹����� �������� �Ѵ�. 
	 * �ҽ� �̹��� ������ width, height �� ũ�Ⱑ ū ���� �������� �Ͽ� ������ ������ä �̹����� �����Ѵ�.
	 * @param srcPath �ҽ� �̹��� ���
	 * @param destPath ��� �̹��� ���
	 * @param width ���������� ���� ������
	 * @param height ���������� ���� ������
	 */
	public static void create(String srcPath, String destPath, int width, int height) {
		resize(srcPath, destPath, width, height);
	}

	/**
	 * �̹����� �������� �Ѵ�. 
	 * �ҽ� �̹��� ������ width, height �� ũ�Ⱑ ū ���� �������� �Ͽ� ������ ������ä �̹����� �����Ѵ�.
	 * @param srcFile �ҽ� �̹��� ����
	 * @param destFile ��� �̹��� ����
	 * @param width ���������� ���� ������
	 * @param height ���������� ���� ������
	 */
	public static void create(File srcFile, File destFile, int width, int height) {
		resize(srcFile, destFile, width, height);
	}

	/**
	 * �̹����� �������� �Ѵ�. 
	 * �ҽ� �̹��� ������ width, height �� ũ�Ⱑ ū ���� �������� �Ͽ� ������ ������ä �̹����� �����Ѵ�.
	 * @param srcPath �ҽ� �̹��� ���
	 * @param destPath ��� �̹��� ���
	 * @param width ���������� ���� ������
	 * @param height ���������� ���� ������
	 */
	public static void resize(String srcPath, String destPath, int width, int height) {
		File srcFile = new File(srcPath);
		File destFile = new File(destPath);
		resize(srcFile, destFile, width, height);
	}

	/**
	 * �̹����� �������� �Ѵ�. 
	 * �ҽ� �̹��� ������ width, height �� ũ�Ⱑ ū ���� �������� �Ͽ� ������ ������ä �̹����� �����Ѵ�.
	 * @param srcFile �ҽ� �̹��� ����
	 * @param destFile ��� �̹��� ����
	 * @param width ���������� ���� ������
	 * @param height ���������� ���� ������
	 */
	public static void resize(File srcFile, File destFile, int width, int height) {
		Image image = new ImageIcon(srcFile.getAbsolutePath()).getImage();
		if (image.getWidth(null) < 1 || image.getHeight(null) < 1) {
			throw new IllegalArgumentException("������ �������� �ʽ��ϴ�.");
		}
		double scale = _getScale(width, height, image.getWidth(null), image.getHeight(null));
		int scaleWidth = (int) (scale * image.getWidth(null));
		int scaleHeight = (int) (scale * image.getHeight(null));
		BufferedImage bufImg = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = bufImg.createGraphics();
		AffineTransform ax = new AffineTransform();
		ax.setToScale(1, 1);
		g2d.drawImage(image, ax, null);
		Image resizedImg = bufImg.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH);
		writePNG(resizedImg, destFile);
	}

	/**
	 * �̹����� PNG �������� �����Ѵ�.
	 * @param image ������ �̹��� ��ü
	 * @param destPath ��� �̹��� ���
	 */
	public static void writePNG(Image image, String destPath) {
		File destFile = new File(destPath);
		writePNG(image, destFile);
	}

	/**
	 * �̹����� PNG �������� �����Ѵ�.
	 * @param image ������ �̹��� ��ü
	 * @param destFile ��� �̹��� ����
	 */
	public static void writePNG(Image image, File destFile) {
		BufferedImage bufImg = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = bufImg.createGraphics();
		g2d.drawImage(image, 0, 0, null);
		try {
			ImageIO.write(bufImg, "png", destFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * CAPTCHA �̹����� ���䰴ü�� �����ϰ�, ������ ���ڿ��� �����Ѵ�.
	 * �⺻������� ���� 200px, ���� 50px���� �Ѵ�.
	 * @param response captcha �̹����� ������ ���䰴ü
	 * @return ������ ���ڿ�
	 */
	public static String captcha(HttpServletResponse response) {
		return captcha(response, 200, 50);
	}

	/**
	 * CAPTCHA �̹����� ���䰴ü�� �����ϰ�, ������ ���ڿ��� �����Ѵ�.
	 * @param response captcha �̹����� ������ ���䰴ü
	 * @param width ���� ������ �ȼ�
	 * @param height ���� ������ �ȼ�
	 * @return ������ ���ڿ�
	 */
	public static String captcha(HttpServletResponse response, int width, int height) {
		response.reset();
		Captcha captcha = new Captcha.Builder(width, height).addText().addBackground().gimp(new RippleGimpyRenderer()).build();
		CaptchaServletUtil.writeImage(response, captcha.getImage());
		return captcha.getAnswer();
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private �޼ҵ�

	/**
	 * ���� �̹��� ������� ���������� ������� �̹��� ������ ������ ���Ѵ�. 
	 * ũ�Ⱑ ū ���� �������� ���� ������ �Ѵ�.
	 * @param resizeWidth ���������� ���� ������
	 * @param resizeHeight ���������� ���� ������
	 * @param imageWidth ���� �̹����� ���� ������
	 * @param imageHeight ���� �̹����� ���� ������
	 * @return ������ ����
	 */
	private static double _getScale(int resizeWidth, int resizeHeight, int imageWidth, int imageHeight) {
		double widthScale = (double) resizeWidth / imageWidth;
		double heightScale = (double) resizeHeight / (double) imageHeight;
		if (widthScale > heightScale) {
			return heightScale;
		} else {
			return widthScale;
		}
	}
}
