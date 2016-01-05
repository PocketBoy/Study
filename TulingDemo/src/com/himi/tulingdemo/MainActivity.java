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
	//集合用来存放Json数据
	private List<ListData> lists;
	
	//主布局UI
	private ListView lv;
	private EditText sendtext;
	private Button send_btn;
	private TextAdapter adapter;
	
	private String content_str;
	//承载欢迎语的数组
	private String[] welcome_array;
	
	//当前的时间
	private double currentTime = 0;
	//过去的时间
	private double oldTime = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	
	/**
	 * 初始化
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
		 * 用户第1次启动程序出现欢迎语,放在初始化中
		 */
		ListData listData;
		listData = new ListData(getRandomWelcomeTips(), ListData.RECEIVER, getTime());
		lists.add(listData);
	}
	
	/**
	 * 获取随机欢迎语
	 * @return
	 * 
	 */
	private String getRandomWelcomeTips() {
		String welcome_tip = null;
		welcome_array = this.getResources().getStringArray(R.array.welcome_tips);
		//获取一个随机索引index
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
	 * 解析图灵机器人返回的JSON数据
	 */
	public void parseText(String str) {
		try {
			JSONObject jb = new JSONObject(str);
			//System.out.println(jb.getString("code"));
			//System.out.println(jb.getString("text"));
			
			//封装Json数据中text到ListData
			ListData listData = null;
			listData = new ListData(jb.getString("text"), ListData.RECEIVER,getTime());
			//将上面的ListData存放到集合中
			lists.add(listData);
			
			//重新适配Adapter,刷新显示
			adapter.notifyDataSetChanged();
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		getTime();
		content_str = sendtext.getText().toString();
		sendtext.setText("");//每次发送完毕,清空输入框
		String dropk = content_str.replace(" ", "");//去除内容中的空格
		String droph = dropk.replace("\n", "");//去除内容中回车
		ListData listData;
		listData = new ListData(content_str, ListData.SEND, getTime());
		lists.add(listData);
		httpdata = (HttpData) new HttpData(this,"http://www.tuling123.com/openapi/api?key=fc09a4a0ac834838cdb6b8d9b9b769e6&info="+
			droph,this).execute();
		
	}
	//获取时间
	private String getTime() {
		currentTime = System.currentTimeMillis();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss");
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
