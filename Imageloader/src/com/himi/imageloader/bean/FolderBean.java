package com.himi.imageloader.bean;

/**
 * 图片的文件夹信息类
 * 
 * 注意：
 *    用来存储当前文件夹的路径,当前文件夹包含多少张图片,以及第一张图片路径用于做文件夹的图标;
 *    注：文件夹的名称,我们在set文件夹的路径的时候,自动提取,仔细看下setDir这个方法.
 * 
 * @author hebao
 * 
 */

public class FolderBean {
	/**
	 * 图片的文件夹路径
	 */
	private String dir;

	/**
	 * 第一张图片的路径
	 */
	private String firstImgPath;

	/**
	 * 文件夹的名称
	 */
	private String name;

	/**
	 * 图片的数量
	 */
	private int count;

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
		int lastIndexOf = this.dir.lastIndexOf("/");
		this.name = this.dir.substring(lastIndexOf);
	}

	public String getFirstImgPath() {
		return firstImgPath;
	}

	public void setFirstImgPath(String firstImgPath) {
		this.firstImgPath = firstImgPath;
	}

	public String getName() {
		return name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
