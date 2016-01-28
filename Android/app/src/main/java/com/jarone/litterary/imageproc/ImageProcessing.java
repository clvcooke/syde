package com.jarone.litterary.imageproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.jarone.litterary.handlers.MessageHandler;
import com.jarone.litterary.helpers.ContextManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by vic on 11/17/15.
 */
public class ImageProcessing {

    private static boolean connected = false;
    private static Mat currentMat;
    private static Mat processingMat;
    private static Bitmap CVPreview;

    public static BaseLoaderCallback loaderCallback = new BaseLoaderCallback(ContextManager.getContext()) {
        @Override
        public void onManagerConnected(int status) {
            switch(status) {
                case LoaderCallbackInterface.SUCCESS:
                    connected = true;
                    currentMat = new Mat();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    public static void initializeOpenCV() {
        MessageHandler.d("INITIALIZING OPENCV");
        OpenCVLoader.initAsync("2.4.8", ContextManager.getActivity(), loaderCallback);
    }

    public static Pair calculateGPSPoints(Bitmap image, int pw, int py, Context context) {
        Pair points = new Pair<>(0, 0);
        int counter = 0, top = 0, bottom = 0, left = 0, right = 0;

        return points;
    }

    public static void setTestImage() {
        try {
            InputStream i = ContextManager.getActivity().getAssets().open("oval.jpg");
            readFrame(BitmapFactory.decodeStream(i));
        } catch(IOException e){

        }
    }

    public static Bitmap testCanny() {
        setTestImage();
        CVPreview = findEdges();
        return CVPreview;
    }

    public static void readFrame(Bitmap image) {
        Utils.bitmapToMat(image, currentMat);
    }

    public static Bitmap findEdges() {
        Mat edges = new Mat();
        Imgproc.Canny(currentMat, edges, 3, 10);
        Bitmap bitmap = Bitmap.createBitmap(edges.width(), edges.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(edges, bitmap);
        return bitmap;
    }

    public static ArrayList<LatLng> identifyLitter(Bitmap photo) {
        return new ArrayList<>();
    }

    public static Bitmap getCVPreview() {
        return CVPreview;
    }

    public static void detectBlobs() {
        processingMat = currentMat;
        Imgproc.cvtColor(processingMat, processingMat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(processingMat, processingMat, 150, 250);
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(100, 100));
        Imgproc.morphologyEx(processingMat, processingMat, Imgproc.MORPH_CLOSE, element);
        //imFill();
        Imgproc.medianBlur(processingMat, processingMat, 25);
        currentMat = processingMat;
        Bitmap preview = Bitmap.createBitmap(currentMat.width(), currentMat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(currentMat, preview);
        CVPreview = preview;
    }

    public static void imFill() {
        Mat fillMask = new Mat();
        Imgproc.floodFill(processingMat, fillMask, new Point(0, 0), new Scalar(255));
        Core.bitwise_not(fillMask, fillMask);
        Core.bitwise_or(processingMat, fillMask, processingMat);
    }
}

/**
 * function [distx, disty] = calculateGPSPoints(image, pw, ph)
 * % image is a binary image
 * [h, w, z] = size(image);
 * counter = 0;
 * top = 0;
 * bottom = 0;
 * left = 0;
 * right = 0;
 * % determine top of object
 * for i=1:h
 * counter = 0;
 * for j=1:w
 * if (image(i,j) == 1)
 * counter = counter +1;
 * if (counter > 10)
 * top = i;
 * break;
 * end
 * end
 * end
 * if (top ~= 0)
 * break;
 * end
 * end
 * % determine bottom of object
 * for i=h:-1:1
 * counter = 0;
 * for j=1:w
 * if (image(i,j) == 1)
 * counter = counter +1;
 * if (counter > 10)
 * bottom = i;
 * break;
 * end
 * end
 * end
 * if (bottom ~= 0)
 * break;
 * end
 * end
 * % determine left side of object
 * for j=1:w
 * counter = 0;
 * for i=top:bottom
 * if (image(i,j) == 1)
 * counter = counter+1;
 * if (counter > 10)
 * left = j;
 * break;
 * end
 * end
 * end
 * if (left ~= 0)
 * break;
 * end
 * end
 * % determine right side of object
 * for j=w:-1:1
 * counter = 0;
 * for i=top:bottom
 * if (image(i,j) == 1)
 * counter = counter +1;
 * if (counter > 5)
 * right = j;
 * break;
 * end
 * end
 * end
 * if (right ~= 0)
 * break;
 * end
 * end
 * % determine location of centre of detected object
 * row = round((bottom+top)/2);
 * col = round((right+left)/2);
 * % calculate distance from the centre of the image to the centre of the
 * % detected oject
 * distx = (col - w/2)/pw; %distance in the x direction from the centre
 * disty = (row - h/2)/ph; %distance in the y direction from the centre
 * end
 */

