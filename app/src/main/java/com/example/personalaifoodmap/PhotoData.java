package com.example.personalaifoodmap;

public class PhotoData {
    String uri;
    boolean isFood;
    float lat; //위도
    float lon; //경도

    public PhotoData(){

    }

    public PhotoData(String uri, boolean isFood){
        this.uri=uri;
        this.isFood=isFood;
    }

}
