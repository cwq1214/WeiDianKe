package com.hzkj.wdk.widget.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hzkj.wdk.R;
import com.hzkj.wdk.model.Province;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by chenweiqi on 2017/5/10.
 */

public class SelCityAdapter extends RecyclerView.Adapter {
    OnViewHolderClickListener onViewHolderClickListener;

    List datas;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextViewHolder holder = new TextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_city,parent,false));

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((TextViewHolder) holder).setData(datas.get(position),position);
    }

    public void setDatas(List datas) {
        this.datas = datas;
    }

    @Override
    public int getItemCount() {
        if (datas!=null)
            return datas.size();
        return 0;
    }

    public void setOnViewHolderClickListener(OnViewHolderClickListener onViewHolderClickListener) {
        this.onViewHolderClickListener = onViewHolderClickListener;
    }

    public class TextViewHolder extends RecyclerView.ViewHolder{
        Object data;
        int position;
        TextView tv;
        public TextViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onViewHolderClickListener!=null){
                        onViewHolderClickListener.onClick(data,position);
                    }
                }
            });
            tv= (TextView) itemView.findViewById(R.id.tv);
        }

        public void setData(Object object , int position){
            this.data = object;
            this.position = position;

            try {
                Field field = data.getClass().getField("name");
                field.setAccessible(true);
                String name = field.get(data).toString();
                tv.setText(name);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnViewHolderClickListener{
        void onClick(Object data,int position);
    }
}
