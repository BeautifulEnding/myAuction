package com.example.administrator.study.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.study.R;
import com.example.administrator.study.util.DialogUtil;
import com.example.administrator.study.util.HttpUtil;
import com.example.administrator.study.util.MyApplication;
import com.example.administrator.study.util.SavePhoto;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText idView;
    private EditText passView;
    private Button loginButton;
    private View divider1;
    private View divider2;

    private String user_name;
    private String user_pass;
    private JSONObject jsonObj;
    private boolean exception=false;

    private SharedPreferences loginPre;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        if (!isNetWorkConnected()) {
            android.app.AlertDialog.Builder builder= new android.app.AlertDialog.Builder(this);
            builder.setTitle("设置网络");
            builder.setMessage("当前网络不可用，请检查网络设置");
            builder.setPositiveButton("设置网络",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                   打开系统设置网络页面
//                    android.provider包提供打开系统页面的常量
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
            builder.setNegativeButton("取消",null);
            builder.create().show();
        }
        initView();
        initEvent();
    }

    public void initView(){
        idView = (EditText) findViewById(R.id.user_id);
        passView = (EditText) findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.login);
        divider1 = (View) findViewById(R.id.divider1);
        divider2 = (View) findViewById(R.id.divider2);
    }

    public void initEvent(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetWorkConnected()){
                    if (validate()){
                        if (loginPro()){
//                            登录成功
                            loginPre = getSharedPreferences("login",MODE_PRIVATE);
                            editor = loginPre.edit();
                            try{
                                editor.putString("user_name",user_name);
                                editor.putString("user_pass",user_pass);
                                editor.putString("user_sex",jsonObj.getString("user_sex"));
                                editor.putString("user_age",jsonObj.getString("user_age"));
//                               处理图片
                                String photoStream=jsonObj.getString("user_photo");
                                editor.putString("user_photo",photoStream);
                                editor.commit();
                                ByteArrayInputStream user_photo = new ByteArrayInputStream(Base64.decode(photoStream.getBytes(), Base64.DEFAULT));
                                // Log.e("photo",user_message.getString("user_photo",null);
                                //得到图片
                                Bitmap bitmap= BitmapFactory.decodeStream(user_photo);
                                String photo_name = System.currentTimeMillis() + ".png";
                                new SavePhoto(bitmap,"/"+photo_name);
                            }catch (Exception e){
                                System.out.println(e.getMessage());
                            }

                            // 启动MainActivity
                            Intent intent = new Intent(LoginActivity.this
                                    , MainActivity.class);
                            startActivity(intent);
                            // 结束该Activity
                            MyApplication.getInstance().removeActivity(LoginActivity.this);

                            finish();
                        }else{
                            if (!exception){
                                DialogUtil.showDialog(LoginActivity.this,"用户名或密码错误，请重新输入",false);
                            }
                        }
                    }
                }else{
                    Toast.makeText(LoginActivity.this,"当前网络不可用，请检查网络设置",Toast.LENGTH_SHORT).show();
                }

            }
        });

        idView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    divider1.setBackgroundColor(getResources().getColor(R.color.button_bg));
                } else {
                    divider1.setBackgroundColor(getResources().getColor(R.color.divider));
                }
            }
        });
        passView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    divider2.setBackgroundColor(getResources().getColor(R.color.button_bg));
                } else {
                    divider2.setBackgroundColor(getResources().getColor(R.color.divider));
                }
            }
        });
    }

    public boolean validate(){
        user_name= idView.getText().toString().trim();
        if (user_name.equals(""))
        {
            DialogUtil.showDialog(this, "用户账户是必填项！", false);
            return false;
        }
        user_pass = passView.getText().toString().trim();
        if (user_pass.equals(""))
        {
            DialogUtil.showDialog(this, "请输入密码！", false);
            return false;
        }
        return true;

    }

    //    判断网络状态
    private boolean isNetWorkConnected(){
        ConnectivityManager manager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();
//        返回联网状态，如果有联网信息并且连了网，则返回true
        return (networkInfo!=null&&networkInfo.isConnected());
    }

    private boolean loginPro()
    {
        try
        {
             jsonObj= query(user_name, user_pass);
            if (!jsonObj.has("userExist")){
                return true;
            }
        }
        catch (Exception e)
        {
            DialogUtil.showDialog(this
                    , "服务器响应异常，请稍后再试！", false);
            e.printStackTrace();
            exception=true;
        }

        return false;
    }
    // 定义发送请求的方法
    private JSONObject query(String username, String password)
            throws Exception
    {
        // 使用Map封装请求参数
        Map<String, String> map = new HashMap<>();
        map.put("user_name", username);
        map.put("user_pass", password);
        // 定义发送请求的URL
        String url = HttpUtil.BASE_URL + "login";
        // 发送请求
        return new JSONObject(HttpUtil.postRequest(url, map));
    }

}
