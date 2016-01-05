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
	//������
	private Context context;
	
    //ʹ��HttpClient����
	private HttpClient mHttpClient;
	//ʹ��Get��ʽ����,�ύ��ַ
	private HttpGet mHttpGet;
	//����õ��Ļظ����ûس�
	private HttpResponse mHttpResponse;
	//http��ʵ��
	private HttpEntity mHttpEntity;
	
	private InputStream in;
	
	//��ȡ���ݵĽӿڣ��Զ��壩
	private HttpGetDataListener listener;
	//�������վ
	private String url;
	public HttpData(Context context,String url, HttpGetDataListener listener) {
		this.context = context;
		this.url = url;
		this.listener = listener;
	}
	
	/**
	 * ��ȡ��������
	 */
	@Override
	protected String doInBackground(String... params) {

		try {
			mHttpClient = new DefaultHttpClient();
			mHttpGet = new HttpGet(url);
			mHttpResponse = mHttpClient.execute(mHttpGet);
			
			//��ȡHttp���󷵻�ʵ��
			mHttpEntity = mHttpResponse.getEntity();
			//�����ص�ʵ�����ת��Ϊ������
			in = mHttpEntity.getContent();
			//ʹ�û������ַ�����Ч��ȡ����
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			//��ȡ����
			String line = null;
			StringBuffer sb = new StringBuffer();
			while((line = br.readLine()) != null) {
				sb.append(line);
			}
			
			return sb.toString();
			
		} catch (ClientProtocolException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}		
		return null;
	}
	
	
	@Override
	protected void onPostExecute(String result) {
		/**
		 * ���resultΪnull,˵���ֻ���������������
		 */
		if(result == null) {
			//System.out.println("�����޷�����");
			Toast ashortToast = Toast.makeText(context, "�����޷�����", Toast.LENGTH_SHORT);
			ashortToast.setGravity(Gravity.CENTER, 0, 0);
			ashortToast.show();
			return;
		}
		listener.getDataUrl(result);
		super.onPostExecute(result);
	}

	
}
