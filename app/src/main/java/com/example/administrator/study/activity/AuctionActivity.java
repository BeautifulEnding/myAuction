package com.example.administrator.study.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.study.R;
import com.example.administrator.study.util.DialogUtil;
import com.example.administrator.study.util.FileUpload;
import com.example.administrator.study.util.FileUploadManager;
import com.example.administrator.study.util.HttpUtil;
import com.example.administrator.study.util.MyApplication;
import com.example.administrator.study.util.ScreenTools;
import com.example.administrator.study.util.UploadUtil;
import com.lidong.photopicker.PhotoPickerActivity;
import com.lidong.photopicker.PhotoPreviewActivity;
import com.lidong.photopicker.SelectModel;
import com.lidong.photopicker.intent.PhotoPickerIntent;
import com.lidong.photopicker.intent.PhotoPreviewIntent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuctionActivity extends AppCompatActivity {

    //定义请求代码
    public static final int GET_CAMERA=0;
    public static final int REQUEST_CODE_PICK_IMAGE=1;
    public static final int GET_MESSAGE=2;
    //    定义一个变量表示当前照片数
    int photoNum=1;
//    定义一个变量表示当前被点击的ListView项
    int itemPosition=-1;
    private GridView gridView=null;
    private GridAdapter gridAdapter=null;
    private ArrayList<String> imagePaths = new ArrayList<>();
    private Map<String,String> map=new HashMap<String,String>();
    private String TAG =MainActivity.class.getSimpleName();
    private ImageView errorImage;
    private ImageView warnImage;
    private Button sureButton;
    private EditText title;
    private EditText contents;

    ListView auctionItem=null;

    SimpleAdapter itemAdapter=null;
    int[] images=new int[]{R.drawable.right,R.drawable.right,R.drawable.right,R.drawable.right,
            R.drawable.right,R.drawable.right};
    String[] auctionItemNames=null;
    String[] auctionChoose=null;
    String[] defaultChoose=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        MyApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_auction);
        initView();
        initEvent();
    }

    private void initView(){
        gridView=(GridView)findViewById(R.id.gridView);
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 4 ? 4 : cols;
        gridView.setNumColumns(cols);

        auctionItem=(ListView)findViewById(R.id.auctionItem);
        auctionItemNames=getResources().getStringArray(R.array.auction_item);
        auctionChoose=getResources().getStringArray(R.array.auction_choose);
//        创建List集合
        List<Map<String,Object>> listItems=new ArrayList<Map<String,Object>>();
        for (int i=0;i<images.length;i++){
            Map<String,Object> listItem=new HashMap<String, Object>();
            listItem.put("item",auctionItemNames[i]);
            listItem.put("choose",auctionChoose[i]);
            listItem.put("image",images[i]);
            listItems.add(listItem);
        }
        itemAdapter =new SimpleAdapter(this,listItems,R.layout.auctionitem_list,
                new String[]{"item","choose","image"},new int[]{R.id.item,R.id.choose,R.id.iamge});
        auctionItem.setAdapter(itemAdapter);

//        得到返回图像
        errorImage =(ImageView) findViewById(R.id.errorImage);
        warnImage =(ImageView)findViewById(R.id.warnImage);
        sureButton=(Button)findViewById(R.id.sureButton);
        title=(EditText)findViewById(R.id.title);
        contents=(EditText)findViewById(R.id.send_content);
    }

    private void initEvent(){
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String imgs = (String) parent.getItemAtPosition(position);
                if ("000000".equals(imgs) ){
                    PhotoPickerIntent intent = new PhotoPickerIntent(AuctionActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true); // 是否显示拍照
                    intent.setMaxTotal(9); // 最多选择照片数量
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                    startActivityForResult(intent, GET_CAMERA);
                }else{
                    PhotoPreviewIntent intent = new PhotoPreviewIntent(AuctionActivity.this);
                    intent.setCurrentItem(position);
                    intent.setPhotoPaths(imagePaths);
                    startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
                }
            }
        });
        imagePaths.add("000000");
        String path=getIntent().getStringExtra("imagePath");
        if (path!=null){
            imagePaths.add(path);
        }
        gridAdapter = new GridAdapter(imagePaths);
        gridView.setAdapter(gridAdapter);

        errorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getInstance().removeActivity(AuctionActivity.this);
                AlertDialog.Builder builder=new AlertDialog.Builder(AuctionActivity.this);
                builder.setTitle("是否保存");
                builder.setMessage("是否保存，以便下次使用？");
                builder.setPositiveButton("是",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Toast.makeText(AuctionActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("否",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.create().show();

            }
        });

        warnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                待实现，弹出一个popupWindow，说明发布注意事项
            }
        });

        auctionItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemPosition=position;
                switch (position){
                    case 0:
                    case 1:
                    case 2:
                        Intent intent=new Intent(AuctionActivity.this,MessageActivity.class);
                        startActivityForResult(intent,GET_MESSAGE);
                        break;
                    case 3:
                    case 4:
                        //得到日历
                        Calendar c=Calendar.getInstance();
                        //弹出一个日期选择对话器
                        new DatePickerDialog(AuctionActivity.this,new DatePickerDialog.OnDateSetListener(){
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                                //更改内容
                                if (itemPosition==4){
                                    TextView textView=(TextView)auctionItem.getChildAt(itemPosition-1).findViewById(R.id.choose);
                                    String value=textView.getText().toString();
                                    int date1=Integer.parseInt(value.replace("-",""));
                                    int date2=change(year,monthOfYear,dayOfMonth);
                                    if (date1>=date2){
                                        DialogUtil.showDialog(AuctionActivity.this,"拍卖结束日期不能小于拍卖开始日期，请重新选择" +
                                                "拍卖结束日期",false);
                                    }else {
                                        changeItemValue(year,monthOfYear,dayOfMonth);
                                    }
                                }
                                else{
                                    SimpleDateFormat sDateFormat    =   new    SimpleDateFormat("yyyyMMdd");
                                    String date  =    sDateFormat.format(new    java.util.Date());
                                    int date1=Integer.parseInt(date);
                                    int date2=change(year,monthOfYear,dayOfMonth);
                                    if (date1>date2){
                                        DialogUtil.showDialog(AuctionActivity.this,"拍卖开始日期不能小于当前日期，请重新选择" +
                                                "拍卖开始日期",false);
                                    }else{
                                        changeItemValue(year,monthOfYear,dayOfMonth);
                                    }
                                }
                            }
                        },
                                c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
                        break;
                    case 5:
                        final TextView textView=(TextView) auctionItem.getChildAt(itemPosition).findViewById(R.id.choose);
                        //为分类选择注册一个PopupMenu菜单
                        PopupMenu popupMenu = new PopupMenu(AuctionActivity.this, textView);
                        //将菜单资源加载到popup菜单中
                        getMenuInflater().inflate(R.menu.sort, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                textView.setText(item.getTitle().toString());
                                return true;
                            }
                        });
                        popupMenu.show();
                        break;
                }
            }
        });

        sureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                如果數據合理
                if (validateMessage()){
//                    如果netConnected
                    if (isNetWorkConnected()){
                        //                将数据上传到服务器
                        map.put("tag",System.currentTimeMillis()+"");
                        map.put("user_name",getSharedPreferences("login",MODE_PRIVATE).getString("user_name",null));
                        map.put("title",title.getText().toString());
                        map.put("contents",contents.getText().toString());
                        for (int i=0;i<images.length;i++){
                            TextView textView=(TextView)auctionItem.getChildAt(i).findViewById(R.id.choose);
                            map.put("listView"+(i+1),textView.getText().toString());
                        }
                        Thread thread=new Thread(){
                            @Override
                            public void run() {
                                super.run();
//                        FileUpload.uploadAuction(imagePaths,map,photoNum);
                                if (photoNum!=9){
                                    map.put("photoNum",(photoNum-1)+"");
                                }else {
                                    map.put("photoNum", photoNum+"");
                                }
                                String url=HttpUtil.BASE_URL+"sendAuction";
                                UploadUtil.uploadMutiData(url,map,imagePaths);
//                        File file=new File(imagePaths.get(0));
//                        UploadUtil.uploadFile(file,url);
                            }
                        };
                        thread.start();
                        Toast.makeText(AuctionActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                        MyApplication.getInstance().removeActivity(AuctionActivity.this);
                        finish();
                    }else {
                        DialogUtil.showDialog(AuctionActivity.this,"您正處於沒有網絡的異次元，請聯網后再進行該操作！",false);
                    }
                }else{
                    DialogUtil.showDialog(AuctionActivity.this,"請完善拍賣信息，否則對方無法拍下！",false);
                }
            }
        });

    }
    private boolean validateMessage(){
        if (TextUtils.equals("",title.getText().toString().trim())
                || TextUtils.equals("",contents.getText().toString().trim()))
            return false;
        int i=0;
        while (i<auctionChoose.length){
            View item=auctionItem.getChildAt(i);
            TextView textView=(TextView) item.findViewById(R.id.choose);
            if (TextUtils.equals(auctionChoose[i],textView.getText().toString().trim()))
            return false;
            i++;
        }
        return true;
    }
    private void changeItemValue(int year, int monthOfYear, int dayOfMonth){
        int month=monthOfYear+1;
        int day=dayOfMonth;
        if (monthOfYear<9){
            month=Integer.parseInt("0"+(monthOfYear+1));
        }
        if (dayOfMonth<10){
            day=Integer.parseInt("0"+dayOfMonth);
        }
        TextView textView2=(TextView)auctionItem.getChildAt(itemPosition).findViewById(R.id.choose);
        textView2.setText(String.format("%d-%d-%d", year, month, day));
    }
    private int change(int year, int monthOfYear, int dayOfMonth){
        String month="";
        String day="";
        if (monthOfYear<9){
            month="0"+(monthOfYear+1);
        }else{
            month=""+(monthOfYear+1);
        }
        if (dayOfMonth<10){
            day="0"+dayOfMonth;
        }else {
            day = "" + dayOfMonth;
        }
        return Integer.parseInt(""+year+month+day);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                // 选择照片
                case GET_CAMERA:
                    ArrayList<String> list = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
                    loadAdpater(list);
                    break;
                // 预览
                case REQUEST_CODE_PICK_IMAGE:
                    ArrayList<String> ListExtra = data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT);
                    loadAdpater(ListExtra);
                    break;
                case GET_MESSAGE:
                    View item=auctionItem.getChildAt(itemPosition);
                    TextView textView=(TextView) item.findViewById(R.id.choose);
                    textView.setText("￥"+data.getStringExtra("message")+".00");
                    break;
            }
        }
    }
    //    判断网络状态
    private boolean isNetWorkConnected(){
        ConnectivityManager manager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();
//        返回联网状态，如果有联网信息并且连了网，则返回true
        return (networkInfo!=null&&networkInfo.isConnected());
    }
    private void loadAdpater(ArrayList<String> paths){
        if (imagePaths!=null&& imagePaths.size()>0){
            imagePaths.clear();
        }
        if (paths.contains("000000")){
            paths.remove("000000");
        }
        paths.add("000000");
        imagePaths.addAll(paths);
        gridAdapter  = new GridAdapter(imagePaths);
        photoNum=gridAdapter.getCount();
        ViewGroup.LayoutParams params=gridView.getLayoutParams();
        params.height= ScreenTools.instance(this).dip2px(100);
        if (photoNum > 4 && photoNum < 9){
            params.height= ScreenTools.instance(this).dip2px(180);

        }
        if (photoNum >= 9){
            params.height= ScreenTools.instance(this).dip2px(260);
        }
        gridView.setLayoutParams(params);
        gridView.setAdapter(gridAdapter);
        /*try{
            JSONArray obj = new JSONArray(imagePaths);
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }

    private class GridAdapter extends BaseAdapter {
        private ArrayList<String> listUrls;
        private LayoutInflater inflater;
        public GridAdapter(ArrayList<String> listUrls) {
            this.listUrls = listUrls;
            if(listUrls.size() == 10){
                listUrls.remove(listUrls.size()-1);
            }
            inflater = LayoutInflater.from(AuctionActivity.this);
        }

        public int getCount(){
            return  listUrls.size();
        }
        @Override
        public String getItem(int position) {
            return listUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_image, parent,false);
                holder.image = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            final String path=listUrls.get(position);
            if (path.equals("000000")){
                holder.image.setImageResource(R.drawable.add);
            }else {
                Glide.with(AuctionActivity.this)
                        .load(path)
                        .placeholder(R.mipmap.default_error)
                        .error(R.mipmap.default_error)
                        .centerCrop()
                        .crossFade()
                        .into(holder.image);
            }
            return convertView;
        }
        class ViewHolder {
            ImageView image;
        }
    }
}
