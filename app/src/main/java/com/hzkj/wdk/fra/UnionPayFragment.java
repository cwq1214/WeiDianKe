package com.hzkj.wdk.fra;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzkj.wdk.R;
import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.utils.Utils;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chenweiqi on 2017/5/9.
 */
//银联支付
public class UnionPayFragment extends BaseFragment {

    View rootView;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head)
    RelativeLayout head;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.product_subject)
    TextView productSubject;
    @BindView(R.id.product_price)
    TextView productPrice;
    @BindView(R.id.pay)
    Button pay;


    // “00” – 银联正式环境
    // “01” – 银联测试环境，该环境中不发生真实交易
    String serverMode = "00";
    private String paySn = "", name = "";
    private float price = 0;
    private int goodsNum = 1;
    private boolean luckNotify = false;
    private String tn;
    private StringBuffer sb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_union_pay, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        productPrice.setText("" + price);
        productSubject.setText("" + name + (goodsNum > 1 ? "等" + goodsNum + "件商品" : ""));
    }

    @OnClick(R.id.back)
    public void onBackClick() {
        mActivity.backFragment();
    }

    @OnClick(R.id.pay)
    public void onPayClick() {
        UPPayAssistEx.startPay(getContext(), null, null, tn, serverMode);
    }

    public void setData(String sname, String sprice, String spaysn, boolean luck, String tn) {
        name = sname;
        price = Float.parseFloat(sprice);
        paySn = spaysn;
        sb = new StringBuffer();
        luckNotify = luck;
        this.tn = tn;
    }

    public void pay() {
        System.out.println("pay");
        UPPayAssistEx.startPay(getContext(), null, null, tn, serverMode);
    }

    String R_SUCCESS = "success";
    String R_FAIL = "fail";
    String R_CANCEL = "cancel";
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("result");
        if (data == null) {
            return;
        }
        String msg = "";
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase(R_SUCCESS)) {

            // 如果想对结果数据验签，可使用下面这段代码，
            // 但建议不验签，直接去商户后台查询交易结果
            // result_data结构见c）result_data参数说明
            if (data.hasExtra("result_data")) {
//                String sign = data.getExtras().getString("result_data");
                // 此处的verify建议送去商户后台做验签
                // 如要放在手机端验，则代码必须支持更新证书
//                if (verify(sign)) {
                    //验签成功，显示支付结果
                    Utils.toastShow(mActivity," 支付成功！ ");

//                } else {
//                // 验签失败
//                }
            }

            // 结果result_data为成功时，去商户后台查询一下再展示成功
        } else if (str.equalsIgnoreCase(R_FAIL)) {
            Utils.toastShow(mActivity," 支付失败！ ");
        } else if (str.equalsIgnoreCase(R_CANCEL)) {
            Utils.toastShow(mActivity," 你已取消了本次订单的支付！ ");
        }

    }
}
