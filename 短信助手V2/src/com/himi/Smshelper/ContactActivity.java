package com.himi.Smshelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ContactActivity extends Activity {
	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		lv = (ListView) findViewById(R.id.lv);
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		for (int i = 0; i < 10; i++) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", "刘德华"+i);
			map.put("phone", "1000" + i);
			data.add(map);
		}
		lv.setAdapter(new SimpleAdapter(ContactActivity.this, data, R.layout.item_contacts,
				new String[] {"name", "phone"}, new int[] {R.id.tv_name,R.id.tv_phone}));
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String phone = "1000"+position;
				Intent data = new Intent();
				data.putExtra("phone", phone);
				setResult(0, data);
				System.out.println("你点击了第"+position+"选项");
				finish();
			}
			
		});
	}

}
