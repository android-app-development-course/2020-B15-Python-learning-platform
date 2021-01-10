package com.example.learing_platform.utils.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learing_platform.R;
import com.example.learing_platform.objects.Knowledge;

import java.util.List;


public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {
    List<Knowledge> list;
    public CardAdapter(List<Knowledge> list) {
        this.list = list;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cell_home,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvtitle.setText(list.get(position).getTitle());
        holder.tvcontent.setText(list.get(position).getIntroduction());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvtitle,tvcontent;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvtitle = itemView.findViewById(R.id.tv_title);
            tvcontent = itemView.findViewById(R.id.tv_content);
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
            public void OnItemClick(View view, Knowledge data);
        }
        //需要外部访问，所以需要设置set方法，方便调用
        private OnItemClickListener onItemClickListener;
        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

    }
