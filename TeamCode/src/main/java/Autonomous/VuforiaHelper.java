package Autonomous;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.teamcode.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by robotics on 12/12/17.
 */

public class VuforiaHelper {

    VuforiaLocalizer vuLoc;


    public VuforiaHelper(){
        try {
            VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
            params.vuforiaLicenseKey = "Afh+Mi//////AAAAGT/WCUCZNUhEt3/AvBZOSpKBjwlgufihL3d3H5uiMfbq/1tDOM6w+dgMIdKUvVFEjNNy9zSaruPDbwX0HwjI6BEvxuWbw+UcZFcfF7i4g7peD4zSCEyZBCi59q5H/a2aTsnJVaG0WO0pPawHDuuScrMsA/QPKQGV/pZOT6rK8cW2C3bEkZpZ1qqkSM5zNeKs2OQtr8Bvl2nQiVK6mQ3ZT4fxWGb7P/iTZ4k1nEhkxI56sr5HlxmSd0WOx9i8hYDTJCASU6wwtOeUHZYigZmdRYuARS+reLJRXUylirmoU8kVvMK1p2Kf8dajEWsTuPwBec/BSaygmpqD0WkAc2B1Vmaa/1zTRfYNR3spIfjHQCYu";
            params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
            vuLoc = ClassFactory.createVuforiaLocalizer(params);
            Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true); //enables RGB565 format for the image
            vuLoc.setFrameQueueCapacity(1); //tells VuforiaLocalizer to only store one frame at a time

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Bitmap getImage(int wantedWidth, int wantedHeight) {
        Image i = null;
        long timeStart = System.currentTimeMillis();
        try{
            i = takeImage();

        }catch (Exception e){
            throw new RuntimeException(e);
        }
        Log.d("VH IMG TAKE TIME", "" + (System.currentTimeMillis() - timeStart));

        if(i != null) {
            long conversionStart = System.currentTimeMillis();
            Bitmap bmp = convertImageToBmp(i);
            Log.d("VH IMG Convert", "" + (System.currentTimeMillis() - conversionStart));
            long copyStart = System.currentTimeMillis();
            Bitmap orig = bmp.copy(Bitmap.Config.ARGB_8888,true);
            Log.d("VH IMG ORIG","Height: " + orig.getHeight() + " Width: " + orig.getWidth());
            Log.d("VH IMG CPY", "" + (System.currentTimeMillis() - copyStart));
            long scaleStart = System.currentTimeMillis();
            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(orig,wantedWidth,wantedHeight,true);
            Log.d("VH IMG Scale", "" + (System.currentTimeMillis() - scaleStart));
            long rotationStart = System.currentTimeMillis();
            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
            Log.d("VH IMG Rotation", "" + (System.currentTimeMillis() - rotationStart));
            return rotatedBitmap;
        }
        return null;
    }


    private Bitmap convertImageToBmp(Image img){
        Bitmap bmp = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.RGB_565);
        bmp.copyPixelsFromBuffer(img.getPixels());
        return bmp;
    }

    private Image takeImage() throws InterruptedException{
        Image img = null;
        VuforiaLocalizer.CloseableFrame frame = vuLoc.getFrameQueue().take(); //takes the frame at the head of the queue
        long numImages = frame.getNumImages();
        for (int i = 0; i < numImages; i++) {
            //Log.d("Format","" + frame.getImage(i).getFormat());
            if (frame.getImage(i).getFormat() == PIXEL_FORMAT.RGB565) {
                img = frame.getImage(i);
                break;
            }
        }
        return img;
    }

    public void saveBMP(Bitmap bmp){

        FileOutputStream out = null;
        try {

            File yourFile = new File(Environment.getExternalStorageDirectory().toString() + "/robot" + System.currentTimeMillis() + ".png");
            yourFile.createNewFile(); // if file already exists will do nothing
            out = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/robot" + System.currentTimeMillis() + ".png",false);
            Log.d("Saving",out.toString());
            bmp.compress(Bitmap.CompressFormat.PNG, 10, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
