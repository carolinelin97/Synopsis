package data;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.*;

/**
 * to provide static methods to process diffenrent form of image data(Mat,
 * byte[], BufferedImage) Also do imgage read and write
 * 
 * 
 */
public class ImageHandler {

	// Convert image to Mat
	public static Mat matify(BufferedImage im) {
		byte[] pixels = ((DataBufferByte) im.getRaster().getDataBuffer()).getData();

		// Create a Matrix the same size of image
		Mat image = new Mat(im.getHeight(), im.getWidth(), CvType.CV_8UC3);
		// Fill Matrix with image values
		image.put(0, 0, pixels);

		return image;

	}

	public static BufferedImage toBufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;

	}

	public static byte[] toBytes(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return targetPixels;

	}

	public static byte[] toByte(BufferedImage im) {
		Picture pic1 = new Picture(im);
		byte[] out_byte = pic1.getByte();
		return out_byte;
	}

	public static byte[] readImageFromFile(String filename) {
		byte[] bytes = null;

		try {
			File file = new File(filename);
			InputStream is = new FileInputStream(file);

			long len = file.length();
			bytes = new byte[(int) len];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}

		return bytes;
	}

	public static byte[] shrinkBy2(byte[] bytes, int h, int w) {
		byte[] results = new byte[bytes.length / 4];

		for (int i = 0; i < h / 2; i++) {
			for (int j = 0; j < w / 2; j++) {
				int[] mean_rgb = meanAdjacent(bytes, j * 2, i * 2, 2, w, h);

				results[j + i * w / 2] = (byte) mean_rgb[0];
				results[j + i * w / 2 + h * w / 4] = (byte) mean_rgb[1];
				results[j + i * w / 2 + h * w / 2] = (byte) mean_rgb[2];

			}
		}
		return results;
	}

	private static int[] meanAdjacent(byte[] a, int x, int y, int num, int w, int h) {
		int[] mean = new int[] { 0, 0, 0 };

		for (int i = 0; i < num; i++) {
			for (int j = 0; j < num; j++) {
				mean[0] += Byte.toUnsignedInt(a[x + j + (y + i) * w]);
				mean[1] += Byte.toUnsignedInt(a[x + j + (y + i) * w + h * w]);
				mean[2] += Byte.toUnsignedInt(a[x + j + (y + i) * w + h * w * 2]);
			}
		}
		mean[0] = Integer.divideUnsigned(mean[0], num * num); // r
		mean[1] = Integer.divideUnsigned(mean[1], num * num); // g
		mean[2] = Integer.divideUnsigned(mean[2], num * num); // b
		return mean;
	}

	public static void writeFileToImage(String filename, byte[] bytes) {
		File file = null;
		OutputStream os = null;
		try {
			file = new File(filename);
			os = new FileOutputStream(file);
			if (!file.exists()) {
				file.createNewFile();
			}
			os.write(bytes);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				System.out.println("Error in closing the Stream");
			}

		}
	}

	public static BufferedImage toBufferedImage(byte[] bytes, int width, int height, int imgType) {
		BufferedImage img = new BufferedImage(width, height, imgType);
		int ind = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				byte r = bytes[ind];
				byte g = bytes[ind + height * width];
				byte b = bytes[ind + height * width * 2];

				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
				img.setRGB(x, y, pix);
				ind++;
			}
		}

		return img;
	}
}
