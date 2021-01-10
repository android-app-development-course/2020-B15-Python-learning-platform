package com.example.learing_platform.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.learing_platform.R;
import com.example.learing_platform.objects.Knowledge;
import com.example.learing_platform.objects.User;
import com.example.learing_platform.ui.personal.EditInfoActivity;
import com.example.learing_platform.ui.personal.SettingsActivity;
import com.example.learing_platform.utils.Utils;
import com.example.learing_platform.utils.home.CardAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import scut.carson_ho.searchview.ICallBack;
import scut.carson_ho.searchview.SearchView;
import scut.carson_ho.searchview.bCallBack;


public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private String TAG = "HomeFragment";
    KnowledgeHandler khandler;
//    private MaterialSearchBar searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    private void refresh(){
        requestKnowledge();
    }

    private void initView(List<Knowledge>list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cardAdapter = new CardAdapter(list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cardAdapter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        //设置标题栏
        Toolbar toolbar = root.findViewById(R.id.home_toolbar);
        toolbar.inflateMenu(R.menu.home_menu);

//        AppCompatActivity parent = (AppCompatActivity) getActivity();
//        parent.setSupportActionBar(toolbar);
//        //去掉默认的标题
//        parent.getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.option_search:
                        Intent intent1 = new Intent(getActivity(), SearchActivity.class);
                        startActivityForResult(intent1,1);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

//        searchView = root.findViewById(R.id.searchBar);

        recyclerView = root.findViewById(R.id.Recyclerview);

        //TODO:向服务器请求列表
        requestKnowledge();

        khandler = new KnowledgeHandler();


        final SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.Know_refresh);
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


    //跳转知识显示页面
    private void JumpToKnowledge(Knowledge data)
    {
        Intent KnowledgeIntent = new Intent(this.getActivity(),KnowledgeActivity.class);
        KnowledgeIntent.putExtra("id",data.getId());
        KnowledgeIntent.putExtra("title",data.getTitle());
        KnowledgeIntent.putExtra("content",data.getContent());
        startActivity(KnowledgeIntent);
    }

    //请求云服务器
    private void requestKnowledge()
    {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("num", "8");
        Request request = new Request.Builder()
                .url("http://39.102.67.182:8080/api2/getknows")
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
                khandler.sendMessage(msg);
            }
        });
    }

    private class KnowledgeHandler extends Handler {
        //处理消息的方法
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String result = data.getString("result");
            Gson gson = new Gson();
            ArrayList<Knowledge> list=new ArrayList<Knowledge>();
            Type type1=new TypeToken<List<Knowledge>>(){}.getType();
            list=gson.fromJson(result, type1);
            initView(list);
            cardAdapter.setOnItemClickListener(new CardAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(View view, Knowledge data) {
                    JumpToKnowledge(data);
                }
            });
            super.handleMessage(msg);
        }
    }

}
