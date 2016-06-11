package com.ryg.imageloader.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;
/**
 * 获取图片的缩略图，这里特别适用ThumbnailUtils工具类
 * @author hebao
 *
 */
public class ImageThumbnailUtils {
	private static final String TAG = "ImageThumbnailUtils";

	public ImageThumbnailUtils() {
		
	}
	
	/**  
     * 根据指定的图像路径和大小来获取缩略图  
     * 此方法有两点好处：  
     *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，  
     *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。  
     *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使  
     *        用这个工具生成的图像不会被拉伸。  
     * @param imagePath 图像的路径  
     * @param width 指定输出图像的宽度  
     * @param height 指定输出图像的高度  
     * @return 生成的缩略图  
     */    
    private Bitmap getImageThumbnail(String imagePath, 
    		int reqWidth, int reqHeight) {    
        Bitmap bitmap = null;    
        BitmapFactory.Options options = new BitmapFactory.Options(); 
        options.inJustDecodeBounds = true;    
        
        // 获取这个图片的宽和高，注意此处的bitmap为null    
        bitmap = BitmapFactory.decodeFile(imagePath, options);    
         
        /*// 计算缩放比    
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
        options.inJustDecodeBounds = false; // 设为 false   
        
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false    
        bitmap = BitmapFactory.decodeFile(imagePath, options);    
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象    
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
