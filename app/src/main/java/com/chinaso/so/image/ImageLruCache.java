package com.chinaso.so.image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;


class ImageLruCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {
	
	private static DiskLruCache mDiskLruCache ; 
	private boolean mIsMemoryCacheAllowed;

    public ImageLruCache(Context context,int maxSize,boolean isMemoryCacheAllowed) {
        super(maxSize);
        mIsMemoryCacheAllowed=isMemoryCacheAllowed;
        //if(misMemoryCacheAllowed){
        	 try {
             	mDiskLruCache = DiskLruCache.open(getDiskCacheDir(context,"images"),
     					getAppVersion(context) , 1, 10*1024*1024);
     		} catch (IOException e) {
     			e.printStackTrace();
     		}
        //}
    }

    @Override
    protected int sizeOf(String key, Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

   
    public Bitmap getBitmap(String url) {
        Bitmap data=null;
        if(mIsMemoryCacheAllowed){
        	data=get(url);
        	if(data!=null){
        		return data;
        	}else{
        		String key = hashKeyForDisk(url);
        		try {
                	DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);  
    			    if (snapShot != null) {  
    			        InputStream is = snapShot.getInputStream(0);  
    			        data = BitmapFactory.decodeStream(is);  
    			    }
                    if (data != null) {
                        put(url, data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        	}
        }else{
        	String key = hashKeyForDisk(url);
    		try {
            	DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);  
			    if (snapShot != null) {  
			        InputStream is = snapShot.getInputStream(0);  
			        data = BitmapFactory.decodeStream(is);  
			    }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
      

        return data;
    }


    public void putBitmap(String url, Bitmap bitmap) {
       //内存缓存
    	if(mIsMemoryCacheAllowed){
        	put(url,bitmap);
        }
    	//自定义磁盘缓存（关闭volley自带的磁盘缓存(设置request的shouldcache)）
        String key = hashKeyForDisk(url);  
        try {
			if(null == mDiskLruCache.get(key)){
			    DiskLruCache.Editor editor = mDiskLruCache.edit(key);  
			    if (editor != null) {  			    	
			        OutputStream outputStream = editor.newOutputStream(0);  
			        if (bitmap.compress(CompressFormat.PNG, 100, outputStream)) {  
			            editor.commit();  
			        } else {  
			            editor.abort();  
			        }  
			    }  
			    mDiskLruCache.flush(); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    //该方法会判断当前sd卡是否存在，然后选择缓存地址
    private static File getDiskCacheDir(Context context, String uniqueName) {  
        String cachePath;  
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())  
                || !Environment.isExternalStorageRemovable()) {  
            cachePath = context.getExternalCacheDir().getPath();  
        } else {  
            cachePath = context.getCacheDir().getPath();  
        }  
        return new File(cachePath + File.separator + uniqueName);  
    }  
    
    //获得应用version号码
    private int getAppVersion(Context context) {  
        try {  
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);  
            return info.versionCode;  
        } catch (NameNotFoundException e) {  
            e.printStackTrace();  
        }  
        return 1;  
    }  
    
    //根据key生成md5值，保证缓存文件名称的合法化
    private String hashKeyForDisk(String key) {  
        String cacheKey;  
        try {  
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");  
            mDigest.update(key.getBytes());  
            cacheKey = bytesToHexString(mDigest.digest());  
        } catch (NoSuchAlgorithmException e) {  
            cacheKey = String.valueOf(key.hashCode());  
        }  
        return cacheKey;  
    }  
      
    private String bytesToHexString(byte[] bytes) {  
        StringBuilder sb = new StringBuilder();  
        for (int i = 0; i < bytes.length; i++) {  
            String hex = Integer.toHexString(0xFF & bytes[i]);  
            if (hex.length() == 1) {  
                sb.append('0');  
            }  
            sb.append(hex);  
        }  
        return sb.toString();  
    }  
}
