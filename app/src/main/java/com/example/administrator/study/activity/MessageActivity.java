package com.example.administrator.study.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.administrator.study.R;
import com.example.administrator.study.util.MyApplication;

import java.util.Timer;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public class MessageActivity extends Activity implements View.OnClickListener{
    //定义输入框
    EditText editText;
    //定义输入框里的内容
    String name=null;
    //定义一个Tag标识输入的内容
    //定义与SettingActivity交互的Intent
    //定义返回图片
    ImageView name_reverse=null;
    //定义保存按钮
    Button save=null;
    //得到顶部文本框
    TextView text=null;

//    定义传回的resultCode
    public static final int RESULT_OK=-1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.change_name);
        //得到返回图像
        name_reverse = (ImageView) findViewById(R.id.name_reverse);
        //增加监听
        name_reverse.setOnClickListener(MessageActivity.this);
        //得到保存按钮
        save = (Button) findViewById(R.id.save);
        //增加监听
        save.setOnClickListener(MessageActivity.this);
        //得到顶部文本框
        text = (TextView) findViewById(R.id.head_message);
        //得到输入框
        editText = (EditText) findViewById(R.id.name);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        //设置输入框输入的字符串长度最长为20
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(editText.getText())){
                    save.setBackgroundResource(R.drawable.button_enable);
                    save.setEnabled(true);
                    save.setTextColor(getResources().getColor(R.color.bg_color));
                }
                else{
                    save.setBackgroundResource(R.drawable.button_unenable);
                    save.setEnabled(false);
                    save.setTextColor(getResources().getColor(R.color.divider));
                }
            }
        });
    }
    @Override
    public void onClick(View v){
        if (v.getId()==R.id.save){
            name=editText.getText().toString();
            Intent intent=new Intent(MessageActivity.this,AuctionActivity.class);
            intent.putExtra("message",name);
//            返回结果到AuctionActivity
            setResult(RESULT_OK,intent);
        }
        MyApplication.getInstance().removeActivity(MessageActivity.this);
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        finish();
    }
}
