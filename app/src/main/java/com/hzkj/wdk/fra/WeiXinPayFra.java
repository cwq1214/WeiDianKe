package com.hzkj.wdk.fra;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkj.wdk.utils.WXMD5;
import com.hzkj.wdk.R;
import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.Utils;
import com.hzkj.wdk.utils.UtilsLog;
import com.hzkj.wdk.utils.WX_Util;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * v信支付
 * @author howie
 *
 */
public class WeiXinPayFra extends BaseFragment implements JiaFenConstants,OnClickListener {
	private MainActivity mActivity;
	
	private PayReq req;
	private IWXAPI msgApi;
	//private TextView show;
	private Map<String,String> resultunifiedorder;
	private StringBuffer sb;
	private String paySn="",name="";
	private float price=0;
	private TextView product_subject,product_price;
	private Button pay;
	private ImageView iv_logo,back;
	private boolean luckNotify=false;

	public static String WEIXIN_APPID;
	//final String WEIXIN_APPID="wxf349b21758ae2638";
	public static String WEIXIN_MERCHANT;//v信商户号
	//final String WEIXIN_MERCHANT="1411024602";//商户号
	public static String WEIXIN_API_KEY;
	//final String WEIXIN_API_KEY="E49165D0E6A92491B138722E0FDB3890";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mActivity=(MainActivity)getActivity();
		Bundle b = getArguments();
		name=b.getString("name");
		price=Float.parseFloat(b.getString("price"));
		paySn=b.getString("paySn");
		UtilsLog.d("====", paySn + "==order==" + paySn + "=name="+price);
				//prodectModel.getGoods_name()+"=price="+prodectModel.getGoods_price());
	}

	public void setData(String sname,String sprice,String spaysn,MainActivity act,boolean luck){
		name=sname;
		price=Float.parseFloat(sprice);
		paySn=spaysn;
		mActivity=act;
		msgApi= WXAPIFactory.createWXAPI(mActivity, null);
		req = new PayReq();
		sb=new StringBuffer();
		luckNotify=luck;
	}

	public void pay(){
		GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
		getPrepayId.execute();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.fra_wx_pay, null);
		mActivity.FrameLayoutVisible(true);
		msgApi= WXAPIFactory.createWXAPI(mActivity, null);
		//sendPayReq();
		//show =(TextView)v.findViewById(R.id.editText_prepay_id);
		req = new PayReq();
		sb=new StringBuffer();
		product_subject=(TextView)v.findViewById(R.id.product_subject);
		product_price=(TextView)v.findViewById(R.id.product_price);
		pay=(Button)v.findViewById(R.id.pay);
		pay.setOnClickListener(this);
		product_subject.setText(name);
		product_price.setText(""+price);
		iv_logo=(ImageView)v.findViewById(R.id.iv_logo);
		back=(ImageView)v.findViewById(R.id.back);
		back.setOnClickListener(this);
		//bitmapUtilsBase.display(iv_logo, prodectModel.getUgoods_pic());
