package com.ryg.imageloader.loader;

import java.io.FileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * 获取图片的缩略图
 * @author hebao
 *
 */
public class ImageResizer {
    private static final String TAG = "ImageResizer";

    public ImageResizer() {
    }

    /**
     * 
     * @param res 原图片资源
     * @param resId 原图片资源id
     * @param reqWidth 缩略图的宽度
     * @param reqHeight 缩略图的高度
     * 
     * @return Bitmap  返回缩略图的Bitmap
     */
    public Bitmap decodeSampledBitmapFromResource(Resources res,
            int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    
	/**
	 * 
	 * @param fd 文件描述符
	 * @param reqWidth 缩略图的宽度
	 * @param reqHeight 缩略图的高度
	 * 
	 * @return Bitmap  返回缩略图的Bitmap
	 */
    public Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor fd, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }
    
    
	/**
	 * 
	 * @param options 
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
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
