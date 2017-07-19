package com.hzkj.wdk.fra;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzkj.wdk.R;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.http.SimpleResponseCallback;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.MemberServiceBean;
import com.hzkj.wdk.utils.SharePreferenceUtil;
import com.hzkj.wdk.utils.Utils;
import com.hzkj.wdk.widget.adapter.VipServiceAdapter;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.tencent.weibo.sdk.android.network.HttpReq;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenweiqi on 2017/4/28.
 */
//会员服务
public class QRCodeVIPFragment extends BaseFragment {

    View rootView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    TwinklingRefreshLayout refreshLayout;

    SharePreferenceUtil spu;

    VipServiceAdapter adapter;
    boolean refreshTop=true;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_qrcode_vip, null);
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

        adapter.setType(1);
        adapter.setOnClickListener(new VipServiceAdapter.OnViewHolderClickListener() {
            @Override
            public void onClick(Object data, int position) {
                FraPaymentType fraPaymentType = new FraPaymentType();
                Bundle b=new Bundle();
                b.putString("id", ((MemberServiceBean) data).id);
                b.putString("content",((MemberServiceBean) data).serviceName);
                b.putString("money",""+((MemberServiceBean) data).price);
                b.putBoolean("luckcode",false);
                fraPaymentType.setArguments(b);
                fraPaymentType.isZuanVip = true;
                mActivity.changeFragment(fraPaymentType,false);
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
                refreshTop = true;
                getData();

            }
        });

        getData();
    }

    public void getData(){
        RequestParams params = new RequestParams();

        params.addQueryStringParameter("r","api/qrcode/getService");
        params.addQueryStringParameter("token",""+spu.getToken());
        params.addQueryStringParameter("payType","1");



        new SimpleProtocol(mActivity).getData(HttpRequest.HttpMethod.GET, SERVER_URL, params, new SimpleResponseCallback(mActivity) {
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
}
