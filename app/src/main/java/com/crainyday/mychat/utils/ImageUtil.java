package com.crainyday.mychat.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtil {

    public static String BitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream outputStream = null;
        try{
            if (bitmap!=null){
                outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                byte[] bitmaps = outputStream.toByteArray();
                result = Base64.encodeToString(bitmaps, Base64.DEFAULT);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(outputStream != null){
                    outputStream.flush();
                    outputStream.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Bitmap Base64ToBitmap(String base64) {
        byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Bitmap transitionContents(String contents) {
        Bitmap bitmap = null;
        if (contents.contains(",")||contents.contains(";")||contents.contains(":")){
            try{
                byte[] bitmapArray = Base64.decode(contents.split(",")[1], Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            bitmap = Base64ToBitmap(contents);
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int newWidth = 600;
        int newHeight = 600;
        float scaleWidth = (float)newWidth / width;
        float scaleHeight = (float)newHeight / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap mBitmap = Bitmap.createBitmap(bitmap, 0, 0,width,height,matrix, true);
        return mBitmap;
    }
}
