package com.androidex.temperaturetesting.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2018/4/27.
 */

public class FileUtils {
    public static String getAppFilesDir(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    public static boolean writeStringToFile(String content,String fileName, boolean isAppend) {
        if (!TextUtils.isEmpty(content)) {
            boolean bFlag = false;
            final int iLen = content.length();
            final File file = new File(fileName);
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                final FileOutputStream fos = new FileOutputStream(file,
                        isAppend);
                byte[] buffer = new byte[iLen];
                try {
                    buffer = content.getBytes();
                    fos.write(buffer);
                    if (isAppend) {
                        fos.write(",".getBytes());
                    }
                    fos.flush();
                    bFlag = true;
                } catch (IOException ioex) {


                } finally {
                    fos.close();
                    buffer = null;
                }
            } catch (Exception ex) {


            } catch (OutOfMemoryError o) {


            }
            return bFlag;
        }
        return false;
    }


    /**
     * 读取文件
     *
     * @param sFileName
     * @return
     */
    public static String readFile(String sFileName) {
        if (TextUtils.isEmpty(sFileName)) {
            return null;
        }
        final StringBuffer sDest = new StringBuffer();
        final File f = new File(sFileName);
        if (!f.exists()) {
            return null;
        }
        try {
            FileInputStream is = new FileInputStream(f);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            try {
                String data = null;
                while ((data = br.readLine()) != null) {
                    sDest.append(data);
                }
            } catch (IOException ioex) {

            } finally {
                is.close();
                is = null;
                br.close();
                br = null;
            }
        } catch (Exception ex) {

        } catch (OutOfMemoryError ex) {

        }
        return sDest.toString().trim();
    }
}
