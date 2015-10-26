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
 * ͼƬ������
 * �����ʹ�õ���ģʽ
 * @author hebao
 *
 */
public class ImageLoader {
	private static ImageLoader mInstance;
	/**
	 * ͼƬ����ĺ��Ķ��� 
	 *      ������������ͼƬ���ص�������ڴ�
	 */
	private LruCache<String, Bitmap> mLruCache;
	/**
	 * �̳߳�
	 *      ִ��һЩ���Ǽ���ͼƬ������
	 */
	private ExecutorService mThreadPool;
	/**
	 * �̳߳���Ĭ���߳���
	 */
	private static final int DEAFULT_THREAD_COUNT = 1;
	
	/**
	 * ���еĵ��ȷ�ʽ
	 */
	private Type mType = Type.LIFO;
	/**
	 * �������
	 * 		��������ṩ���̳߳�ȡ�����
	 */
	private LinkedList<Runnable> mTaskQueue;
	/**
	 * ��̨��ѯ�߳�
	 */
	private Thread mPoolThread;
	/**
	 * ��̨��ѯ�̵߳�handler
	 */
	private Handler mPoolThreadHandler;
	/**
	 * UI�̵߳�handler
	 * ���ڣ�����ImageView
	 */
	private Handler mUIHandler;
	/**
	 * mPoolThreadHandler���ź���,��ֹʹ��mPoolThreadHandler��ʱ���䱾��û�г�ʼ����ϣ�����ָ���쳣
	 */
	private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
	/**
	 * �����߳��ź�������֤�̳߳���������LIFO
	 */
	private Semaphore mSemaphoreThreadPool;
	
	/**
	 * 
	 * ���ȷ�ʽ
	 *FIFO�������ȳ�
	 *LIFO�������ȳ�
	 */
	
	public enum Type  {
		FIFO,LIFO;
	}
	
	
	private ImageLoader(int threadCount, Type type) {
		init(threadCount, type);
	}
	
