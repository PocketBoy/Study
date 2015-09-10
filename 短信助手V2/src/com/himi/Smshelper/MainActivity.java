package com.himi.Smshelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
	private EditText et_Sms;
	private EditText et_phone;
	public static int GET_SMS = 1;//��ȡ���ŵ�����
	public static int GET_PHONE = 2;//��ȡ�绰����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		et_Sms = (EditText) findViewById(R.id.et_Sms);
		et_phone = (EditText) findViewById(R.id.et_phone);

	}
	/**
	 * ѡ���������
	 * @param view
	 */
	public void select_Sms(View view) {
		Intent intent = new Intent(this, SmsActivity.class);
		// ֱ�Ӵ��µĽ���
		// startActivity(intent);
		startActivityForResult(intent, GET_SMS);

	}
	/**
	 * ѡ����ϵ��
	 * @param view
	 */
	public void select_contacts(View view) {
		Intent intent = new Intent(this, ContactActivity.class);
		// ֱ�Ӵ��µĽ���
		// startActivity(intent);
		startActivityForResult(intent, GET_PHONE);
	}
	
	/**
	 *��������  �����ǿ�����Activity�رյ�ʱ��(�������ǿ����ľ���SmsActivity)��SmsActivity�ر�֮������������
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
