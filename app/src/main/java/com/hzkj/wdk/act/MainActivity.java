package com.hzkj.wdk.act;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzkj.wdk.fra.Home3Fra;
import com.hzkj.wdk.fra.HomeFra;
import com.hzkj.wdk.fra.QRCodePlFFragment;
import com.hzkj.wdk.http.IResponseCallback;
import com.hzkj.wdk.http.SimpleProtocol;
import com.hzkj.wdk.model.ErrorModel;
import com.hzkj.wdk.model.UserInfo;
import com.hzkj.wdk.update.ApkUpdateUtils;
import com.hzkj.wdk.utils.LoadingD;
import com.hzkj.wdk.utils.SharePreferenceUtil;
import com.hzkj.wdk.utils.Utils;
import com.hzkj.wdk.MyApplication;
import com.hzkj.wdk.fra.BaseFragment;
import com.hzkj.wdk.fra.Home2Fra;
import com.hzkj.wdk.R;
import com.hzkj.wdk.model.MobileModel;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.UtilsLog;
import com.hzkj.wdk.widget.CustomDialog;
import com.hzkj.wdk.widget.PWLogin;
import com.hzkj.wdk.widget.ShareDialog;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.tencent.android.tpush.XGBasicPushNotificationBuilder;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.service.XGPushService;
import com.tencent.open.utils.Util;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements JiaFenConstants,View.OnClickListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    //private PlaceholderFragment pf;
    private static List<MobileModel> listData=new ArrayList<MobileModel>();
    private ListView listview;
    static SQLiteDatabase sqLite;
    private List<BaseFragment> listF=new ArrayList<>();
    private HomeFra homeFra=new HomeFra();
    private Home2Fra home2Fra=new Home2Fra();
    private Home3Fra home3Fra=new Home3Fra();
    private FragmentManager manager;
    private String currentFraName;
    private BaseFragment currentFragment;// 当前fragment
    private FrameLayout tabs_container;
    public SharePreferenceUtil spu;
    private TextView tv_home1,tv_home2,tv_home3,tv_home4;
    private Drawable home1,home2,home3,home1_1,home2_1,home3_1;
    private PWLogin pwLogin;
    private IResponseCallback<String> cb;
    private SimpleProtocol pro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XGPushConfig.enableDebug(this, true);
        setContentView(R.layout.activity_main);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Utils.deviceWidth = dm.widthPixels;
        Utils.deviceHeight = dm.heightPixels;
        Utils.deviceDensity = dm.density;
        Context context = getApplicationContext();
//        XGPushManager.registerPush(this);
        XGBasicPushNotificationBuilder builder = new XGBasicPushNotificationBuilder();
        builder.setFlags(Notification.FLAG_AUTO_CANCEL);
        XGPushManager.setPushNotificationBuilder(this, 2, builder);
        // 2.36（不包括）之前的版本需要调用以下2行代码
        Intent service = new Intent(context, XGPushService.class);
        context.startService(service);

        spu=new SharePreferenceUtil(this);
