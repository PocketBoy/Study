package com.himi.weather.bean;
/**
 * 
 * 当天每个3小时的天气状况Bean类
 * @author hebao
 *
 */
public class HoursWeatherBean {

	private String time;//时间
	private String weather_id;//天气ID
	private String temp;//温度
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
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
}
