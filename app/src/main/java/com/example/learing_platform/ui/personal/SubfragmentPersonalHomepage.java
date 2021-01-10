package com.example.learing_platform.ui.personal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.learing_platform.R;
import com.example.learing_platform.objects.User;
import com.example.learing_platform.objects.Userinfo;
import com.example.learing_platform.utils.personal.BaseListViewUserInfoAdapter;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SubfragmentPersonalHomepage extends Fragment {

    int userId = -1;
    String TAG = "SubfragmentPersonalHomepage";



    public SubfragmentPersonalHomepage() {

    }

    public SubfragmentPersonalHomepage(int userId) {
        this.userId = userId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.subfrag_listview_withoutbottomheight, container, false);

        reFreshList(root);

        return root;
    }

    void reFreshList(View root)
    {
        //加载用于展示通知的listview
        ListView listView = root.findViewById(R.id.listview);
        final BaseListViewUserInfoAdapter adapter = new BaseListViewUserInfoAdapter(getContext());
        listView.setAdapter(adapter);
        if (((Userinfo)getActivity().getApplication()).getUser().getId() != 0) {
            adapter.setData(((Userinfo)getActivity().getApplication()).getUser());
        }

//        //TODO:服务器获得用户信息并显示
//        OkHttpClient client = new OkHttpClient();
//        FormBody.Builder builder = new FormBody.Builder();
//        builder.add("username", ((Userinfo)getActivity().getApplication()).getUser().getUsername());
//        builder.add("password", ((Userinfo)getActivity().getApplication()).getUser().getPassword());
//        Request request = new Request.Builder()
//                .url("http://39.102.67.182:8080/api1/login")
//                .post(builder.build())
//                .build();
//        Call call = client.newCall(request);
//
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                Log.d(TAG, "onFailure: " + e.getMessage());
//
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                String result = response.body().string().replaceAll("null", "\"\"");
//                Gson gson = new Gson();
//                User user = gson.fromJson(result, User.class);
//                Log.d(TAG, "onResponse: " + result);
//                if (user.getId() != 0) {
//                    adapter.setData(user);
//                }
//            }
//        });
    }

}