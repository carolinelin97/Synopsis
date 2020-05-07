package process;

//import java.util.PriorityQueue;

import data.*;

/**
 * to get index of chosen frames by analyzing wav sound level
 * 
 * 
 */
public class VideoSample {
	private static final int INDEX_NUM = 3;
	private static final int SAMPLES_PER_FRAME = 1470; // 44100 / 30 = 1470
	private int[] index = new int[INDEX_NUM];
	private WavFile wav = null;

	public VideoSample(WavFile wav) {

		// int amplitudeExample = wav.getSampleInt(1400); // 140th amplitude value.
		// System.out.println(amplitudeExample);
		// System.out.println(wav.getFramesCount());
		// for (int i = 0; i < wav.getFramesCount(); i++) {
		// int amplitude = wav.getSampleInt(i);
		// System.out.println(amplitude);
		// // Plot.
		// }

		this.wav = wav;
		init();

	}

	private void init() {
		// PriorityQueue<int[]> minHeap = new PriorityQueue<>(INDEX_NUM, new
		// Comparator<int[]>() {

		// @Override
		// public int compare(int[] one, int[] two) {
		// if (one[1] == (two[1])) {
		// return 0;
		// }
		// return one[1] > two[1] ? 1 : -1;
		// }
		//
		// });

		int length = wav.getFramesCount();
		int num_frame = (int) Math.ceil((double) length / (double) SAMPLES_PER_FRAME);
		int[] max1, max2, max3 = { -1, Integer.MIN_VALUE };
		int step = num_frame / INDEX_NUM;
		max1 = getMax(0, step);
		max2 = getMax(step, step * 2);
		max3 = getMax(step * 2, num_frame);

		index[0] = max1[0];
		index[1] = max2[0];
		index[2] = max3[0];

		// for (int i = 0; i < num_frame; i++) {
		// int index = i * SAMPLES_PER_FRAME;
		// int end = (index + SAMPLES_PER_FRAME);
		// if (end >= length) {
		// end = length;
		// }
		// int avgAmp = avgAmp(index, end);
		// int[] frame_info = new int[]{i, avgAmp};

		// if (i < INDEX_NUM) {
		// minHeap.offer(frame_info);
		// } else if (frame_info[1] > minHeap.peek()[1]) {
		// minHeap.poll();
		// minHeap.offer(frame_info);
		// }
		// }

		// for (int i = 0; i < INDEX_NUM; i++) {
		// index[INDEX_NUM - i - 1] = minHeap.poll()[0];
		// }
	}

	private int[] getMax(int start, int fin) {
		int[] max = { -1, Integer.MIN_VALUE };
		int length = wav.getFramesCount();

		for (int i = start; i < fin; i++) {
			int index = i * SAMPLES_PER_FRAME;
			int end = (index + SAMPLES_PER_FRAME);
			if (end >= length) {
				end = length;
			}
			int avgAmp = avgAmp(index, end);
			int[] frame_info = new int[] { i, avgAmp };
			if (max[1] < frame_info[1]) {
				max = frame_info;
			}
		}

		return max;

	}

	private int avgAmp(int index, int end) {
		int counter = 0;
		long sum = 0;
		for (int i = index; i < end; i++) {
			sum = sum + wav.getSampleInt(i);
			counter++;
		}
		long result = sum / counter;
		// System.out.println(result);

		return (int) result;
	}

	public int[] sampleIndex() {
		return index;
	}
}
