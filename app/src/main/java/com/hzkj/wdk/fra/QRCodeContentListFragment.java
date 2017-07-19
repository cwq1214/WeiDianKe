package com.hzkj.wdk.fra;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzkj.wdk.R;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.http.SimpleResponseCallback;
import com.hzkj.wdk.listener.ViewHolderClickListener;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.QRCodeListItemBean;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.SharePreferenceUtil;
import com.hzkj.wdk.utils.Utils;
import com.hzkj.wdk.widget.AddQRCodeDialog;
import com.hzkj.wdk.widget.QRCodeListViewHolder;
import com.hzkj.wdk.widget.adapter.QRCodeListAdapter;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chenweiqi on 2017/4/28.
 */

public class QRCodeContentListFragment extends BaseFragment {

    public static int TYPE_PERSON=0;
    public static int TYPE_GROUP=1;
    public static int TYPE_SUBSCRIPTION=2;

    @BindView(R.id.sendQRCode)
    TextView sendQRCode;
    @BindView(R.id.getTopService)
    TextView getTopService;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    TwinklingRefreshLayout refreshLayout;

    boolean startTimer = true;
    GuaJiBaoFenFragment parent;
    SharePreferenceUtil spu;
    QRCodeListAdapter adapter;
    int TYPE = -1;
    View rootView;
    Handler handler = new Handler();
    boolean refreshTop = true;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_qrcode_list, null);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.sendQRCode)
    public void onSendQRCodeClick(){
        ReleaseQRCodeFragment releaseQRCodeFragment = new ReleaseQRCodeFragment();
        releaseQRCodeFragment.startPageIndex=TYPE;
        mActivity.changeFragment(releaseQRCodeFragment,false);
    }


    Runnable timerRunnable;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spu=new SharePreferenceUtil(mActivity);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter = new QRCodeListAdapter());
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (startTimer)
                    adapter.notifyDataSetChanged();
                handler.postDelayed(this,500);
            }
        };

        handler.postDelayed(timerRunnable ,500);

        adapter.setOnClickListener(new ViewHolderClickListener() {
            @Override
            public void onClick(Object data, int position) {
                QRCodeCardDetailFragment fragment = new QRCodeCardDetailFragment();
                fragment.bean = (QRCodeListItemBean.ItemBean) data;
                fragment.type = TYPE;
                mActivity.changeFragment(fragment,false);
            }
        });



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    startTimer = true;
                }else {
                    startTimer = false;
                }
            }
        });

        adapter.setOnAddFriendClick(new QRCodeListViewHolder.OnAddFriendClick() {
            @Override
            public void onClick(Object data) {
                final QRCodeListItemBean.ItemBean itemBean = ((QRCodeListItemBean.ItemBean) data);
                addFriendClick(itemBean.userId);

                final AddQRCodeDialog d = new AddQRCodeDialog(mActivity);
                d.canAddToContacts(!TextUtils.isEmpty(itemBean.sex)&&!TextUtils.isEmpty(itemBean.mobile));
                d.setOnAddClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(itemBean.mobile)){
                            return;
                        }
                        Utils.AddContacts(mActivity,itemBean.mobile);
                        Toast.makeText(getContext(),"已导入",Toast.LENGTH_SHORT).show();
                        d.dismiss();
                    }
                });
                d.setOnSaveClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final String path = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM+File.separator+"Camera"+File.separator;
                        //获取内部存储状态
                        String state = Environment.getExternalStorageState();
                        //如果状态不是mounted，无法读写
                        if (TextUtils.isEmpty(itemBean.qrcode)){
                            return;
                        }

                        if (!state.equals(Environment.MEDIA_MOUNTED)) {
                            Toast.makeText(getContext(),"无法读写",Toast.LENGTH_SHORT).show();
                            return;
                        }
                            Utils.toastShow(getContext(),"开始下载图片");
                            Glide.with(getContext()).load(itemBean.qrcode).asBitmap().into(new SimpleTarget<Bitmap>(500,500) {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    try {

                                        File file = new File(path +"/"+ itemBean.userId+"_code" + ".png");

                                    if (!file.exists()){
                                        file.createNewFile();
                                    }
                                    FileOutputStream out = new FileOutputStream(file);
                                        resource.compress(Bitmap.CompressFormat.PNG, 90, out);
                                    out.flush();
                                    out.close();
                                    Utils.toastShow(getContext(),"文件保存路径："+path + itemBean.userId+"_code" + ".png");
                                        mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Utils.toastShow(getContext(),"图片保存失败");
                                    return;
                                }
                                    d.dismiss();
                                }

                            });

                    }
                });

                d.show();
            }
        });
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onFinishLoadMore() {
                super.onFinishLoadMore();
            }

            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                refreshTop = true;
                getData();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                refreshTop = false;
                getData();
            }

            @Override
            public void onFinishRefresh() {
                super.onFinishRefresh();

            }
        });


        getData();

    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(timerRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timerRunnable);
    }

    @OnClick(R.id.getTopService)
    public void onGetTopServiceClick(){
        parent.parent.viewPager.setCurrentItem(1);
    }



    public void setType(int type){
        this.TYPE = type;
    }

    public void getData(){
        RequestParams params = new RequestParams();
        if (refreshTop){
            params.addQueryStringParameter("r", "api/qrcode/getInfo");
        }else {
            params.addQueryStringParameter("r", "api/qrcode/getInfoPage");
            List<QRCodeListItemBean.ItemBean> d = adapter.getQrCodeListItemBeen();
            if (d.size()!=0) {
                params.addQueryStringParameter("id", d.get(d.size() - 1).id);
            }
        }

        if (TYPE == TYPE_PERSON) {
            params.addQueryStringParameter("type", "1");
        }else if (TYPE == TYPE_GROUP){
            params.addQueryStringParameter("type", "2");
        }else if (TYPE == TYPE_SUBSCRIPTION){
            params.addQueryStringParameter("type", "3");
        }
        params.addQueryStringParameter("token", "" + spu.getToken());

        startTimer = true;
        SimpleProtocol simpleProtocol = new SimpleProtocol(getContext());
        simpleProtocol.getData(HttpRequest.HttpMethod.POST, SERVER_URL, params, new SimpleResponseCallback(mActivity){
            @Override
            public void onSuccess(String o) {
                super.onSuccess(o);
                System.out.println(o);
                try {

                    List list = new ArrayList();

                    if (refreshTop){
                        QRCodeListItemBean bean = new Gson().fromJson(o,new TypeToken<QRCodeListItemBean>(){}.getType());
                        if (bean.zizhuan!=null) {
                            for (QRCodeListItemBean.ItemBean zizuanItem:
                                 bean.zizhuan) {
                                zizuanItem.isZiZuan = true;
                            }

                            list.addAll(bean.zizhuan);

                        }
                        if (bean.vip!=null) {
                            for (QRCodeListItemBean.ItemBean vip:
                                    bean.vip) {
                                vip.isVip = true;
                            }
                            list.addAll(bean.vip);
                        }
                        if (bean.common!=null)
                            list.addAll(bean.common);
                        adapter.setQrCodeListItemBeen(list);
                    }else {
                        List<QRCodeListItemBean.ItemBean> bean = new Gson().fromJson(o,new TypeToken<List<QRCodeListItemBean.ItemBean>>(){}.getType());


                        list.addAll(bean);
                        adapter.getQrCodeListItemBeen().addAll(list);
                    }
                    adapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }

                refreshLayout.finishLoadmore();
                refreshLayout.finishRefreshing();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                super.onFailure(errorModel);
                refreshLayout.finishLoadmore();
                refreshLayout.finishRefreshing();
            }


        });
    }
    public void addFriendClick(String friendId){
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("r","api/qrcode/addFans");
        params.addQueryStringParameter("token",mActivity.spu.getToken());
        params.addQueryStringParameter("mainId",friendId);
        params.addQueryStringParameter("fansType", String.valueOf(Integer.valueOf(TYPE)+1));
        new SimpleProtocol(getContext()).getData(HttpRequest.HttpMethod.GET,SERVER_URL,params,new SimpleResponseCallback(mActivity){

        });
    }
}

