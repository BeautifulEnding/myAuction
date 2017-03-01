package com.example.administrator.study.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager{

    private float mInitX;
    private float mInitY;
    private boolean mFirstTime;
    private boolean mSideMenuShow;
    private ViewPagerListener mViewPagerListener;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("viewPager:","onTouchEvent");
        // TODO Auto-generated method stub
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitX = event.getX();
                mInitY = event.getY();
                mFirstTime = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if(mFirstTime) {
                    mInitX = event.getX();
                    mInitY = event.getY();
                    mFirstTime = false;
                }
                float tmpX = event.getX();
                float tmpY = event.getY();
//右移或者侧滑菜单出现
                if((tmpX - mInitX) > 0 || mSideMenuShow){
//                    如果当前页为第一页并且水平移动
                    if(Math.abs(tmpX - mInitX) > Math.abs(tmpY - mInitY) && getCurrentItem() == 0){
                        mSideMenuShow = true;
                        mViewPagerListener.setScroll((int) (mInitX - tmpX), false);
                        mInitX = tmpX;
                        mInitY = tmpY;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mFirstTime = true;
                if(mSideMenuShow){
                    mSideMenuShow = false;
                    mViewPagerListener.setScroll(0, true);
                }
            default:
                break;
        }
//        如果侧滑菜单不存在，则CustomViewPager将触摸事件交给系统定义的ViewPager处理
//        也就是能够交换ViewPager中内容
//        否则不可以交换ViewPager中内容
        if(!mSideMenuShow) {
            return super.onTouchEvent(event);
        }
        return true;
    }

    public void setViewPagerListener(ViewPagerListener viewPagerListener) {
        this.mViewPagerListener = viewPagerListener;
    }

    public interface ViewPagerListener {
        //        触摸事件是否停止
        public void setScroll(int s, boolean isAction_UP);
    }
}
