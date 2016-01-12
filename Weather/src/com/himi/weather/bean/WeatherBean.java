package com.himi.weather.bean;

import java.util.List;

/**
 * 实时天气 与 未来3天的天气 (数据汇总)
 *    数据封装 bean 类
 * @author hebao
 *
 */
public class WeatherBean {
	//城市
	private String city;
	//发布时间
	private String release;
	//天气情况(对应于不同天气状况的图片)
	private String weather_id;
	//天气情况(文字说明：晴, 阴……)
	private String weather_str;
	//当天的温度
	private String temp;
	//当天的实时温度
	private String now_temp;
	//体感温度
	private String felt_temp;
	//湿度
	private String humidity;
	//风向风力
	private String wind;
	//紫外线强度
	private String uv_index;
	//穿衣指数
	private String dress_index;
	
	
	
	//未来几天的天气数据
	private List<FutureWeatherBean> futureList;



	public String getCity() {
		return city;
	}



	public void setCity(String city) {
		this.city = city;
	}



	public String getRelease() {
		return release;
	}



	public void setRelease(String release) {
		this.release = release;
	}



	public String getWeather_id() {
		return weather_id;
	}



	public void setWeather_id(String weather_id) {
		this.weather_id = weather_id;
	}



	public String getWeather_str() {
		return weather_str;
	}



	public void setWeather_str(String weather_str) {
		this.weather_str = weather_str;
	}



	public String getTemp() {
		return temp;
	}



	public void setTemp(String temp) {
		this.temp = temp;
	}



	public String getNow_temp() {
		return now_temp;
	}



	public void setNow_temp(String now_temp) {
		this.now_temp = now_temp;
	}



	public String getFelt_temp() {
		return felt_temp;
	}



	public void setFelt_temp(String felt_temp) {
		this.felt_temp = felt_temp;
	}



	public String getHumidity() {
		return humidity;
	}



	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}



	public String getWind() {
		return wind;
	}



	public void setWind(String wind) {
		this.wind = wind;
	}



	public String getUv_index() {
		return uv_index;
	}



	public void setUv_index(String uv_index) {
		this.uv_index = uv_index;
	}



	public String getDress_index() {
		return dress_index;
	}



	public void setDress_index(String dress_index) {
		this.dress_index = dress_index;
	}



	public List<FutureWeatherBean> getFutureList() {
		return futureList;
	}



	public void setFutureList(List<FutureWeatherBean> futureList) {
		this.futureList = futureList;
	}
	
	
	
	
	
}
