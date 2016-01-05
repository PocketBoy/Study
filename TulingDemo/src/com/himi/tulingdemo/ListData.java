package com.himi.tulingdemo;

/**
 * 封装Json数据解析的结果
 * @author Administrator
 *
 */
public class ListData {
	
	public static final int SEND = 1;
	public static final int RECEIVER =2;
	
	private String content;
	/**
	 * 加载ListView的Item的状态值
	 * 		flag = SEND(用户)
	 * 		flag = RECEIVER(机器人)
	 */
	private int flag;
	//时间数据
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
