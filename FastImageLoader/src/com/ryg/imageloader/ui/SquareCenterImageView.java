package com.ryg.imageloader.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
/**
 * 正方形的ImageView
 * @author dty
 *
 */
public class SquareCenterImageView extends ImageView {
    public SquareCenterImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
 
    public SquareCenterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public SquareCenterImageView(Context context) {
        super(context);
    }
    
    @Override
    public void setImageBitmap(Bitmap bm) {
    	Log.d("roamer", "bm.getWidth():"+bm.getWidth());
    	Log.d("roamer", "bm.getHeight():"+bm.getHeight());
    	
    	super.setImageBitmap(bm);
    }
    
    private void clipCenterImage(){
    	
    }
 
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // For simple implementation, or internal size is always 0.
        // We depend on the container to specify the layout size of
        // our view. We can't really know what it is since we will be
        // adding and removing different arbitrary views and do not
        // want the layout to change as this happens.
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
 
        // Children are just made to fill our space.
        int childWidthSize = getMeasuredWidth();
        int childHeightSize = getMeasuredHeight();
        //高度和宽度一�?
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}


