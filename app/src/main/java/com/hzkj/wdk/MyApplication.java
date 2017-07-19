	package com.hzkj.wdk;

	import android.app.Application;
    import android.os.Vibrator;

    import com.umeng.socialize.PlatformConfig;

    public class MyApplication extends Application {
        public Vibrator mVibrator;
        @Override
        public void onCreate() {
            super.onCreate();

            //处理系统异常（崩溃后不显示错误弹出框）
    		//CrashHandler crashHandler = CrashHandler.getInstance();
    		//crashHandler.init(getApplicationContext());
        }

        //各个平台的配置，建议放在全局Application或者程序入口
        {
            //v信 wx12342956d1cab4f9,a5ae111de7d9ea137e88a5e02c07c94d
            PlatformConfig.setWeixin("wxfc2fdeb0868877ac", "3da019df02117fa684be6227123d744c");
            //PlatformConfig.setWeixin("wxf349b21758ae2638", "0f4124d6b9dbbb56b1d93f7917f607a3");
            //豆瓣RENREN平台目前只能在服务器端配置
            //新浪微博
            //PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad");
            //易信
           // PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
            //PlatformConfig.setQQZone("101351568", "fe41200dae3a2e9556f74db8c6e304ec");
            //PlatformConfig.setQQZone("1105691964", "DcHYvuTy87C1Cr4T");
            PlatformConfig.setQQZone("1105846348","hgEYRZrXoPaVnl06");
            //PlatformConfig.setTencentWB("101351568", "fe41200dae3a2e9556f74db8c6e304ec");
//            PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi", "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
            //PlatformConfig.setAlipay("2015111700822536");
//            PlatformConfig.setLaiwang("laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
//            PlatformConfig.setPinterest("1439206");

        }

    }
