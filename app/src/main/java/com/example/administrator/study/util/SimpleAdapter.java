package com.example.administrator.study.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.study.R;


import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2016/12/20 0020.
 */

public class SimpleAdapter extends BaseAdapter{
    private  List<List<Drawable>> bitmaps;
    private List<String> allAuctions;
    private Context mContext;
    private ImageView head_image;
    private TextView user_name_View;
    private GridView image;
    private TextView title_view;
    private String user_name;
    private String title;
    private android.widget.SimpleAdapter adapter=null;
    public SimpleAdapter(List<List<Drawable>> images, List<String> allAuctions, Context context){
        Log.e("images",images.size()+"");
        Log.e("allAuctions",allAuctions.size()+"");
        bitmaps=images;
        mContext=context;
        this.allAuctions=allAuctions;
        /*try{
            this.title= URLDecoder.decode(title,"utf-8");
        }catch (Exception e){

        }*/
    }
    public int getCount(){
        return allAuctions.size();
    }
    @Override
    public String getItem(int position){
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.refresh_item,null,false);
            head_image=(ImageView)convertView.findViewById(R.id.head_image);
            user_name_View=(TextView)convertView.findViewById(R.id.user_name);
            image=(GridView) convertView.findViewById(R.id.image);
            title_view=(TextView)convertView.findViewById(R.id.title);
        }
        head_image.setBackground(mContext.getResources().getDrawable(R.drawable.head));
        try{
            JSONObject object=new JSONObject(allAuctions.get(position));
            user_name=object.getString("user_name");
            Log.e("user_name",user_name);
            title=object.getString("title");
            Log.e("title",title);
        }catch (Exception e){
            Log.e("jsonException",e.getMessage());
        }
        user_name_View.setText(user_name);
        title_view.setText(title);
        GridviewImageAdapter adapter=new GridviewImageAdapter(bitmaps.get(position),mContext);
        image.setAdapter(adapter);
        return convertView;
    }
}
