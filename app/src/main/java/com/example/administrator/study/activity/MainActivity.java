package com.example.administrator.study.activity;
//重新更改，将ViewPager中的内容换成两页
//中间就是一个普通的点击按钮
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.study.util.CustomSideMenu;
import com.example.administrator.study.util.CustomViewPager;
import com.example.administrator.study.R;
import com.example.administrator.study.util.MyApplication;
import com.example.administrator.study.util.SavePhoto;
import com.example.administrator.study.util.ScreenTools;
import com.example.administrator.study.fragment.AuctionFragment;
import com.example.administrator.study.fragment.MessageFragment;
import com.example.administrator.study.fragment.SendFragment;
import com.example.administrator.study.util.MyFragmentPagerAdapter;
import com.lidong.photopicker.PhotoPickerActivity;
import com.lidong.photopicker.PhotoPreviewActivity;
import com.lidong.photopicker.SelectModel;
import com.lidong.photopicker.intent.PhotoPickerIntent;
import com.lidong.photopicker.intent.PhotoPreviewIntent;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    private View mShadow;
    private CustomSideMenu mCustomSideMenu;
    private CustomViewPager mCustomViewPager;
    private int mWidthOfSideMenu;
    private ScreenTools mScreenTools;
    private ImageButton mBtnSideMenu;
    private Uri fileUri;
    private int GET_CAMERA=12;

    //首页
    private static final int AUCTION = 0;

    //消息
    private static final int MESSAGE= 1;
    //当前在哪个页面
    private int current = 0;

    private View setting;

    private View message;

    private View auction;

    private TextView settingText;

    private ImageView settingImg;

    private TextView messageText;

    private ImageView messageImg;

    private TextView auctionText;

    private ImageView auctionImg;

    private ArrayList<Fragment> fragmentList;

    private Handler mHandler;

