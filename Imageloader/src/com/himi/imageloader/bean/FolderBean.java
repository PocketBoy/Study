package com.himi.imageloader.bean;

/**
 * ͼƬ���ļ�����Ϣ��
 * 
 * ע�⣺
 *    �����洢��ǰ�ļ��е�·��,��ǰ�ļ��а���������ͼƬ,�Լ���һ��ͼƬ·���������ļ��е�ͼ��;
 *    ע���ļ��е�����,������set�ļ��е�·����ʱ��,�Զ���ȡ,��ϸ����setDir�������.
 * 
 * @author hebao
 * 
 */

public class FolderBean {
	/**
	 * ͼƬ���ļ���·��
	 */
	private String dir;

	/**
	 * ��һ��ͼƬ��·��
	 */
	private String firstImgPath;

	/**
	 * �ļ��е�����
	 */
	private String name;

	/**
	 * ͼƬ������
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
