package com.documentcenterapp.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
	
	private static final String TAG = "[AnimateFirstDisplayListener] : ";
	
	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		Log.i(TAG, "onLoadingComplete(String, View, Bitmap) called for imageUri : "+imageUri+", view : "+view+", loadedImage : "+loadedImage);
		try{
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !ImageLazyLoadingConfig.displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					ImageLazyLoadingConfig.displayedImages.add(imageUri);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}			
	}
}
