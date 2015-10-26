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
 * �Զ����PopupWindow
 * ���ã�չ���ļ�����Ϣ
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
	 * �ļ���ѡ�еļ�����
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
		
		//���ÿɴ���
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
	 * ���ü����¼�
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
	 * ����popupWindow�Ŀ�Ⱥ͸߶�
	 * @param context
	 */
	private void calWidthAndHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		//Andorid.util ���µ�DisplayMetrics ���ṩ��һ�ֹ�����ʾ��ͨ����Ϣ������ʾ��С���ֱ��ʺ����塣
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
			//����
			holder.mImg.setImageResource(R.drawable.pictures_no);
			
			//�ص�����ͼƬ
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
