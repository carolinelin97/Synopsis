package com.codebind;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class PlayWaveFile {
    private final int EXTERNAL_BUFFER_SIZE = 524288;

    public PlayWaveFile(String file) {
        try {
            // read wav file to audio stream
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(file));
            // read audio format from stream
            AudioFormat audioFormat = audioInputStream.getFormat();
            System.out.println("Sample Rate: " + ((AudioFormat) audioFormat).getSampleRate());
            System.out.println("Frame Length: " + audioInputStream.getFrameLength());
            System.out.println("Time Length: " + audioInputStream.getFrameLength() / audioFormat.getSampleRate());
            // SourceDataLine info
            Info dataLineInfo = new Info(SourceDataLine.class, audioFormat);

            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            byte[] b = new byte[this.EXTERNAL_BUFFER_SIZE];
            int len = 0;
            sourceDataLine.open(audioFormat, this.EXTERNAL_BUFFER_SIZE);
            sourceDataLine.start();
            while ((len = audioInputStream.read(b)) > 0) {
                sourceDataLine.write(b, 0, len);
            }

            audioInputStream.close();
            sourceDataLine.drain();
            sourceDataLine.close();

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        // get the command line parameters
        if (args.length < 1) {
            System.err.println("Error with arguments.");
            return;
        }
        String filename = args[0];
        new PlayWaveFile(filename);

    }
}
