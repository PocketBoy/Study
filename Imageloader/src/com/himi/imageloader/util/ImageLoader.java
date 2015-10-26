package com.himi.imageloader.util;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * 图片加载类
 * 这个类使用单例模式
 * @author hebao
 *
 */
public class ImageLoader {
	private static ImageLoader mInstance;
	/**
	 * 图片缓存的核心对象 
	 *      管理我们所有图片加载的所需的内存
	 */
	private LruCache<String, Bitmap> mLruCache;
	/**
	 * 线程池
	 *      执行一些我们加载图片的任务
	 */
	private ExecutorService mThreadPool;
	/**
	 * 线程池中默认线程数
	 */
	private static final int DEAFULT_THREAD_COUNT = 1;
	
	/**
	 * 队列的调度方式
	 */
	private Type mType = Type.LIFO;
	/**
	 * 任务队列
	 * 		任务队列提供给线程池取任务的
	 */
	private LinkedList<Runnable> mTaskQueue;
	/**
	 * 后台轮询线程
	 */
	private Thread mPoolThread;
	/**
	 * 后台轮询线程的handler
	 */
	private Handler mPoolThreadHandler;
	/**
	 * UI线程的handler
	 * 用于：更新ImageView
	 */
	private Handler mUIHandler;
	/**
	 * mPoolThreadHandler的信号量,防止使用mPoolThreadHandler的时候其本身没有初始化完毕，报空指针异常
	 */
	private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
	/**
	 * 任务线程信号量，保证线程池真正做到LIFO
	 */
	private Semaphore mSemaphoreThreadPool;
	
	/**
	 * 
	 * 调度方式
	 *FIFO：先入先出
	 *LIFO：后入先出
	 */
	
	public enum Type  {
		FIFO,LIFO;
	}
	
	
	private ImageLoader(int threadCount, Type type) {
		init(threadCount, type);
	}
	
