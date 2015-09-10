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
			"��Ϧ�ڵ��ˣ�����һ�볤���棬ף���ǵİ���������һ�������þã���Զ�����롣����һ����������������������������һ��������ζ����ϵ�ף������Ϧ���֡�",
			"ѩ����������Ʈ��������ĵ�ǣ�ʥ������˼�����㣬������ף�������⣬���Ҹ��ۻ�������ףԸ����ֿ��ʥ����ף���������⣡",
			"����������ҴҶ��������ε���ͣ������Ŵ�������Ц���Ҫ����̾Ϣ�����÷�����ȥ��������������Ҫ���˻��ݾ�ϲ����Ϣ��",
			"�װ���֯Ů����Ϧ���˽ڽ�����Ը���Ǹ߾��й���ɫ��������ΰ�����ģ�����ȵ�����������ͳ�����ֶ������簮�����ߣ�Я�ֿ��������¾��档ţ�ɾ��ϡ�"
			
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO �Զ����ɵķ������
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms);
		lv = (ListView) findViewById(R.id.iv);
		lv.setAdapter(new ArrayAdapter<String>(this, R.layout.item, sms));
		
		
		//��listview����Ŀ���õ���¼�
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
