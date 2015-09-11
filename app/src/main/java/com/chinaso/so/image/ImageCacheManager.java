package com.chinaso.so.image;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageListener;


public class ImageCacheManager {
	private Context mContext;
	// 取运行内存阈值的1/8作为图片缓存
	private  final int MEM_CACHE_SIZE ;
	private  ImageLruCache mImageLruCache,mImageLruCacheWithMemory ;
	private  ImageLoader  mImageLoader,mImageLoaderWithMemory ;

	public ImageCacheManager(Context context){
		mContext=context;
		MEM_CACHE_SIZE = 1024 * 1024 * ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() / 8; //1024 * 1024 *8
		
		mImageLruCache = new ImageLruCache(context,MEM_CACHE_SIZE,false);		
		mImageLoader = new ImageLoader(Volley.newRequestQueue(mContext),mImageLruCache);
		
		mImageLruCacheWithMemory = new ImageLruCache(context,MEM_CACHE_SIZE,true);		
		mImageLoaderWithMemory = new ImageLoader(Volley.newRequestQueue(mContext),mImageLruCacheWithMemory);
	}
	public static ImageLoader.ImageListener getImageListener(final ImageView view,
			final Bitmap defaultImageBitmap, final Bitmap errorImageBitmap){

		return new ImageLoader.ImageListener() {
			@Override
			public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
				if(imageContainer.getBitmap() != null ){
					view.setImageBitmap(imageContainer.getBitmap());
				}else if(defaultImageBitmap != null ){
					view.setImageBitmap(defaultImageBitmap);
				}
			}

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				if(errorImageBitmap != null){
					view.setImageBitmap(errorImageBitmap);
				}
			}
		};
	}
	/**
	 * 将url处图片呈现在view上，内存硬盘双缓存。
	 * @param url
	 * @param view
	 * @param defaultImageResourceId
	 * @param errorImageResourceId
	 * @return
	 */
	public  ImageLoader.ImageContainer loadImageWithMemory(final String url, final ImageView view,final int defaultImageResourceId, final int errorImageResourceId){	
		if(TextUtils.isEmpty(url))return null;	
		return mImageLoaderWithMemory.get(url,ImageLoader.getImageListener(view, defaultImageResourceId,errorImageResourceId), 0, 0);
	}
	/**
	 * 将url处图片呈现在view上，默认只有硬盘缓存。
	 * @param url 远程url地址
	 * @param view 待现实图片的view
	 * @param defaultImageBitmap 默认显示的图片
	 * @param errorImageBitmap 网络出错时显示的图片
	 */
	public  ImageLoader.ImageContainer loadImage(final String url, final ImageView view,final int defaultImageResourceId, final int errorImageResourceId){	
		if(TextUtils.isEmpty(url))return null;	
		return mImageLoader.get(url,ImageLoader.getImageListener(view, defaultImageResourceId,errorImageResourceId), 0, 0);
	}
	
	/**
	 * 将url处图片呈现在view上，默认只有硬盘缓存。
	 * @param url 远程url地址
	 * @param view 待现实图片的view
	 * @param defaultImageBitmap 默认显示的图片
	 * @param errorImageBitmap 网络出错时显示的图片
	 * @param maxWidth 设置图片显示宽度
	 * @param maxHeight 设置图片显示高度
	 */
	public  ImageLoader.ImageContainer loadImage(final String url, final ImageView view,final int defaultImageResourceId, final int errorImageResourceId, int maxWidth, int maxHeight){			
		if(TextUtils.isEmpty(url))return null;	
		return mImageLoader.get(url, ImageLoader.getImageListener(view, defaultImageResourceId,errorImageResourceId), maxWidth, maxHeight);
					
							
	}
	/**
	 * 自定义listener
	 * @param url
	 * @param listener
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public ImageLoader.ImageContainer loadImage(final String url,ImageListener listener,int maxWidth,int maxHeight){
		if(TextUtils.isEmpty(url))return null;
		return mImageLoader.get(url, listener, maxWidth, maxHeight);
	}
	/**
	 * 自定义listener
	 * @param url
	 * @param listener
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public ImageLoader.ImageContainer loadImage(final String url,ImageListener listener){
		if(TextUtils.isEmpty(url))return null;
		return mImageLoader.get(url, listener, 0, 0);
	}
}
