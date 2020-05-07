package com.codebind;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class PlaySound {
    private Clip clip;
    long clipTimePosition;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Clip getClip() {
        return clip;
    }

    public void setClip(Clip clip) {
        this.clip = clip;
    }

    public PlaySound(){
        status = "null";
        clipTimePosition = 0;
    }

    public void Play(String imgPath){
        try{
            int framePos = Integer.parseInt(imgPath.substring(imgPath.lastIndexOf("/")+7, imgPath.length()-4));
            clipTimePosition = (long)(framePos / 29.97 * 1000000);
//            System.out.println(clipTimePosition);
            String audioPath = imgPath.substring(0, imgPath.lastIndexOf("/")+1) + "audio.wav";
            File file = new File(audioPath);
            if (file.exists()){
                // read wav file to audio stream
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);

                clip.setMicrosecondPosition(clipTimePosition);
                clip.start();
                status = "play";
            } else {
                System.out.println("File:" + audioPath +" doesn't exist.");
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void pause(){
        clipTimePosition = clip.getMicrosecondPosition();
        clip.stop();
        status = "pause";
    }

    public void resume(){
//        System.out.println(clipTimePosition);
        clip.setMicrosecondPosition(clipTimePosition);
        clip.start();
        status = "play";
    }

    public void stop(){
        clip.stop();
        status = "null";
    }
}
