package com.hzkj.wdk.model;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by chenweiqi on 2017/4/28.
 */

public class QRCodeListItemBean {

    public List<ItemBean> zizhuan;
    public List<ItemBean> vip;
    public List<ItemBean> common;


    public static class ItemBean{
        public String id;//条目id
        public String userImg;//用户头像
        public String description;// true string 描述
        public String nickname;// true string 昵称
        public String userId;// true string 昵称
        public String popularity;// true string 人气
        public String special;// true string 是否是红砖，1代表是，0代表不是
        public String timeStamp;// true number 会员到期时间戳
        public boolean isZiZuan;
        public boolean isVip;
        public String mobile;
        public String qrcode;
        public String sex;
        public String userArea;
        public String wechatName;
    }
}