//    侧滑菜单
    private RelativeLayout slideMainLayout;
    //定义保存个人信息的SharePreferences
    SharedPreferences user_message=null;
    //定义个人信息
    RelativeLayout personMessage=null;
    //定义Fragment中的ListView
    ListView list;
    //设置
    LinearLayout settingLayout=null;
    //定义启动SettingActivity的Action
    String SETTING_ACTION="this.is.personnal.activity";
    private String[] names;
    private int[] iamgesId=new int[]{ R.drawable.send, R.drawable.auction, R.drawable.auction_prepare};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        MyApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_main);
        initView();
        initViewPager();
        initEvent();
    }
    public void initView(){
        mShadow = (View) findViewById(R.id.shadow);
        mCustomSideMenu = (CustomSideMenu) findViewById(R.id.side_menu_layout);
        mCustomViewPager = (CustomViewPager) findViewById(R.id.viewpager);
        mBtnSideMenu = (ImageButton) findViewById(R.id.btn_side_menu);

        mShadow.getBackground().setAlpha(0);
        mShadow.bringToFront();
        mCustomSideMenu.bringToFront();

        mScreenTools = ScreenTools.instance(getApplicationContext());
        mWidthOfSideMenu = (int) (mScreenTools.getScreenWidth() * CustomSideMenu.SIDEMENUSCAL);

        //获取设置页面的菜单
        setting = findViewById(R.id.setting);
        settingText = (TextView)setting.findViewById(R.id.settingText);
        settingImg = (ImageView)setting.findViewById(R.id.settingImg);
        //获取消息页面的菜单
        message = findViewById(R.id.message);
        messageText = (TextView)message.findViewById(R.id.messageText);
        messageImg = (ImageView)message.findViewById(R.id.messageImg);
        //获取主页菜单
        auction = findViewById(R.id.auction);
        auctionText = (TextView)auction.findViewById(R.id.auctionText);
        auctionImg = (ImageView)auction.findViewById(R.id.auctionImg);

//        侧滑菜单中的布局
        slideMainLayout =(RelativeLayout)findViewById(R.id.sideMain_layout);
        names=getResources().getStringArray(R.array.slide_list);
        personMessage=(RelativeLayout)slideMainLayout.findViewById(R.id.personmessage);
        ImageView user=(ImageView)personMessage.findViewById(R.id.portraits);
        TextView user_name=(TextView)personMessage.findViewById(R.id.network_name);
        TextView user_id=(TextView)personMessage.findViewById(R.id.id);
        //将sharepreferences中的头像，用户名，用户账号提取出来
        user_message=this.getSharedPreferences("login", MODE_PRIVATE);
        user_name.setText(user_message.getString("user_name",null));
        user_id.setText("账号:"+"暂无账号的定义");
        //获得头像
        String photoStream=user_message.getString("user_photo",null);
        if (!TextUtils.equals(photoStream,"")){
            ByteArrayInputStream user_photo = new ByteArrayInputStream(Base64.decode(photoStream.getBytes(), Base64.DEFAULT));
            user.setImageDrawable(Drawable.createFromStream(user_photo,"user_photo"));
        }
        //得到图片
        //创建一个List集合，List集合的元素是Map
        List<Map<String,Object>> listItems=new ArrayList<>();
        for (int i=0;i<names.length;i++){
            Map<String,Object> listItem=new HashMap<String,Object>();
            listItem.put("header",iamgesId[i]);
            listItem.put("settingName",names[i]);
            listItems.add(listItem);
        }
        //创建一个SimpleAdapter
        SimpleAdapter adapter=new SimpleAdapter(this,listItems,R.layout.setting_list_item,
                new String[]{"header","settingName"},new int[]{R.id.header,R.id.name});
        list=(ListView)slideMainLayout.findViewById(R.id.my_list);
        //为ListView设置Adapter
        list.setAdapter(adapter);
        settingLayout=(LinearLayout)slideMainLayout.findViewById(R.id.setting);

    }
    public void initViewPager(){
        fragmentList = new ArrayList<Fragment>();
        AuctionFragment aFragment=new AuctionFragment();
        MessageFragment mFragment=new MessageFragment();
//        SendFragment sFragment=new SendFragment();
        fragmentList.add(aFragment);
        fragmentList.add(mFragment);
//        fragmentList.add(sFragment);
        mCustomViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(),fragmentList));
        mCustomViewPager.setCurrentItem(0);

    }

    public void initEvent(){

        mCustomSideMenu.setSideMenuListen(new CustomSideMenu.OnSideMenuListen() {

            @Override
            public void setShadowAlpha(int alpha) {
                // TODO Auto-generated method stub
//                设置阴影
                mShadow.getBackground().setAlpha(alpha);
            }
        });

        mCustomViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                current=position;
                switchState();
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mCustomViewPager.setViewPagerListener(new CustomViewPager.ViewPagerListener() {

            @Override
            public void setScroll(int s, boolean isActionUp) {
                // TODO Auto-generated method stub
                if(isActionUp) {
                    if(mCustomSideMenu.getScrollX() < -mWidthOfSideMenu/2)
                        mCustomSideMenu.doScroll(mCustomSideMenu.getScrollX(), 0, -mWidthOfSideMenu-mCustomSideMenu.getScrollX(), 0);
                    else
                        mCustomSideMenu.doScroll(mCustomSideMenu.getScrollX(), 0, -mCustomSideMenu.getScrollX(), 0);
                }
                else {
                    if(mCustomSideMenu.getScrollX()+s < -mWidthOfSideMenu)
                        mCustomSideMenu.scrollTo((int) (-mWidthOfSideMenu), 0);
                    else
                        mCustomSideMenu.scrollTo((int) (mCustomSideMenu.getScrollX()+s), 0);
                        mShadow.getBackground().setAlpha(mCustomSideMenu.getShadowAlpha());
                }

            }
        });

        mBtnSideMenu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mShadow.setVisibility(View.VISIBLE);
                mCustomSideMenu.sideMenuScroll(true);
            }
        });

        setting.setOnClickListener(this);
        auction.setOnClickListener(this);
        message.setOnClickListener(this);

        /*mHandler=new Handler() {

            @Override
            public void handleMessage(Message msg) {
                    if (msg.what==0x123){
                        mCustomViewPager.setVisibility(View.VISIBLE);
                        switchState();
                    }
            }

            };*/

        //为个人信息添加点击事件
        personMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),"成功点击个人信息",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.setAction(SETTING_ACTION);
                startActivity(intent);
            }
        });
        //对ListView的Item添加点击事件
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    //已发布
                    case 0:
                        //Toast.makeText(getActivity(),"这是设置Item",Toast.LENGTH_SHORT).show();
                        //Intent intent=new Intent(getActivity(), SettingActivity.class);
                        break;
                    //已拍下
                    case 1:
                        break;
                    //待拍下
                    case 2:
                        break;

                }
                // Toast.makeText(getActivity(),names[position],Toast.LENGTH_SHORT).show();
            }
        });
        //处理设置
        settingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });



    }

    private void switchState() {
        switchHomeState();
//        switchMessageState();
        switchSettingState();
    }
    /**
     * 切换消息的状态
     */
    private void switchSettingState() {
        if( current == MESSAGE) {
            settingText.setTextColor(Color.BLUE);
            settingImg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.message_selected));
        } else {
            settingText.setTextColor(Color.DKGRAY);
            settingImg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.message_nomal));
        }
    }

   /* *//**
     * 转换消息菜单的状态
     *//*
    private void switchMessageState() {
        if( current == MESSAGE) {
            messageText.setTextColor(Color.BLUE);
            messageImg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.message_selected));
        } else {
            messageText.setTextColor(Color.DKGRAY);
            messageImg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.message_nomal));
        }
    }*/

    /**
     * 转换拍卖菜单的状态
     */
    private void switchHomeState() {
        if( current == AUCTION) {
            auctionText.setTextColor(Color.BLUE);
            auctionImg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.auction_selected));
        } else {
            auctionText.setTextColor(Color.DKGRAY);
            auctionImg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.auction_nomal));
        }
    }

    public void onClick(View v) {
        int id = v.getId();
        switch( id ) {
            case R.id.setting :
                //设置消息的颜色
                current = MESSAGE;
                break;
            case R.id.auction:
                current = AUCTION;
                break;
            case R.id.message:
//        创建popupWindow对象
                initPopupWindow();
                break;
            default:
                if (v.getX()>mWidthOfSideMenu && v.getY()< mScreenTools.getScreenHeight()- 65 && mShadow.getVisibility() == View.VISIBLE){
                    mShadow.setVisibility(View.INVISIBLE);
                    mCustomSideMenu.sideMenuScroll(false);
                }
                break;
        }
        /*mCustomViewPager.setVisibility(View.GONE);
        mCustomViewPager.setCurrentItem(current,false);
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                  mHandler.sendEmptyMessage(0x123);
            }
        },200);*/
        mCustomViewPager.setCurrentItem(current,false);
    }

    /**
     * 创建PopupWindow
     */
    protected void initPopupWindow() {
        View root=getLayoutInflater().inflate(R.layout.popup,null);
        final PopupWindow popupWindow=new PopupWindow(root,ScreenTools.instance(MainActivity.this).getScreenWidth(),
                WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
//        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg,null));
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(message, Gravity.BOTTOM,0,0);
//        Animation alphaAnimation=AnimationUtils.loadAnimation(MainActivity.this,R.anim.alpha);
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
        popupWindow.update();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5F; //0.0-1.0
        getWindow().setAttributes(lp);
//        获取popupWindow中的text
        ImageView text=(ImageView)root.findViewById(R.id.text);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, AuctionActivity.class);
                startActivity(intent);
                popupWindow.dismiss();
            }
        });
        ImageView cancel=(ImageView) root.findViewById(R.id.cancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0F; //0.0-1.0
                getWindow().setAttributes(lp);*/
//                        消失
                popupWindow.dismiss();
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0F; //0.0-1.0
                getWindow().setAttributes(lp);
            }
        });

        ImageView camera=(ImageView) root.findViewById(R.id.camera);
        camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                调用系统拍照功能
                  takePhoto();
                popupWindow.dismiss();
            }
        });
    }

    private void takePhoto(){
        //系统常量， 启动相机的关键
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //设置图片的保存名
        String f = System.currentTimeMillis() + ".jpg";
        String path = SavePhoto.SD_PATH + "/images";
        //设置图片的保存路径
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        fileUri = Uri.fromFile(new File(path + "/" + f));
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); //指定图片存放位置，指定后，在onActivityResult里得到的Data将为null
        startActivityForResult(openCameraIntent, GET_CAMERA);
        // 参数常量为自定义的request code, 在取返回结果时有用
    }
    //    判断是否真的要退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            如果在main页面按了返回键，则判断是否需要退出程序
            showTips();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void showTips() {

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("退出程序").setMessage("是否退出程序")
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).create(); // 创建对话框
        alertDialog.show(); // 显示对话框
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK && requestCode == GET_CAMERA){
            Intent intent =new Intent(MainActivity.this,AuctionActivity.class);
            intent.putExtra("imagePath",fileUri.toString());
            startActivity(intent);
        }
    }

}
