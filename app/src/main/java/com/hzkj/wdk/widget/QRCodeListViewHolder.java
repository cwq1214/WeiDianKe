package com.hzkj.wdk.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.util.Util;
import com.hzkj.wdk.R;
import com.hzkj.wdk.listener.ViewHolderClickListener;
import com.hzkj.wdk.model.QRCodeListItemBean;
import com.hzkj.wdk.utils.Utils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import jp.wasabeef.glide.transformations.gpu.ContrastFilterTransformation;

/**
 * Created by chenweiqi on 2017/4/28.
 */

public class QRCodeListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.likes)
    TextView likes;
    @BindView(R.id.vipName)
    TextView vipName;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.des)
    TextView des;
    @BindView(R.id.qrcodeVip_name)
    TextView qrcodeVipName;
    @BindView(R.id.done)
    TextView done;


    QRCodeListItemBean.ItemBean bean;
    int position;
    ViewHolderClickListener onClickListener;
    Context context;
    OnAddFriendClick onAddFriendClick;

    public QRCodeListViewHolder(View itemView) {
        super(LayoutInflater.from(itemView.getContext()).inflate(R.layout.viewholder_qrcode_item, (ViewGroup) itemView,false));
        context = itemView.getContext();
        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener!=null)
                    onClickListener.onClick(bean,position);
            }
        });
        ButterKnife.bind(this,this.itemView);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onAddFriendClick!=null)
                    onAddFriendClick.onClick(bean);
            }
        });

    }

    public void setData(QRCodeListItemBean.ItemBean bean, int position) {
        this.bean = bean;
        this.position = position;
        setCommStyle();

        if (!TextUtils.isEmpty(bean.special)&&bean.special.equals("1")){
            setHongZuanStyle();
        }

        if (bean.isVip){
            setVipStyle();
        }

        if (bean.isZiZuan){
            setZiZuanStyle();
        }
//        if (!TextUtils.isEmpty(bean.special)&&bean.special.equals("1")){
//            setHongZuanStyle();
//        }

        name.setText(bean.nickname);
//        Glide.with(context).load("https://imgsa.baidu.com/baike/c0%3Dbaike92%2C5%2C5%2C92%2C30/sign=5a659521b1de9c82b268f1dd0de8eb6f/f9198618367adab482d06a5b89d4b31c8701e4a2.jpg").asBitmap().transform(new RoundedCornersTransformation(context,8,0)).into(avatar);
        Glide.with(context).load(bean.userImg).bitmapTransform(new CenterCrop(itemView.getContext()),new RoundedCornersTransformation(itemView.getContext(),dp2px(6),0, RoundedCornersTransformation.CornerType.ALL)).into(avatar);
        des.setText(bean.description);


        String like=null;
        if (TextUtils.isEmpty(bean.popularity)||bean.popularity.equals("0")){
            like=null;
            likes.setVisibility(View.INVISIBLE);
        }else if (!TextUtils.isEmpty(bean.popularity)&&Integer.valueOf(bean.popularity)>199){
            like = "199+";
            likes.setVisibility(View.VISIBLE);
        }else {
            like = bean.popularity;
            likes.setVisibility(View.VISIBLE);
        }
        likes.setText(like);
    }


    public void setOnClickListener(ViewHolderClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnAddFriendClick(OnAddFriendClick onAddFriendClick) {
        this.onAddFriendClick = onAddFriendClick;
    }

    private void setZiZuanStyle(){
        vipName.setVisibility(View.VISIBLE);
        qrcodeVipName.setVisibility(View.VISIBLE);
        vipName.setBackground(context.getResources().getDrawable(R.drawable.shape_purple_vip_type));
        done.setBackground(context.getResources().getDrawable(R.drawable.shape_purple_angle));
        name.setTextColor(Color.parseColor("#9012fe"));
        qrcodeVipName.setBackground(context.getResources().getDrawable(R.drawable.shape_vip_name_purple));

        if (!TextUtils.isEmpty(bean.timeStamp)&&!TextUtils.isEmpty(formatTime(Long.valueOf(bean.timeStamp)))) {
//            String value = formatTime(Long.valueOf(bean.timeStamp));
            long value  =  (Long.valueOf(bean.timeStamp)-System.currentTimeMillis()/1000);
            value = value>0?value:0;
            qrcodeVipName.setText("紫钻会员置顶中.剩余:" +value+"秒");
        }else {
            qrcodeVipName.setText("紫钻会员置顶中");
        }



        vipName.setText("紫钻会员");
    }

    /*
 * 毫秒转化时分秒毫秒
 */
    public  String formatTime(Long ms) {
        ms = (ms*1000)-System.currentTimeMillis();
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if(day > 0) {
            sb.append(day+"天");
        }
        if(hour > 0) {
            sb.append(hour+"小时");
        }
        if(minute > 0) {
            sb.append(minute+"分");
        }
        if(second > 0) {
            sb.append(second+"秒");
        }
//        if(milliSecond > 0) {
//            sb.append(milliSecond+"毫秒");
//        }
        return sb.toString();
    }

    private void setVipStyle(){

        vipName.setVisibility(View.VISIBLE);
        vipName.setBackground(context.getResources().getDrawable(R.drawable.shape_red_vip_type));
        vipName.setText("VIP");
        name.setTextColor(Color.parseColor("#e2333c"));
        done.setBackground(context.getResources().getDrawable(R.drawable.shape_red_angle));
    }
    private void setHongZuanStyle(){
        qrcodeVipName.setVisibility(View.VISIBLE);
        qrcodeVipName.setBackground(context.getResources().getDrawable(R.drawable.shape_vip_name_red));
        qrcodeVipName.setText("红钻会员暴机中");
//        name.setTextColor(Color.parseColor("#e2333c"));
//        done.setBackground(context.getResources().getDrawable(R.drawable.shape_blue_angle));


    }

    private void setCommStyle(){
        vipName.setVisibility(View.INVISIBLE);
        qrcodeVipName.setVisibility(View.INVISIBLE);
        name.setTextColor(Color.BLACK);
        done.setBackground(context.getResources().getDrawable(R.drawable.shape_blue_angle));

    }
    public  int dp2px(float dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
    public interface OnAddFriendClick{
        void onClick(Object data);
    }
}
