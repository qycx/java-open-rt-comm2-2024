

package com.qycx.base;


import android.content.Context;

import com.qycx.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.*;

public class file_android {
    public static class Tmp_file {
        public boolean bW;

        public File file;
        public RandomAccessFile raf;
        //public System.IO.Stream    outputStream;
        //public OutputStreamWriter  outputStreamWriter;

        public byte[] buf;


    }

    ;

    //
    public static class qyFuncs_Droid {


        public static guiShare.PF_fclose fclose_android=(Object stream)-> {
            if (stream == null) return 0;
            Tmp_file tf = (Tmp_file) stream;
            try {
                if (tf.raf != null) {
                    tf.raf.close();
                    tf.raf = null;
                }
                if (tf.file != null) {
                    tf.file = null;
                }
                tf.buf = null;
            } catch (Exception e) {

            }
            return 0;
        };

        public static guiShare.PF_tfopen _tfopen_android=(String filename, String mode)-> {
            int iErr = 0;
            Tmp_file tf = new Tmp_file();
            tf.bW = true;

            Context context = MyApplication.getContext();
            String real_mode = "rws";

            try {
                tf.file = new File(filename);
                //
                if (mode.equals("rb")) {
                    real_mode = "r";
                }

                //
                //tf.raf = new RandomAccessFile(tf.file, "rws");
                tf.raf = new RandomAccessFile(tf.file, real_mode);
                tf.buf = new byte[1024];
            } catch (Exception e) {
                fclose_android.call(tf);
                return iErr;
            }

            iErr = 0;

            
            if (0 != iErr) {
                fclose_android.call(tf);

            }
            return iErr != 0 ? null : tf;
        };



        //
        public static guiShare.PF_fseeki64 fseeki64_android=(Object stream, long offset, int origin)-> {
            int iErr = -1;
            Tmp_file tf = (Tmp_file) stream;
            if (tf == null) return -1;

            try {
                tf.raf.seek(offset);
            } catch (Exception e) {
                return iErr;
            }
            iErr = 0;
            

            return iErr;
        };


        public static guiShare.PF_fread fread_android=(byte[] buffer, int size, int count, Object stream)-> {
            int iErr = -1;
            Tmp_file tf = (Tmp_file) stream;
            if (tf == null) return -1;
            int nRead = 0;
            int nCnt_read = 0;

            //
            if (count != 1) return -1;

            //
            try {
                long nTotal = size * count;
                if (nTotal == 0) return iErr;
                if (tf.buf.length < nTotal) {
                    tf.buf = new byte[(int) nTotal];
                }
                nRead = tf.raf.read(tf.buf, 0, (int) nTotal);
                if (nRead > 0) {
                    System.arraycopy(tf.buf, 0, buffer,0, nRead);
                }
                nCnt_read = nRead / size;

            } catch (Exception e) {
                return iErr;
            }
            iErr = 0;
            
            return iErr != 0 ? iErr : nCnt_read;
        };


        public static guiShare.PF_fwrite fwrite_android=(byte[] buffer, long size, long count, Object stream)-> {
            int iErr = -1;
            Tmp_file tf = (Tmp_file) stream;
            if (tf == null) return -1;

            try {
                long nTotal = size * count;
                if (nTotal == 0) return iErr;
                if (tf.buf.length < nTotal) {
                    tf.buf = new byte[(int) nTotal];
                }
                System.arraycopy(buffer,0, tf.buf, 0, (int) nTotal);

                tf.raf.write(tf.buf, 0, (int) nTotal);

            } catch (Exception e) {
                return iErr;

            }
            iErr = 0;
            
            return iErr != 0 ? iErr : count;
        };

        private static void copyFileUsingFileChannels(File source, File dest) {
            FileChannel inputChannel = null;
            FileChannel outputChannel = null;


            try {
                inputChannel = new FileInputStream(source).getChannel();
                outputChannel = new FileOutputStream(dest).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            } catch (Exception e) {

            } finally {
                try {
                    inputChannel.close();
                    outputChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //
        public static guiShare.PF_CopyFile CopyFile_android=(String lpExistingFileName, String lpNewFileName, boolean bFailIfExists)-> {
            boolean bRet = false;

            try {
                File file_in = new File(lpExistingFileName);
                File file_out = new File(lpNewFileName);

                //
                copyFileUsingFileChannels(file_in, file_out);


            } catch (Exception e) {
                return bRet;

            }

            bRet = true;
            

            return bRet;
        };


        public static guiShare.PF_DeleteFile DeleteFile_android=(String lpFileName)-> {
            boolean bRet = false;
            try {
                File file = new File(lpFileName);
                file.delete();
            } catch (Exception e) {
                return bRet;

            }
            bRet = true;
            
            return bRet;

        };


    }


}
