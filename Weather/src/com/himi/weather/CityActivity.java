package com.himi.weather;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.himi.weather.adapter.CityListAdapter;
import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;
/**
 * 城市选择页面
 * @author hebao
 *
 */
public class CityActivity extends Activity {
	
	private ListView lv_city;
	private List<String> list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city);
		initViews();
		getCities();
	}
	
	private void initViews() {
		findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
		lv_city = (ListView) findViewById(R.id.lv_city);
	}

	/**
	 * 获取城市列表信息
	 */
	
	private void getCities() {
		Parameters params = new Parameters();
		params.add("dtype", "json");
		JuheData.executeWithAPI(this, 39, "http://v.juhe.cn/weather/citys",
				JuheData.GET, params, new DataCallBack()  {
			
					@Override
					public void onSuccess(int statusCode, String responseString) {
						//System.out.println(responseString);
						try {
							JSONObject json = new JSONObject(responseString);
							int code = json.getInt("resultcode");
							int error_code = json.getInt("error_code");
							if (error_code == 0 && code == 200) {// 获取数据成功,解析数据
								JSONArray resultArray = json.getJSONArray("result");
								list = new ArrayList<String>();
								Set<String> citySet = new HashSet<String>();
								
								for(int i=0; i<resultArray.length(); i++) {
									String city = resultArray.getJSONObject(i)
											.getString("city");
									citySet.add(city);	
								}
								list.addAll(citySet);
								CityListAdapter adapter = new CityListAdapter(CityActivity.this, 
										list);
								lv_city.setAdapter(adapter);
								/**
								 * 城市名item监听事件，将用户点击的城市名传递给WeatherActivity
								 * 让WeatherActivity完成刷新显示
								 */
								lv_city.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {
										Intent intent = new Intent();
										intent.putExtra("city", list.get(position));
										setResult(1,intent);
										finish();
										
									}
								});;
							}
							
						} catch (JSONException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int arg0, String arg1, Throwable arg2) {
						// TODO 自动生成的方法存根
						
					}

					@Override
					public void onFinish() {
						// TODO 自动生成的方法存根
						
					}

			
		});
	}
}
