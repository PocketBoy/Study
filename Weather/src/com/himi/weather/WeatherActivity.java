package com.himi.weather;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.himi.weather.bean.FutureWeatherBean;
import com.himi.weather.bean.HoursWeatherBean;
import com.himi.weather.bean.PMBean;
import com.himi.weather.bean.WeatherBean;
import com.himi.weather.service.WeatherService;
import com.himi.weather.service.WeatherService.WeatherServiceBinder;
import com.himi.weather.service.WeatherService.onParserCallBack;
import com.himi.weather.swiperefresh.PullToRefreshBase;
import com.himi.weather.swiperefresh.PullToRefreshBase.OnRefreshListener;
import com.himi.weather.swiperefresh.PullToRefreshScrollView;

public class WeatherActivity extends Activity {
	// PM 2.5 申请应用的App Key
	private static final String App_Key = "65035b89e78ca1976445ff9490f7b150";

	private PullToRefreshScrollView mPullToRefreshScrollView;
	private ScrollView mScrollView;

	private TextView tv_city, // 城市
			tv_release,// 发布时间
			tv_now_weather,// 天气
			tv_today_temp,// 温度
			tv_now_temp,// 当前温度
			tv_aqi,// 空气质量指数
			tv_quality,// 空气质量
			tv_next_three,// 3小时
			tv_next_six,// 6小时
			tv_next_nine,// 9小时
			tv_next_twelve,// 12小时
			tv_next_fifteen,// 15小时
			tv_next_three_temp,// 3小时温度
			tv_next_six_temp,// 6小时温度
			tv_next_nine_temp,// 9小时温度
			tv_next_twelve_temp,// 12小时温度
			tv_next_fifteen_temp,// 15小时温度
			tv_today_temp_a,// 今天温度a
			tv_today_temp_b,// 今天温度b
			tv_tommorrow,// 明天
			tv_tommorrow_temp_a,// 明天温度a
			tv_tommorrow_temp_b,// 明天温度b
			tv_thirdday,// 第三天
			tv_thirdday_temp_a,// 第三天温度a
			tv_thirdday_temp_b,// 第三天温度b
			tv_fourthday,// 第四天
			tv_fourthday_temp_a,// 第四天温度a
			tv_fourthday_temp_b,// 第四天温度b
			tv_humidity,// 湿度
			tv_wind, tv_uv_index,// 紫外线指数
			tv_dressing_index;// 穿衣指数

	private ImageView iv_now_weather,// 现在
			iv_next_three,// 3小时
			iv_next_six,// 6小时
			iv_next_nine,// 9小时
			iv_next_twelve,// 12小时
			iv_next_fifteen,// 15小时
			iv_today_weather,// 今天
			iv_tommorrow_weather,// 明天
			iv_thirdday_weather,// 第三天
			iv_fourthday_weather;// 第四天

	private RelativeLayout rl_city;
	
