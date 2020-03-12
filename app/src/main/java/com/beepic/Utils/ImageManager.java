package com.beepic.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageManager {
    private static final String TAG = "ImageManager";

    public static Bitmap getBitmap(String imgUrl){
        File imageFile = new File(imgUrl);
        FileInputStream fis = null;
        Bitmap bitmap = null;


        if(imageFile == null){
            
          return bitmap;
        }else {


            try {
                fis = new FileInputStream(imageFile);
                bitmap = BitmapFactory.decodeStream(fis);


            } catch (FileNotFoundException e) {
                Log.e(TAG, "getBitmap: File not found! " + e.getMessage());
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    Log.e(TAG, "getBitmap: File Not Found! " + e.getMessage());
                }
            }
        }


        return bitmap;
    }

    /**
     * return byte array for bitmap
     * quality can be between 0 and 100
     * @param bm
     * @param quality
     * @return
     */
    public static byte[] getBytesFromBitmap(Bitmap bm, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }
}
