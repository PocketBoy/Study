package com.himi.tulingdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

public class HttpData extends AsyncTask<String, Void, String>{
	//上下文
	private Context context;
	
    //使用HttpClient请求
	private HttpClient mHttpClient;
	//使用Get方式请求,提交网址
	private HttpGet mHttpGet;
	//请求得到的回复，敲回车
	private HttpResponse mHttpResponse;
	//http的实体
	private HttpEntity mHttpEntity;
	
	private InputStream in;
	
	//获取数据的接口（自定义）
	private HttpGetDataListener listener;
	//请求的网站
	private String url;
	public HttpData(Context context,String url, HttpGetDataListener listener) {
		this.context = context;
		this.url = url;
		this.listener = listener;
	}
	
	/**
	 * 获取网络数据
	 */
	@Override
	protected String doInBackground(String... params) {

		try {
			mHttpClient = new DefaultHttpClient();
			mHttpGet = new HttpGet(url);
			mHttpResponse = mHttpClient.execute(mHttpGet);
			
			//获取Http请求返回实体
			mHttpEntity = mHttpResponse.getEntity();
			//将返回的实体对象转化为流对象
			in = mHttpEntity.getContent();
			//使用缓冲区字符流高效读取数据
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			//获取数据
			String line = null;
			StringBuffer sb = new StringBuffer();
			while((line = br.readLine()) != null) {
				sb.append(line);
			}
			
			return sb.toString();
			
		} catch (ClientProtocolException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}		
		return null;
	}
	
	
	@Override
	protected void onPostExecute(String result) {
		/**
		 * 如果result为null,说明手机网络连接有问题
		 */
		if(result == null) {
			//System.out.println("网络无法连接");
			Toast ashortToast = Toast.makeText(context, "网络无法连接", Toast.LENGTH_SHORT);
			ashortToast.setGravity(Gravity.CENTER, 0, 0);
			ashortToast.show();
			return;
		}
		listener.getDataUrl(result);
		super.onPostExecute(result);
	}

	
}
