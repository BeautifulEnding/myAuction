package com.example.administrator.study.updateactivity;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;

import com.example.administrator.study.util.SavePhoto;

import java.util.List;

/**
 * Created by Administrator on 2017/1/25 0025.
 */

public class SaveUtil {

    public static void saveToDatabase(List<String> tempAuctions, List<List<Drawable>> images){
        SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(SavePhoto.SD_PATH+"/auction.db3",null);
        new Thread(){
            @Override
            public void run(){
//                将数据保存在数据库中

            }
        }.start();
    }
}
