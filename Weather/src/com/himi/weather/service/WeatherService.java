package com.himi.weather.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.himi.weather.bean.FutureWeatherBean;
import com.himi.weather.bean.HoursWeatherBean;
import com.himi.weather.bean.PMBean;
import com.himi.weather.bean.WeatherBean;
import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

public class WeatherService extends Service{
	/**
	 *  PM 2.5 申请应用的App Key
	 */
	private static final String App_Key = "65035b89e78ca1976445ff9490f7b150";
	
	private static final int REPEAT_MSG = 0x01;
	
	private WeatherServiceBinder binder = new WeatherServiceBinder();
	
	private boolean isRunning = false;
	private int count = 0;
	
	private List<HoursWeatherBean> list;
	private PMBean pmBean;
	private WeatherBean weatherBean;
	private String city;
	
	
	private onParserCallBack callBack;
	
	
	/**
	 * 自定义Handler
	 * 实现每隔半个小时刷新一次数据
	 */
	private Handler myHandler = new Handler() {
		
		public void handleMessage(android.os.Message msg) {
			
			if(msg.what == REPEAT_MSG) {
				getCityWeather();
				sendEmptyMessageDelayed(REPEAT_MSG, 30*60*1000);
			}
			
		};
	};
	
	
	/**
	 * 回调接口
	 */
	public interface onParserCallBack {
		public void onParserComplete(List<HoursWeatherBean> list,
				PMBean pmBean, WeatherBean weatherBean);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO 自动生成的方法存根
		return binder;
	}
	
	@Override
	public void onCreate() {
		// TODO 自动生成的方法存根
		super.onCreate();
		city = "北京";
		//System.out.println("Service oncreate");
		myHandler.sendEmptyMessage(REPEAT_MSG);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO 自动生成的方法存根
		System.out.println("Service onStartCommand");
		return super.onStartCommand(intent, flags, startId);
		
	}
	
	
	
	@Override
	public void onDestroy() {
		System.out.println("Service onDestory");
		super.onDestroy();
	}
	
	
	
	public class WeatherServiceBinder extends Binder {
		
		public WeatherService getService() {
			return WeatherService.this;
		}
	}
	
	
	
	/**
	 * 注册回调接口
	 * @param callBack
	 */
	public void setCallBack(onParserCallBack callBack) {
		this.callBack = callBack;
	}
	
	/**
	 * 注销回调接口
	 */
	public void removeCallBack() {
		callBack = null;
	}
		

	
	
	/**
	 * 用户选择不同的城市，刷新显示
	 */
	public void getCityWeather(String city) {
		this.city = city;
		getCityWeather();
			
	}
	
	
	public void getCityWeather() {

		if (isRunning) {
			return;
		}

		isRunning = true;
		count = 0;

		Parameters params = new Parameters();
		params.add("cityname", city);
		params.add("dtype", "json");
		params.add("format", 1);

		/**
		 * 请求的方法 参数: 第一个参数 当前请求的context 第二个参数 接口id 第三二个参数 接口请求的url 第四个参数 接口请求的方式
		 * 第五个参数 接口请求的参数,键值对com.thinkland.sdk.android.Parameters类型; 第六个参数
		 * 请求的回调方法,com.thinkland.sdk.android.DataCallBack;
		 * 
		 */
		JuheData.executeWithAPI(this, 39, "http://v.juhe.cn/weather/index",
				JuheData.GET, params, new DataCallBack() {
					/**
					 * 请求成功时调用的方法 statusCode为http状态码, responseString为请求返回数据.
					 */
					@Override
					public void onSuccess(int statusCode, String responseString) {
						// System.out.println(responseString);
						synchronized (this) {
							count++;
						}	
						try {
							 weatherBean = parserWeather(new JSONObject(
									responseString));
						
							
							if(count ==3 ) {
								if(callBack!= null) {
									callBack.onParserComplete(list, pmBean, weatherBean);
								}
								isRunning = false;
							}
						} catch (JSONException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}

					}

					/**
					 * 请求完成时调用的方法,无论成功或者失败都会调用.
					 */
					@Override
					public void onFinish() {
						// TODO 自动生成的方法存根

					}

					/**
					 * 请求失败时调用的方法,statusCode为http状态码,throwable为捕获到的异常
					 * statusCode:30002 没有检测到当前网络. 30003 没有进行初始化. 0
					 * 未明异常,具体查看Throwable信息. 其他异常请参照http状态码.
					 */
					@Override
					public void onFailure(int arg0, String arg1, Throwable arg2) {
						// TODO 自动生成的方法存根

					}

				});

		// 再次请求,获取当天每隔3小时的天气状况
		Parameters params_threeHour = new Parameters();
		params_threeHour.add("cityname", city);
		params_threeHour.add("dtype", "json");
		JuheData.executeWithAPI(this, 39,
				"http://v.juhe.cn/weather/forecast3h", JuheData.GET,
				params_threeHour, new DataCallBack() {

					@Override
					public void onSuccess(int statusCode, String responseString) {
						count++;
						try {
							// 当天每隔3小时的天气状况数据解析
							 list = parserForecast3h(new JSONObject(
									responseString));
							
							if(count ==3) {
								if(callBack!= null) {
									callBack.onParserComplete(list, pmBean, weatherBean);
								}
								isRunning = false;
							}

						} catch (JSONException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}

					}

					@Override
					public void onFinish() {
						// TODO 自动生成的方法存根

					}

					@Override
					public void onFailure(int arg0, String arg1, Throwable arg2) {
						// TODO 自动生成的方法存根

					}
				});

		// PM 2.5 请求数据
		Parameters params_pm25 = new Parameters();
		params_pm25.add("city", city);
		params_pm25.add("key", App_Key);
		JuheData.executeWithAPI(this, 33,
				"http://web.juhe.cn:8080/environment/air/pm", JuheData.GET,
				params_pm25, new DataCallBack() {

					@Override
					public void onFailure(int arg0, String arg1, Throwable arg2) {
						// TODO 自动生成的方法存根

					}

					@Override
					public void onFinish() {
						// TODO 自动生成的方法存根

					}

					@Override
					public void onSuccess(int statusCode, String responseString) {
						// System.out.println(responseString);
						count++;
						try {
							pmBean= parserPM(new JSONObject(
									responseString));
							                      
							if(count ==3) {
								if(callBack!= null) {
									callBack.onParserComplete(list, pmBean, weatherBean);
								}
								isRunning = false;
							}

						} catch (JSONException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}

					}

				});

	}


