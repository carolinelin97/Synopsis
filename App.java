package com.codebind;

import java.util.Date;
import java.util.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import java.util.TimerTask;

public class App {
    private static JFrame frame;
    private JPanel panelMain;
    private JPanel ImagePanel;
    private JPanel SynopsisPanel;
    private JPanel ButtonPanel;
    private JButton PlayButton;
    private JButton PauseButton;
    private JButton StopButton;
    private JLabel ImageLabel;
    private JLabel SynopsisLabel;

    Timer timer;
    String status;
    int index = 0;      // Pseudo Video Starting Frame Index

    MetaData[] metaData;

    // Image/Video Resource (from synopsis image click event)
    String resourcePath;
    char resourceType;

    // Sound
    PlaySound bgMusic = new PlaySound();


    // Constructor
    public App() {

        // Load MetaData
        MetaDataLoader metaDataLoader = new MetaDataLoader();
        metaDataLoader.loadMetaData("metadata.txt");
        metaData = metaDataLoader.getMetaData();
        for (int i = 0; i < 20; i++)    metaData[i].showMetaData();


        // Load Synopsis Image
        BufferedImage synImg = new BufferedImage(1000, 200, BufferedImage.TYPE_INT_RGB);
        readImageRGB("MySynopsis.rgb", synImg, 1000, 200);
        SynopsisLabel.setIcon(new ImageIcon(synImg));

        timer = new Timer();
        status = "null";

        // Play Button
        PlayButton.addActionListener(actionEvent -> {
            // Button Only Available When Playing Video
            if (resourceType == 'V' && status != "play"){
                showVideo(resourcePath);
                status = "play";
            }
        });

        // Pause Button
        PauseButton.addActionListener(actionEvent -> {
            // Button Only Available When Playing Video
            if (resourceType == 'V'){
                if (status == "play"){
                    timer.cancel();
                    status = "pause";
                }
                if (bgMusic.getStatus() == "play"){
                    bgMusic.pause();
                }
            }
        });

        // Stop Button
        StopButton.addActionListener(actionEvent -> {
            // Button Only Available When Playing Video
            if (resourceType == 'V'){
                index = 0;      // Go back to the beginning of the video
                timer.cancel();

                String temp = resourcePath.substring(0, resourcePath.lastIndexOf("/")+1) + "image-0001.rgb";
                resourcePath = temp;
                showImage(resourcePath);
                bgMusic.stop();
                status = "null";
            }
        });

        // Synopsis Image Click Event Links To Resource
        SynopsisLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (status == "play") {
                    // Stop playing previous video
                    timer.cancel();
                    bgMusic.pause();
                    bgMusic.setStatus("null");
                    status = "null";
                    index = 0;
                }

                super.mouseClicked(e);
                Point point = e.getPoint();
                int x = (int)point.getX();
                int y = (int)point.getY();

                // Get resource index by location in synopsis image
                int idx = Math.floorDiv(x, 100) + Math.floorDiv(y, 100) * 10;
                resourcePath = metaData[idx].getPath();
                System.out.println(resourcePath);
                resourceType = metaData[idx].getType();

                // Button Only Available When Playing Video
                if (resourceType == 'V'){
                    // Start playing next video
                    showVideo(resourcePath);
                    status = "play";
                }
                if (resourceType == 'I') {
                    showImage(resourcePath);
                }
            }
        });
    }

    private void readImageRGB(String imgPath, BufferedImage img, int width, int height)
    {
        try
        {
            int frameLength = width*height*3;

            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);

            long len = frameLength;
            byte[] bytes = new byte[(int) len];

            raf.read(bytes);

            int ind = 0;
            for(int y = 0; y < height; y++)
            {
                for(int x = 0; x < width; x++)
                {
                    byte a = 0;
                    byte r = bytes[ind];
                    byte g = bytes[ind+height*width];
                    byte b = bytes[ind+height*width*2];

                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    //int pix = ((a << 24) + (r << 16) + (g << 8) + b)

                    img.setRGB(x,y,pix);
                    ind++;
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void showImage(String imgPath){
        BufferedImage img = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
        readImageRGB(imgPath, img, 352, 288);
        ImageLabel.setIcon(new ImageIcon(img));
        ImageLabel.paintComponents(ImageLabel.getGraphics());
        ImageLabel.repaint();
    }


    private void showVideo(String imgPath){
        File tempFile = new File(imgPath.trim());
        String img = tempFile.getName();
        String imgDir = imgPath.substring(0, imgPath.lastIndexOf("/")+1);

        // Get image file list under the folder
        ArrayList<String> files = new ArrayList<String>();
        File file = new File(imgDir);
        File[] tempList = file.listFiles();

        // Choose the images to be played as video
        Boolean start = false;
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                if (tempList[i].getName().equals(img)){
                    start = true;
                }
                if (start) {
                    files.add(tempList[i].getName().toString());
                }
            }
        }

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                showImage(imgDir + files.get(index));
                index++;
                if (index == files.size()){
                    index = 0;
                    timer.cancel();
                    status = "null";
                    bgMusic.stop();
                }
            }
        };

        if (status != "play"){
            timer = new Timer();
            timer.scheduleAtFixedRate(task, new Date(), 33);
            playMusic(resourcePath);
        }
    }

    private void playMusic(String imgPath){
        if (bgMusic.getStatus() == "null"){
            bgMusic.Play(imgPath);
        }
        if (bgMusic.getStatus() == "pause"){
            bgMusic.resume();
        }
    }

    public static void main(String[] args) {
        frame = new JFrame("Synopsis");
        frame.setContentPane(new App().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
