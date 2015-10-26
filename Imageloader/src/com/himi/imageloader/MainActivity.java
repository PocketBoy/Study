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
	 * ��ǰ��ʾͼƬ�ļ���·��
	 */
	private File mCurrentDir;
	/**
	 * ��ǰ��ʾͼƬ�ļ���ͼƬ����(�������)
	 */
	private int mMaxCount;
	/**
	 * �ֻ���ͼƬ����
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
				// �����ݵ�GridView
				data2View();
				// ��ʼ��PopupWindow
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
	 * ����ContentProviderɨ���ֻ��е�ͼƬ���˷��������������߳��� ���ͼƬ��ɨ�裬���ջ��jpg�����Ǹ��ļ��� 
	 */
	private void initDatas() {

		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "��ǰ�洢��������", Toast.LENGTH_SHORT).show();
			return;
		}
		/**
		 * ��ʾ������
		 */
		mProgressDialog = ProgressDialog.show(this, null, "���ڼ��ء���");
		/**
		 * ɨ���ֻ������е�ͼƬ,����������һ����ʱ�Ĳ���,�������ǲ�����UI�߳���,�������߳�.
		 * ɨ��õ����ļ��м���ͼƬ��Ϣ �� List<FolderBean> mFolderBeans�洢.
		 */
		new Thread() {
			public void run() {
				Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver cr = MainActivity.this.getContentResolver();
				//ֻ��ѯjpeg��png��ͼƬ  
				Cursor cursor = cr.query(mImgUri, null,
						MediaStore.Images.Media.MIME_TYPE + "? or"
								+ MediaStore.Images.Media.MIME_TYPE + "?",
						new String[] { "image/jpeg", "image/png", },
						MediaStore.Images.Media.DATE_MODIFIED);

				/**
				 * ����Ѿ��������ļ���·��,��ֹ�ظ�����
				 */
				Set<String> mDirPaths = new HashSet<String>();
				/**
				 * �����ֻ�ͼƬ
				 */
				while (cursor.moveToNext()) {
					// ��ȡͼƬ��·��  
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					// ��ȡ��ͼƬ�ĸ�·���� 
					File parentFile = new File(path).getParentFile();
					if (parentFile == null) {
						continue;
					}
					String dirPath = parentFile.getAbsolutePath();

					FolderBean folderBean = null;
					 // ����һ��HashSet��ֹ���ɨ��ͬһ���ļ��У���������жϣ�ͼƬ�����������൱�ֲ���~~��  
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						// ��ʼ��imageFloder  
						folderBean = new FolderBean();
						
						//ͼƬ���ļ���·��
						folderBean.setDir(dirPath);
						//��һ��ͼƬ��·��
						folderBean.setFirstImgPath(path);
					}
                    //��ЩͼƬ�ȽϹ���~~;�޷���ʾ��������ж�,��ֹ��ָ���쳣
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
					//ͼƬ������
					folderBean.setCount(picSize);
					mFolderBeans.add(folderBean);
					/**
					 * �����ʱɨ�赽ͼƬ�ļ�����ͼƬ�������,��ֵ��mMaxCount,mCurrentDir
					 */
					if (picSize > mMaxCount) {
						mMaxCount = picSize;
						mCurrentDir = parentFile;
					}

				}
				//�ر��α�
				cursor.close();
				// ֪ͨhandlerɨ��ͼƬ���
				mHandler.sendEmptyMessage(DATA_LOADED);

			};
		}.start();

	}

	/**
	 * ��ӵ���¼�
	 */
	private void initEvent() {
		mBottomLy.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// ����PopupWindow����
				mDirPopupWindow.setAnimationStyle(R.style.dir_popupwindow_anim);

				// ����PopupWindow�ĳ���
				mDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);
				lightOff();

			}
		});

	}
	/**
	 * ��ʼ��չʾ�ļ��е�popupWindw 
	 */
	private void initDirPopupWindow() {
		mDirPopupWindow = new ListImageDirPopupWindow(this, mFolderBeans);

		mDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			public void onDismiss() {
				lightOn();

			}
		});

		/**
		 *  ����ѡ���ļ��еĻص�  
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
	 * �����������
	 */

	protected void lightOn() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 1.0f;
		getWindow().setAttributes(lp);
	}

	/**
	 * ��������䰵
	 */
	protected void lightOff() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = .3f;
		getWindow().setAttributes(lp);

	}

	/**
	 * ΪView������ 
	 */
	private void data2View() {
		if (mCurrentDir == null) {
			Toast.makeText(this, "δɨ�赽�κ�ͼƬ", Toast.LENGTH_SHORT).show();
			return;
		}

		mImgs = Arrays.asList(mCurrentDir.list());
		
		/** 
         * ���Կ����ļ��е�·����ͼƬ��·���ֿ����棬����ļ������ڴ�����ģ� 
         */  
		mImgAdapter = new ImageAdapter(this, mImgs,
				mCurrentDir.getAbsolutePath());
		mGridView.setAdapter(mImgAdapter);

		mDirCount.setText(mMaxCount + "");
		mDirName.setText(mCurrentDir.getName());

	};

}
