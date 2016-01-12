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
	 *  PM 2.5 ����Ӧ�õ�App Key
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
	 * �Զ���Handler
	 * ʵ��ÿ�����Сʱˢ��һ������
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
	 * �ص��ӿ�
	 */
	public interface onParserCallBack {
		public void onParserComplete(List<HoursWeatherBean> list,
				PMBean pmBean, WeatherBean weatherBean);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO �Զ����ɵķ������
		return binder;
	}
	
	@Override
	public void onCreate() {
		// TODO �Զ����ɵķ������
		super.onCreate();
		city = "����";
		//System.out.println("Service oncreate");
		myHandler.sendEmptyMessage(REPEAT_MSG);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO �Զ����ɵķ������
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
	 * ע��ص��ӿ�
	 * @param callBack
	 */
	public void setCallBack(onParserCallBack callBack) {
		this.callBack = callBack;
	}
	
	/**
	 * ע���ص��ӿ�
	 */
	public void removeCallBack() {
		callBack = null;
	}
		

	
	
	/**
	 * �û�ѡ��ͬ�ĳ��У�ˢ����ʾ
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
		 * ����ķ��� ����: ��һ������ ��ǰ�����context �ڶ������� �ӿ�id ������������ �ӿ������url ���ĸ����� �ӿ�����ķ�ʽ
		 * ��������� �ӿ�����Ĳ���,��ֵ��com.thinkland.sdk.android.Parameters����; ����������
		 * ����Ļص�����,com.thinkland.sdk.android.DataCallBack;
		 * 
		 */
		JuheData.executeWithAPI(this, 39, "http://v.juhe.cn/weather/index",
				JuheData.GET, params, new DataCallBack() {
					/**
					 * ����ɹ�ʱ���õķ��� statusCodeΪhttp״̬��, responseStringΪ���󷵻�����.
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
							// TODO �Զ����ɵ� catch ��
							e.printStackTrace();
						}

					}

					/**
					 * �������ʱ���õķ���,���۳ɹ�����ʧ�ܶ������.
					 */
					@Override
					public void onFinish() {
						// TODO �Զ����ɵķ������

					}

					/**
					 * ����ʧ��ʱ���õķ���,statusCodeΪhttp״̬��,throwableΪ���񵽵��쳣
					 * statusCode:30002 û�м�⵽��ǰ����. 30003 û�н��г�ʼ��. 0
					 * δ���쳣,����鿴Throwable��Ϣ. �����쳣�����http״̬��.
					 */
					@Override
					public void onFailure(int arg0, String arg1, Throwable arg2) {
						// TODO �Զ����ɵķ������

					}

				});

		// �ٴ�����,��ȡ����ÿ��3Сʱ������״��
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
							// ����ÿ��3Сʱ������״�����ݽ���
							 list = parserForecast3h(new JSONObject(
									responseString));
							
							if(count ==3) {
								if(callBack!= null) {
									callBack.onParserComplete(list, pmBean, weatherBean);
								}
								isRunning = false;
							}

						} catch (JSONException e) {
							// TODO �Զ����ɵ� catch ��
							e.printStackTrace();
						}

					}

					@Override
					public void onFinish() {
						// TODO �Զ����ɵķ������

					}

					@Override
					public void onFailure(int arg0, String arg1, Throwable arg2) {
						// TODO �Զ����ɵķ������

					}
				});

		// PM 2.5 ��������
		Parameters params_pm25 = new Parameters();
		params_pm25.add("city", city);
		params_pm25.add("key", App_Key);
		JuheData.executeWithAPI(this, 33,
				"http://web.juhe.cn:8080/environment/air/pm", JuheData.GET,
				params_pm25, new DataCallBack() {

					@Override
					public void onFailure(int arg0, String arg1, Throwable arg2) {
						// TODO �Զ����ɵķ������

					}

					@Override
					public void onFinish() {
						// TODO �Զ����ɵķ������

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
							// TODO �Զ����ɵ� catch ��
							e.printStackTrace();
						}

					}

				});

	}


	/**
	 * ʵʱ������δ����������״�������ݽ���
	 */
	private WeatherBean parserWeather(JSONObject json) {
		WeatherBean bean = null;
		try {
			// ��������֮ǰ,�ȼ����Ƿ��ȡ���ݳɹ��� ���������ϰ��
			int code = json.getInt("resultcode");
			int error_code = json.getInt("error_code");
			if (error_code == 0 && code == 200) {// ��ȡ���ݳɹ�,��������
				JSONObject resultJson = json.getJSONObject("result");

				/**
				 * ���������������ݷ�װ��WeatherBean֮��
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

				// ���δ��3�������״����WeatherBean֮��
				bean.setFutureList(futureList);

			} else {
				Toast.makeText(this, "ʵʱ������δ����������״�������ݻ�ȡʧ��", 0).show();
			}
		} catch (JSONException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}

		return bean;
	}

	/**
	 * ����ÿ��3Сʱ����״�����ݽ���
	 */

	private List<HoursWeatherBean> parserForecast3h(JSONObject json) {
		List<HoursWeatherBean> list = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
		Date dateCurr = new Date(System.currentTimeMillis());
		// ��������֮ǰ,�ȼ����Ƿ��ȡ���ݳɹ��� ���������ϰ��
		try {
			int code = json.getInt("resultcode");
			int error_code = json.getInt("error_code");
			if (error_code == 0 && code == 200) {// ��ȡ���ݳɹ�,��������
				list = new ArrayList<HoursWeatherBean>();
				JSONArray resultArray = json.getJSONArray("result");
				for (int i = 0; i < resultArray.length(); i++) {
					JSONObject hourJson = resultArray.getJSONObject(i);
					Date hDate = format.parse(hourJson.getString("sfdate"));
					if (!hDate.after(dateCurr)) {
						continue;
					}
					// ��װ���ݵ�Bean֮��
					HoursWeatherBean bean = new HoursWeatherBean();
					bean.setWeather_id(hourJson.getString("weatherid"));
					bean.setTemp(hourJson.getString("temp1"));

					Calendar cal = Calendar.getInstance();
					cal.setTime(hDate);
					bean.setTime(cal.get(Calendar.HOUR_OF_DAY) + "");

					list.add(bean);
					if (list.size() == 5) {// ��������ֻ��Ҫȡ��5����ֵ
						break;
					}
				}
			} else {
				Toast.makeText(this, "����ÿ��3Сʱ�������ݻ�ȡʧ��", 0).show();
			}
				
		} catch (JSONException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * PM2.5���ݽ���
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
				Toast.makeText(this, "PM 2.5�����������ݻ�ȡʧ��", 0).show();
			}
		} catch (JSONException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}

		return bean;
	}


}
