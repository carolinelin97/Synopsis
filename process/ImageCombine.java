package process;

/**
 * to combine the processed chosen frames into a 1000x200 picture
 * 
 * 
 */
public class ImageCombine {
	public static final int NUM = 20;
	public static final int HEIGHT = 200;
	public static final int WIDTH = 1000;
	public static final int UNIT_HEIGHT = 100;
	public static final int UNIT_WIDTH = 100;

	private byte[] synopsis_image = new byte[WIDTH * HEIGHT * 3];

	public ImageCombine(byte[][] matrix) {
		init(matrix);
	}

	private void init(byte[][] matrix) {
		if (matrix == null || matrix.length != NUM) {
			System.out.println("matrix not available");
			return;
		}
		int unit_length = UNIT_HEIGHT * UNIT_WIDTH;
		if (matrix[0].length != unit_length * 3) {
			System.out.println("unit_pics error");
			return;
		}

		for (int rgb_flag = 0; rgb_flag < 3; rgb_flag++) {
			for (int layer = 0; layer < 2; layer++) {
				fillBytes(layer, rgb_flag, matrix);
			}
		}
	}

	/**
	 * fill synopsis image
	 * 
	 * @param layer    first or second layer of 10 unit pics, 0 or 1
	 * @param rgb_flag indicate type of data to fill this time, r:0, g:1, b:2
	 * @param matrix   given matrix of unit images
	 * 
	 * 
	 */
	private void fillBytes(int layer, int rgb_flag, byte[][] matrix) {
		for (int i = 0; i < UNIT_HEIGHT; i++) {
			for (int j = 0; j < UNIT_WIDTH; j++) {
				for (int k = 0; k < NUM / 2; k++) {
					synopsis_image[j + k * UNIT_WIDTH + i * WIDTH + layer * WIDTH * UNIT_HEIGHT
							+ rgb_flag * WIDTH * HEIGHT] = matrix[k + layer * NUM / 2][j + i * UNIT_WIDTH
									+ rgb_flag * UNIT_WIDTH * UNIT_HEIGHT];
				}
			}
		}

	}

	public byte[] getCombinedImage() {
		return synopsis_image;
	}

}