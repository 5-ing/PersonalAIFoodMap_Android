package com.example.personalaifoodmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect2d;
import org.opencv.core.Point;
import org.opencv.core.Rect2d;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class DetectionActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_CAMERA=1001;

    ImageView detectionImageView;
    boolean startYolo = false;
    boolean firstTimeYolo = false;
    Net tinyYolo;
    String url;

    static {
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        detectionImageView = (ImageView) findViewById(R.id.detectionImageView);
        Glide.with(this).load(url).into( detectionImageView);
    }


    public void YOLO(View Button) {

        if (startYolo == false) {
            startYolo = true;

            //yolo 처음 실행 시 파일 불러오기
            if (firstTimeYolo == false) {
                firstTimeYolo = true;
                String tinyYoloCfg = getPath("yolov3-tiny.cfg",this);
                String tinyYoloWeights = getPath("yolov3-tiny.weights",this);

                tinyYolo = Dnn.readNetFromDarknet(tinyYoloCfg, tinyYoloWeights);
            }
            imageDetection();

        } else {
            startYolo = false;
        }
    }

    private static String getPath(String file, Context context) {
        AssetManager assetManager = context.getAssets();
        BufferedInputStream inputStream = null;
        try {

            inputStream = new BufferedInputStream(assetManager.open(file));
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            // Create copy file in storage.
            File outFile = new File(context.getFilesDir(), file);
            FileOutputStream os = new FileOutputStream(outFile);
            os.write(data);
            os.close();

            return outFile.getAbsolutePath();
        } catch (IOException ex) {
            Log.i(TAG, "Failed to upload a file");
        }
        return "";
    }

    public Mat imageDetection() {

        Mat mat = new Mat();

        if (startYolo) {

            Bitmap bitmap = BitmapFactory.decodeFile(url);
            OpenCVLoader.initDebug();

            Utils.bitmapToMat(bitmap, mat);

            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2BGR);
            Mat imageBlob = Dnn.blobFromImage(mat, 0.00392, new Size(416, 416), new Scalar(0, 0, 0),/*swapRB*/false, /*crop*/false);

            tinyYolo.setInput(imageBlob);

            java.util.List<Mat> result = new java.util.ArrayList<Mat>(2);

            List<String> outBlobNames = new java.util.ArrayList<>();
            outBlobNames.add(0, "yolo_16");
            outBlobNames.add(1, "yolo_23");

            tinyYolo.forward(result, outBlobNames);

            float confThreshold = 0.3f;

            List<Integer> clsIds = new ArrayList<>();
            List<Float> confs = new ArrayList<>();
            List<Rect2d> rects = new ArrayList<>();


            for (int i = 0; i < result.size(); ++i) {

                Mat level = result.get(i);

                for (int j = 0; j < level.rows(); ++j) {
                    Mat row = level.row(j);
                    Mat scores = row.colRange(5, level.cols());

                    Core.MinMaxLocResult mm = Core.minMaxLoc(scores);


                    float confidence = (float) mm.maxVal;


                    Point classIdPoint = mm.maxLoc;


                    if (confidence > confThreshold) {
                        int centerX = (int) (row.get(0, 0)[0] * mat.cols());
                        int centerY = (int) (row.get(0, 1)[0] * mat.rows());
                        int width = (int) (row.get(0, 2)[0] * mat.cols());
                        int height = (int) (row.get(0, 3)[0] * mat.rows());


                        int left = centerX - width / 2;
                        int top = centerY - height / 2;

                        clsIds.add((int) classIdPoint.x);
                        confs.add((float) confidence);


                        rects.add(new Rect2d(left, top, width, height));
                    }
                }
            }
            int ArrayLength = confs.size();

            if (ArrayLength >= 1) {
                // Apply non-maximum suppression procedure.
                float nmsThresh = 0.1f;

                MatOfFloat confidences = new MatOfFloat(Converters.vector_float_to_Mat(confs));


                Rect2d[] boxesArray = rects.toArray(new Rect2d[0]);


                MatOfRect2d bboxes = new MatOfRect2d(boxesArray);

                MatOfInt indices = new MatOfInt();

                Dnn.NMSBoxes(bboxes, confidences, confThreshold, nmsThresh, indices);

                // Detection 후 결과 박스 그리기
                int[] ind = indices.toArray();
                for (int i = 0; i < ind.length; ++i) {

                    int idx = ind[i];
                    Rect2d box = boxesArray[idx];

                    int idGuy = clsIds.get(idx);
                    float conf = confs.get(idx);

                    List<String> cocoNames = Arrays.asList(
                            "rice",
                            "eels on rice",
                            "pilaf",
                            "chicken-'n'-egg on rice",
                            "pork cutlet on rice",
                            "beef curry",
                            "sushi",
                            "chicken rice",
                            "fried rice",
                            "tempura bowl",
                            "bibimbap",
                            "toast",
                            "croissant",
                            "roll bread",
                            "raisin bread",
                            "chip butty",
                            "hamburger",
                            "pizza",
                            "sandwiches",
                            "udon noodle",
                            "tempura udon",
                            "soba noodle",
                            "ramen noodle",
                            "beef noodle",
                            "tensin noodle",
                            "fried noodle",
                            "spaghetti",
                            "Japanese-style pancake",
                            "takoyaki",
                            "gratin",
                            "sauteed vegetables",
                            "croquette",
                            "grilled eggplant",
                            "sauteed spinach",
                            "vegetable tempura",
                            "miso soup",
                            "potage",
                            "sausage",
                            "oden",
                            "omelet",
                            "ganmodoki",
                            "jiaozi",
                            "stew",
                            "teriyaki grilled fish",
                            "fried fish",
                            "grilled salmon",
                            "salmon meuniere",
                            "sashimi",
                            "grilled pacific saury",
                            "sukiyaki",
                            "sweet and sour pork",
                            "lightly roasted fish",
                            "steamed egg hotchpotch",
                            "tempura",
                            "fried chicken",
                            "sirloin cutlet",
                            "nanbanzuke",
                            "boiled fish",
                            "seasoned beef with potatoes",
                            "hambarg steak",
                            "steak",
                            "dried fish",
                            "ginger pork saute",
                            "spicy chili-flavored tofu",
                            "yakitori",
                            "cabbage roll",
                            "omelet",
                            "egg sunny-side up",
                            "natto",
                            "cold tofu",
                            "egg roll",
                            "chilled noodle",
                            "stir-fried beef and peppers",
                            "simmered pork",
                            "boiled chicken and vegetables",
                            "sashimi bowl",
                            "sushi bowl",
                            "fish-shaped pancake with bean jam",
                            "shrimp with chill source",
                            "roast chicken",
                            "steamed meat dumpling",
                            "omelet with fried rice",
                            "cutlet curry",
                            "spaghetti meat sauce",
                            "fried shrimp",
                            "potato salad",
                            "green salad",
                            "macaroni salad",
                            "Japanese tofu and vegetable chowder",
                            "pork miso soup",
                            "chinese soup",
                            "beef bowl",
                            "kinpira-style sauteed burdock",
                            "rice ball",
                            "pizza toast",
                            "dipping noodles",
                            "hot dog",
                            "french fries",
                            "mixed rice",
                            "goya chanpuru",
                            "green curry",
                            "okinawa soba",
                            "mango pudding",
                            "almond jelly",
                            "jjigae",
                            "dak galbi",
                            "dry curry",
                            "kamameshi",
                            "rice vermicelli",
                            "paella",
                            "tanmen",
                            "kushikatu",
                            "yellow curry",
                            "pancake",
                            "champon",
                            "crape",
                            "tiramisu",
                            "waffle",
                            "rare cheese cake",
                            "shortcake",
                            "chop suey",
                            "twice cooked pork",
                            "mushroom risotto",
                            "samul",
                            "zoni",
                            "french toast",
                            "fine white noodles",
                            "minestrone",
                            "pot au feu",
                            "chicken nugget",
                            "namero",
                            "french bread",
                            "rice gruel",
                            "broiled eel bowl",
                            "clear soup",
                            "yudofu",
                            "mozuku",
                            "inarizushi",
                            "pork loin cutlet",
                            "pork fillet cutlet",
                            "chicken cutlet",
                            "ham cutlet",
                            "minced meat cutlet",
                            "thinly sliced raw horsemeat",
                            "bagel",
                            "scone",
                            "tortilla",
                            "tacos",
                            "nachos",
                            "meat loaf",
                            "scrambled egg",
                            "rice gratin",
                            "lasagna",
                            "Caesar salad",
                            "oatmeal",
                            "fried pork dumplings served in soup",
                            "oshiruko",
                            "muffin",
                            "popcorn",
                            "cream puff",
                            "doughnut",
                            "apple pie",
                            "parfait",
                            "fried pork in scoop",
                            "lamb kebabs",
                            "dish consisting of stir-fried potato, eggplant and green pepper",
                            "roast duck",
                            "hot pot",
                            "pork belly",
                            "xiao long bao",
                            "moon cake",
                            "custard tart",
                            "beef noodle soup",
                            "pork cutlet",
                            "minced pork rice",
                            "fish ball soup",
                            "oyster omelette",
                            "glutinous oil rice",
                            "trunip pudding",
                            "stinky tofu",
                            "lemon fig jelly",
                            "khao soi",
                            "Sour prawn soup",
                            "Thai papaya salad",
                            "boned, sliced Hainan-style chicken with marinated rice",
                            "hot and sour, fish and vegetable ragout",
                            "stir-fried mixed vegetables",
                            "beef in oyster sauce",
                            "pork satay",
                            "spicy chicken salad",
                            "noodles with fish curry",
                            "Pork Sticky Noodles",
                            "Pork with lemon",
                            "stewed pork leg",
                            "charcoal-boiled pork neck",
                            "fried mussel pancakes",
                            "Deep Fried Chicken Wing",
                            "Barbecued red pork in sauce with rice",
                            "Rice with roast duck",
                            "Rice crispy pork",
                            "Wonton soup",
                            "Chicken Rice Curry With Coconut",
                            "Crispy Noodles",
                            "Egg Noodle In Chicken Yellow Curry",
                            "coconut milk soup",
                            "pho",
                            "Hue beef rice vermicelli soup",
                            "Vermicelli noodles with snails",
                            "Fried spring rolls",
                            "Steamed rice roll",
                            "Shrimp patties",
                            "ball shaped bun with pork",
                            "Coconut milk-flavored crepes with shrimp and beef",
                            "Small steamed savory rice pancake",
                            "Glutinous Rice Balls",
                            "loco moco",
                            "haupia",
                            "malasada",
                            "laulau",
                            "spam musubi",
                            "oxtail soup",
                            "adobo",
                            "lumpia",
                            "brownie",
                            "churro",
                            "jambalaya",
                            "nasi goreng",
                            "ayam goreng",
                            "ayam bakar",
                            "bubur ayam",
                            "gulai",
                            "laksa",
                            "mie ayam",
                            "mie goreng",
                            "nasi campur",
                            "nasi padang",
                            "nasi uduk",
                            "babi guling",
                            "kaya toast",
                            "bak kut teh",
                            "curry puff",
                            "chow mein",
                            "zha jiang mian",
                            "kung pao chicken",
                            "crullers",
                            "eggplant with garlic sauce",
                            "three cup chicken",
                            "bean curd family style",
                            "salt & pepper fried shrimp with shell",
                            "baked salmon",
                            "braised pork meat ball with napa cabbage",
                            "winter melon soup",
                            "steamed spareribs",
                            "chinese pumpkin pie",
                            "eight treasure rice",
                            "hot & sour soup");

                    int intConf = (int) (conf * 100);

                    Imgproc.putText(mat, cocoNames.get(idGuy) + " " + intConf + "%", box.tl(), Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 255, 0), 2);
                    Imgproc.rectangle(mat, box.tl(), box.br(), new Scalar(255, 0, 0), 2);
                }

                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGBA);
                Utils.matToBitmap(mat, bitmap);
                detectionImageView.setImageBitmap(bitmap);
            }
        }

        return mat;
    }

}