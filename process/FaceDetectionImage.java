package process;

import java.awt.image.BufferedImage;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Range;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;

import data.*;

public class FaceDetectionImage {

	private static final int RAD = 100;
	private static int width = 352;
	private static int height = 288;
	private static int unit_wh = 100;
	private final static String XML = "C:/opencv/sources/data/lbpcascades/lbpcascade_frontalface.xml";
	public static int face_num = 0;
	private byte[] bytes = null;

	public FaceDetectionImage(String file) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		byte[] imgbyte = ImageHandler.readImageFromFile(file);
		BufferedImage img = ImageHandler.toBufferedImage(imgbyte, width, height, BufferedImage.TYPE_3BYTE_BGR);
		Mat src = ImageHandler.matify(img);

		// Instantiating the CascadeClassifier
		CascadeClassifier classifier = new CascadeClassifier(XML);

		// Detecting the face in the snap
		MatOfRect faceDetections = new MatOfRect();
		classifier.detectMultiScale(src, faceDetections);

		// get face/image center coordinates
		int horiz_mid = width / 2;
		int vertic_mid = height / 2;
		Rect[] rects = faceDetections.toArray();

		face_num = rects.length;

		if (face_num % 2 == 1) {
			int i = rects.length / 2;
			horiz_mid = rects[i].x + rects[i].width / 2;
			vertic_mid = rects[i].y - rects[i].height / 2;
		}
		Point center = new Point(horiz_mid, vertic_mid);
		Range[] square = getCropSquare(center);
		Mat output = src.submat(square[1], square[0]);

		// 
		BufferedImage img2 = ImageHandler.toBufferedImage(output);
		byte[] out_byte = ImageHandler.toByte(img2);

		bytes = ImageHandler.shrinkBy2(out_byte, 2 * RAD, 2 * RAD);

	}

	public byte[] getBytes() {
		return bytes;
	}

	public int getFaces() {
		return face_num;
	}

	public static byte[] faceDetect(String file) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		byte[] imgbyte = ImageHandler.readImageFromFile(file);
		BufferedImage img = ImageHandler.toBufferedImage(imgbyte, width, height, BufferedImage.TYPE_3BYTE_BGR);
		// Picture pic = new Picture(img);
		// pic.save("test.png");
		// Mat src = Imgcodecs.imread("test.png");
		Mat src = ImageHandler.matify(img);

		// Instantiating the CascadeClassifier
		CascadeClassifier classifier = new CascadeClassifier(XML);

		// Detecting the face in the snap
		MatOfRect faceDetections = new MatOfRect();
		classifier.detectMultiScale(src, faceDetections);
		System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

		int horiz_mid = width / 2;
		int vertic_mid = height / 2;
		Rect[] rects = faceDetections.toArray();

		face_num = rects.length;

		if (rects.length > 1) {
			int i = rects.length / 2;
			horiz_mid = rects[i].x + rects[i].width / 2;
			vertic_mid = rects[i].y - rects[i].height / 2;
		}
		Point center = new Point(horiz_mid, vertic_mid);
		Range[] square = getCropSquare(center);
		Mat output = src.submat(square[1], square[0]);

		Imgproc.rectangle(output, // Matrix obj of the image
				new Point(0, 2 * RAD), // p1
				new Point(2 * RAD, 0), // p2
				new Scalar(0, 0, 255), // Scalar object for color
				2 // Thickness of the line
		);

		BufferedImage img3 = ImageHandler.toBufferedImage(output);
		// Picture pic1 = new Picture(img3);
		// byte[] out_byte = pic1.getByte();
		byte[] out_byte = ImageHandler.toByte(img3);
		byte[] out_byte2 = ImageHandler.shrinkBy2(out_byte, 2 * RAD, 2 * RAD);

		// some verification
		Picture pic1 = new Picture(img3);
		pic1.save("crop.png");
		BufferedImage img2 = ImageHandler.toBufferedImage(out_byte2, unit_wh, unit_wh, BufferedImage.TYPE_INT_RGB);
		Picture pic2 = new Picture(img2);
		pic2.save("crop2.png");

		return out_byte2;
	}

	private static Range[] getCropSquare(Point center) {
		int x_mid = (int) center.x;
		int y_mid = (int) center.y;

		int x_left = x_mid - RAD;
		int x_right = x_mid + RAD;
		int y_top = y_mid - RAD;
		int y_bot = y_mid + RAD;
		if (x_left < 0) {
			x_right -= x_left;
			x_left = 0;
		} else if (x_right > width) {
			x_left = x_left - (x_right - width + 1);
			x_right = width - 1;
		}
		if (y_top < 0) {
			y_bot -= y_top;
			y_top = 0;
		} else if (y_bot > height) {
			y_top = y_top - (y_bot - height + 1);
			y_bot = height - 1;
		}

		Range colrange = new Range(x_left, x_right);
		Range rowrange = new Range(y_top, y_bot);

		Range[] res = { colrange, rowrange };
		return res;
	}

	public static void main(String[] args) {
		faceDetect("offline_data/dataset/image/17.rgb");

	}

}