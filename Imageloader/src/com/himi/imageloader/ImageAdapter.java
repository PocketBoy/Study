package com.himi.imageloader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.himi.imageloader.util.ImageLoader;
import com.himi.imageloader.util.ImageLoader.Type;

public class ImageAdapter extends BaseAdapter {
		/** 
	     * �û�ѡ���ͼƬ���洢ΪͼƬ������·�� 
	     */  
		private static Set<String> mSelectedImg = new HashSet<String>();
		/** 
	     * �ļ���·�� 
	     */  
		private String mDirPath;
		private List<String> mImgPaths;
		private LayoutInflater mInflater;
		//�ֿ��洢�ļ�Ŀ¼�����ļ�������ʡ�ڴ�
		public ImageAdapter(Context context, List<String> mDatas, String dirPath) {
			this.mDirPath = dirPath;
			this.mImgPaths = mDatas;
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return mImgPaths.size();
		}

		public Object getItem(int position) {
			return mImgPaths.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.item_gridview, parent,false);
				
				viewHolder = new ViewHolder();
				viewHolder.mImg = (ImageView) convertView.findViewById(R.id.id_item_image);
				viewHolder.mSelect = (ImageButton) convertView.findViewById(R.id.id_item_select);
				convertView.setTag(viewHolder);		
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			/**
			 * ����״̬,��������õ�һ��ѡ�У��ڶ��λ��Ḵ��֮ǰ�ģ������ͻ��������
			 */
			viewHolder.mImg.setImageResource(R.drawable.pictures_no);
			viewHolder.mSelect.setImageResource(R.drawable.picture_unselected);
			viewHolder.mImg.setColorFilter(null);
			
			ImageLoader.getInstance(3, Type.LIFO).loadImage(mDirPath+"/"+mImgPaths.get(position),
					viewHolder.mImg);
			final String filePath = mDirPath+"/"+mImgPaths.get(position);
			
			// ����ImageView�ĵ���¼�  
			viewHolder.mImg.setOnClickListener(new OnClickListener() {	
				// ѡ����ͼƬ�䰵����֮��֮  
				public void onClick(View v) {
					//�Ѿ���ѡ��
					if(mSelectedImg.contains(filePath)) {
						mSelectedImg.remove(filePath);
						//�ı�Item״̬,û�б�Ҫˢ����ʾ
						viewHolder.mImg.setColorFilter(null);
						viewHolder.mSelect.setImageResource(R.drawable.picture_unselected);
					}else {//δ��ѡ��
						mSelectedImg.add(filePath);
						//�ı�Item״̬,û�б�Ҫˢ����ʾ
						viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
						viewHolder.mSelect.setImageResource(R.drawable.pictures_selected);
					}
					//notifyDataSetChanged();����ʹ�ã����������
					
				}
			});
			
			 /** 
	         * �Ѿ�ѡ�����ͼƬ����ʾ��ѡ�����Ч�� 
	         */  
			if(mSelectedImg.contains(filePath)) {
				viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
				viewHolder.mSelect.setImageResource(R.drawable.pictures_selected);
			}
		   
			return convertView;
		}
		
		private class ViewHolder {
			ImageView mImg;
			ImageButton mSelect;
		}
		
	}