//        spu.setToken("a12ccc0ede72afb34464aada6a28659b");

        new ImportTask().execute();
        manager = getSupportFragmentManager();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        tabs_container=(FrameLayout)findViewById(R.id.tabs_container);
        home2Fra=new Home2Fra();
        home3Fra=new Home3Fra();
        listF.add(homeFra);
        //listF.add(home2Fra);
        listF.add(home3Fra);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tv_home1=(TextView)findViewById(R.id.tv_home1);
        tv_home1.setOnClickListener(this);
        tv_home2=(TextView)findViewById(R.id.tv_home2);
        tv_home2.setOnClickListener(this);
        tv_home3=(TextView)findViewById(R.id.tv_home3);
        tv_home3.setOnClickListener(this);
        tv_home4 = (TextView) findViewById(R.id.tv_home4);
        tv_home4.setOnClickListener(this);
        home1=getResources().getDrawable(R.drawable.home_zidong);
        home2=getResources().getDrawable(R.drawable.home_beidong);
        home3=getResources().getDrawable(R.drawable.home_zhinan);
        home1_1=getResources().getDrawable(R.drawable.home_zidong1);
        home2_1=getResources().getDrawable(R.drawable.home_beidong1);
        home3_1=getResources().getDrawable(R.drawable.home_zhinan1);
        pwLogin=new PWLogin(this,(MyApplication)(getApplication()));
        initData();
        getConfigData();
        Utils.initV(this);

        reloadNameTv4();
    }

    @Override
    protected void onResume() {
        super.onResume();
        XGPushClickedResult result = XGPushManager.onActivityStarted(this);
        if (result != null) {
            String customContent = result.getCustomContent();
           // pushIntent(customContent);
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        XGPushManager.onActivityStoped(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }

    @Override
    public void onClick(View view) {
        if(TextUtils.isEmpty(spu.getLoginName())) {
            pwLogin.showPW(mViewPager);
            return;
        }
        listF.clear();
        home2Fra=new Home2Fra();
        home3Fra=new Home3Fra();
        listF.add(homeFra);
        //listF.add(home2Fra);
        listF.add(home3Fra);

        if(view==tv_home1){
            mViewPager.setCurrentItem(0);
            tv_home1.setCompoundDrawablesRelativeWithIntrinsicBounds(null, home1, null, null);
            tv_home2.setCompoundDrawablesRelativeWithIntrinsicBounds(null, home2_1, null, null);
            tv_home3.setCompoundDrawablesRelativeWithIntrinsicBounds(null, home3_1, null, null);
            tv_home1.setTextColor(getResources().getColor(R.color.red));
            tv_home2.setTextColor(getResources().getColor(R.color.black3));
            tv_home3.setTextColor(getResources().getColor(R.color.black3));
        }else if(view==tv_home2){
            mViewPager.setCurrentItem(1);
            tv_home1.setCompoundDrawablesRelativeWithIntrinsicBounds(null, home1_1, null, null);
            tv_home2.setCompoundDrawablesRelativeWithIntrinsicBounds(null, home2, null, null);
            tv_home3.setCompoundDrawablesRelativeWithIntrinsicBounds(null, home3_1, null, null);
            tv_home1.setTextColor(getResources().getColor(R.color.black3));
            tv_home2.setTextColor(getResources().getColor(R.color.red));
            tv_home3.setTextColor(getResources().getColor(R.color.black3));
        }else if(view==tv_home3){
            System.out.println("1");
            mViewPager.setCurrentItem(2);
            tv_home1.setCompoundDrawablesRelativeWithIntrinsicBounds(null, home1_1, null, null);
            tv_home2.setCompoundDrawablesRelativeWithIntrinsicBounds(null, home2_1, null, null);
            tv_home3.setCompoundDrawablesRelativeWithIntrinsicBounds(null, home3, null, null);
            tv_home1.setTextColor(getResources().getColor(R.color.black3));
            tv_home2.setTextColor(getResources().getColor(R.color.black3));
            tv_home3.setTextColor(getResources().getColor(R.color.red));
        }else if (view==tv_home4){
            System.out.println("2");
            changeFragment(new QRCodePlFFragment(),false);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private void initData(){
        pro=new SimpleProtocol(MainActivity.this);
        cb=new IResponseCallback<String>() {
            @Override
            public void onSuccess(String s) {
                try{
                    JSONObject obj=new JSONObject(s);
                    String version=obj.getString("version");
                    String des=obj.getString("des");
                    String size=obj.getString("size");
                    final String ismust=obj.getString("ismust");
                   final String url=obj.getString("url");
                    if(Integer.valueOf(version)> Utils.getVersion(MainActivity.this)){
                        String content="有新的版本可更新";
                        if(ismust.equals("1"))
                            content="当前版本是旧版,必须更新最新版才可用";
                        final CustomDialog cd=new CustomDialog(MainActivity.this,"温馨提示",
                                content,"取消","确定");
                        cd.setCancelOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(ismust.equals("1")){
                                    Utils.toastShow(MainActivity.this,"当前版本是旧版,必须更新最新版才可用");
                                }else{
                                    cd.dismiss();;
                                }
                            }
                        });
                        cd.setConfirmOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cd.dismiss();
                                download(url);
                            }
                        });
                        cd.show();
                    }
                }catch (Exception e){

                }
            }

            @Override
            public void onFailure(ErrorModel errorModel) {

            }

            @Override
            public void onStart() {

            }
        };
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return listF.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return listF.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

