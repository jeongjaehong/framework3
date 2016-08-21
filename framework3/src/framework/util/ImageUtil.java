package framework.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import nl.captcha.Captcha;
import nl.captcha.gimpy.RippleGimpyRenderer;
import nl.captcha.servlet.CaptchaServletUtil;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * 이미지 포맷 변경, 크기 변경시 이용할 수 있는 유틸리티 클래스이다.
 */
public class ImageUtil {

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private ImageUtil() {
	}

	/**
	 * 이미지를 리사이즈 한다. 
	 * 소스 이미지 파일의 width, height 중 크기가 큰 쪽을 기준으로 하여 비율을 유지한채 이미지를 생성한다.
	 * @param srcPath 소스 이미지 경로
	 * @param destPath 대상 이미지 경로
	 * @param width 리사이즈할 가로 사이즈
	 * @param height 리사이즈할 세로 사이즈
	 */
	public static void resize(String srcPath, String destPath, int width, int height) {
		File srcFile = new File(srcPath);
		File destFile = new File(destPath);
		resize(srcFile, destFile, width, height);
	}

	/**
	 * 이미지를 리사이즈 한다. 
	 * 소스 이미지 파일의 width, height 중 크기가 큰 쪽을 기준으로 하여 비율을 유지한채 이미지를 생성한다.
	 * @param srcFile 소스 이미지 파일
	 * @param destFile 대상 이미지 파일
	 * @param width 리사이즈할 가로 사이즈
	 * @param height 리사이즈할 세로 사이즈
	 */
	public static void resize(File srcFile, File destFile, int width, int height) {
		BufferedImage bufImg = null;
		Image image = null;
		Image resizedImg = null;
		try {
			image = new ImageIcon(srcFile.getAbsolutePath()).getImage();
			if (image.getWidth(null) < 1 || image.getHeight(null) < 1) {
				throw new IllegalArgumentException("파일이 존재하지 않습니다.");
			}
			double scale = getScale(width, height, image.getWidth(null), image.getHeight(null));
			int scaleWidth = (int) (scale * image.getWidth(null));
			int scaleHeight = (int) (scale * image.getHeight(null));
			bufImg = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g2d = bufImg.createGraphics();
			AffineTransform ax = new AffineTransform();
			ax.setToScale(1, 1);
			g2d.drawImage(image, ax, null);
			resizedImg = bufImg.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH);
			writePNG(resizedImg, destFile);
		} finally {
			if (resizedImg != null) {
				resizedImg.flush();
			}
			if (bufImg != null) {
				bufImg.flush();
			}
			if (image != null) {
				image.flush();
			}
		}
	}

	/**
	 * 이미지를 리사이즈 한다. 
	 * 소스 이미지 파일의 width를 기준으로 하여 비율을 유지한채 이미지를 생성한다.
	 * @param srcPath 소스 이미지 경로
	 * @param destPath 대상 이미지 경로
	 * @param width 리사이즈할 가로 사이즈
	 */
	public static void resizeWidth(String srcPath, String destPath, int width) {
		File srcFile = new File(srcPath);
		File destFile = new File(destPath);
		resizeWidth(srcFile, destFile, width);
	}

	/**
	 * 이미지를 리사이즈 한다. 
	 * 소스 이미지 파일의 width를 기준으로 하여 비율을 유지한채 이미지를 생성한다.
	 * @param srcFile 소스 이미지 파일
	 * @param destFile 대상 이미지 파일
	 * @param width 리사이즈할 가로 사이즈
	 */
	public static void resizeWidth(File srcFile, File destFile, int width) {
		BufferedImage bufImg = null;
		Image image = null;
		Image resizedImg = null;
		try {
			image = new ImageIcon(srcFile.getAbsolutePath()).getImage();
			if (image.getWidth(null) < 1 || image.getHeight(null) < 1) {
				throw new IllegalArgumentException("파일이 존재하지 않습니다.");
			}
			double scale = getWidthScale(width, image.getWidth(null));
			int scaleWidth = (int) (scale * image.getWidth(null));
			int scaleHeight = (int) (scale * image.getHeight(null));
			bufImg = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g2d = bufImg.createGraphics();
			AffineTransform ax = new AffineTransform();
			ax.setToScale(1, 1);
			g2d.drawImage(image, ax, null);
			resizedImg = bufImg.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH);
			writePNG(resizedImg, destFile);
		} finally {
			if (resizedImg != null) {
				resizedImg.flush();
			}
			if (bufImg != null) {
				bufImg.flush();
			}
			if (image != null) {
				image.flush();
			}
		}
	}

	/**
	 * 이미지를 리사이즈 한다. 
	 * 소스 이미지 파일의 height를 기준으로 하여 비율을 유지한채 이미지를 생성한다.
	 * @param srcPath 소스 이미지 경로
	 * @param destPath 대상 이미지 경로
	 * @param height 리사이즈할 세로 사이즈
	 */
	public static void resizeHeight(String srcPath, String destPath, int height) {
		File srcFile = new File(srcPath);
		File destFile = new File(destPath);
		resizeHeight(srcFile, destFile, height);
	}

	/**
	 * 이미지를 리사이즈 한다. 
	 * 소스 이미지 파일의 height를 기준으로 하여 비율을 유지한채 이미지를 생성한다.
	 * @param srcFile 소스 이미지 파일
	 * @param destFile 대상 이미지 파일
	 * @param height 리사이즈할 세로 사이즈
	 */
	public static void resizeHeight(File srcFile, File destFile, int height) {
		BufferedImage bufImg = null;
		Image image = null;
		Image resizedImg = null;
		try {
			image = new ImageIcon(srcFile.getAbsolutePath()).getImage();
			if (image.getWidth(null) < 1 || image.getHeight(null) < 1) {
				throw new IllegalArgumentException("파일이 존재하지 않습니다.");
			}
			double scale = getHeightScale(height, image.getHeight(null));
			int scaleWidth = (int) (scale * image.getWidth(null));
			int scaleHeight = (int) (scale * image.getHeight(null));
			bufImg = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g2d = bufImg.createGraphics();
			AffineTransform ax = new AffineTransform();
			ax.setToScale(1, 1);
			g2d.drawImage(image, ax, null);
			resizedImg = bufImg.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH);
			writePNG(resizedImg, destFile);
		} finally {
			if (resizedImg != null) {
				resizedImg.flush();
			}
			if (bufImg != null) {
				bufImg.flush();
			}
			if (image != null) {
				image.flush();
			}
		}
	}

	/**
	 * CAPTCHA 이미지를 응답객체로 전송하고, 생성된 문자열을 리턴한다.
	 * 기본사이즈는 가로 200px, 세로 50px으로 한다.
	 * @param response captcha 이미지를 전송할 응답객체
	 * @return 생성된 문자열
	 */
	public static String captcha(HttpServletResponse response) {
		return captcha(response, 200, 50);
	}

	/**
	 * CAPTCHA 이미지를 응답객체로 전송하고, 생성된 문자열을 리턴한다.
	 * @param response captcha 이미지를 전송할 응답객체
	 * @param width 가로 사이즈 픽셀
	 * @param height 세로 사이즈 픽셀
	 * @return 생성된 문자열
	 */
	public static String captcha(HttpServletResponse response, int width, int height) {
		response.reset();
		Captcha captcha = new Captcha.Builder(width, height).addText().addBackground().gimp(new RippleGimpyRenderer()).build();
		CaptchaServletUtil.writeImage(response, captcha.getImage());
		return captcha.getAnswer();
	}

	/**
	 * QRCode 이미지를 생성한다.
	 * @param url QRCode 스캔 시 이동할 곳의 URL
	 * @param destPath QRCode 파일명
	 * @param width QRCode 이미지 가로 길이
	 */
	public static void qrcode(String url, String destPath, int width) {
		File destFile = new File(destPath);
		qrcode(url, destFile, width);
	}

	/**
	 * QRCode 이미지를 생성한다.
	 * @param url QRCode 스캔 시 이동할 곳의 URL
	 * @param destFile QRCode 이미지 파일 객체
	 * @param width QRCode 이미지 길이
	 */
	public static void qrcode(String url, File destFile, int width) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(destFile);
			qrcode(url, fos, width);
			fos.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * QRCode 이미지를 생성한다.
	 * @param url QRCode 스캔 시 이동할 곳의 URL
	 * @param response qrcode 이미지를 전송할 응답객체
	 * @param width QRCode 이미지 길이
	 */
	public static void qrcode(String url, HttpServletResponse response, int width) {
		try {
			response.reset();
			response.setContentType("image/png");
			qrcode(url, response.getOutputStream(), width);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * QRCode 이미지를 생성한다.
	 * @param url QRCode 스캔 시 이동할 곳의 URL
	 * @param os 출력 스트림
	 * @param width QRCode 이미지 길이
	 */
	public static void qrcode(String url, OutputStream os, int width) {
		QRCodeWriter qrWriter = new QRCodeWriter();
		try {
			String encodedUrl = new String(url.getBytes("UTF-8"), "ISO-8859-1");
			BitMatrix bitMatrix = qrWriter.encode(encodedUrl, BarcodeFormat.QR_CODE, width, width);
			MatrixToImageWriter.writeToStream(bitMatrix, "png", os);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 바코드 이미지를 생성한다.
	 * @param num 생성할 바코드 번호
	 * @param destPath 바코드 이미지 파일명
	 * @param width 바코드 이미지 가로 길이
	 * @param height 바코드 이미지 세로길이
	 */
	public static void barcode(String num, String destPath, int width, int height) {
		barcode(num, new File(destPath), width, height);
	}

	/**
	 * 바코드 이미지를 생성한다.
	 * @param num 생성할 바코드 번호
	 * @param destFile 바코드 이미지 파일 객체
	 * @param width 바코드 이미지 가로길이
	 * @param height 바코드 이미지 세로길이
	 */
	public static void barcode(String num, File destFile, int width, int height) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(destFile);
			barcode(num, fos, width, height);
			fos.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 바코드 이미지를 생성한다.
	 * @param num 생성할 바코드 번호
	 * @param response 바코드 이미지를 전송할 응답객체
	 * @param width 바코드 이미지 가로길이
	 * @param height 바코드 이미지 세로길이
	 */
	public static void barcode(String num, HttpServletResponse response, int width, int height) {
		try {
			response.reset();
			response.setContentType("image/png");
			barcode(num, response.getOutputStream(), width, height);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 바코드 이미지를 생성한다.
	 * @param num 생성할 바코드 번호
	 * @param os 출력 스트림
	 * @param width 바코드 이미지 가로길이
	 * @param height 바코드 이미지 세로길이
	 */
	public static void barcode(String num, OutputStream os, int width, int height) {
		MultiFormatWriter barcodeWriter = new MultiFormatWriter();
		try {
			BitMatrix bitMatrix = barcodeWriter.encode(num, BarcodeFormat.CODE_128, width, height);
			MatrixToImageWriter.writeToStream(bitMatrix, "png", os);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private 메소드

	/**
	 * 원본 이미지 사이즈와 리사이즈할 사이즈로 이미지 스케일 비율을 구한다. 
	 * 크기가 큰 폭을 기준으로 동일 비율로 한다.
	 * @param resizeWidth 리사이즈할 가로 사이즈
	 * @param resizeHeight 리사이즈할 세로 사이즈
	 * @param imageWidth 원본 이미지의 가로 사이즈
	 * @param imageHeight 원본 이미지의 세로 사이즈
	 * @return 스케일 비율
	 */
	private static double getScale(int resizeWidth, int resizeHeight, int imageWidth, int imageHeight) {
		double widthScale = (double) resizeWidth / imageWidth;
		double heightScale = (double) resizeHeight / (double) imageHeight;
		if (widthScale > heightScale) {
			return heightScale;
		} else {
			return widthScale;
		}
	}

	/**
	 * 원본 이미지 사이즈와 리사이즈할 사이즈로 이미지 스케일 비율을 구한다. 
	 * 크기가 큰 폭을 기준으로 동일 비율로 한다.
	 * @param resizeWidth 리사이즈할 가로 사이즈
	 * @param imageWidth 원본 이미지의 가로 사이즈
	 * @return 스케일 비율
	 */
	private static double getWidthScale(int resizeWidth, int imageWidth) {
		double widthScale = (double) resizeWidth / imageWidth;
		return widthScale;
	}

	/**
	 * 원본 이미지 사이즈와 리사이즈할 사이즈로 이미지 스케일 비율을 구한다. 
	 * 크기가 큰 폭을 기준으로 동일 비율로 한다.
	 * @param resizeHeight 리사이즈할 세로 사이즈
	 * @param imageHeight 원본 이미지의 세로 사이즈
	 * @return 스케일 비율
	 */
	private static double getHeightScale(int resizeHeight, int imageHeight) {
		double heightScale = (double) resizeHeight / (double) imageHeight;
		return heightScale;
	}

	/**
	 * 이미지를 PNG 형식으로 저장한다.
	 * @param image 저장할 이미지 객체
	 * @param destFile 대상 이미지 파일
	 */
	private static void writePNG(Image image, File destFile) {
		BufferedImage bufImg = null;
		try {
			bufImg = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g2d = bufImg.createGraphics();
			g2d.drawImage(image, 0, 0, null);
			ImageIO.write(bufImg, "png", destFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (bufImg != null) {
				bufImg.flush();
			}
		}
	}
}