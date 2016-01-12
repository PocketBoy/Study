package com.himi.weather;

import com.thinkland.sdk.android.JuheSDKInitializer;

import android.app.Application;

public class WeatherApplication extends Application {
	@Override
	public void onCreate() {
		// TODO 自动生成的方法存根
		super.onCreate();
		JuheSDKInitializer.initialize(getApplicationContext());
	}

}
