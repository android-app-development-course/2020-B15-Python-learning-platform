package com.example.learing_platform.utils.blog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learing_platform.R;
import com.example.learing_platform.objects.Blog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Handler;

import de.hdodenhof.circleimageview.CircleImageView;


public class blogCardAdapter extends RecyclerView.Adapter<blogCardAdapter.MyViewHolder> {
    private List<Blog> list;
    public Context context;
    public blogCardAdapter(Context context,List<Blog> list) {
        this.list = list;
        while (this.list.remove(null));
        this.context=context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cell_blog,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(list.get(position)!=null) {
            String x = list.get(position).getCoverUrl();
            if (x != null)
                Glide.with(context)
                        .load(x)
                        .into(holder.cover);
            Glide.with(context)
                    .load(list.get(position).getAuthorPortraitUrl())
                    .into(holder.profile);
            holder.tvtitle.setText(list.get(position).getTitle());
            holder.tvcontent.setText(list.get(position).getIntroduction());
            holder.relies.setText(list.get(position).getRepliesNum() + " 评论");
            holder.author.setText(list.get(position).getAuthor());
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvtitle,tvcontent,relies,author;
        private CircleImageView profile;
        private ImageView cover;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvtitle = itemView.findViewById(R.id.tv_title);
            tvcontent = itemView.findViewById(R.id.tv_content);
            relies = itemView.findViewById(R.id.tv_relies);
            author = itemView.findViewById(R.id.tv_author);
            profile = itemView.findViewById(R.id.profile_img);
            cover = itemView.findViewById(R.id.cover_img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //此处回传点击监听事件
                    if(onItemClickListener!=null){
                        onItemClickListener.OnItemClick(v, list.get(getLayoutPosition()));
                    }
                }
            });
        }
    }

    /**
     * 设置item的监听事件的接口
     */
    public interface OnItemClickListener {
        /**
         * 接口中的点击每一项的实现方法，参数自己定义
         */
        public void OnItemClick(View view, Blog data);
    }
    //需要外部访问，所以需要设置set方法，方便调用
    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
