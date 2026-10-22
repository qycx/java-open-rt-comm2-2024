package com.qycx.tmpjavalibrary;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileUtil {

    public static final String SAVE_DIR = "/sdcard/DCIM/Camera2GetPreview/";

    public static boolean saveBytes(byte[] bytes, boolean bAppend, String imagePath,boolean bDbg) {
        File file = new File(imagePath);
        /*
        try {
            if (!bAppend){
                if ( file.exists()) {
                    file.delete();
                }
                if ( file.exists()){
                    Log.e("saveByte","createNewFile failed: file already exists,");
                    return false;
                }
                //
                file.createNewFile();
            }
            long fileLen=file.length();
            RandomAccessFile raf=new RandomAccessFile(file,"rws");
            raf.seek(fileLen);
            raf.write(bytes);
            raf.close();
            return true;
        }
        catch (Exception e){
            Log.e("saveBytes","exception");
        }
        finally {

        }
         */


        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        try {
            //file.createNewFile();
            //
            FileOutputStream fos = new FileOutputStream(file,bAppend);
            if ( bytes!=null) {
                fos.write(bytes);
            }
            fos.flush();
            fos.getFD().sync();
            //
            if ( bDbg){
                Log.e("FileUtil","safvBytes, after getFD().sync");
            }
            //
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean saveBitmap(Bitmap bitmap, String imagePath) {
        if (bitmap == null) {
            return false;
        }
        File file = new File(imagePath);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.getFD().sync();
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
