package com.himi.weather.bean;

/**
 * 未来3天的天气预报
 * 		数据封装 bean 类
 * @author hebao
 *
 */
public class FutureWeatherBean {
    //星期几
	private String week;
	//天气情况(对应于不同天气状况的图片)
	private String weather_id;
	//温度
	private String temp;
	//日期
	private String date;
	
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getWeather_id() {
		return weather_id;
	}
	public void setWeather_id(String weather_id) {
		this.weather_id = weather_id;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	
	
}
