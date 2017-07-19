package com.hzkj.wdk.widget.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hzkj.wdk.R;
import com.hzkj.wdk.listener.ViewHolderClickListener;
import com.hzkj.wdk.model.QRCodeListItemBean;
import com.hzkj.wdk.widget.QRCodeListViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * Created by chenweiqi on 2017/4/28.
 */

public class QRCodeListAdapter extends RecyclerView.Adapter<QRCodeListViewHolder> {

    List<QRCodeListItemBean.ItemBean> qrCodeListItemBeen;

    ViewHolderClickListener onClickListener;
    QRCodeListViewHolder.OnAddFriendClick onAddFriendClick;

    @Override
    public QRCodeListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        QRCodeListViewHolder viewHolder = new QRCodeListViewHolder(parent);
        viewHolder.setOnClickListener(onClickListener);
        viewHolder.setOnAddFriendClick(onAddFriendClick);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(QRCodeListViewHolder holder, int position) {
        holder.setData(qrCodeListItemBeen.get(position),position);
    }

    @Override
    public int getItemCount() {
        if (qrCodeListItemBeen!=null)
            return qrCodeListItemBeen.size();
        return 0;
    }

    public void setQrCodeListItemBeen(List<QRCodeListItemBean.ItemBean> qrCodeListItemBeen) {
        this.qrCodeListItemBeen = qrCodeListItemBeen;
    }

    public List<QRCodeListItemBean.ItemBean> getQrCodeListItemBeen() {
        if (qrCodeListItemBeen==null){
            qrCodeListItemBeen = new ArrayList<>();
        }
        return qrCodeListItemBeen;
    }

    public void setOnClickListener(ViewHolderClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnAddFriendClick(QRCodeListViewHolder.OnAddFriendClick onAddFriendClick) {
        this.onAddFriendClick = onAddFriendClick;
    }
}
