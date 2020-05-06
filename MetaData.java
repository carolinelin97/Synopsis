package com.codebind;

public class MetaData {
    private char type;
    private String path;

    public MetaData(){
        this.type = 'N';
        this.path = "";
    }

    public void showMetaData(){
        System.out.println(type + " " + path);
    }

    public void setMetaData(char t, String p) {
        this.type = t;
        this.path = p;
    }
}