	/**
	 * 实时天气与未来三天天气状况的数据解析
	 */
	private WeatherBean parserWeather(JSONObject json) {
		WeatherBean bean = null;
		try {
			// 解析数据之前,先检验是否获取数据成功。 养成这个好习惯
			int code = json.getInt("resultcode");
			int error_code = json.getInt("error_code");
			if (error_code == 0 && code == 200) {// 获取数据成功,解析数据
				JSONObject resultJson = json.getJSONObject("result");

				/**
				 * 将解析出来的数据封装到WeatherBean之中
				 */

				bean = new WeatherBean();

				// today
				JSONObject todayJson = resultJson.getJSONObject("today");
				bean.setCity(todayJson.getString("city"));
				bean.setUv_index(todayJson.getString("uv_index"));
				bean.setTemp(todayJson.getString("temperature"));
				bean.setWeather_str(todayJson.getString("weather"));
				bean.setWeather_id(todayJson.getJSONObject("weather_id")
						.getString("fa"));
				bean.setDress_index(todayJson.getString("dressing_index"));

				// sk
				JSONObject skJson = resultJson.getJSONObject("sk");
				bean.setWind(skJson.getString("wind_direction")
						+ skJson.getString("wind_strength"));
				bean.setNow_temp(skJson.getString("temp"));
				bean.setRelease(skJson.getString("time"));
				bean.setHumidity(skJson.getString("humidity"));

				// future
				JSONObject futureJson = resultJson.getJSONObject("future");
				Calendar cal = Calendar.getInstance();
				List<FutureWeatherBean> futureList = new ArrayList<FutureWeatherBean>();
				/*
				 * String year = String.valueOf(cal.get(Calendar.YEAR)); String
				 * month = String.valueOf(cal.get(Calendar.MONTH)); String date
				 * = String.valueOf(cal.get(Calendar.DATE));
				 * System.out.println(year+(month+1)+date);
				 */

				// today
				String year_today = String.valueOf(cal.get(Calendar.YEAR));
				String month_today = String.valueOf(cal.get(Calendar.MONTH));
				String date_today = String.valueOf(cal.get(Calendar.DATE));
				String today_index = "day_" + year_today + (month_today + 1)
						+ date_today;
				JSONObject today_json = futureJson.getJSONObject(today_index);
				// System.out.println(today_index.toString());
				FutureWeatherBean todayBean = new FutureWeatherBean();
				todayBean.setTemp(today_json.getString("temperature"));
				todayBean.setWeather_id(today_json.getJSONObject("weather_id")
						.getString("fa"));
				todayBean.setWeek(today_json.getString("week"));

				futureList.add(todayBean);

				// tomorrow
				cal.add(Calendar.DATE, 1);
				String year_tomorrow = String.valueOf(cal.get(Calendar.YEAR));
				String month_tomorrow = String.valueOf(cal.get(Calendar.MONTH));
				String date_tomorrow = String.valueOf(cal.get(Calendar.DATE));
				String tomorrow_index = "day_" + year_tomorrow
						+ (month_tomorrow + 1) + date_tomorrow;
				JSONObject tomorrow_json = futureJson
						.getJSONObject(tomorrow_index);
				// System.out.println(tomorrow_json.toString());

				FutureWeatherBean tomorrowBean = new FutureWeatherBean();
				tomorrowBean.setTemp(tomorrow_json.getString("temperature"));
				tomorrowBean.setWeather_id(tomorrow_json.getJSONObject(
						"weather_id").getString("fa"));
				tomorrowBean.setWeek(tomorrow_json.getString("week"));
				futureList.add(tomorrowBean);

				// thirdday
				cal.add(Calendar.DATE, 1);
				String year_thirdday = String.valueOf(cal.get(Calendar.YEAR));
				String month_thirdday = String.valueOf(cal.get(Calendar.MONTH));
				String date_thirdday = String.valueOf(cal.get(Calendar.DATE));
				String thirdday_index = "day_" + year_thirdday
						+ (month_thirdday + 1) + date_thirdday;
				JSONObject thirdday_json = futureJson
						.getJSONObject(thirdday_index);
				// System.out.println(thirdday_json.toString());

				FutureWeatherBean thirddayBean = new FutureWeatherBean();
				thirddayBean.setTemp(thirdday_json.getString("temperature"));
				thirddayBean.setWeather_id(thirdday_json.getJSONObject(
						"weather_id").getString("fa"));
				thirddayBean.setWeek(thirdday_json.getString("week"));
				futureList.add(thirddayBean);

				// fourthday
				cal.add(Calendar.DATE, 1);
				String year_fourthday = String.valueOf(cal.get(Calendar.YEAR));
				String month_fourthday = String
						.valueOf(cal.get(Calendar.MONTH));
				String date_fourthday = String.valueOf(cal.get(Calendar.DATE));
				String fourthday_index = "day_" + year_fourthday
						+ (month_fourthday + 1) + date_fourthday;
				JSONObject fourthday_json = futureJson
						.getJSONObject(fourthday_index);
				// System.out.println(fourthday_json.toString());

				FutureWeatherBean fourthdayBean = new FutureWeatherBean();
				fourthdayBean.setTemp(fourthday_json.getString("temperature"));
				fourthdayBean.setWeather_id(fourthday_json.getJSONObject(
						"weather_id").getString("fa"));
				fourthdayBean.setWeek(fourthday_json.getString("week"));
				futureList.add(fourthdayBean);

				// 添加未来3天的天气状况到WeatherBean之中
				bean.setFutureList(futureList);

			} else {
				Toast.makeText(this, "实时天气与未来三天天气状况的数据获取失败", 0).show();
			}
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		return bean;
	}

	/**
	 * 当天每隔3小时天气状况数据解析
	 */

	private List<HoursWeatherBean> parserForecast3h(JSONObject json) {
		List<HoursWeatherBean> list = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
		Date dateCurr = new Date(System.currentTimeMillis());
		// 解析数据之前,先检验是否获取数据成功。 养成这个好习惯
		try {
			int code = json.getInt("resultcode");
			int error_code = json.getInt("error_code");
			if (error_code == 0 && code == 200) {// 获取数据成功,解析数据
				list = new ArrayList<HoursWeatherBean>();
				JSONArray resultArray = json.getJSONArray("result");
				for (int i = 0; i < resultArray.length(); i++) {
					JSONObject hourJson = resultArray.getJSONObject(i);
					Date hDate = format.parse(hourJson.getString("sfdate"));
					if (!hDate.after(dateCurr)) {
						continue;
					}
					// 封装数据到Bean之中
					HoursWeatherBean bean = new HoursWeatherBean();
					bean.setWeather_id(hourJson.getString("weatherid"));
					bean.setTemp(hourJson.getString("temp1"));

					Calendar cal = Calendar.getInstance();
					cal.setTime(hDate);
					bean.setTime(cal.get(Calendar.HOUR_OF_DAY) + "");

					list.add(bean);
					if (list.size() == 5) {// 我们这里只需要取出5个数值
						break;
					}
				}
			} else {
				Toast.makeText(this, "当天每隔3小时天气数据获取失败", 0).show();
			}
				
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * PM2.5数据解析
	 */
	private PMBean parserPM(JSONObject json) {
		PMBean bean = null;
		try {
			int code = json.getInt("resultcode");
			int error_code = json.getInt("error_code");
			if (error_code == 0 && code == 200) {
				JSONArray resultArray = json.getJSONArray("result");
				JSONObject resultJson = resultArray.getJSONObject(0);
				bean = new PMBean();
				bean.setAqi(resultJson.getString("AQI"));
				bean.setQuality(resultJson.getString("quality"));
			} else {
				Toast.makeText(this, "PM 2.5空气质量数据获取失败", 0).show();
			}
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		return bean;
	}


}
