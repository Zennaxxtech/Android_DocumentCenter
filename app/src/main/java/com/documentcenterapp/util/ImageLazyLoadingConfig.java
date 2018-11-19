package com.documentcenterapp.util;

import android.content.Context;
import android.util.Log;

import com.documentcenterapp.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ImageLazyLoadingConfig {
	public static File cacheDir;
	public static DisplayImageOptions displayImageOptions;
	public static ImageLoaderConfiguration imageLoaderConfiguration;
	public static List<String> displayedImages;
	private static String tag = "ImageLazyLoadingConfig";
	public static int defaltImageLoading = R.drawable.ic_launcher;
	
	public static void setDefaultImageLoading(int defaultImageLoading){
		ImageLazyLoadingConfig.defaltImageLoading = defaultImageLoading;
	}
	
	public static void init(Context context) {
		try {									

			displayImageOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(defaltImageLoading)
			.showImageForEmptyUri(defaltImageLoading)
			.showImageOnFail(defaltImageLoading)
			.cacheInMemory(true).cacheOnDisc(true)
			.considerExifParams(true)
			//.displayer(new RoundedBitmapDisplayer(20))
			.build();
			
			/*
			 * initializing default Image loading to its default value
			 */
			defaltImageLoading = R.drawable.ic_launcher;

			cacheDir = StorageUtils.getCacheDirectory(context);
			displayedImages = Collections.synchronizedList(new LinkedList<String>());
			imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(context)
			.memoryCacheExtraOptions(480, 800)
			// default = device screen dimensions
			// .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75)
			// .taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
			// .taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR)
			.threadPoolSize(3)
			.threadPriority(Thread.NORM_PRIORITY - 1)
			.tasksProcessingOrder(QueueProcessingType.FIFO)
			.denyCacheImageMultipleSizesInMemory()
			.memoryCache(
					new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
					.memoryCacheSize(2 * 1024 * 1024)
					.discCache(new UnlimitedDiscCache(cacheDir))
					.discCacheSize(50 * 1024 * 1024)
					.discCacheFileCount(100)
					.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
					.imageDownloader(new BaseImageDownloader(context))
					.defaultDisplayImageOptions(
							DisplayImageOptions.createSimple())
							// .enableLogging()
							.build();

		} catch (Exception e) {
			Log.e(tag, "init "+e, e);
		}

	}
}
