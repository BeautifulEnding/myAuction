package com.example.administrator.study.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Map;
//上传大图片出现问题，改用上传文件试试，？直接用HttpUrlConnection
/**
 * Created by Administrator on 2016/12/18 0018.
 */

public class FileUpload {
    public static void uploadAuction(ArrayList<String> paths, Map<String,String> map,int photoNum){
        //                上传图片
        map.put("photoNum",String.valueOf(photoNum));
        for (int i=0;i<paths.size();i++){
            try {
                Bitmap bitmap=BitmapFactory.decodeFile(paths.get(i));
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,20,stream);
                stream.close();
                byte[] buffer=stream.toByteArray();
                Log.e("bufferLength",buffer.length+"");

                map.put("image"+(i+1),Base64.encodeToString(buffer,0,buffer.length,Base64.DEFAULT));
//                Log.e("stream",stream.toByteArray().toString());
//                Log.e("streamToString",stream.toString());
            }catch (Exception e){
                Log.e("exception",e.getMessage());
            }

        }
        String url=HttpUtil.BASE_URL+"sendAuction";
        try {
            for (String set:map.keySet()){
                Log.e("map",map.get(set));
            }
            HttpUtil.postRequest(url,map);
        }catch (Exception e){
            Log.e("sendAuctionException",e.getMessage());
        }
    }
}