	/**
	 * 初始化操作
	 * @param threadCount
	 * @param type
	 */
	private void init(int threadCount, Type type) {
		//后台轮询线程初始化
		mPoolThread = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				mPoolThreadHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						//线程池取出一个任务进行执行
						mThreadPool.execute(getTask());
						try {
							mSemaphoreThreadPool.acquire();
						} catch (InterruptedException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
					}
				};
				//释放一个信号量
				mSemaphorePoolThreadHandler.release();
				//Looper不断进行轮询
				Looper.loop();
			};
		};
		mPoolThread.start();
		
		//获取我们应用的最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheMemory = maxMemory / 8;
		//图片缓存初始化
		mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
			/**
			 * 测量每一个Bitmap图片的大小
			 */
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// 每一个Bitmap图片的大小 = 每一行字节数 * 高度
				return value.getRowBytes() * value.getHeight();
			}
		};
		
		//创建线程池
		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mTaskQueue = new LinkedList<Runnable>();
		mType = type;
		
		//初始化信号量
		mSemaphoreThreadPool = new Semaphore(threadCount);
	}
	
	/**
	 * 从任务队列中取出一个方法 
	 * @return
	 */
	private Runnable getTask() {
		if(mType == Type.FIFO) {
			return mTaskQueue.removeFirst();
		}else if(mType == Type.LIFO) {
			return mTaskQueue.removeLast();
		}
		return null;
	}
	

	public static ImageLoader getInstance() {
		if(mInstance == null) {
			synchronized (ImageLoader.class) {
				if(mInstance == null) {
					mInstance = new ImageLoader(DEAFULT_THREAD_COUNT, Type.LIFO);
				}
			}
			
		}
		return mInstance;
	}
	
	public static ImageLoader getInstance(int threadCount, Type type) {
		if(mInstance == null) {
			synchronized (ImageLoader.class) {
				if(mInstance == null) {
					mInstance = new ImageLoader(threadCount, type);
				}
			}
			
		}
		return mInstance;
	}
	
	
	/**
	 * 根据path为ImageView是设置图片
	 * @param path
	 * @param imageView
	 */
	public void loadImage(final String path, final ImageView imageView ) {
		imageView.setTag(path);//设置Tag主要是为了校验，防止图片的混乱
		if(mUIHandler == null) {
			mUIHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					//获取得到图片,为imageview回调设置图片
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					Bitmap bm = holder.bitmap;
					ImageView imageview = holder.imageView;
					String path = holder.path;
					/**
					 * 将path和getTag存储路径进行比较
					 * 如果不比较，就会出现我们滑动到第二张图片，但是显示的还是第一张的图片
					 * 这里我们绑定imageview和path就是为了防止这种情况
					 */
					if(imageview.getTag().toString().equals(path)) {
						imageview.setImageBitmap(bm);
					}
					
				};
			};
		}
		//根据path在缓存中获取bitmap
		Bitmap bm = getBitmapFromLruCache(path);
		if(bm != null) {
			refreashBitmap(path, imageView, bm);	
		} else {//内存中没有图片，加载图片到内存
			addTasks(new Runnable() {
				public void run() {
					/**加载图片
					 *  图片的压缩
					 */
					//1. 获得图片需要显示的大小
					ImageSize imageSize = getImageViewSize(imageView);
					//2. 压缩图片
					Bitmap bm = decodeSampleBitmapFromPath(path,imageSize.width,imageSize.height);
					//3. 把图片加载到缓存 (一定要记得)
					addBitmapToLruCache(path,bm);		
					refreashBitmap(path, imageView, bm);	
					//每次线程任务加载完图片，之后释放一个信号量,即:信号量-1,此时就会寻找下一个任务(根据FIFO/LIFO不同的策略取出任务)
					mSemaphoreThreadPool.release();
				}

			});
		}
	}
	
	
	public void refreashBitmap(final String path,
			final ImageView imageView, Bitmap bm) {
		Message message = Message.obtain();	
		ImgBeanHolder holder = new ImgBeanHolder();
		holder.bitmap = bm;
		holder.path = path;
		holder.imageView = imageView;
		
		message.obj = holder;
		mUIHandler.sendMessage(message);
	}				
	
	/**
	 * 将图片加入缓存LruCache
	 * @param path
	 * @param bm
	 */
	private void addBitmapToLruCache(String path, Bitmap bm) {
		if(getBitmapFromLruCache(path) == null) {
			if(bm != null) {
				mLruCache.put(path, bm);
			}
		}
		
	}

	
	/**
	 * 根据图片需要显示的宽和高，对图片进行压缩
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	private Bitmap decodeSampleBitmapFromPath(String path,
			int width, int height) {
		//获取图片的宽和高,但是不把图片加载到内存中
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds =true;//不把图片加载到内存中
		BitmapFactory.decodeFile(path, options);
		
		options.inSampleSize = caculateInSampleSize(options,width, height);//计算获取压缩比
		//使用获取到的inSampleSize再次解析图片
		options.inJustDecodeBounds =false;//加载图片到内存
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		
		
		return bitmap;
	}

	
	/**
	 *根据需求的宽和高，以及图片实际的宽和高，计算inSampleSize
	 * @param options
	 * @param width
	 * @param height
	 * @return inSampleSize 压缩比
	 */
	private int caculateInSampleSize(Options options, int reqWidth, int reqHeight) {
		int width = options.outWidth;
		int height = options.outHeight;
		
		int inSampleSize = 1;
		if(width>reqWidth || height > reqHeight) {
			int widthRadio = Math.round(width*1.0f / reqWidth);
			int heightRadio = Math.round(height*1.0f / reqHeight);
			
			inSampleSize = Math.max(widthRadio, heightRadio);	
		}
		
		return inSampleSize;
	}

	/**
	 * 根据ImageView获取适当的压缩的宽和高
	 * @param imageView
	 * @return
	 */
	protected ImageSize getImageViewSize(ImageView imageView) {
		ImageSize imageSize = new ImageSize();
		DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
		LayoutParams lp = imageView.getLayoutParams();
		
		int width = imageView.getWidth();//获取imageview的实际宽度
		if(width<=0) {
			width = lp.width;//获取imageview在layout中声明的宽度
		}
		if(width<=0) {
			width = getImageViewFieldValue(imageView, "mMaxWidth");//利用反射,检测获得最大值
		}
		if(width<=0) {
			width = displayMetrics.widthPixels;
		}
		
		
		int height = imageView.getHeight();//获取imageview的实际高度
		if(height<=0) {
			height = lp.height;//获取imageview在layout中声明的高度
		}
		if(height<=0) {
			height = getImageViewFieldValue(imageView, "mMaxHeight");//利用反射,检测获得最大值
		}
		if(height<=0) {
			height = displayMetrics.heightPixels;
		}
		
		imageSize.width = width;
		imageSize.height = height;
		return imageSize;
	};
	
/**
 * 
 * 通过反射获取imageview的某个属性值
 * @param object
 * @param fieldName
 * @return
 * 由于方法getMaxHeight是API16以上的才能使用，这里我们用反射使用这个方法
 */
private static int getImageViewFieldValue(Object object, String fieldName) {
	int value=0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);

			int fieldValue = field.getInt(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	return value;
}
	
/**
 * 添加任务到任务队列，交给线程池执行
 * @param runnable
 */
    @SuppressLint("NewApi")
	private synchronized void addTasks(Runnable runnable) {//synchronized同步代码，防止多个线程进来出现死锁
		mTaskQueue.add(runnable);
		//if(mPoolThreadHandler == null) wait();
		//确保我们在使用mPoolThreadHandler之前，我们初始化完毕mPoolThreadHandler(不为空)，这里引入信号量
		try {
			if(mPoolThreadHandler == null) {
				mSemaphorePoolThreadHandler.acquire();
			}	
		} catch (InterruptedException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		mPoolThreadHandler.sendEmptyMessage(0x110);
		
		
	}
	

	/**
     * 根据path在缓存中获取bitmap
     * @param key
     * @return
     */
	private Bitmap getBitmapFromLruCache(String key) {
		// TODO 自动生成的方法存根
		return mLruCache.get(key);
	}
	
	/**
	 * 压缩图片之后的宽和高
	 * @author Administrator
	 *
	 */
	private class ImageSize {
		int width;
		int height;
	}
	
	private class ImgBeanHolder {
		Bitmap bitmap;
		ImageView imageView;
		String path;
	}

}
