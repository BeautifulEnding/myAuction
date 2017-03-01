package com.example.administrator.study.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.study.R;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomSideMenu extends RelativeLayout{

    public final static double SIDEMENUSCAL = 0.75;
    private Scroller mScroller;
    private int mWidthOfSideMenu;
    private int mEdge;
    private Point mInitPoint = new Point();
    private float iniEventX;
    private float iniEvnetY;
    private OnSideMenuListen mOnSideMenuListen;

    public CustomSideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onFinishInflate() {
        mScroller = new Scroller(getContext());
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        View child = (View) getChildAt(0);
        LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        ScreenTools screenTools = ScreenTools.instance(getContext());
//        侧滑菜单的宽度占总屏幕的3/4
        layoutParams.width = mWidthOfSideMenu = (int) (screenTools.getScreenWidth() * SIDEMENUSCAL);
        layoutParams.height = screenTools.getScreenHeight();
        child.setLayoutParams(layoutParams);
//        将dip转化为px
        mEdge = screenTools.dip2px(15);//mEdge=30;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        View child = (View) getChildAt(0);
//        先将侧滑菜单放在屏幕的外面
        child.layout(-child.getMeasuredWidth(), 0, 0, child.getMeasuredHeight());
//        initPoint记录侧滑菜单的左上方的那个点
        mInitPoint.x = child.getLeft();
        mInitPoint.y = child.getTop();
    }

    @Override
    public void computeScroll() {
        // TODO Auto-generated method stub
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(), 0);
//            System.out.println(mScroller.getCurrX());
            mOnSideMenuListen.setShadowAlpha(getShadowAlpha());
            invalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
//        return false.父类默认返回false，不拦截触摸事件
          return super.onInterceptTouchEvent(ev);
//          return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
//        mEdge定义边距，当触摸事件的起点小于mEdge，说明触摸事件进入了侧滑菜单的范围，
//        则侧滑菜单开始滚动
//        getScrollX得到滚动的View在屏幕左边距上的点的X，
//        在这View的最左边的X为-576，所以当侧滑菜单全部显示出来了之后，getScrollX=-576，
        if(event.getX() < mEdge || getScrollX() < 0 ) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    iniEventX = event.getX();
                    iniEvnetY = event.getY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float tmp = event.getX() - iniEventX;
                    iniEventX = event.getX();
                    if(getScrollX()-tmp < -mWidthOfSideMenu)
//                        说明侧滑菜单已全部显示
                        scrollTo((int) (-mWidthOfSideMenu), 0);
                    else
//                    继续滚动到getScrollX()-tmp位置
                        scrollTo((int) (getScrollX()-tmp), 0);
                    mOnSideMenuListen.setShadowAlpha(getShadowAlpha());
                    break;
                case MotionEvent.ACTION_UP:
                    int endX = (int) event.getX();
                    int endY = (int) event.getY();
                    if(endX == (int)iniEventX && endY == (int)iniEvnetY && iniEventX > mWidthOfSideMenu) {
                        sideMenuScroll(false);
                        break;
                    }
                    else {
                        if(getScrollX() < (-mWidthOfSideMenu/2))
//                            超过1/2，全部显示侧滑菜单
                            mScroller.startScroll(getScrollX(), 0, (int) (-mWidthOfSideMenu-getScrollX()), 0, 500);
                        else
//                        没超过1/2，则侧滑菜单滚回去，不显示
                            mScroller.startScroll(getScrollX(), 0, (int) (0-getScrollX()), 0, 500);
                        break;
                    }
                default:
                    break;
            }
            invalidate();
            return true;
        }
//        false,触摸事件传递给下一个组件
        return false;
    }

    public void sideMenuScroll(boolean isLeftToRight) {
        if(isLeftToRight) {
            mScroller.startScroll(0, 0, 0-mWidthOfSideMenu, 0, 1000);
//            scrollTo(-mWidthOfSideMenu,0);没有滚动效果
        }
        else
            mScroller.startScroll(0-mWidthOfSideMenu, 0, mWidthOfSideMenu, 0, 1000);
        invalidate();
    }

    public void doScroll(int sx, int sy, int dx, int dy) {
        mScroller.startScroll(sx, sy, dx, dy, 500);
        invalidate();
    }

    public int getShadowAlpha() {
//        得到正在滚动的内容的最左边的x坐标
        int currentScroll = getScrollX();
        if(currentScroll > 0)
            currentScroll = 0;
        if(-currentScroll > mWidthOfSideMenu)
            currentScroll = -mWidthOfSideMenu;
        int alpha = (int) (-currentScroll*1.0/mWidthOfSideMenu * 100);
        return alpha;
    }

    public void setSideMenuListen(OnSideMenuListen onSideMenuListen){
        mOnSideMenuListen = onSideMenuListen;
    }

    public interface OnSideMenuListen{
        public void setShadowAlpha(int alpha);
    }


}
