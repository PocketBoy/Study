package com.himi.imageloader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.himi.imageloader.ListImageDirPopupWindow.OnDirSelectedListener;
import com.himi.imageloader.bean.FolderBean;

public class MainActivity extends Activity {
	private GridView mGridView;
	
	private ImageAdapter mImgAdapter;

	private RelativeLayout mBottomLy;
	private TextView mDirName;
	private TextView mDirCount;

	/**
	 * 当前显示图片文件夹路径
	 */
	private File mCurrentDir;
	/**
	 * 当前显示图片文件夹图片数量(数量最多)
	 */
	private int mMaxCount;
	/**
	 * 手机中图片总数
	 */
	private int totalCount;
	private List<String> mImgs;
	private List<FolderBean> mFolderBeans = new ArrayList<FolderBean>();
	private ProgressDialog mProgressDialog;

	private static final int DATA_LOADED = 0x110;
	private ListImageDirPopupWindow mDirPopupWindow;
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (msg.what == DATA_LOADED) {
				mProgressDialog.dismiss();
				// 绑定数据到GridView
				data2View();
				// 初始化PopupWindow
				initDirPopupWindow();
			}
		}
	};

	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initDatas();
		initEvent();
	}

	private void initView() {
		mGridView = (GridView) findViewById(R.id.id_gridView);
		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
		mDirName = (TextView) findViewById(R.id.id_dir_name);
		mDirCount = (TextView) findViewById(R.id.id_dir_count);

	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹 
	 */
	private void initDatas() {

		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "当前存储卡不可用", Toast.LENGTH_SHORT).show();
			return;
		}
		/**
		 * 显示进度条
		 */
		mProgressDialog = ProgressDialog.show(this, null, "正在加载……");
		/**
		 * 扫描手机中所有的图片,很明显这是一个耗时的操作,所以我们不能在UI线程中,采用子线程.
		 * 扫描得到的文件夹及其图片信息 在 List<FolderBean> mFolderBeans存储.
		 */
		new Thread() {
			public void run() {
				Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver cr = MainActivity.this.getContentResolver();
				//只查询jpeg和png的图片  
				Cursor cursor = cr.query(mImgUri, null,
						MediaStore.Images.Media.MIME_TYPE + "? or"
								+ MediaStore.Images.Media.MIME_TYPE + "?",
						new String[] { "image/jpeg", "image/png", },
						MediaStore.Images.Media.DATE_MODIFIED);

				/**
				 * 存放已经遍历的文件夹路径,防止重复遍历
				 */
				Set<String> mDirPaths = new HashSet<String>();
				/**
				 * 遍历手机图片
				 */
				while (cursor.moveToNext()) {
					// 获取图片的路径  
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					// 获取该图片的父路径名 
					File parentFile = new File(path).getParentFile();
					if (parentFile == null) {
						continue;
					}
					String dirPath = parentFile.getAbsolutePath();

					FolderBean folderBean = null;
					 // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）  
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						// 初始化imageFloder  
						folderBean = new FolderBean();
						
						//图片的文件夹路径
						folderBean.setDir(dirPath);
						//第一张图片的路径
						folderBean.setFirstImgPath(path);
					}
                    //有些图片比较诡异~~;无法显示，这里加判断,防止空指针异常
					if (parentFile.list() == null) {
						continue;
					}

					int picSize = parentFile.list(new FilenameFilter() {

						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".jpeg")
									|| filename.endsWith(".png")) {
								return true;
							}
							return false;
						}
					}).length;
					totalCount += picSize; 
					//图片的数量
					folderBean.setCount(picSize);
					mFolderBeans.add(folderBean);
					/**
					 * 如果此时扫描到图片文件夹中图片数量最多,则赋值给mMaxCount,mCurrentDir
					 */
					if (picSize > mMaxCount) {
						mMaxCount = picSize;
						mCurrentDir = parentFile;
					}

				}
				//关闭游标
				cursor.close();
				// 通知handler扫描图片完成
				mHandler.sendEmptyMessage(DATA_LOADED);

			};
		}.start();

	}

	/**
	 * 添加点击事件
	 */
	private void initEvent() {
		mBottomLy.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 设置PopupWindow动画
				mDirPopupWindow.setAnimationStyle(R.style.dir_popupwindow_anim);

				// 设置PopupWindow的出现
				mDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);
				lightOff();

			}
		});

	}
	/**
	 * 初始化展示文件夹的popupWindw 
	 */
	private void initDirPopupWindow() {
		mDirPopupWindow = new ListImageDirPopupWindow(this, mFolderBeans);

		mDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			public void onDismiss() {
				lightOn();

			}
		});

		/**
		 *  设置选择文件夹的回调  
		 */
		mDirPopupWindow.setOnDirSelectedListener(new OnDirSelectedListener() {

			public void onSelected(FolderBean folderBean) {
				mCurrentDir = new File(folderBean.getDir());
				mImgs = Arrays.asList(mCurrentDir.list(new FilenameFilter() {

					public boolean accept(File dir, String filename) {
						if (filename.endsWith(".jpg")
								|| filename.endsWith(".jpeg")
								|| filename.endsWith(".png")) {
							return true;
						}
						return false;
					}
				}));

				mImgAdapter = new ImageAdapter(MainActivity.this, mImgs,
						mCurrentDir.getAbsolutePath());
				mGridView.setAdapter(mImgAdapter);

				mDirCount.setText(mImgs.size() + "");
				mDirName.setText(folderBean.getName());

				mDirPopupWindow.dismiss();
			}
		});

	}

	/**
	 * 内容区域变亮
	 */

	protected void lightOn() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 1.0f;
		getWindow().setAttributes(lp);
	}

	/**
	 * 内容区域变暗
	 */
	protected void lightOff() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = .3f;
		getWindow().setAttributes(lp);

	}

	/**
	 * 为View绑定数据 
	 */
	private void data2View() {
		if (mCurrentDir == null) {
			Toast.makeText(this, "未扫描到任何图片", Toast.LENGTH_SHORT).show();
			return;
		}

		mImgs = Arrays.asList(mCurrentDir.list());
		
		/** 
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗； 
         */  
		mImgAdapter = new ImageAdapter(this, mImgs,
				mCurrentDir.getAbsolutePath());
		mGridView.setAdapter(mImgAdapter);

		mDirCount.setText(mMaxCount + "");
		mDirName.setText(mCurrentDir.getName());

	};

}