//    public  class PlaceholderFragment extends Fragment {
//
//        private String ARG_SECTION_NUMBER = "section_number";
//
//        public  PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//
//            return fragment;
//        }
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//            final ListView lv=(ListView)rootView.findViewById(R.id.listview);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
//            textView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(!myTask.isCancelled()){
//                        myTask.cancel(true);
//                        myTask=null;
//                    }
//                    myTask=new ToastTask();
//                    myTask.execute(lv);
//                }
//            });
//
//            listview=lv;
//            //MyAdapter adapter=new MyAdapter(listData);
//            //lv.setAdapter(adapter);
//            return rootView;
//        }
//    }

    public void backFragment() {
        if (manager != null) {
            manager.popBackStackImmediate();
            currentFraName = getVisibleFragment().getClass().getSimpleName();
        }
    }

    public void changeFragment(BaseFragment fragment, boolean init) {
        currentFraName = fragment.getClass().getSimpleName();
        FragmentTransaction fTransaction = manager.beginTransaction();
        if (!fragment.isAdded()) {
            currentFragment = fragment;
            // FragmentTransaction fTransaction = manager.beginTransaction();
            //fTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            // fTransaction.setCustomAnimations(R.anim.old_in,R.anim.new_out,R.anim.new_in,R.anim.old_out);
            if (!init) {
                fTransaction.addToBackStack(null);
            }
            fTransaction.replace(R.id.tabs_container, fragment);
            fTransaction.commitAllowingStateLoss();
        } else {
            UtilsLog.d("====", "isadded");
        }
    }

    private void clearStack() {
        if (manager.getBackStackEntryCount() > 0) {
            for (int i = 0; i < manager.getBackStackEntryCount(); i++) {
                manager.popBackStack();
            }
        }
    }

    private Fragment getVisibleFragment() {
        List<Fragment> fragments = manager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }

    public void FrameLayoutVisible(boolean b){
        tabs_container.setVisibility(b==true?View.VISIBLE:View.GONE);
    }

    private  class ViewHolder{
        TextView tv_MobileNumber,tv_MobileArea,tv_MobileType,tv_AreaCode,tv_PostCode;

    }

    private class MyAdapter extends BaseAdapter{
        List<MobileModel> listD=new ArrayList<MobileModel>();
        public  MyAdapter(List<MobileModel> list){
            listD=list;
        }
        @Override
        public int getCount() {
            return listD.size();
        }

        @Override
        public Object getItem(int i) {
            return listD.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View v, ViewGroup viewGroup) {
            ViewHolder holder;
            if(v==null){
                holder=new ViewHolder();
                v=View.inflate(getApplicationContext(),R.layout.item_mobile,null);
                holder.tv_MobileNumber=(TextView)v.findViewById(R.id.tv_MobileNumber);
                holder.tv_MobileArea=(TextView)v.findViewById(R.id.tv_MobileArea);
                holder.tv_MobileType=(TextView)v.findViewById(R.id.tv_MobileType);
                holder.tv_AreaCode=(TextView)v.findViewById(R.id.tv_AreaCode);
                holder.tv_PostCode=(TextView)v.findViewById(R.id.tv_PostCode);
                v.setTag(holder);
            }else{
                holder=(ViewHolder)v.getTag();
            }
            MobileModel mm=listD.get(i);
            holder.tv_MobileNumber.setText(""+mm.getMobileNumber());
            holder.tv_MobileArea.setText(""+mm.getMobileArea());
            holder.tv_MobileType.setText(""+mm.getMobileType());
            holder.tv_AreaCode.setText(""+mm.getAreaCode());
            holder.tv_PostCode.setText("" + mm.getPostCode());
            return v;
        }
    }

    private class ImportTask extends AsyncTask<ListView, String, List<MobileModel>>{

        @Override
        protected List<MobileModel> doInBackground(ListView... params) {
            // TODO Auto-generated method stub
            importDB();
            return null;
        }

        @Override
        protected void onPostExecute(List<MobileModel> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
        }
    }

    private void importDB() {
        File file = new File(getFilesDir(), DBNAME);
        if (file.exists() && file.length() > 0) {
            Log.i("import_db", "simida");
        } else {
            AssetManager asset = getAssets();
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = asset.open(DBNAME);
                fos = new FileOutputStream(file);
                int len = 0;
                byte[] buf = new byte[1024];
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 分享
     *
     * @param url
     * @param title
     * @param desc
     * @param imgUrl
     */
    public void openUmengShare(String url, String title, String desc, String imgUrl) {
        if (true) {
            ShareDialog dialog = new ShareDialog(this);
            dialog.title = title;
            dialog.desc = desc;
            dialog.show();
            return;
        }
        //UMImage image = new UMImage(MainActivity.this, "http://www.umeng.com/images/pic/social/integrated_3.png");
        UMImage image = new UMImage(MainActivity.this, R.drawable.ic_launcher);
        //,SHARE_MEDIA.QQ
        new ShareAction(MainActivity.this).setDisplayList(SHARE_MEDIA.QQ,SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QZONE)
                .withText(TextUtils.isEmpty(desc)?"免费下载.免费使用.被动加粉坐等被加为好友！突破单日加粉限制.快速建立5千好友朋友圈！":desc)
                .withTargetUrl("http://weidianke.qb1611.cn/wjb")
                .withTitle(TextUtils.isEmpty(title)?"被动加粉，3天加满5千好友":title)
                .withMedia(image)
                .setCallback(umShareListener)
                .open();

//        new ShareAction(this).setDisplayList(SHARE_MEDIA.SINA,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.WEIXIN_FAVORITE)
//                .withText("来自友盟分享面板")
//                .withMedia(image)
//                .setCallback(umShareListener)
//                        //.withShareBoardDirection(view, Gravity.TOP|Gravity.LEFT)
//                .open();
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            if(platform.name().equals("WEIXIN_FAVORITE")){
                Toast.makeText(MainActivity.this, platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(MainActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(MainActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    //获取版本更新
    private void getConfigData(){
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("r", "api/common/version");
        params.addQueryStringParameter("version", "" +Utils.getVersion(this));
        pro.getData(HttpRequest.HttpMethod.GET, SERVER_URL, params, cb);
    }

    private void showDownloadSetting() {
        String packageName = "com.android.providers.downloads";
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        if (intentAvailable(intent)) {
            startActivity(intent);
        }
    }

    private boolean intentAvailable(Intent intent) {
        PackageManager packageManager = getPackageManager();
        List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private boolean canDownloadState() {
        try {
            int state = this.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");

            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void download(String url) {
        if (!canDownloadState()) {
            Utils.toastShow(this,"下载服务不用,请您启用");
            showDownloadSetting();
            return;
        }

        ApkUpdateUtils.download(this, url, getResources().getString(R.string.app_name));
    }

    @Override
    public void onBackPressed() {
        if(currentFraName.equals(homeFra.getClass().getSimpleName())
                ||currentFraName.equals(home2Fra.getClass().getSimpleName())
                ||currentFraName.equals(home3Fra.getClass().getSimpleName())) {
            super.onBackPressed();
        }else {
            backFragment();
        }
    }

    String R_SUCCESS = "success";
    String R_FAIL = "fail";
    String R_CANCEL = "cancel";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{


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
                Utils.toastShow(this," 支付成功！ ");
                backFragment();
//                } else {
//                // 验签失败
//                }
            }

            // 结果result_data为成功时，去商户后台查询一下再展示成功
        } else if (str.equalsIgnoreCase(R_FAIL)) {
            Utils.toastShow(this," 支付失败！ ");
        } else if (str.equalsIgnoreCase(R_CANCEL)) {
            Utils.toastShow(this," 你已取消了本次订单的支付！ ");
        }
        }catch (Exception e){
        }
    }

    public void backToHome(){
        while (!(currentFraName.equals(homeFra.getClass().getSimpleName())
                ||currentFraName.equals(home2Fra.getClass().getSimpleName())
                ||currentFraName.equals(home3Fra.getClass().getSimpleName()))) {
            backFragment();
        }
        FrameLayoutVisible(false);
    }

    public void changeHomeSel(int index){
        mViewPager.setCurrentItem(index);
    }


    public void reloadNameTv4(){
        tv_home4.setText(spu.getQrCodeBtnName());
    }

    public void reloadNameTV1(){
        tv_home1.setText(spu.getPersonalCenterBtnName());
    }

    public void getUserInfo(final GetUserInfoCallback getUserInfoCallback){
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("r", "api/qrcode/getUserInfo");
        params.addQueryStringParameter("token", "" + spu.getToken());
        SimpleProtocol simpleProtocol = new SimpleProtocol(MainActivity.this);
        simpleProtocol.getData(HttpRequest.HttpMethod.POST, SERVER_URL, params, new IResponseCallback<String>() {
            @Override
            public void onSuccess(String s) {
                LoadingD.hideDialog();
                System.out.println(s);
                UserInfo baseJson = new Gson().fromJson(s, new TypeToken<UserInfo>() {
                }.getType());
//                userInfo = baseJson.result;


                spu.setAvatar(baseJson.userImg);
                spu.setNickName(baseJson.nickname);
                spu.setShareLink(baseJson.share_link);

                if (getUserInfoCallback!=null)
                    getUserInfoCallback.onSuccess();
            }

            @Override
            public void onFailure(ErrorModel errorModel) {
                LoadingD.hideDialog();
                System.out.println(errorModel.getMsg());
            }

            @Override
            public void onStart() {
                LoadingD.hideDialog();
                LoadingD.showDialog(MainActivity.this);
            }
        });
    }

    public interface GetUserInfoCallback{
        void onSuccess();
    }
}
