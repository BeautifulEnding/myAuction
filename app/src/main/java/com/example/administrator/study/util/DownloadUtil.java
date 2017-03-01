package com.example.administrator.study.util;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by Administrator on 2017/1/22 0022.
 */

public class DownloadUtil {

    public static List<String> downloadAuction(final String url) throws Exception {
        FutureTask<List<String>> task = new FutureTask<List<String>>(
                new Callable<List<String>>() {
                    @Override
                    public List<String> call() throws Exception {
                        URL realUrl=new URL(url);
//		打开和URL之间的连接
                        HttpURLConnection connection=(HttpURLConnection)realUrl.openConnection();
//		设置通用的请求属性
//		允许输入输出
                        connection.setDoOutput(true);
                        connection.setDoInput(true);
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(10000);
                       /* connection.setRequestProperty("Accept-language","zh-CN");
                        connection.setRequestProperty("Charset","utf-8");
                        connection.setRequestProperty("Connection","Keep-Alive");*/
//		http://localhost:8080/notepad/notepad/index.jsp?filename=123
//		建立实际连接
                        connection.connect();
                        Log.e("contentLength",connection.getContentLength()+"");
//		        得到服务器的回复（响应）
                        BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String lString="";
                        int i=1;
                        List<String> result=new ArrayList<>();
                        while ((lString=reader.readLine())!=null) {
                            result.add(lString);
                        }
                            return result;
                    }
                });
        new Thread(task).start();
        return task.get();
    }
}