//		//生成prepay_id
//		Button payBtn = (Button) v.findViewById(R.id.unifiedorder_btn);
//		payBtn.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
//				getPrepayId.execute();
//			}
//		});
//		//生成签名参数
//		Button appay_pre_btn = (Button) v.findViewById(R.id.appay_pre_btn);
//		appay_pre_btn.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				genPayReq();
//			}
//		});
//		//		调起v信支付
//		Button appayBtn = (Button) v.findViewById(R.id.appay_btn);
//		appayBtn.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				sendPayReq();
//			}
//		});
		
		return v;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==pay){
			GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
			getPrepayId.execute();
		}else if(v==back){
			mActivity.backFragment();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Intent mIntent = new Intent(GET_DATA);
		mActivity.sendBroadcast(mIntent);
	}
	/**
	 生成签名
	 */

	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(WEIXIN_API_KEY);

		String packageSign = WXMD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.d("orion", "----" + packageSign);
		return packageSign;
	}
	
	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(WEIXIN_API_KEY);

		this.sb.append("sign str\n"+sb.toString()+"\n\n");
		String appSign = WXMD5.getMessageDigest(sb.toString().getBytes());
		Log.e("orion", "genAppSign----" + appSign);
		return appSign;
	}
	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<"+params.get(i).getName()+">");
			sb.append(params.get(i).getValue());
			sb.append("</"+params.get(i).getName()+">");
		}
		sb.append("</xml>");

		Log.e("orion", "toXml----" + sb.toString());
		return sb.toString();
	}

	private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String,String>> {

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(mActivity, "提示", "进行中");
		}

		@Override
		protected void onPostExecute(Map<String,String> result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			sb.append("prepay_id\n"+result.get("prepay_id")+"\n\n");
			//show.setText(sb.toString());

			resultunifiedorder=result;
			genPayReq();
			sendPayReq();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Map<String,String> doInBackground(Void... params) {
			String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			String entity = genProductArgs();
			Log.d("orion", "1111----" + entity);
			byte[] buf = WX_Util.httpPost(url, entity);
			String content = new String(buf);
			Log.d("orion", "222----" + content);
			Map<String,String> xml=decodeXml(content);
           
			return xml;
		}
	}

	public Map<String,String> decodeXml(String content) {
		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName=parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if("xml".equals(nodeName)==false){
						//实例化student对象
						xml.put(nodeName,parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("orion", "----" + e.toString());
		}
		return null;
	}

	private String genNonceStr() {
		Random random = new Random();
		return WXMD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	private String genOutTradNo() {
		Random random = new Random();
//		return "COATBAE810"; //订单号写死的话只能支付一次，第二次不能生成订单
		return WXMD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	//
	private String genProductArgs() {
		StringBuffer xml = new StringBuffer();
		try {
			String nonceStr = genNonceStr();

			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid", WEIXIN_APPID));
			packageParams.add(new BasicNameValuePair("body", "(微点客)"+name));//中文提交不了
			packageParams.add(new BasicNameValuePair("mch_id", WEIXIN_MERCHANT));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			if(luckNotify) {
//				packageParams.add(new BasicNameValuePair("notify_url", "http://zfjiafen.heizitech.com/luckywxnotify.html"));
//				packageParams.add(new BasicNameValuePair("notify_url","http://www.dreamwintime.com/zhuanfa_jiafen/luckywxnotify.php"));
				packageParams.add(new BasicNameValuePair("notify_url","http://weike.qb1611.cn/zhuanfa_jiafen/luckywxnotify.php"));
			}
			else {
//				packageParams.add(new BasicNameValuePair("notify_url", "http://zfjiafen.heizitech.com/newwxnotify.html"));
//				packageParams.add(new BasicNameValuePair("notify_url", "http://www.dreamwintime.com/zhuanfa_jiafen/newwxnotify.php"));
				packageParams.add(new BasicNameValuePair("notify_url", "http://weike.qb1611.cn/zhuanfa_jiafen/newwxnotify.php"));
			}
			packageParams.add(new BasicNameValuePair("out_trade_no",paySn));
			packageParams.add(new BasicNameValuePair("spbill_create_ip",""+ Utils.getLocAddress()));
//			packageParams.add(new BasicNameValuePair("total_fee", ""+(prodectModel.getGoods_price().contains(".")?
//					prodectModel.getGoods_price().replace(".", ""):prodectModel.getGoods_price())));//小数点提交不了
			UtilsLog.d("====","price*100==="+price * 100+"=="+((int)(price * 100)));
			packageParams.add(new BasicNameValuePair("total_fee", "" + ((int) (price * 100))));//小数点提交不了
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));
	
			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));
	
			String xmlstring =toXml(packageParams);
	
			return xmlstring;

		} catch (Exception e) {
			//Log.e(TAG, "----genProductArgs fail, ex = " + e.getMessage());
			return null;
		}
	}
	
	private void genPayReq() {
		req.appId = WEIXIN_APPID;
		req.partnerId = WEIXIN_MERCHANT;
		req.prepayId = resultunifiedorder.get("prepay_id");
		req.packageValue = "prepay_id="+resultunifiedorder.get("prepay_id");
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

		req.sign = genAppSign(signParams);

		sb.append("sign\n"+req.sign+"\n\n");

		//show.setText(sb.toString());

		Log.d("orion", "genPayReq----" + signParams.toString());

	}
	private void sendPayReq() {
		msgApi.registerApp(WEIXIN_APPID);
		msgApi.sendReq(req);
	}

	
}