	/**
	 * ��ʼ������
	 * @param threadCount
	 * @param type
	 */
	private void init(int threadCount, Type type) {
		//��̨��ѯ�̳߳�ʼ��
		mPoolThread = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				mPoolThreadHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						//�̳߳�ȡ��һ���������ִ��
						mThreadPool.execute(getTask());
						try {
							mSemaphoreThreadPool.acquire();
						} catch (InterruptedException e) {
							// TODO �Զ����ɵ� catch ��
							e.printStackTrace();
						}
					}
				};
				//�ͷ�һ���ź���
				mSemaphorePoolThreadHandler.release();
				//Looper���Ͻ�����ѯ
				Looper.loop();
			};
		};
		mPoolThread.start();
		
		//��ȡ����Ӧ�õ��������ڴ�
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheMemory = maxMemory / 8;
		//ͼƬ�����ʼ��
		mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
			/**
			 * ����ÿһ��BitmapͼƬ�Ĵ�С
			 */
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// ÿһ��BitmapͼƬ�Ĵ�С = ÿһ���ֽ��� * �߶�
				return value.getRowBytes() * value.getHeight();
			}
		};
		
		//�����̳߳�
		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mTaskQueue = new LinkedList<Runnable>();
		mType = type;
		
		//��ʼ���ź���
		mSemaphoreThreadPool = new Semaphore(threadCount);
	}
	
	/**
	 * �����������ȡ��һ������ 
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
	 * ����pathΪImageView������ͼƬ
	 * @param path
	 * @param imageView
	 */
	public void loadImage(final String path, final ImageView imageView ) {
		imageView.setTag(path);//����Tag��Ҫ��Ϊ��У�飬��ֹͼƬ�Ļ���
		if(mUIHandler == null) {
			mUIHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					//��ȡ�õ�ͼƬ,Ϊimageview�ص�����ͼƬ
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					Bitmap bm = holder.bitmap;
					ImageView imageview = holder.imageView;
					String path = holder.path;
					/**
					 * ��path��getTag�洢·�����бȽ�
					 * ������Ƚϣ��ͻ�������ǻ������ڶ���ͼƬ��������ʾ�Ļ��ǵ�һ�ŵ�ͼƬ
					 * �������ǰ�imageview��path����Ϊ�˷�ֹ�������
					 */
					if(imageview.getTag().toString().equals(path)) {
						imageview.setImageBitmap(bm);
					}
					
				};
			};
		}
		//����path�ڻ����л�ȡbitmap
		Bitmap bm = getBitmapFromLruCache(path);
		if(bm != null) {
			refreashBitmap(path, imageView, bm);	
		} else {//�ڴ���û��ͼƬ������ͼƬ���ڴ�
			addTasks(new Runnable() {
				public void run() {
					/**����ͼƬ
					 *  ͼƬ��ѹ��
					 */
					//1. ���ͼƬ��Ҫ��ʾ�Ĵ�С
					ImageSize imageSize = getImageViewSize(imageView);
					//2. ѹ��ͼƬ
					Bitmap bm = decodeSampleBitmapFromPath(path,imageSize.width,imageSize.height);
					//3. ��ͼƬ���ص����� (һ��Ҫ�ǵ�)
					addBitmapToLruCache(path,bm);		
					refreashBitmap(path, imageView, bm);	
					//ÿ���߳����������ͼƬ��֮���ͷ�һ���ź���,��:�ź���-1,��ʱ�ͻ�Ѱ����һ������(����FIFO/LIFO��ͬ�Ĳ���ȡ������)
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
	 * ��ͼƬ���뻺��LruCache
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
	 * ����ͼƬ��Ҫ��ʾ�Ŀ�͸ߣ���ͼƬ����ѹ��
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	private Bitmap decodeSampleBitmapFromPath(String path,
			int width, int height) {
		//��ȡͼƬ�Ŀ�͸�,���ǲ���ͼƬ���ص��ڴ���
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds =true;//����ͼƬ���ص��ڴ���
		BitmapFactory.decodeFile(path, options);
		
		options.inSampleSize = caculateInSampleSize(options,width, height);//�����ȡѹ����
		//ʹ�û�ȡ����inSampleSize�ٴν���ͼƬ
		options.inJustDecodeBounds =false;//����ͼƬ���ڴ�
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		
		
		return bitmap;
	}

	
	/**
	 *��������Ŀ�͸ߣ��Լ�ͼƬʵ�ʵĿ�͸ߣ�����inSampleSize
	 * @param options
	 * @param width
	 * @param height
	 * @return inSampleSize ѹ����
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
	 * ����ImageView��ȡ�ʵ���ѹ���Ŀ�͸�
	 * @param imageView
	 * @return
	 */
	protected ImageSize getImageViewSize(ImageView imageView) {
		ImageSize imageSize = new ImageSize();
		DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
		LayoutParams lp = imageView.getLayoutParams();
		
		int width = imageView.getWidth();//��ȡimageview��ʵ�ʿ��
		if(width<=0) {
			width = lp.width;//��ȡimageview��layout�������Ŀ��
		}
		if(width<=0) {
			width = getImageViewFieldValue(imageView, "mMaxWidth");//���÷���,��������ֵ
		}
		if(width<=0) {
			width = displayMetrics.widthPixels;
		}
		
		
		int height = imageView.getHeight();//��ȡimageview��ʵ�ʸ߶�
		if(height<=0) {
			height = lp.height;//��ȡimageview��layout�������ĸ߶�
		}
		if(height<=0) {
			height = getImageViewFieldValue(imageView, "mMaxHeight");//���÷���,��������ֵ
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
 * ͨ�������ȡimageview��ĳ������ֵ
 * @param object
 * @param fieldName
 * @return
 * ���ڷ���getMaxHeight��API16���ϵĲ���ʹ�ã����������÷���ʹ���������
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
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	return value;
}
	
/**
 * �������������У������̳߳�ִ��
 * @param runnable
 */
    @SuppressLint("NewApi")
	private synchronized void addTasks(Runnable runnable) {//synchronizedͬ�����룬��ֹ����߳̽�����������
		mTaskQueue.add(runnable);
		//if(mPoolThreadHandler == null) wait();
		//ȷ��������ʹ��mPoolThreadHandler֮ǰ�����ǳ�ʼ�����mPoolThreadHandler(��Ϊ��)�����������ź���
		try {
			if(mPoolThreadHandler == null) {
				mSemaphorePoolThreadHandler.acquire();
			}	
		} catch (InterruptedException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		mPoolThreadHandler.sendEmptyMessage(0x110);
		
		
	}
	

	/**
     * ����path�ڻ����л�ȡbitmap
     * @param key
     * @return
     */
	private Bitmap getBitmapFromLruCache(String key) {
		// TODO �Զ����ɵķ������
		return mLruCache.get(key);
	}
	
	/**
	 * ѹ��ͼƬ֮��Ŀ�͸�
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
