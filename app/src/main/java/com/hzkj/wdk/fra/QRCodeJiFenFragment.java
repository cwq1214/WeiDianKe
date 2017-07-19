package com.hzkj.wdk.fra;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzkj.wdk.R;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.http.SimpleResponseCallback;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.MemberServiceBean;
import com.hzkj.wdk.model.UserInfo;
import com.hzkj.wdk.utils.SharePreferenceUtil;
import com.hzkj.wdk.utils.Utils;
import com.hzkj.wdk.widget.adapter.VipServiceAdapter;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenweiqi on 2017/4/28.
 */
//积分商城
public class QRCodeJiFenFragment extends BaseFragment {

    View rootView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    TwinklingRefreshLayout refreshLayout;
    @BindView(R.id.integral)
    TextView integral;

    SharePreferenceUtil spu;
    VipServiceAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_qrcode_jifen, null);
        mActivity.FrameLayoutVisible(true);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spu=new SharePreferenceUtil(mActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter = new VipServiceAdapter());
        adapter.setType(2);
        adapter.setOnClickListener(new VipServiceAdapter.OnViewHolderClickListener() {
            @Override
            public void onClick(final Object data, int position) {
                new AlertDialog.Builder(mActivity).setMessage("确定要兑换"+ ((MemberServiceBean) data).serviceName+"吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        submitData(((MemberServiceBean) data).id);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

            }
        });

        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                refreshLayout.finishLoadmore();
            }

            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                getData();
            }
        });
        getUserScore();
        getData();
    }

    public void getUserScore(){
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("token", "" + spu.getToken());
        params.addQueryStringParameter("r", "api/qrcode/getUserInfo");

        new SimpleProtocol(getContext()).getData(HttpRequest.HttpMethod.GET,SERVER_URL,params,new SimpleResponseCallback(mActivity){
            @Override
            public void onSuccess(String o) {
                super.onSuccess(o);
                UserInfo userInfo = new Gson().fromJson(o,new TypeToken<UserInfo>(){}.getType());
                spu.setScore(userInfo.score);
                integral.setText("剩余积分："+userInfo.score+"积分");
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                super.onFailure(errorModel);
            }
        });
    };

    public void getData(){
        RequestParams params = new RequestParams();

        params.addQueryStringParameter("token", "" + spu.getToken());
        params.addQueryStringParameter("r", "api/qrcode/getService");
        params.addQueryStringParameter("payType", "2");
        new SimpleProtocol(getContext()).getData(HttpRequest.HttpMethod.GET,SERVER_URL,params,new SimpleResponseCallback(mActivity){
            @Override
            public void onSuccess(String o) {
                super.onSuccess(o);
                refreshLayout.finishLoadmore();
                refreshLayout.finishRefreshing();

                List<MemberServiceBean> list = new Gson().fromJson(o,new TypeToken<List<MemberServiceBean>>(){}.getType());

                adapter.setList(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                super.onFailure(errorModel);
                refreshLayout.finishLoadmore();
                refreshLayout.finishRefreshing();
            }
        });
    }

    public void submitData(String id){
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("r","api/user/openQrcodeMember");
        params.addQueryStringParameter("serviceId",id);
        params.addQueryStringParameter("token",mActivity.spu.getToken());
        new SimpleProtocol(getContext()).getData(HttpRequest.HttpMethod.GET,SERVER_URL,params,new SimpleResponseCallback(mActivity){
            @Override
            public void onSuccess(String o) {
                super.onSuccess(o);
                try {
                    JSONObject jsonObject = new JSONObject(o);
                    if (!TextUtils.isEmpty(jsonObject.getString("pay_value"))){
                        Utils.toastShow(mActivity,"兑换成功");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    if (!TextUtils.isEmpty(o))
//                        Utils.toastShow(mActivity,o);
                }

                getUserScore();
                getData();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                super.onFailure(errorModel);
                Utils.toastShow(mActivity,errorModel.getMsg());
                getUserScore();
                getData();
            }
        });
    }


}
