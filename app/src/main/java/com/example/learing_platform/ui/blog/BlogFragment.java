package com.example.learing_platform.ui.blog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.learing_platform.R;
import com.example.learing_platform.objects.Blog;
import com.example.learing_platform.objects.Knowledge;
import com.example.learing_platform.ui.home.HomeFragment;
import com.example.learing_platform.ui.home.KnowledgeActivity;
import com.example.learing_platform.utils.blog.blogCardAdapter;
import com.example.learing_platform.utils.home.CardAdapter;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import kotlin.time.AbstractLongTimeSource;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class BlogFragment extends Fragment {

    private RecyclerView recyclerView;
    private blogCardAdapter blogcardAdapter;
    private String TAG = "BlogFragment";

    BlogFragment.BlogHandler bhandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void refresh(){
        requestBlog();
    }

    private void initView(List<Blog> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        try {
            blogcardAdapter = new blogCardAdapter(this.getActivity().getApplicationContext(), list);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(blogcardAdapter);
        }catch(Exception e){

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_blog, container, false);

        //设置标题栏
        Toolbar toolbar = root.findViewById(R.id.blog_toolbar);
        AppCompatActivity parent = (AppCompatActivity) getActivity();
        parent.setSupportActionBar(toolbar);
        //去掉默认的标题
        parent.getSupportActionBar().setDisplayShowTitleEnabled(false);

        //点击发布按钮：跳转到deliver activity
        root.findViewById(R.id.blog_btn_deliver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DeliverActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = root.findViewById(R.id.Recyclerview);


        //TODO:向服务器请求列表
        requestBlog();

        bhandler = new BlogFragment.BlogHandler();


        final SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.Blog_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 200);
            }
        });

        return root;
    }

    //跳转博客显示页面
    private void JumpToBlog(Blog data)
    {
        Intent BlogIntent = new Intent(this.getActivity(), BlogActivity.class);
        BlogIntent.putExtra("id",data.getId());
        BlogIntent.putExtra("title",data.getTitle());
        BlogIntent.putExtra("content",data.getContent());
        BlogIntent.putExtra("author_name",data.getAuthor());
        BlogIntent.putExtra("post_time",data.getPost_time());
        BlogIntent.putExtra("author_img", data.getAuthorPortraitUrl());
        startActivity(BlogIntent);
    }

    //请求云服务器
    private void requestBlog()
    {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("num", "10");
        Request request = new Request.Builder()
                .url("http://39.102.67.182:8080/api3/getblogs")
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
                Log.d(TAG, "onResponse: " + "success");
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("result",result);
                msg.setData(data);
                bhandler.sendMessage(msg);
            }
        });
    }

    private class BlogHandler extends Handler {
        //处理消息的方法
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String result = data.getString("result");
            Gson gson = new Gson();
            ArrayList<Blog> list;
            Type type1=new TypeToken<List<Blog>>(){}.getType();
            list=gson.fromJson(result, type1);
            System.out.println(list.size());
            initView(list);
            blogcardAdapter.setOnItemClickListener(new blogCardAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(View view, Blog data) {
                    JumpToBlog(data);
                }
            });
            super.handleMessage(msg);
        }
    }
}
