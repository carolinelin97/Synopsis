package com.codebind;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MetaDataLoader {
    private MetaData[] metaData = new MetaData[20];

    public MetaData[] getMetaData() {
        return metaData;
    }

    public MetaDataLoader(){
        for (int i = 0; i<20; i++){
            this.metaData[i] = new MetaData();
        }
    }

    public void loadMetaData(String metaDataPath){
        int lineNumber = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader((metaDataPath)));
            String line = null;
            while ((line = in.readLine()) != null){
                String path = line.substring(2);
                metaData[lineNumber].setMetaData(line.charAt(0), path);
                lineNumber++;
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
