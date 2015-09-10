package com.himi.Smshelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SmsActivity extends Activity {
	private ListView lv;
	private String[] sms  = {
			"七夕节到了，送你一碗长寿面，祝你们的爱情像长寿面一样长长久久，永远不分离。送你一份酸辣汤，让你们生活像酸辣汤一样有滋有味。真诚的祝福你七夕快乐。",
			"雪花的美丽，飘舞着心情的惦记，圣诞节最思念是你，给你我祝福的深意，把幸福累积，祈祷着祝愿的真挚，圣诞节祝你万事如意！",
			"三年光阴，匆匆而过，如梦的年纪，弥漫着串串欢声笑语，不要挥手叹息，觉得繁花尽去，鼓足勇气，不要忘了互递惊喜的消息。",
			"亲爱的织女：七夕情人节将至，愿我们高举中国特色痴情主义伟大旗帜，发扬鹊桥相会优良传统，保持二人世界爱情在线，携手开创爱情新局面。牛郎敬上。"
			
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms);
		lv = (ListView) findViewById(R.id.iv);
		lv.setAdapter(new ArrayAdapter<String>(this, R.layout.item, sms));
		
		
		//给listview的条目设置点击事件
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				String context = sms[position];
				Intent intent = new Intent();
				intent.putExtra("context", context);
				setResult(0, intent);
				finish();
			}
			
		});
		
	}

}
