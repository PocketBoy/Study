package com.himi.weather;

import com.thinkland.sdk.android.JuheSDKInitializer;

import android.app.Application;

public class WeatherApplication extends Application {
	@Override
	public void onCreate() {
		// TODO �Զ����ɵķ������
		super.onCreate();
		JuheSDKInitializer.initialize(getApplicationContext());
	}

}
