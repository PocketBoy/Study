package com.himi.tulingdemo;

/**
 * ��װJson���ݽ����Ľ��
 * @author Administrator
 *
 */
public class ListData {
	
	public static final int SEND = 1;
	public static final int RECEIVER =2;
	
	private String content;
	/**
	 * ����ListView��Item��״ֵ̬
	 * 		flag = SEND(�û�)
	 * 		flag = RECEIVER(������)
	 */
	private int flag;
	//ʱ������
	private String time;
	public ListData(String content, int flag, String time) {
		setContent(content);
		setFlag(flag);
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	public int getFlag() {
		return flag;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}

}
