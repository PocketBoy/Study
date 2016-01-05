package com.himi.tulingdemo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements HttpGetDataListener,OnClickListener{

	private HttpData httpdata;
	//�����������Json����
	private List<ListData> lists;
	
	//������UI
	private ListView lv;
	private EditText sendtext;
	private Button send_btn;
	private TextAdapter adapter;
	
	private String content_str;
	//���ػ�ӭ�������
	private String[] welcome_array;
	
	//��ǰ��ʱ��
	private double currentTime = 0;
	//��ȥ��ʱ��
	private double oldTime = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	
	/**
	 * ��ʼ��
	 */
	private void initView() {
		lv = (ListView) findViewById(R.id.lv);
		sendtext = (EditText) findViewById(R.id.sendText);
		send_btn = (Button) findViewById(R.id.send_btn);
	
		lists = new ArrayList<ListData>();
		
		send_btn.setOnClickListener(this);
		adapter = new TextAdapter(lists, this);
		lv.setAdapter(adapter);
		
		/**
		 * �û���1������������ֻ�ӭ��,���ڳ�ʼ����
		 */
		ListData listData;
		listData = new ListData(getRandomWelcomeTips(), ListData.RECEIVER, getTime());
		lists.add(listData);
	}
	
	/**
	 * ��ȡ�����ӭ��
	 * @return
	 * 
	 */
	private String getRandomWelcomeTips() {
		String welcome_tip = null;
		welcome_array = this.getResources().getStringArray(R.array.welcome_tips);
		//��ȡһ���������index
		int index = (int) (Math.random()*(welcome_array.length - 1));
		welcome_tip = welcome_array[index];
		
		return welcome_tip;
	}
	
	
	
	@Override
	public void getDataUrl(String data) {
		//System.out.println(data);
		parseText(data);
		
	}
	
	
	
	/**
	 * ����ͼ������˷��ص�JSON����
	 */
	public void parseText(String str) {
		try {
			JSONObject jb = new JSONObject(str);
			//System.out.println(jb.getString("code"));
			//System.out.println(jb.getString("text"));
			
			//��װJson������text��ListData
			ListData listData = null;
			listData = new ListData(jb.getString("text"), ListData.RECEIVER,getTime());
			//�������ListData��ŵ�������
			lists.add(listData);
			
			//��������Adapter,ˢ����ʾ
			adapter.notifyDataSetChanged();
		} catch (JSONException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		getTime();
		content_str = sendtext.getText().toString();
		sendtext.setText("");//ÿ�η������,��������
		String dropk = content_str.replace(" ", "");//ȥ�������еĿո�
		String droph = dropk.replace("\n", "");//ȥ�������лس�
		ListData listData;
		listData = new ListData(content_str, ListData.SEND, getTime());
		lists.add(listData);
		httpdata = (HttpData) new HttpData(this,"http://www.tuling123.com/openapi/api?key=fc09a4a0ac834838cdb6b8d9b9b769e6&info="+
			droph,this).execute();
		
	}
	//��ȡʱ��
	private String getTime() {
		currentTime = System.currentTimeMillis();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy��MM��dd��    HH:mm:ss");
		Date curDate = new Date();
		String str = format.format(curDate);
		if(currentTime - oldTime >= 5*60*1000) {
			oldTime = currentTime;
			return str;
		}else {
			return "";		
		}
		
		
	}
	

}