	/**
	 * 上下文
	 */
	private Context mcontext;
	private WeatherService mService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
//		getCityWeather();
		mcontext = this;
		init();
		initService();
	}
	
	/**
	 * 初始化Service
	 */
	private void initService() {
		Intent intent = new Intent(mcontext, WeatherService.class);
		startService(intent);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}
	
	ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO 自动生成的方法存根
			mService.removeCallBack();
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			//System.out.println("onServiceConnected.....");
			mService = ((WeatherServiceBinder)service).getService();
			mService.setCallBack(new onParserCallBack() {
				
				public void onParserComplete(List<HoursWeatherBean> list, PMBean pmBean,
						WeatherBean weatherBean) {
					mPullToRefreshScrollView.onRefreshComplete();
					//当天每隔3小时天气状况数据填充
					if(list != null && list.size()>=5) {
						setHourViews(list);
					}
					
					//PM2.5数据填充
					if(pmBean != null) {
						setPMViews(pmBean);
					}
					
					//实时天气与未来三天天气状况的数据填充
					if(weatherBean != null) {
						setWeatherViews(weatherBean);
					}
					
				}
			});
			//绑定Service同时，调用方法获取天气状况数据
			mService.getCityWeather();
		}
	};
	
	
	
	
	private void setPMViews(PMBean bean) {
		tv_aqi.setText(bean.getAqi());
		tv_quality.setText(bean.getQuality());

	}

	/**
	 * 当天每个3小时天气状况数据填充
	 * 
	 * @param bean
	 */
	private void setHourViews(List<HoursWeatherBean> list) {

		setHourViews(tv_next_three, iv_next_three, tv_next_three_temp,
				list.get(0));
		setHourViews(tv_next_six, iv_next_six, tv_next_six_temp, list.get(1));
		setHourViews(tv_next_nine, iv_next_nine, tv_next_nine_temp, list.get(2));
		setHourViews(tv_next_twelve, iv_next_twelve, tv_next_twelve_temp,
				list.get(3));
		setHourViews(tv_next_fifteen, iv_next_fifteen, tv_next_fifteen_temp,
				list.get(4));

	}

	private void setHourViews(TextView tv_hour, ImageView iv_weather,
			TextView tv_temp, HoursWeatherBean bean) {
		String prefixStr = null;
		int time = Integer.parseInt(bean.getTime());
		if (time >= 6 && time < 18) {
			prefixStr = "d";
		} else {
			prefixStr = "n";
		}

		tv_hour.setText(bean.getTime());
		iv_weather.setImageResource(getResources().getIdentifier(
				prefixStr + bean.getWeather_id(), "drawable",
				"com.himi.weather"));
		tv_temp.setText(bean.getTemp());
	}

	/**
	 * 实时天气与未来三天天气情况数据填充
	 * 
	 * @param bean
	 */
	private void setWeatherViews(WeatherBean bean) {
		tv_city.setText(bean.getCity());
		tv_release.setText(bean.getRelease());
		tv_now_weather.setText(bean.getWeather_str());
		tv_now_temp.setText(bean.getNow_temp());
		// 温度：-8℃~1℃ ↑↓ °
		String[] tempArr = bean.getTemp().split("~");
		String temp_str_a = tempArr[1].substring(0, tempArr[1].indexOf("℃"));// 最高温度
		String temp_str_b = tempArr[0].substring(0, tempArr[0].indexOf("℃"));// 最低温度
		tv_today_temp.setText("↑ " + temp_str_a + "°  " + "↓" + temp_str_b
				+ "°");
		;
		iv_today_weather.setImageResource(getResources().getIdentifier(
				"d" + bean.getWeather_id(), "drawable", "com.himi.weather"));

		tv_today_temp_a.setText(temp_str_a + "°");// 今天温度a
		tv_today_temp_b.setText(temp_str_b + "°");// 今天温度b

		// 未来三天天气状况(我加了当天天气：1+3)
		List<FutureWeatherBean> futureList = bean.getFutureList();

		if (futureList.size() == 4) {
			setFutureViews(tv_tommorrow, iv_tommorrow_weather,
					tv_tommorrow_temp_a, tv_tommorrow_temp_b, futureList.get(1));
			setFutureViews(tv_thirdday, iv_thirdday_weather,
					tv_thirdday_temp_a, tv_thirdday_temp_b, futureList.get(2));
			setFutureViews(tv_fourthday, iv_fourthday_weather,
					tv_fourthday_temp_a, tv_fourthday_temp_b, futureList.get(3));
		}

		// 当前天气状况
		Calendar cal = Calendar.getInstance();
		int time = cal.get(Calendar.HOUR_OF_DAY);
		String prefixStr = null;
		if (time >= 6 && time < 18) {
			prefixStr = "d";
		} else {
			prefixStr = "n";
		}
		iv_now_weather.setImageResource(getResources().getIdentifier(
				prefixStr + bean.getWeather_id(), "drawable",
				"com.himi.weather"));

		// 详细信息数据填充
		tv_humidity.setText(bean.getHumidity());
		tv_dressing_index.setText(bean.getDress_index());
		;
		tv_uv_index.setText(bean.getUv_index());
		tv_wind.setText(bean.getWind());

	}

	/**
	 * 未来3天天气情况数据填充
	 */
	private void setFutureViews(TextView tv_week, ImageView iv_weather,
			TextView tv_temp_a, TextView tv_temp_b, FutureWeatherBean bean) {
		tv_week.setText(bean.getWeek());
		iv_weather.setImageResource(getResources().getIdentifier(
				"d" + bean.getWeather_id(), "drawable", "com.himi.weather"));
		String[] tempArr = bean.getTemp().split("~");
		String temp_str_a = tempArr[1].substring(0, tempArr[1].indexOf("℃"));// 最高温度
		String temp_str_b = tempArr[0].substring(0, tempArr[0].indexOf("℃"));// 最低温度
		tv_temp_a.setText(temp_str_a);
		tv_temp_b.setText(temp_str_b);
	}

	private void init() {
		mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
		mPullToRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
               
					@Override
					public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
						 new AsyncTask<Void, Void, Void>() {

							@Override
							protected Void doInBackground(Void... params) {
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e) {
									// TODO 自动生成的 catch 块
									e.printStackTrace();
								}		
								return null;
							}
							
							@Override
							protected void onPostExecute(Void result) {
								mService.getCityWeather();
								mPullToRefreshScrollView.onRefreshComplete();
							}
						}.execute();	

				}
			});
		

		mScrollView = mPullToRefreshScrollView.getRefreshableView();

		tv_city = (TextView) findViewById(R.id.tv_city);
		tv_release = (TextView) findViewById(R.id.tv_release);
		tv_now_weather = (TextView) findViewById(R.id.tv_now_weather);
		tv_today_temp = (TextView) findViewById(R.id.tv_today_temp);
		tv_now_temp = (TextView) findViewById(R.id.tv_now_temp);
		tv_aqi = (TextView) findViewById(R.id.tv_aqi);
		tv_quality = (TextView) findViewById(R.id.tv_quality);
		tv_next_three = (TextView) findViewById(R.id.tv_next_three);
		tv_next_six = (TextView) findViewById(R.id.tv_next_six);
		tv_next_nine = (TextView) findViewById(R.id.tv_next_nine);
		tv_next_twelve = (TextView) findViewById(R.id.tv_next_twelve);
		tv_next_fifteen = (TextView) findViewById(R.id.tv_next_fifteen);
		tv_next_three_temp = (TextView) findViewById(R.id.tv_next_three_temp);
		tv_next_six_temp = (TextView) findViewById(R.id.tv_next_six_temp);
		tv_next_nine_temp = (TextView) findViewById(R.id.tv_next_nine_temp);
		tv_next_twelve_temp = (TextView) findViewById(R.id.tv_next_twelve_temp);
		tv_next_fifteen_temp = (TextView) findViewById(R.id.tv_next_fifteen_temp);
		tv_today_temp_a = (TextView) findViewById(R.id.tv_today_temp_a);
		tv_today_temp_b = (TextView) findViewById(R.id.tv_today_temp_b);
		tv_tommorrow = (TextView) findViewById(R.id.tv_tommorrow);
		tv_tommorrow_temp_a = (TextView) findViewById(R.id.tv_tommorrow_temp_a);
		tv_tommorrow_temp_b = (TextView) findViewById(R.id.tv_tommorrow_temp_b);
		tv_thirdday = (TextView) findViewById(R.id.tv_thirdday);
		tv_thirdday_temp_a = (TextView) findViewById(R.id.tv_thirdday_temp_a);
		tv_thirdday_temp_b = (TextView) findViewById(R.id.tv_thirdday_temp_b);
		tv_fourthday = (TextView) findViewById(R.id.tv_fourthday);
		tv_fourthday_temp_a = (TextView) findViewById(R.id.tv_fourthday_temp_a);
		tv_fourthday_temp_b = (TextView) findViewById(R.id.tv_fourthday_temp_b);
		tv_humidity = (TextView) findViewById(R.id.tv_humidity);
		tv_wind = (TextView) findViewById(R.id.tv_wind);
		tv_uv_index = (TextView) findViewById(R.id.tv_uv_index);
		tv_dressing_index = (TextView) findViewById(R.id.tv_dressing_index);

		iv_now_weather = (ImageView) findViewById(R.id.iv_now_weather);
		iv_next_three = (ImageView) findViewById(R.id.iv_next_three);
		iv_next_six = (ImageView) findViewById(R.id.iv_next_six);
		iv_next_nine = (ImageView) findViewById(R.id.iv_next_nine);
		iv_next_twelve = (ImageView) findViewById(R.id.iv_next_twelve);
		iv_next_fifteen = (ImageView) findViewById(R.id.iv_next_fifteen);
		iv_today_weather = (ImageView) findViewById(R.id.iv_today_weather);
		iv_tommorrow_weather = (ImageView) findViewById(R.id.iv_tommorrow_weather);
		iv_thirdday_weather = (ImageView) findViewById(R.id.iv_thirdday_weather);
		iv_fourthday_weather = (ImageView) findViewById(R.id.iv_fourthday_weather);
		
		
		/**
		 * 添加监听事件完成到城市选择界面的跳转
		 */
		rl_city =(RelativeLayout) findViewById(R.id.rl_city);
				
		rl_city.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				startActivityForResult(new Intent(mcontext,CityActivity.class), 1);
				
			}
		});

	}
	
	/**
	 * CityActivity返回数据交给WeatherActivity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode ==1 && resultCode ==1) {
			String city = data.getStringExtra("city");
			mService.getCityWeather(city);
		}
	}
	
	
	
	@Override
	protected void onDestroy() {
		// 解除Service的绑定
		unbindService(conn);
		super.onDestroy();
	}
  }

