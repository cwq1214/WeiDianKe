package com.hzkj.wdk.fra;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.hzkj.wdk.act.MainActivity;
import com.lidroid.xutils.BitmapUtils;
import com.hzkj.wdk.R;
import com.hzkj.wdk.utils.JiaFenConstants;


public class BaseFragment extends Fragment implements JiaFenConstants{
	protected FragmentCallback fragmentCallback;
	/**
	 * 是否需要走线程中的回调
	 */
	protected boolean ifResponsCall=true;

	/**
	 * @category phoneItem 电话图标
	 * @category closeItem 返回图标
	 * @category searchItem 搜索图标
	 * @category addItem 添加图标
	 * @category editItem 编辑图标
	 * @category searchbyItem 筛选
	 * @category choseItem 选择
	 * @category save 保存
	 * @category other 其他
	 * @category select 系统通知选择
	 * @category selectall 系统通知全选
	 * @category selectcancel 系统通知全选取消
	 */
	public static enum MethodType {
		phoneItem, closeItem, searchItem, addItem, editItem, searchbyItem, choseItem, other, save,share,deleteItem,select,selectall,selectcancel,shenqing
	}
	/**
	 * 供外部调用的方法
	 * 
	 * @param object
	 */
	public void outMethod(Object object, MethodType type) {
	};

	public void setFragmentCallback(FragmentCallback callback) {
		this.fragmentCallback = callback;
	}
	
	protected MainActivity mActivity;

	protected BitmapUtils bitmapUtilsBase;
	private TextView tvLoc;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = (MainActivity)activity;
		bitmapUtilsBase=new BitmapUtils(activity,IMAGE_CACHE);
		//bitmapUtilsBase.configDefaultLoadingImage(R.drawable.banner_temp);
		//bitmapUtilsBase.configDefaultLoadFailedImage(R.drawable.banner_temp);
	}
	public void setback(){};


}
