package com.example.learing_platform.ui.personal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.learing_platform.R;
import com.example.learing_platform.objects.Blog;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SubfragmentPersonalhistory extends Fragment {

    int userId = -1;

    public SubfragmentPersonalhistory() {
    }

    //如果指定了userId，说明要看别人的主页
    public SubfragmentPersonalhistory(int userId) {
        this.userId = userId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.subfrag_listview_withoutbottomheight, container, false);


        return root;
    }

}