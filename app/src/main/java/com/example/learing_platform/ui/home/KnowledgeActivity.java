package com.example.learing_platform.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.learing_platform.R;
import com.example.learing_platform.utils.ActivityCollector;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class KnowledgeActivity extends AppCompatActivity {

    private int id;
    private String title;
    private String content;
    private TextView title_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge);
        ActivityCollector.addActivity(this);
        ProgressBar progressBar = findViewById(R.id.spin_kit);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);

        id = getIntent().getIntExtra("id",-1);

        String content = "http://39.102.67.182:8025/website/"+id+".html";
        System.out.println("已成功获取URL地址，URL为:"+content);
        init(content);
    }

    private void init(String url) {
        WebView webView = (WebView) findViewById(R.id.webview);
        //WebView加载web资源
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁活动时，将其从管理器中移除
        ActivityCollector.removeActivity(this);
    }

}
