package com.example.administrator.study.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.study.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/21 0021.
 */

public class GridviewImageAdapter extends BaseAdapter{
    private List<Drawable> drawables;
    private Context mContext;
    public GridviewImageAdapter(List<Drawable> drawables,Context context){
        this.drawables=drawables;
        mContext=context;
    }
    public int getCount(){
        return drawables.size();
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
            convertView=(ImageView)LayoutInflater.from(mContext).inflate(R.layout.gridview_item,null,false).findViewById(R.id.image);
            ViewGroup.LayoutParams params=convertView.getLayoutParams();
            params.width=ScreenTools.instance(mContext).getScreenWidth()/3;
            convertView.setLayoutParams(params);
            convertView.setBackground(drawables.get(position));
        }
        return convertView;
    }
}
