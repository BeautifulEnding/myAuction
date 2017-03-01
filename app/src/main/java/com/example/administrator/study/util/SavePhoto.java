package com.example.administrator.study.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/10/11 0011.
 */
public class SavePhoto {
    public static final String SD_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/auction";
    //定义图片
    public SavePhoto(Bitmap photo, String path)throws IOException {

        File preFile=new File(SD_PATH);
        if (!preFile.exists()) {
            //如果文件不存在，创建该文件
            preFile.mkdir();
        }
        File file=new File(SD_PATH+path);
        /*if (!file.exists()) {
            //如果文件不存在，创建该文件
            file.mkdir();
        }*/
        //得到图片的输出流
        BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(file));
        //将图片按原质量压缩到指定文件夹
        photo.compress(Bitmap.CompressFormat.PNG,100,bos);
        bos.flush();
        //关闭输出流
        bos.close();
        Log.e("保存圖片","保存圖片成功");
    }
}
