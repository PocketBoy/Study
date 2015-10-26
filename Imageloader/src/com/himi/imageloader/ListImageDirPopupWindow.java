package com.himi.imageloader;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.himi.imageloader.bean.FolderBean;
import com.himi.imageloader.util.ImageLoader;

/**
 * 自定义的PopupWindow
 * 作用：展现文件夹信息
 * @author hebao
 *
 */
public class ListImageDirPopupWindow extends PopupWindow {
	private int mWidth;
	private int mHeight;
	private View mConvertView;
	private ListView mListView;
	
	
	private List<FolderBean> mDatas;
	
	
	/**
	 * 文件夹选中的监听器
	 * @author hebao
	 *
	 */
	public interface OnDirSelectedListener {
		void onSelected(FolderBean folderBean);
	}
	public OnDirSelectedListener mListener;
	public void setOnDirSelectedListener (OnDirSelectedListener mListener) {
		this.mListener = mListener;
	}
	
	

	public ListImageDirPopupWindow(Context context, List<FolderBean> datas) {
		calWidthAndHeight(context);
		
		mConvertView = LayoutInflater.from(context).inflate(R.layout.popup_main, null);
		setContentView(mConvertView);
		
		setWidth(mWidth);
		setHeight(mHeight);
		
		//设置可触摸
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new BitmapDrawable());
		
		setTouchInterceptor(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
					dismiss();
					return true;
				}
				return false;
			}
		});
		
		initViews(context);
		initEvent();
		
	}

	private void initViews(Context context) {
		mListView = (ListView) mConvertView.findViewById(R.id.id_list_dir);
		mListView.setAdapter(new ListDirAdapter(context, mDatas));
	}
	
	/**
	 * 设置监听事件
	 */
	private void initEvent() {
		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(mListener != null) {
					mListener.onSelected(mDatas.get(position));
				}
				
			}
			
		});
		
	}

	

	/**
	 * 计算popupWindow的宽度和高度
	 * @param context
	 */
	private void calWidthAndHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		//Andorid.util 包下的DisplayMetrics 类提供了一种关于显示的通用信息，如显示大小，分辨率和字体。
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		
		
		mWidth = outMetrics.widthPixels;
		mHeight = (int) (outMetrics.heightPixels * 0.7);
	}
	
	
	private class ListDirAdapter extends ArrayAdapter<FolderBean> {
		private LayoutInflater mInflater;
		private List<FolderBean> mDatas;
		
		public ListDirAdapter(Context context,
				List<FolderBean> objects) {
			super(context, 0, objects);
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_popup_main, parent, false);
				
				holder.mImg = (ImageView) convertView.findViewById(R.id.id_id_dir_item_image);
				holder.mDirName = (TextView) convertView.findViewById(R.id.id_dir_item_name);
				holder.mDirCount = (TextView) convertView.findViewById(R.id.id_dir_item_count);
				
				convertView.setTag(holder);
			} else {
				holder =(ViewHolder) convertView.getTag();
			}
			FolderBean bean  =getItem(position);
			//重置
			holder.mImg.setImageResource(R.drawable.pictures_no);
			
			//回调加载图片
			ImageLoader.getInstance().loadImage(bean.getFirstImgPath(), holder.mImg);	
			holder.mDirCount.setText(bean.getCount()+"");
			holder.mDirName.setText(bean.getName());
			return convertView;
		}
		
		private class ViewHolder {
			ImageView mImg;
			TextView mDirName;
			TextView mDirCount;
		}
	}

}
