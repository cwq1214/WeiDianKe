package com.hzkj.wdk.widget.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.hzkj.wdk.model.MemberServiceBean;
import com.hzkj.wdk.widget.QRCodeVipServiceViewHolder;

import java.util.List;

/**
 * Created by chenweiqi on 2017/5/2.
 */

public class VipServiceAdapter extends RecyclerView.Adapter {

    List<MemberServiceBean> list;
    int type;
    OnViewHolderClickListener onClickListener;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        QRCodeVipServiceViewHolder vipServiceViewHolder = new QRCodeVipServiceViewHolder(parent);
        vipServiceViewHolder.setOnClickListener(onClickListener);
        vipServiceViewHolder.setType(type);
        return vipServiceViewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((QRCodeVipServiceViewHolder) holder).setData(list.get(position),position);
    }

    @Override
    public int getItemCount() {
        if (list!=null){
            return list.size();
        }
        return 0;
    }

    public void setOnClickListener(OnViewHolderClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public List<MemberServiceBean> getList() {
        return list;
    }

    public void setList(List<MemberServiceBean> list) {
        this.list = list;
    }

    public void setType(int type) {
        this.type = type;
    }

    public interface OnViewHolderClickListener {
        void onClick(Object data,int position);
    }
}
