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
	     * 用户选择的图片，存储为图片的完整路径 
	     */  
		private static Set<String> mSelectedImg = new HashSet<String>();
		/** 
	     * 文件夹路径 
	     */  
		private String mDirPath;
		private List<String> mImgPaths;
		private LayoutInflater mInflater;
		//分开存储文件目录，和文件名。节省内存
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
			 * 重置状态,如果不重置第一次选中，第二次还会复用之前的，这样就会产生错乱
			 */
			viewHolder.mImg.setImageResource(R.drawable.pictures_no);
			viewHolder.mSelect.setImageResource(R.drawable.picture_unselected);
			viewHolder.mImg.setColorFilter(null);
			
			ImageLoader.getInstance(3, Type.LIFO).loadImage(mDirPath+"/"+mImgPaths.get(position),
					viewHolder.mImg);
			final String filePath = mDirPath+"/"+mImgPaths.get(position);
			
			// 设置ImageView的点击事件  
			viewHolder.mImg.setOnClickListener(new OnClickListener() {	
				// 选择，则将图片变暗，反之则反之  
				public void onClick(View v) {
					//已经被选择
					if(mSelectedImg.contains(filePath)) {
						mSelectedImg.remove(filePath);
						//改变Item状态,没有必要刷新显示
						viewHolder.mImg.setColorFilter(null);
						viewHolder.mSelect.setImageResource(R.drawable.picture_unselected);
					}else {//未被选择
						mSelectedImg.add(filePath);
						//改变Item状态,没有必要刷新显示
						viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
						viewHolder.mSelect.setImageResource(R.drawable.pictures_selected);
					}
					//notifyDataSetChanged();不能使用，会出现闪屏
					
				}
			});
			
			 /** 
	         * 已经选择过的图片，显示出选择过的效果 
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