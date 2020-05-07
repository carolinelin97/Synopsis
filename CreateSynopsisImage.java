
import data.*;
import process.*;
import process.VideoSample;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter; // Import the FileWriter class
import java.util.Arrays;
import java.awt.image.BufferedImage;

import javax.sound.sampled.UnsupportedAudioFileException;

public class CreateSynopsisImage {
	// input offline_data/CSCI576ProjectMedia
	// input offline_data/StudentsUse_Dataset_Armenia
	// input offline_data/dataset

	// private final static String AUDIO_NAME = "video_"; // video_[num].wav
	private final static String AUDIO_NAME = "audio"; // audio.wav

	private final static String OUTPUT = "MySynopsis.rgb";
	// private final static String VID = "576RGBVideo";
	private final static String VID = "video";

	// private final static String IMG = "Image";
	private final static String IMG = "image";

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException {

		// handle inputs
		String folder_path = args[0];

		String[] vid_path = new String[4];
		String[] wav_path = new String[4];
		int[] img_num = new int[4];
		for (int i = 1; i <= 4; i++) {
			vid_path[i - 1] = folder_path + "/" + VID + i;
			// wav_path[i - 1] = vid_path[i - 1] + "/" + AUDIO_NAME + i + ".wav";
			wav_path[i - 1] = vid_path[i - 1] + "/" + AUDIO_NAME + ".wav";

			File directory = new File(vid_path[i - 1]);
			img_num[i - 1] = directory.list().length - 1;
			// System.out.println(img_num[i - 1]);
			// System.out.println(wav_path[i - 1]);
			// System.out.println(vid_path[i - 1]);
		}
		String image_path = folder_path + "/" + IMG;
		File image_folder = new File(image_path);
		String[] image_path_list = image_folder.list();
		String[] synopsis_comp_path = new String[20];

		// frame pick algorithm, wav sound level analysis
		for (int i = 0; i < 4; i++) {
			VideoSample vid1 = new VideoSample(new WavFile(new File(wav_path[i])));
			int[] idx1 = vid1.sampleIndex();
			Arrays.sort(idx1);

			for (int j = 0; j < idx1.length; j++) {
				synopsis_comp_path[j + i * 3] = vid_path[i] + "/" + index2FrameName(idx1[j]);
				System.out.println(synopsis_comp_path[j + i * 3]);
			}

		}

		// image pick algorithm
		int imglst_len = image_path_list.length;
		int count = 0;
		int index = 0;
		while (count < 8 && index < imglst_len) { // get images with 2 or more faces detected as many as possible
			String imgpath = image_path + "/" + image_path_list[index];
			FaceDetectionImage fdi = new FaceDetectionImage(imgpath);
			if (fdi.getFaces() >= 2) {
				synopsis_comp_path[count + 12] = imgpath;
				count++;
				System.out.println(imgpath);
			}
			index++;
		}

		index = 0;
		while (count < 8 && index < imglst_len) { // get the rest of images with only one face detected
			String imgpath = image_path + "/" + image_path_list[index];
			FaceDetectionImage fdi = new FaceDetectionImage(imgpath);
			if (fdi.getFaces() == 1) {
				synopsis_comp_path[count + 12] = imgpath;
				count++;
				System.out.println(imgpath);
			}
			index++;
		}

		// create synopsis image
		byte[][] matrix = new byte[20][100 * 100 * 3];
		for (int i = 0; i < 20; i++) {
			// String img_path = vid_path[1] + "\\" + index2FrameName(vid_index[0][0]);
			String img_path = synopsis_comp_path[i];
			// byte[] img_byte = ImageHandler.readImageFromFile(img_path);
			// byte[] img_byte_shr = ImageHandler.shrinkBy2(img_byte, 288, 352);
			// BufferedImage bfimg = ImageHandler.toBufferedImage(img_byte_shr, 176, 144,
			// BufferedImage.TYPE_INT_RGB);
			//
			//
			// Picture inputImg = new Picture(bfimg);
			// int removeColumns = 76;
			// int removeRows = 44;
			//
			// // System.out.printf("image is %d columns by %d rows\n", inputImg.width(),
			// inputImg.height());
			// SeamCarver sc = new SeamCarver(inputImg);
			//
			// for (int j = 0; j < removeRows; j++) {
			// int[] horizontalSeam = sc.findHorizontalSeam();
			// sc.removeHorizontalSeam(horizontalSeam);
			// }
			//
			// for (int j = 0; j < removeColumns; j++) {
			// int[] verticalSeam = sc.findVerticalSeam();
			// sc.removeVerticalSeam(verticalSeam);
			// }
			// Picture outputImg = sc.picture();
			// // System.out.printf("new image size is %d columns by %d rows\n", sc.width(),
			// sc.height());
			// // inputImg.show();
			// // outputImg.show();
			//
			// byte[] outputImage = sc.picture("s");
			// matrix[i] = outputImage;
			byte[] outputImage2 = FaceDetectionImage.faceDetect(img_path);
			matrix[i] = outputImage2;
		}

		ImageCombine ic = new ImageCombine(matrix);
		byte[] synopsis_img = ic.getCombinedImage();
		BufferedImage synopsis_image = ImageHandler.toBufferedImage(synopsis_img, 1000, 200,
				BufferedImage.TYPE_INT_RGB);

		// for verification
		Picture syno = new Picture(synopsis_image);
		syno.show();
		syno.save("synopsis.png");
		////////////////

		// write image and map to metadata
		ImageHandler.writeFileToImage(OUTPUT, synopsis_img);
		try {
			FileWriter myWriter = new FileWriter("metadata.txt");
			for (int i = 0; i < synopsis_comp_path.length; i++) {
				String datatype = (i < 12) ? "V " : "I ";
				myWriter.write(datatype + synopsis_comp_path[i]);
				if (i != synopsis_comp_path.length - 1) {
					myWriter.write("\n");
				}
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}

	private static String index2FrameName(int index) {
		String[] prefix = { "image-000", "image-00", "image-0", "image-" };
		String ind = Integer.toString(index);
		int len = ind.length();
		String res = prefix[len - 1] + ind + ".rgb";
		return res;
	}

}