package com.himi.weather.bean;

import java.util.List;

/**
 * ʵʱ���� �� δ��3������� (���ݻ���)
 *    ���ݷ�װ bean ��
 * @author hebao
 *
 */
public class WeatherBean {
	//����
	private String city;
	//����ʱ��
	private String release;
	//�������(��Ӧ�ڲ�ͬ����״����ͼƬ)
	private String weather_id;
	//�������(����˵������, ������)
	private String weather_str;
	//������¶�
	private String temp;
	//�����ʵʱ�¶�
	private String now_temp;
	//����¶�
	private String felt_temp;
	//ʪ��
	private String humidity;
	//�������
	private String wind;
	//������ǿ��
	private String uv_index;
	//����ָ��
	private String dress_index;
	
	
	
	//δ���������������
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
