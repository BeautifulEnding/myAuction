package com.example.administrator.study.util;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/13 0013.
 */
//管理应用程序中的所有的Activity
public class MyApplication extends Application {
//    定义一个链表放入所有的Activity
    private List<Activity> activityList=new LinkedList<Activity>();
    private static MyApplication instance;
    private MyApplication(){

    }
    public synchronized static MyApplication getInstance() {
        if (null == instance) {
            instance = new MyApplication();
        }
        return instance;
    }

    // add Activity
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }
    //remove Activity
    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }
    public void exit() {
        try {
            for (Activity activity : activityList) {
                if (activity != null)
                    Log.e("activity",activity.toString()+"正在被销毁");
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
