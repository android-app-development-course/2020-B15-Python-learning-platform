package com.example.learing_platform.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.learing_platform.MainActivity;
import com.example.learing_platform.R;
import com.example.learing_platform.objects.Knowledge;
import com.example.learing_platform.utils.ActivityCollector;
import com.example.learing_platform.utils.home.CardAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import scut.carson_ho.searchview.ICallBack;
import scut.carson_ho.searchview.SearchView;
import scut.carson_ho.searchview.bCallBack;

public class SearchActivity extends AppCompatActivity {

    // 1. 初始化搜索框变量
    private SearchView searchView;
//    //加载
//    private LoadingDialog ld;
    private String TAG = "SearchActivity";
    private SearchHandler handler = new SearchHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        // 2. 绑定视图
        setContentView(R.layout.activity_search);

//        ld = new LoadingDialog(this);
//        ld.setLoadingText("搜索ing").setFailedText("找不到相应知识点~");

        // 3. 绑定组件
        searchView = (SearchView) findViewById(R.id.search_view);

        // 4. 设置点击键盘上的搜索按键后的操作（通过回调接口）
        // 参数 = 搜索框输入的内容
        searchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String str) {
                Search(str);
            }
        });

        // 5. 设置点击返回按键后的操作（通过回调接口）
        searchView.setOnClickBack(new bCallBack() {
            @Override
            public void BackAciton() {
                finish();
            }
        });
    }

    private void Search(String str)
    {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("keyword", str);
        Request request = new Request.Builder()
                .url("http://39.102.67.182:8080/api2/search")
                .post(builder.build())
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
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("result",result);
                msg.setData(data);
                handler.sendMessage(msg);
            }
        });
    }

    private class SearchHandler extends Handler {
        //处理消息的方法
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String result = data.getString("result");
            Gson gson = new Gson();
            ArrayList<Knowledge> list = new ArrayList<Knowledge>();
            Type type1 = new TypeToken<List<Knowledge>>() {
            }.getType();
            list = gson.fromJson(result, type1);
            if(list.size()>0)
            {
                Intent intent = new Intent(SearchActivity.this,SearchResultActivity.class);
                intent.putExtra("list", list);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"找不到相应知识点噢！",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁活动时，将其从管理器中移除
        ActivityCollector.removeActivity(this);
    }
}
