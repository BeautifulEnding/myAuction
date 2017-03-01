package com.example.administrator.study.util;

/**
 * Created by Administrator on 2016/12/19 0019.
 */

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import android.util.Log;

public class UploadUtil {
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10*1000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码
    /**
     * android上传文件到服务器
     * @param file  需要上传的文件
     * @param RequestURL  请求的rul
     * @return  返回响应的内容
     */
    public static String uploadFile(File file,String RequestURL)
    {
        String result = null;
        String  BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--" , LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型

        try {
            Log.e("上传文件","正在和服务器取得联系");
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

            if(file!=null)
            {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
            Log.e("上传文件","正在准备上传图片文件");
                DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的   比如:abc.png
                 */

                sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""+file.getName()+"\""+LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="+CHARSET+LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                Log.e("上传文件","正在上传实体首部");
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while((len=is.read(bytes))!=-1)
                {
                    Log.e("上传文件","正在将图片转换成数据流输出");
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                dos.write(end_data);
//                流清空
                dos.flush();
                /**
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                Log.e(TAG, "response code:"+res);
//                if(res==200)
//                {
                Log.e(TAG, "request success");
                InputStream input =  conn.getInputStream();
                StringBuffer sb1= new StringBuffer();
                int ss ;
                while((ss=input.read())!=-1)
                {
                    sb1.append((char)ss);
                }
                result = sb1.toString();
                Log.e(TAG, "result : "+ result);
//                }
//                else{
//                    Log.e(TAG, "request error");
//                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 直接通过HTTP协议提交数据到服务器,实现表单提交功能
     * @param Url 上传路径
     * @param params 请求参数 key为参数名,value为参数值
     * @param filename 上传文件
     */
    public static void uploadMutiData(String Url, Map<String, String> params, ArrayList<String> filename) {
        if (filename.contains("000000")){
            filename.remove("000000");
        }
        Log.e("filename",filename.size()+"");
        Log.e("上传文件","正在上传文件");
        String PREFIX = "--" , LINE_END = "\r\n";
        try{
            String BOUNDARY ="--------------et567z";//数据分隔线
            String MULTIPART_FORM_DATA ="Multipart/form-data";
            URL url =new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);//允许输入
            conn.setDoOutput(true);//允许输出
            conn.setUseCaches(false);//不使用Cache
            conn.setConnectTimeout(TIME_OUT);
            conn.setReadTimeout(TIME_OUT);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection","Keep-Alive");
            conn.setRequestProperty("Charset","UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA +";boundary="+ BOUNDARY);
            StringBuilder sb =new StringBuilder();
            //上传的表单参数部分，格式请参考文章
            for(Map.Entry<String, String> entry : params.entrySet()) {//构建表单字段内容
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\""+ entry.getKey() +"\"\r\n\r\n");
                sb.append(entry.getValue());
                sb.append(LINE_END);
            }
            DataOutputStream outStream =new DataOutputStream(conn.getOutputStream());
            outStream.write(sb.toString().getBytes());//发送表单字段数据
            int count=1;
            for (String fileName: filename){
                if (!filename.equals("") && filename!=null){
                    File file=new File(fileName);
                    /**
                     * 当文件不为空，把文件包装并且上传*/

                    StringBuilder split =new StringBuilder();
                    split.append(PREFIX);
                    split.append(BOUNDARY);
                    split.append(LINE_END);
                    split.append("Content-Disposition: form-data; name=\"img"+count+"\"; filename=\""+file.getName()+"\""+LINE_END);
                    Log.e("filename",file.getName()+"");
                    count++;
                    split.append("Content-Type: application/octet-stream; charset="+CHARSET+LINE_END);
                    split.append(LINE_END);
                    Log.e("split",split.toString());
                    outStream.write(split.toString().getBytes());
                    InputStream is = new FileInputStream(file);
                    byte[] bytes = new byte[1024];
                    int len = 0;
                    while((len=is.read(bytes))!=-1)
                    {
                        outStream.write(bytes, 0, len);
                    }
                    is.close();
                    outStream.write(LINE_END.getBytes());
                }
            }
            byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
            outStream.write(end_data);
//                流清空
            outStream.flush();
            int cah = conn.getResponseCode();
            if(cah ==200)//如果发布成功则提示成功
            {
//            读返回数据
                BufferedInputStream inputStream=new BufferedInputStream(conn.getInputStream());
                int len=0;
                byte[] bytes=new byte[1024];
                while ((len=inputStream.read(bytes))!=-1){
                    Log.e("服务器返回结果",new String(bytes));
                }

            }else{
                if (cah==503){
                    Log.e("失败","上传文件超时");
                }else{
                    Log.e("失败","上传文件失败"+cah);
                }
            }
            outStream.close();
            conn.disconnect();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


}
