package com.hzkj.wdk.widget;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.hzkj.wdk.model.CityModel;
import com.hzkj.wdk.model.ProvinceModel;
import com.hzkj.wdk.widget.adapter.ArrayWheelAdapter;
import com.hzkj.wdk.R;
import com.hzkj.wdk.model.DistrictModel;
import com.hzkj.wdk.utils.XmlParserHandler;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * 选择城市
 * 
 * @author berton
 * 
 */
public class PWSelectCity extends LinearLayout implements View.OnClickListener,OnWheelChangedListener{

	private Context c;
	private Fragment fra;
	private ImageView iv;
	private PopupWindow aPopuwindow;
	private View showView;
	private WheelView mViewProvince;
	private WheelView mViewCity;
	private WheelView mViewDistrict;

	public interface OKClick{
		public void okClick(String area,String city,String code);
	}
	private OKClick okClick;

	public PWSelectCity(Context context) {
		super(context);
		c = context;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	public void cancle(){
		if(aPopuwindow!=null)
		aPopuwindow.dismiss();
	}

	//从底部浮出
	public void ShowPop(View v){
		showView=View.inflate(c, R.layout.pw_select_city, null);

		mViewProvince = (WheelView)showView.findViewById(R.id.id_province);
		mViewCity = (WheelView) showView.findViewById(R.id.id_city);
		mViewDistrict = (WheelView) showView.findViewById(R.id.id_district);
		mViewProvince.addChangingListener(this);
		mViewCity.addChangingListener(this);
		mViewDistrict.addChangingListener(this);
		setUpData();

		aPopuwindow=new PopupWindow(showView,
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		aPopuwindow.setAnimationStyle(R.style.popwin_anim_style);
		// 使其聚集 ，要想监听菜单里控件的事件就必须要调用此方法
		aPopuwindow.setFocusable(true);
				// 设置允许在外点击消失
		aPopuwindow.setOutsideTouchable(true);
				// 设置背景，这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		aPopuwindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.transparent));
				// 软键盘不会挡着popupwindow
		aPopuwindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
				// 设置菜单显示的位置
		aPopuwindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
		okClick.okClick(mCurrentProviceName, mCurrentCityName, mCurrentZipCode);
	}
	
	public void setOkClick(OKClick click){
		this.okClick=click;
	}

	private void setUpData() {
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(c, mProvinceDatas));
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		mViewDistrict.setVisibleItems(7);
		updateCities();
		updateAreas();
	}


	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		// TODO Auto-generated method stub
		if (wheel == mViewProvince) {
			updateCities();

		} else if (wheel == mViewCity) {
			updateAreas();

		} else if (wheel == mViewDistrict) {
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
		}
		okClick.okClick(mCurrentProviceName,mCurrentCityName,mCurrentZipCode);
	}

	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityName);

		if (areas == null) {
			areas = new String[] { "" };
		}
		mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(c, areas));
		mViewDistrict.setCurrentItem(0);
		//////////////////////
		mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
		mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
		//////////////////////
	}

	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(c, cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}

	protected String[] mProvinceDatas;
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

	protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

	protected String mCurrentProviceName="";
	protected String mCurrentCityName="";
	protected String mCurrentDistrictName ="";

	protected String mCurrentZipCode ="";


	protected void initProvinceDatas()
	{
		List<ProvinceModel> provinceList = null;
		AssetManager asset = c.getAssets();
		try {
			InputStream input = asset.open("province_data.xml");
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			provinceList = handler.getDataList();
			if (provinceList!= null && !provinceList.isEmpty()) {
				mCurrentProviceName = provinceList.get(0).getName();
				List<CityModel> cityList = provinceList.get(0).getCityList();
				if (cityList!= null && !cityList.isEmpty()) {
					mCurrentCityName = cityList.get(0).getName();
					List<DistrictModel> districtList = cityList.get(0).getDistrictList();
					mCurrentDistrictName = districtList.get(0).getName();
					mCurrentZipCode = districtList.get(0).getZipcode();
				}
			}
			//*/
			mProvinceDatas = new String[provinceList.size()];
			for (int i=0; i< provinceList.size(); i++) {
				mProvinceDatas[i] = provinceList.get(i).getName();
				List<CityModel> cityList = provinceList.get(i).getCityList();
				String[] cityNames = new String[cityList.size()];
				for (int j=0; j< cityList.size(); j++) {
					cityNames[j] = cityList.get(j).getName();
					List<DistrictModel> districtList = cityList.get(j).getDistrictList();
					String[] distrinctNameArray = new String[districtList.size()];
					DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
					for (int k=0; k<districtList.size(); k++) {
						DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
						mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
						distrinctArray[k] = districtModel;
						distrinctNameArray[k] = districtModel.getName();
					}
					mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
				}
				mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {

		}
	}

}
