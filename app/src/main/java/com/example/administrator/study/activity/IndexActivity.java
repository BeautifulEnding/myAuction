package com.example.administrator.study.activity;

import android.app.Activity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.administrator.study.R;
import com.example.administrator.study.util.MyApplication;

/**
 * Created by Administrator on 2016/12/1 0001.
 */

public class IndexActivity extends AppCompatActivity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_index);
//        设置动画
        AlphaAnimation animation=new AlphaAnimation(0.0f,1.0f);
        animation.setDuration(1000);
//        为图片设置动画
        LinearLayout view=(LinearLayout)findViewById(R.id.index);
        view.setAnimation(animation);
//        设置监听
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                //获取只能被本程序读写的sharedPreferences对象
                SharedPreferences preferences=getSharedPreferences("login",MODE_PRIVATE);
                if (preferences.contains("user_name")){
                    Intent intent=new Intent(IndexActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent=new Intent(IndexActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                MyApplication.getInstance().removeActivity(IndexActivity.this);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
