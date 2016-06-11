package com.ryg.imageloader.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;
/**
 * ��ȡͼƬ������ͼ�������ر�����ThumbnailUtils������
 * @author hebao
 *
 */
public class ImageThumbnailUtils {
	private static final String TAG = "ImageThumbnailUtils";

	public ImageThumbnailUtils() {
		
	}
	
	/**  
     * ����ָ����ͼ��·���ʹ�С����ȡ����ͼ  
     * �˷���������ô���  
     *     1. ʹ�ý�С���ڴ�ռ䣬��һ�λ�ȡ��bitmapʵ����Ϊnull��ֻ��Ϊ�˶�ȡ��Ⱥ͸߶ȣ�  
     *        �ڶ��ζ�ȡ��bitmap�Ǹ��ݱ���ѹ������ͼ�񣬵����ζ�ȡ��bitmap����Ҫ������ͼ��  
     *     2. ����ͼ����ԭͼ������û�����죬����ʹ����2.2�汾���¹���ThumbnailUtils��ʹ  
     *        ������������ɵ�ͼ�񲻻ᱻ���졣  
     * @param imagePath ͼ���·��  
     * @param width ָ�����ͼ��Ŀ��  
     * @param height ָ�����ͼ��ĸ߶�  
     * @return ���ɵ�����ͼ  
     */    
    private Bitmap getImageThumbnail(String imagePath, 
    		int reqWidth, int reqHeight) {    
        Bitmap bitmap = null;    
        BitmapFactory.Options options = new BitmapFactory.Options(); 
        options.inJustDecodeBounds = true;    
        
        // ��ȡ���ͼƬ�Ŀ�͸ߣ�ע��˴���bitmapΪnull    
        bitmap = BitmapFactory.decodeFile(imagePath, options);    
         
        /*// �������ű�    
        int h = options.outHeight;    
        int w = options.outWidth;    
        int beWidth = w / width;    
        int beHeight = h / height;    
        int be = 1;    
        if (beWidth < beHeight) {    
            be = beWidth;    
        } else {    
            be = beHeight;    
        }    
        if (be <= 0) {    
            be = 1;    
        }    
        options.inSampleSize = be; */   
        
        options.inSampleSize =calculateInSampleSize(options, reqWidth,
        		reqHeight);
        options.inJustDecodeBounds = false; // ��Ϊ false   
        
        // ���¶���ͼƬ����ȡ���ź��bitmap��ע�����Ҫ��options.inJustDecodeBounds ��Ϊ false    
        bitmap = BitmapFactory.decodeFile(imagePath, options);    
        // ����ThumbnailUtils����������ͼ������Ҫָ��Ҫ�����ĸ�Bitmap����    
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, reqWidth, reqHeight,    
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);    
        return bitmap;    
    }    
    

    public int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(TAG, "origin, w= " + width + " h=" + height);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        Log.d(TAG, "sampleSize:" + inSampleSize);
        return inSampleSize;
    }
}
