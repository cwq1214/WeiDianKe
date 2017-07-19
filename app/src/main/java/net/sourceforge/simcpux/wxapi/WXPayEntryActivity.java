package net.sourceforge.simcpux.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.hzkj.wdk.R;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.Utils;
import com.hzkj.wdk.utils.UtilsLog;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

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

		if (resp.errCode==0){
			Utils.toastShow(getApplicationContext(),"支付成功");
		}else if (resp.errCode == -1 ){
			Utils.toastShow(getApplicationContext(),"发生错误");
		}else if (resp.errCode == -2){
			Utils.toastShow(getApplicationContext(),"取消支付");
		}

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			UtilsLog.d("====","==wxpay=="+resp.errCode);
			Intent mIntent = new Intent(JiaFenConstants.GET_DATA);
			WXPayEntryActivity.this.sendBroadcast(mIntent);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage("v信支付结果："+String.valueOf(resp.errCode));
			builder.show();
		}
		
	}
}