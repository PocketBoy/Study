package com.ryg.imageloader;

import java.util.ArrayList;
import java.util.List;

import com.ryg.chapter_12.R;
import com.ryg.imageloader.loader.ImageLoader;
import com.ryg.imageloader.utils.MyUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnScrollListener {
	private static final String TAG = "MainActivity";

	private List<String> mUrList = new ArrayList<String>();
	ImageLoader mImageLoader;
	private GridView mImageGridView;
	private BaseAdapter mImageAdapter;

	private boolean mIsGridViewIdle = true;
	private int mImageWidth = 0;
	private boolean mIsWifi = false;
	private boolean mCanGetBitmapFromNetWork = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initData();
		initView();
		mImageLoader = ImageLoader.build(MainActivity.this);
	}

	private void initData() {
		String[] imageUrls = {
				"http://b.hiphotos.baidu.com/zhidao/pic/item/a6efce1b9d16fdfafee0cfb5b68f8c5495ee7bd8.jpg",
				"http://pic47.nipic.com/20140830/7487939_180041822000_2.jpg",
				"http://pic41.nipic.com/20140518/4135003_102912523000_2.jpg",
				"http://img2.imgtn.bdimg.com/it/u=1133260524,1171054226&fm=21&gp=0.jpg",
				"http://h.hiphotos.baidu.com/image/pic/item/3b87e950352ac65c0f1f6e9efff2b21192138ac0.jpg",
				"http://pic42.nipic.com/20140618/9448607_210533564001_2.jpg",
				"http://pic10.nipic.com/20101027/3578782_201643041706_2.jpg",
				"http://picview01.baomihua.com/photos/20120805/m_14_634797817549375000_37810757.jpg",
				"http://img2.3lian.com/2014/c7/51/d/26.jpg", "http://img3.3lian.com/2013/c1/34/d/93.jpg",
				"http://b.zol-img.com.cn/desk/bizhi/image/3/960x600/1375841395686.jpg",
				"http://picview01.baomihua.com/photos/20120917/m_14_634834710114218750_41852580.jpg",
				"http://cdn.duitang.com/uploads/item/201311/03/20131103171224_rr2aL.jpeg",
				"http://imgrt.pconline.com.cn/images/upload/upc/tx/wallpaper/1210/17/c1/spcgroup/14468225_1350443478079_1680x1050.jpg",
				"http://pic41.nipic.com/20140518/4135003_102025858000_2.jpg",
				"http://www.1tong.com/uploads/wallpaper/landscapes/200-4-730x456.jpg",
				"http://pic.58pic.com/58pic/13/00/22/32M58PICV6U.jpg",
				"http://picview01.baomihua.com/photos/20120629/m_14_634765948339062500_11778706.jpg",
				"http://h.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=429e7b1b92ef76c6d087f32fa826d1cc/7acb0a46f21fbe09cc206a2e69600c338744ad8a.jpg",
				"http://pica.nipic.com/2007-12-21/2007122115114908_2.jpg",
				"http://cdn.duitang.com/uploads/item/201405/13/20140513212305_XcKLG.jpeg",
				"http://photo.loveyd.com/uploads/allimg/080618/1110324.jpg",
				"http://img4.duitang.com/uploads/item/201404/17/20140417105820_GuEHe.thumb.700_0.jpeg",
				"http://cdn.duitang.com/uploads/item/201204/21/20120421155228_i52eX.thumb.600_0.jpeg",
				"http://img4.duitang.com/uploads/item/201404/17/20140417105856_LTayu.thumb.700_0.jpeg",
				"http://img04.tooopen.com/images/20130723/tooopen_20530699.jpg",
				"http://www.qjis.com/uploads/allimg/120612/1131352Y2-16.jpg",
				"http://pic.dbw.cn/0/01/33/59/1335968_847719.jpg",
				"http://a.hiphotos.baidu.com/image/pic/item/a8773912b31bb051a862339c337adab44bede0c4.jpg",
				"http://h.hiphotos.baidu.com/image/pic/item/f11f3a292df5e0feeea8a30f5e6034a85edf720f.jpg",
				"http://img0.pconline.com.cn/pconline/bizi/desktop/1412/ER2.jpg",
				"http://pic.58pic.com/58pic/11/25/04/91v58PIC6Xy.jpg", "http://img3.3lian.com/2013/c2/32/d/101.jpg",
				"http://pic25.nipic.com/20121210/7447430_172514301000_2.jpg",
				"http://img02.tooopen.com/images/20140320/sy_57121781945.jpg",
				"http://www.renyugang.cn/emlog/content/plugins/kl_album/upload/201004/852706aad6df6cd839f1211c358f2812201004120651068641.jpg",
				"http://imgsrc.baidu.com/forum/w%3D580/sign=7294122b5cee3d6d22c687c373176d41/fb628535e5dde711b346427aa2efce1b9c1661a1.jpg",
				"http://www.feelp.com/uploads/allimg/150807/051935Wa_1.png",
				"http://img0.imgtn.bdimg.com/it/u=488371062,3701804952&fm=21&gp=0.jpg",
				"http://img3.yxlady.com/yl/UploadFiles_5361/20150721/20150721102115597.jpg",
				"http://imgsrc.baidu.com/forum/pic/item/4d086e061d950a7ba13505890ad162d9f2d3c97f.jpg",
				"http://imgsrc.baidu.com/forum/w%3D580/sign=352f13a23af33a879e6d0012f65d1018/2e2eb9389b504fc235742097e5dde71190ef6d7d.jpg",
				"http://imgsrc.baidu.com/forum/pic/item/f254eb502b3647751138c27d.jpg",
				"http://gb.cri.cn/mmsource/images/2014/07/25/31/5127827029518456463.jpg",
				"http://imgsrc.baidu.com/forum/pic/item/d53bb9389b504fc2d6670c88e5dde71190ef6d1c.jpg",
				"http://img3.imgtn.bdimg.com/it/u=1472430227,1296758422&fm=11&gp=0.jpg",
				"http://img5.imgtn.bdimg.com/it/u=2642059724,3475166611&fm=11&gp=0.jpg",
				"http://img3.imgtn.bdimg.com/it/u=3493704925,904469772&fm=23&gp=0.jpg",
				"http://img4.imgtn.bdimg.com/it/u=3602198515,906823699&fm=23&gp=0.jpg",
				"http://imgsrc.baidu.com/baike/pic/item/8bc3a7012e787d6e728da5f3.jpg",
				"http://img1.imgtn.bdimg.com/it/u=1928749733,1528082564&fm=21&gp=0.jpg",
				"http://img0.imgtn.bdimg.com/it/u=1213916353,2023809785&fm=21&gp=0.jpg",
                "http://pic.58pic.com/58pic/15/13/05/58PIC8D58PICW78_1024.jpg",
                "http://pic49.nipic.com/file/20140924/2531170_152254451000_2.jpg",
                "http://pic49.nipic.com/file/20140924/2531170_152302445000_2.jpg"

		};

		for (String url : imageUrls) {
			mUrList.add(url);
		}
		int screenWidth = MyUtils.getScreenMetrics(this).widthPixels;
		int space = (int) MyUtils.dp2px(this, 20f);
		mImageWidth = (screenWidth - space) / 3;
		mIsWifi = MyUtils.isWifi(this);
		if (mIsWifi) {
			mCanGetBitmapFromNetWork = true;
		}
	}

	private void initView() {
		mImageGridView = (GridView) findViewById(R.id.gridView1);
		mImageAdapter = new ImageAdapter(this);
		mImageGridView.setAdapter(mImageAdapter);
		mImageGridView.setOnScrollListener(this);

		if (!mIsWifi) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("初次使用会从网络下载大概5MB的图片，确认要下载吗�?");
			builder.setTitle("注意");
			builder.setPositiveButton("�?", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mCanGetBitmapFromNetWork = true;
					mImageAdapter.notifyDataSetChanged();
				}
			});
			builder.setNegativeButton("�?", null);
			builder.show();
		}
	}

	private class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Drawable mDefaultBitmapDrawable;

		private ImageAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
			mDefaultBitmapDrawable = context.getResources().getDrawable(R.drawable.image_default);
		}

		@Override
		public int getCount() {
			return mUrList.size();
		}

		@Override
		public String getItem(int position) {
			return mUrList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.image_list_item, parent, false);
				holder = new ViewHolder();
				holder.imageView = (ImageView) convertView.findViewById(R.id.image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final ImageView imageView = holder.imageView;
			final String tag = (String) imageView.getTag();
			final String uri = getItem(position);
			if (!uri.equals(tag)) {
				imageView.setImageDrawable(mDefaultBitmapDrawable);
			}
			if (mIsGridViewIdle && mCanGetBitmapFromNetWork) {
				imageView.setTag(uri);
				mImageLoader.bindBitmap(uri, imageView, mImageWidth, mImageWidth);
			}
			
			/**
			 * 给GridView中的Item(ImageView)设置监听，完成点击选中，实现全屏显示
			 */
			imageView.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, SpaceImageDetailActivity.class);
					intent.putExtra("images", (ArrayList<String>) mUrList);
					intent.putExtra("position", position);
					
					int[] location = new int[2];
					imageView.getLocationOnScreen(location);
					intent.putExtra("locationX", location[0]);
					intent.putExtra("locationY", location[1]);
					
					intent.putExtra("width", imageView.getWidth());
					intent.putExtra("height", imageView.getHeight());
					startActivity(intent);
					/*
					 * Activity A 跳转至Activity B的时候:
					 * Activity A 有退出动画，Activity B 有进入动画。
					 * overridePendingTransition(int enterAnim, int exitAnim)
					 *  enterAnim 定义Activity进入屏幕时的动画
                     * 
					 *  exitAnim 定义Activity退出屏幕时的动画
					 *
					 */
					overridePendingTransition(0, 0);
				}
			});
			
			return convertView;
		}

	}

	private static class ViewHolder {
		public ImageView imageView;
	}

	/**
	 * onScrollStateChanged在GirdView状�?�改变时被调�?
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		/**
		 * 可以用来获取当前View的状态： 空闲:SCROLL_STATE_IDLE 滑动停止
		 * 滑动:SCROLL_STATE_TOUCH_SCROLL 手接触ScrollView触发�?�?
		 * 惯�?�滑�?:SCROLL_STATE_FLING 正在滚动
		 * 
		 * 总结�? ScrollView 图片列表的滚动，应该在： SCROLL_STATE_FLING
		 * 时让图片不显示，提高滚动性能让滚动小姑更平滑 SCROLL_STATE_IDLE 时显示当前屏幕可见的图片
		 */
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			mIsGridViewIdle = true;
			mImageAdapter.notifyDataSetChanged();
		} else {
			mIsGridViewIdle = false;
		}
	}

	/**
	 * 在GirdView滑动过程中被调用
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// ignored

	}
}
