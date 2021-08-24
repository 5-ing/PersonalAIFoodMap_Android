package com.example.personalaifoodmap;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import androidx.exifinterface.media.ExifInterface;
import java.io.Serializable;

public class PhotoData implements Serializable {
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

    // exif에서 위도, 경도 값 추출
    public float[] getGPS(ExifInterface exif) {
        float lat, lon;
        String attLat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String attLatR = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String attLon = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String attLonR = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

        if (attLat != null && attLatR != null && attLon != null && attLonR != null) {
            if (attLatR.equals("N")) {
                lat = convertToDegree(attLat);
            } else  {
                lat = 0 - convertToDegree(attLat);
            }

            if(attLonR.equals("E")) {
                lon = convertToDegree(attLon);
            } else {
                lon = 0 - convertToDegree(attLon);
            }
        } else {
            // 위치 정보 없을 때 0으로 초기화
            lat = 0;
            lon = 0;
        }

        float[] result = new float[2];
        result[0] = lat;
        result[1] = lon;

        return result;
    }

    // exif에서 위도, 경도가 도분초로 표기. 기본 위도 경도로 변환.
    private float convertToDegree(String stringDms) {
        String[] dms = stringDms.split(",", 3);

        String[] stringD = dms[0].split("/", 2);
        double D0 = parseDouble(stringD[0]);
        double D1 = parseDouble(stringD[1]);
        double FloatD = D0 / D1;

        String[] stringM = dms[1].split("/", 2);
        double M0 = parseDouble(stringM[0]);
        double M1 = parseDouble(stringM[1]);
        double FloatM = M0 / M1;

        String[] stringS = dms[2].split("/", 2);
        double S0 = parseDouble(stringS[0]);
        double S1 = parseDouble(stringS[1]);
        double FloatS = S0 / S1;

        float result = parseFloat(String.valueOf(FloatD + (FloatM / 60) + (FloatS / 3600)));

        return result;
    }


}
