package com.hzkj.wdk.wxapi;



import com.hzkj.wdk.utils.Utils;
import com.hzkj.wdk.R;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import static com.hzkj.wdk.fra.WeiXinPayFra.WEIXIN_APPID;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler,JiaFenConstants{
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, WEIXIN_APPID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d("====", "onPayFinish, errCode = " + resp.errCode);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			Intent mIntent = new Intent(GET_DATA);
			sendBroadcast(mIntent);
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("提示");
//			builder.setMessage(String.format("v信支付结果：%s", String.valueOf(resp.errCode)));
			//builder.show();
			if(resp.errCode==0) {
				Utils.toastShow(WXPayEntryActivity.this, "支付成功");
			}
			WXPayEntryActivity.this.finish();;
		}
	}
}