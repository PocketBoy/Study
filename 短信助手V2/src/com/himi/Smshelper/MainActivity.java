package com.himi.Smshelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
	private EditText et_Sms;
	private EditText et_phone;
	public static int GET_SMS = 1;//获取短信的内容
	public static int GET_PHONE = 2;//获取电话号码

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		et_Sms = (EditText) findViewById(R.id.et_Sms);
		et_phone = (EditText) findViewById(R.id.et_phone);

	}
	/**
	 * 选择短信内容
	 * @param view
	 */
	public void select_Sms(View view) {
		Intent intent = new Intent(this, SmsActivity.class);
		// 直接打开新的界面
		// startActivity(intent);
		startActivityForResult(intent, GET_SMS);

	}
	/**
	 * 选择联系人
	 * @param view
	 */
	public void select_contacts(View view) {
		Intent intent = new Intent(this, ContactActivity.class);
		// 直接打开新的界面
		// startActivity(intent);
		startActivityForResult(intent, GET_PHONE);
	}
	
	/**
	 *返回数据  当我们开启的Activity关闭的时候(这里我们开启的就是SmsActivity)，SmsActivity关闭之后调用这个方法
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == GET_SMS) {
			if (data != null) {
				String context = data.getStringExtra("context");
				et_Sms.setText(context);
			}
		}else {
			String context = data.getStringExtra("phone");
			et_phone.setText(context);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
