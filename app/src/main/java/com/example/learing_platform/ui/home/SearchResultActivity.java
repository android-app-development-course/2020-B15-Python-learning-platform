package com.example.learing_platform.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.learing_platform.R;
import com.example.learing_platform.objects.Knowledge;
import com.example.learing_platform.utils.ActivityCollector;
import com.example.learing_platform.utils.home.CardAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private String TAG = "SearchResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ActivityCollector.addActivity(this);

        recyclerView = findViewById(R.id.search_Recyclerview);

        List<Knowledge> list = (ArrayList<Knowledge>) getIntent().getSerializableExtra("list");

        initView(list);
    }

    private void initView(List<Knowledge> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cardAdapter = new CardAdapter(list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cardAdapter);
        cardAdapter.setOnItemClickListener(new CardAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Knowledge data) {
                JumpToKnowledge(data);
            }
        });
    }

    //跳转知识显示页面
    private void JumpToKnowledge(Knowledge data)
    {
        Intent KnowledgeIntent = new Intent(this,KnowledgeActivity.class);
        KnowledgeIntent.putExtra("id",data.getId());
        KnowledgeIntent.putExtra("title",data.getTitle());
        KnowledgeIntent.putExtra("content",data.getContent());
        startActivity(KnowledgeIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁活动时，将其从管理器中移除
        ActivityCollector.removeActivity(this);
    }
}
