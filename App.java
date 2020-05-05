package com.codebind;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

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

    public App() {
        panelMain.setBorder(new EmptyBorder(5,5,5,5));

        PlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String imgPath = "C:\\Users\\carol\\Downloads\\CSCI576ProjectMedia\\CSCI576ProjectMedia\\Image\\RGB\\image-0010.rgb";
//                showImage(imgPath);
                showVideo(imgPath);
            }
        });

        PauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
//                String imgPath = "C:\\Users\\carol\\Downloads\\CSCI576ProjectMedia\\CSCI576ProjectMedia\\Image\\RGB\\image-0023.rgb";
//                showVideo(imgPath);
            }
        });
    }

    private void readImageRGB(String imgPath, BufferedImage img)
    {
        try
        {
            int width = 352;
            int height = 288;
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
//        String imgPath = "C:\\Users\\carol\\Downloads\\CSCI576ProjectMedia\\CSCI576ProjectMedia\\Image\\RGB\\image-0003.rgb";
        BufferedImage img = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
        readImageRGB(imgPath, img);
        ImageLabel.setIcon(new ImageIcon(img));
        ImageLabel.paintComponents(ImageLabel.getGraphics());
        ImageLabel.repaint();
        frame.pack();
        frame.setVisible(true);
    }

    private void showVideo(String imgPath){
        File tempFile =new File(imgPath.trim());
        String img = tempFile.getName();
        String imgDir = imgPath.substring(0, imgPath.lastIndexOf("\\")+1);

//        System.out.println(img);
//        System.out.println(imgDir);

        ArrayList<String> files = new ArrayList<String>();
        File file = new File(imgDir);
        File[] tempList = file.listFiles();

        Boolean flag = false;
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                if (tempList[i].getName().equals(img)){
                    flag = true;
                }
                if (flag) {
                    System.out.println("文     件：" + tempList[i].getName());
                    files.add(tempList[i].getName().toString());
                }
            }
        }


        try{
            showImage(imgDir+img);
            Thread.sleep(500);
//            showImage(imgDir+files.get(6));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        frame = new JFrame("App");
        frame.setContentPane(new App().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
