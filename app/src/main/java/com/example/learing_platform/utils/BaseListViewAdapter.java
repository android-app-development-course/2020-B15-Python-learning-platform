package com.example.learing_platform.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//ListViewAdapter的基类，负责公用的启动后台下载线程和初始化Handler类
public abstract class BaseListViewAdapter extends BaseAdapter {

    protected LayoutInflater mInflater;
    protected Handler handler = new myHandler();

    ViewGroup parentListView;



    //调用一次getView使对应position位置的view得到更新，parent是容纳viewitem的ListView
    //这个不能写在BaseListViewAdapter里，不然getView会调用BaseAdapter的方法 and do nothing
    protected void updateSingleItem(int position, ViewGroup parent) {
        //getChildAt的参数index记录相对位置，即可见项的第一条为下标0开始，不管它实际是data的第几条。所以需要计算相对位置
        int startIndex = ((ListView) parent).getFirstVisiblePosition();
        View oldView = ((ListView) parent).getChildAt(position - startIndex);
        //这里调用getView，在getView中更新oldView对应的图片内容，达到更新视图的效果
        if (oldView != null)
            getView(position, oldView, parent);
    }


    class myHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String notifyAll = data.getString("notifyAll");
            if(notifyAll!= null && notifyAll.equals("ok")) {
//                loadingView.dismiss();
                notifyDataSetChanged();
            }
            String notifySingle = data.getString("notifySingle");
            if(notifySingle != null && notifySingle.equals("ok")) {
                int position = data.getInt("position");
                updateSingleItem(position, parentListView);
            }
        }
    }


}
