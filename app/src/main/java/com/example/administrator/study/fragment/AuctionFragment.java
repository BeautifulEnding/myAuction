package com.example.administrator.study.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.study.R;
import com.example.administrator.study.util.DownloadUtil;
import com.example.administrator.study.util.HttpUtil;
import com.example.administrator.study.util.RefreshableView;
import com.example.administrator.study.util.SimpleAdapter;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/1 0001.
 */

public class AuctionFragment extends Fragment{
    private SharedPreferences aucPre;
    private SharedPreferences.Editor aucEditor;
    RefreshableView refreshableView=null;
    ListView listView;
//    List<Drawable> images=new ArrayList<>();
    List<List<Drawable>> images=new ArrayList<>();
    List<String> allAuctions=new ArrayList<>();
    List<String> tempAuctions=new ArrayList<>();
    String title=null;
    String user_name=null;
    Drawable headImage=null;
    SimpleAdapter simpleAdapter=null;
    private Handler handler=null;
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.auctionfragment_layout, container, false);
        refreshableView = (RefreshableView) rootView.findViewById(R.id.refreshable_view);
        headImage=getResources().getDrawable(R.drawable.head);
        listView = (ListView) rootView.findViewById(R.id.list_view);
        aucPre=getContext().getSharedPreferences("auction", Context.MODE_PRIVATE);
        aucEditor=aucPre.edit();
//        衹有listView中有内容的時候headerView才會出現
        if (allAuctions.size()==0){
//            如果没有人发布拍品
            TextView textView=new TextView(getContext());
            textView.setText("现在还没有人发布拍品，赶紧去发布吧！");
            textView.setTextSize(30);
            listView.addHeaderView(textView);
        }else{
            images和allAuctions要在这之前进行赋值，现在还没有进行赋值
            simpleAdapter=new SimpleAdapter(images,allAuctions,getContext());
            listView.setAdapter(simpleAdapter);
        }
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                if (msg.what==0){
                    /*simpleAdapter=new SimpleAdapter(images,allAuctions,getContext());
                    listView.setAdapter(simpleAdapter);*/
//                    更新适配器数据
                    simpleAdapter.notifyDataSetChanged();
                }
            }
        };
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                /*try{
                    Thread.sleep(3000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }*/
//                不能直接在AyncTask綫程中對View進行更改，
//                 Caused by: android.view.ViewRootImpl$CalledFromWrongThreadException:
// Only the original thread that created a view hierarchy can touch its views.
//                所以應該在該綫程中完成從服務器得到相應的數據，最後將數據保存，
// 最後在正確的綫程中對listView進行修改
                /*TextView view=(TextView)listView.getChildAt(0);
                view.setText("哈哈，刷新成功");*/
//                JSONObject object=receiveAuction();
//                allAuctions=receiveAuction();
                tempAuctions=receiveAuction();
                allAuctions.addAll(tempAuctions);
                try{
                    for(int i=0;i<tempAuctions.size();i++) {
                        JSONObject object=new JSONObject(tempAuctions.get(i));
                        streamToBitmap(object);
                    }
                    handler.sendEmptyMessage(0);
                }catch (Exception e){
                    Log.e("jsonObjectException",e.getMessage());
                }
                refreshableView.finishRefreshing();
            }
        }, 0);
        refreshableView.setOnShowOtherContentListener(new RefreshableView.ShowOtherContentListener() {
            @Override
            public void onShowOtherContent() {
                unComplished
                Log.e("ContentListener","success");
            }
        });
        return rootView;
    }

    private void streamToBitmap(JSONObject jsonObj) throws Exception{
        String name=null;
        List<Drawable> list=new ArrayList<>();
            for (int i=0;i<9;i++) {
                name="image"+i;
                if (!jsonObj.has(name)){
                    break;
                }
                String photoStream = jsonObj.getString(name);
                ByteArrayInputStream user_photo = new ByteArrayInputStream(Base64.decode(photoStream.getBytes(), Base64.DEFAULT));

                Drawable drawable = Drawable.createFromStream(user_photo, name);
                list.add(drawable);
            }
        images.add(list);
        把拍卖数据存到Android本地，要不要用另外的线程
    }
    private List<String> receiveAuction(){
        String url=HttpUtil.BASE_URL+"receiveAuction";
        try {
            return DownloadUtil.downloadAuction(url);
        }catch (Exception e){
            Log.e("ReceiveAuctionException",e.getMessage());
        }
        return null;
    }
}
