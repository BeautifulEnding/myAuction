package com.example.administrator.study.util;

/**
 * Created by Administrator on 2016/9/8 0008.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.Toast;

public class DialogUtil
{
    // 定义一个显示消息的对话框
    public static void showDialog(final Context ctx
            , String msg , boolean goHome)
    {
        // 创建一个AlertDialog.Builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx)
                .setMessage(msg).setCancelable(false);
        if(goHome)
        {
            builder.setPositiveButton("确定", new OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Context context=null;
                    Toast.makeText(context,"it is goHome ",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            builder.setPositiveButton("确定", null);
        }
        builder.create().show();
    }
    // 定义一个显示指定组件的对话框
    public static void showDialog(Context ctx , View view)
    {
        new AlertDialog.Builder(ctx)
                .setView(view).setCancelable(false)
                .setPositiveButton("确定", null)
                .create()
                .show();
    }
}


