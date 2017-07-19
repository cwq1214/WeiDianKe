package com.hzkj.wdk.widget;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.hzkj.wdk.R;
import com.hzkj.wdk.model.MemberServiceBean;
import com.hzkj.wdk.widget.adapter.VipServiceAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by chenweiqi on 2017/5/2.
 */

public class QRCodeVipServiceViewHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.des)
    TextView des;
    @BindView(R.id.arrow)
    ImageView arrow;
    @BindView(R.id.exchange)
    TextView exchange;

    int type;
    VipServiceAdapter.OnViewHolderClickListener onClickListener;
    MemberServiceBean bean;
    int position;

    public QRCodeVipServiceViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_member_service_viewholder, parent, false));
        ButterKnife.bind(this,itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener!=null){
                    onClickListener.onClick(bean,position);
                }
            }
        });
        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener!=null){
                    onClickListener.onClick(bean,position);
                }
            }
        });

    }


    public void setData(MemberServiceBean bean, int position){
        this.bean  = bean;
        this.position = position;
        if (type==1){
            arrow.setVisibility(View.VISIBLE);
            exchange.setVisibility(View.GONE);
        }else {
            arrow.setVisibility(View.GONE);
            exchange.setVisibility(View.VISIBLE);
        }

        name.setText(bean.serviceName);
        des.setText(bean.description);
        Glide.with(itemView.getContext()).load(bean.serviceImg).bitmapTransform(new RoundedCornersTransformation(itemView.getContext(),8,0)).into(avatar);

    }

    public void setOnClickListener(VipServiceAdapter.OnViewHolderClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
