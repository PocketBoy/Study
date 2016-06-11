package com.ryg.imageloader;

import java.util.ArrayList;

import com.ryg.imageloader.loader.ImageLoader;
import com.ryg.imageloader.ui.SmoothImageView;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class SpaceImageDetailActivity extends Activity {
	
	private ArrayList<String> mDatas;
	private int mPosition;
	private int mLocationX;
	private int mLocationY;
	private int mWidth;
	private int mHeight;
	
	SmoothImageView imageview = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mDatas = (ArrayList<String>) getIntent().getSerializableExtra("images");
		mPosition = getIntent().getIntExtra("position", 0);
		mLocationX = getIntent().getIntExtra("locationX", 0);
		mLocationY = getIntent().getIntExtra("locationY", 0);
		mWidth = getIntent().getIntExtra("width", 0);
		mHeight = getIntent().getIntExtra("height", 0);
		
		imageview = new SmoothImageView(this);
		imageview.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
		imageview.transformIn();
		imageview.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
		imageview.setScaleType(ScaleType.FIT_CENTER);
		setContentView(imageview);
		
		
		ImageLoader.build(this).bindBitmap(mDatas.get(mPosition), imageview);
		
	}
	
	/**
	 * 用户点击后退键
	 */
	
	@Override
	public void onBackPressed() {
		imageview.setOnTransformListener(new SmoothImageView.TransformListener() {
			@Override
			public void onTransformComplete(int mode) {
				if (mode == 2) {
					finish();
				}
			}
		});
		imageview.transformOut();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()) {
			overridePendingTransition(0, 0);
		}
	}
}
