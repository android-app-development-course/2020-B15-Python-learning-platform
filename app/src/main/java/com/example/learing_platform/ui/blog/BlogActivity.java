package com.example.learing_platform.ui.blog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.learing_platform.R;
import com.example.learing_platform.utils.ActivityCollector;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        ActivityCollector.addActivity(this);
        TextView title=findViewById(R.id.detail_title);
        TextView author_name=findViewById(R.id.author_name);
        TextView detail_time= findViewById(R.id.detail_time);
        EditText et_comment=findViewById(R.id.et_comment);
        CircleImageView author_img=findViewById(R.id.author_img);
        WebView webView = findViewById(R.id.webView);
        Button back=findViewById(R.id.backhome);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_comment.setOnFocusChangeListener(onFocusAutoClearHintListener);
        AssetManager mgr = this.getAssets();
        Typeface t=Typeface.createFromAsset(mgr,"Ziti/simhei.ttf");
        title.setTypeface(t);
        author_name.setTypeface(t);
        Intent i=getIntent();
        Glide.with(this)
                .load(i.getStringExtra("author_img"))
                .into(author_img);
        title.setText(i.getStringExtra("title"));
        author_name.setText(i.getStringExtra("author_name"));
        detail_time.setText(i.getStringExtra("post_time").substring(0,10));
        //WebView加载web资源
        String temp=i.getStringExtra("content");
        String url=temp.substring(0,temp.indexOf("blog")+4)+"/"+temp.substring(temp.indexOf("blog")+4,temp.length());
        webView.loadUrl(url);
        //覆盖WebView默认通过第三方或者是系统浏览器打开网页的行为，使得网页可以在WebView中打开
        webView.setWebViewClient(new WebViewClient(){
                                     /*@Override
                                     public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                         //返回值是true的时候是控制网页在WebView中去打开，如果为false调用系统浏览器或第三方浏览器打开
                                         view.loadUrl(url);
                                         return true;
                                     }*/
                                     //WebViewClient帮助WebView去处理一些页面控制和请求通知
                                     @Override
                                     public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                                         try {
                                             if (url.startsWith("http:") || url.startsWith("https:")) {
                                                 view.loadUrl(url);
                                             } else {
                                                 Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                                 startActivity(intent);
                                             }
                                             return true;
                                         } catch (Exception e){
                                             return false;
                                         }
                                     }
                                 }
        );
        //启用支持Javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        //WebView加载页面优先使用缓存加载
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
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
}