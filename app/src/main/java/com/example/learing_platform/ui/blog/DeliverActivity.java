package com.example.learing_platform.ui.blog;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.health.SystemHealthManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learing_platform.MainActivity;
import com.example.learing_platform.R;
import com.example.learing_platform.objects.User;
import com.example.learing_platform.objects.Userinfo;
import com.example.learing_platform.utils.ActivityCollector;
import com.example.learing_platform.utils.Utils;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.roger.catloadinglibrary.CatLoadingView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class DeliverActivity extends AppCompatActivity {

    private RichEditor mEditor;
    private EditText publish_title;
    private String cover = "";
    private String introduction;
    private String htmlcode = "";    //html代码
    String TAG = "DeliverActivity";
    //上传图片（放置等待框）
    private MyHandler handler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliverblog);

        ActivityCollector.addActivity(this);
        //设置标题栏
        Toolbar toolbar = findViewById(R.id.deliver_toolbar);
        setSupportActionBar(toolbar);
        //去掉默认的标题
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);


        //点击取消：返回上一个activity
        findViewById(R.id.deliver_btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //点击发布：向服务器发布帖子
        findViewById(R.id.deliver_btn_publish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: 向服务器发送
                publishBlog();
            }
        });

        publish_title = (EditText) findViewById(R.id.publish_title);
        mEditor = (RichEditor) findViewById(R.id.editor);

        //初始化编辑高度
        mEditor.setEditorHeight(200);
        //初始化字体大小
        mEditor.setEditorFontSize(22);
        //初始化字体颜色
        mEditor.setEditorFontColor(Color.BLACK);
        //mEditor.setEditorBackgroundColor(Color.BLUE);

        //初始化内边距
        mEditor.setPadding(5, 10, 10, 10);
        //设置编辑框背景，可以是网络图片
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        //设置默认显示语句
        mEditor.setPlaceholder("请在此输入正文");
        //设置编辑器是否可用
        mEditor.setInputEnabled(true);
        publish_title.setOnFocusChangeListener(onFocusAutoClearHintListener);
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                htmlcode = text;
                if(text.indexOf("img")<0) cover="";
                System.out.println(text);
                if(text.indexOf("<")>0)  introduction=text.substring(0,text.indexOf("<")).replace("&nbsp;","");
                else introduction=text;
                System.out.println(introduction);
            }
        });

        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
            }
        });


        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });

        findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });

        findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

        findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(6);
            }
        });

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBullets();
            }
        });

        findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setNumbers();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectPic();
//                mEditor.insertImage("http://39.102.67.182:8025/pic/6671620201222.jpg","dachshund",320);
                //判断是否授权这里以一个权限为例
                if (ContextCompat.checkSelfPermission(DeliverActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//没有授权进行权限申请
                    ActivityCompat.requestPermissions(DeliverActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectPic();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    public  View.OnFocusChangeListener onFocusAutoClearHintListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText editText=(EditText)v;
            if (!hasFocus) {// 失去焦点
                editText.setHint(editText.getTag().toString());
            } else {
                String hint=editText.getHint().toString();
                editText.setTag(hint);
                editText.setHint("");
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁活动时，将其从管理器中移除
        ActivityCollector.removeActivity(this);
    }

    private  void publishBlog()
    {
        String title = publish_title.getText().toString();
        String content = htmlcode;   //html内容
        if(title.isEmpty()||content.isEmpty())
        {
            Toast.makeText(this,"标题或内容不得为空",Toast.LENGTH_LONG).show();
            return;
        }
        User user=((Userinfo)getApplication()).getUser();
        Map<Object,Object> map = new HashMap<>();
        map.put("author",user.getNickname());
        map.put("title",title);
        map.put("html",content);
        map.put("introduction",introduction);
        map.put("cover_url",cover);
        map.put("portrait_url","http://pic3.nipic.com/20090615/2847388_082823092_2.jpg");
        map.put("author_id",user.getId());
        String jsonStr = new Gson().toJson(map);
        RequestBody body = FormBody.create(MediaType.parse("application/json;charset=utf-8"), jsonStr);
        Request request = new Request.Builder()
                .url("http://39.102.67.182:8080/api3/postblog")
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result = response.body().string();
                Log.d(TAG, "onResponse: " + result);
                try{
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("id", result);
                    data.putBoolean("flag",false);
                    msg.setData(data);
                    handler.sendMessage(msg);
                }catch (Exception e) {

                }
            }
        });
    }



    //选择图片
    private void selectPic()
    {
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent,0);

    }

    //选择图片后回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK && requestCode == 0)
        {
            Bitmap bitmap1 = null;
            Uri photoUri1 = data.getData();
            try {
                bitmap1 =BitmapFactory.decodeFileDescriptor(getContentResolver().openFileDescriptor(photoUri1,"r").getFileDescriptor());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if(bitmap1!=null)
            {
                uploadpic(bitmap1);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //上传图片到服务器
    private void uploadpic(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        int options = 100;
        while ( baos.toByteArray().length / 1024>1000) { //循环判断如果压缩后图片是否大于1000kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        byte[] bytes = baos.toByteArray();
        String picture= Base64.encodeToString(bytes, Base64.DEFAULT).replace(" ","");
        OkHttpClient client = new OkHttpClient();
        Map<String,String> map = new HashMap<>();
        map.put("base64",picture);
        String jsonStr = new Gson().toJson(map);
        RequestBody body = FormBody.create(MediaType.parse("application/json;charset=utf-8"), jsonStr);
        Request request = new Request.Builder()
                .url("http://39.102.67.182:8080/api3/uploadpic")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result = response.body().string();
                Log.d(TAG, "onResponse: " + result);
                try{
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("picUrl", result);
                    data.putBoolean("flag",true);
                    msg.setData(data);
                    handler.sendMessage(msg);
                }catch (Exception e) {

                }
            }
        });
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            if(data.getBoolean("flag")) {
                String picUrl = data.getString("picUrl");
                mEditor.insertImage(picUrl,
                        "dachshund", 320);
                if (cover == "") cover = picUrl;
                super.handleMessage(msg);
            }
            else{
                String id = data.getString("id");
                System.out.println(id);
                System.out.println(1);
                if(id.equals("true"))
                {
                    Toast.makeText(getApplicationContext(),"发表成功",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"发表失败",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
