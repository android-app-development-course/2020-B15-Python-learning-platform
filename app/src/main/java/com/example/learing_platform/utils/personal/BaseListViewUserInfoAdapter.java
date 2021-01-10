package com.example.learing_platform.utils.personal;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.learing_platform.R;
import com.example.learing_platform.objects.User;
import com.example.learing_platform.utils.BaseListViewAdapter;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//userInfo的适配器，显示user的名字、兴趣、经历等，可以被personal的两个界面继承
public class BaseListViewUserInfoAdapter extends BaseListViewAdapter {

    ArrayList<User> userInfos;

    String TAG = "BaseListViewUserInfoAdapter";

    public BaseListViewUserInfoAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        userInfos = new ArrayList<>();
    }

    @Override
    public int getCount() {
        if(userInfos == null)
            return 0;
        else
            return userInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return userInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.listitem_personinfo, parent, false);


        User info = userInfos.get(position);
        ((TextView) view.findViewById(R.id.personal_info_tv_nickName)).setText(info.getNickname());
        ((TextView) view.findViewById(R.id.personal_info_tv_sex)).setText(info.getSex());
        if(info.getAge()!=0)
            ((TextView) view.findViewById(R.id.personal_info_tv_age)).setText(String.valueOf(info.getAge()));
        ((TextView) view.findViewById(R.id.personal_info_tv_identity)).setText(info.getIdentity());
        ((TextView) view.findViewById(R.id.personal_info_tv_school)).setText(info.getSchool());
        ((TextView) view.findViewById(R.id.personal_info_tv_vocation)).setText(info.getVocation());
        ((TextView) view.findViewById(R.id.personal_info_tv_introduction)).setText(info.getIntroduction());
        ((TextView) view.findViewById(R.id.personal_info_tv_email)).setText(info.getEmail());

        return view;
    }

//    class myHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            Bundle data = msg.getData();
//            String notifyAll = data.getString("notifyAll");
//            if(notifyAll.equals("ok")) {
////                loadingView.dismiss();
//                notifyDataSetChanged();
//            }
//        }
//    }

    public void setData(User userInfo) {
        userInfos.add(userInfo);
        Message message = new Message();
        Bundle data = new Bundle();
        data.putString("notifyAll", "ok");
        message.setData(data);
        handler.sendMessage(message);
    }




}
