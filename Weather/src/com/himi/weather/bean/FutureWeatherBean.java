package com.himi.weather.bean;

/**
 * δ��3�������Ԥ��
 * 		���ݷ�װ bean ��
 * @author hebao
 *
 */
public class FutureWeatherBean {
    //���ڼ�
	private String week;
	//�������(��Ӧ�ڲ�ͬ����״����ͼƬ)
	private String weather_id;
	//�¶�
	private String temp;
	//����
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
