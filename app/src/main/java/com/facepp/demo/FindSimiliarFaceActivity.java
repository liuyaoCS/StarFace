package com.facepp.demo;

import java.io.ByteArrayOutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chinaso.so.image.ImageCacheManager;
import com.facepp.demo.PickSearchActivity.DetectCallback;
import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FindSimiliarFaceActivity extends Activity {

	final private int PICTURE_CHOOSE = 1;
	private ImageCacheManager mImageCacheManager;
	
	private ImageView imageView = null;
	private ImageView img1,img2,img3 = null;
	private ImageView[] imageViews;
	private TextView tv1,tv2,tv3;
	private TextView[] textViews;
	private Bitmap img = null;
	private Button buttonDetect = null;
	private TextView textView = null;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_similiar_face);
        
        mImageCacheManager = new ImageCacheManager(this);
        
        Button button = (Button)this.findViewById(R.id.button1);
        button.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				//get a picture form your phone
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		        photoPickerIntent.setType("image/*");
		        startActivityForResult(photoPickerIntent, PICTURE_CHOOSE);
			}
		});
        
        textView = (TextView)this.findViewById(R.id.textView1);
        
        buttonDetect = (Button)this.findViewById(R.id.button2);
        buttonDetect.setVisibility(View.INVISIBLE);
        buttonDetect.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				
				textView.setText("Waiting ...");
				for(TextView tv:textViews){
    				tv.setText("");
    			}
    			for(ImageView iv:imageViews){
    				Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.loading);
    				iv.setImageBitmap(bitmap);
    			}
				
				DetectAndSearch faceppDetect = new DetectAndSearch();
				faceppDetect.setDetectCallback(new DetectCallback() {
					
					public void detectResult(JSONObject rst) {
				
						
						//use the red paint
						Paint paint = new Paint();
						paint.setColor(Color.RED);
						paint.setStrokeWidth(Math.max(img.getWidth(), img.getHeight()) / 100f);

						//create a new canvas
						Bitmap bitmap = Bitmap.createBitmap(img.getWidth(), img.getHeight(), img.getConfig());
						Canvas canvas = new Canvas(bitmap);
						canvas.drawBitmap(img, new Matrix(), null);
						
						
						try {
							//find out all faces
							final int count = rst.getJSONArray("face").length();
							for (int i = 0; i < count; ++i) {
								float x, y, w, h;
								//get the center point
								x = (float)rst.getJSONArray("face").getJSONObject(i)
										.getJSONObject("position").getJSONObject("center").getDouble("x");
								y = (float)rst.getJSONArray("face").getJSONObject(i)
										.getJSONObject("position").getJSONObject("center").getDouble("y");

								//get face size
								w = (float)rst.getJSONArray("face").getJSONObject(i)
										.getJSONObject("position").getDouble("width");
								h = (float)rst.getJSONArray("face").getJSONObject(i)
										.getJSONObject("position").getDouble("height");
								
								//change percent value to the real size
								x = x / 100 * img.getWidth();
								w = w / 100 * img.getWidth() * 0.7f;
								y = y / 100 * img.getHeight();
								h = h / 100 * img.getHeight() * 0.7f;

								//draw the box to mark it out
								canvas.drawLine(x - w, y - h, x - w, y + h, paint);
								canvas.drawLine(x - w, y - h, x + w, y - h, paint);
								canvas.drawLine(x + w, y + h, x - w, y + h, paint);
								canvas.drawLine(x + w, y + h, x + w, y - h, paint);
							}
							
							//save new image
							img = bitmap;

							FindSimiliarFaceActivity.this.runOnUiThread(new Runnable() {
								
								public void run() {
									//show the image
									imageView.setImageBitmap(img);
									textView.setText("Searching ");
								}
							});
							
						} catch (JSONException e) {
							e.printStackTrace();
							FindSimiliarFaceActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									textView.setText("detect json Error.");
								}
							});
						}
						
					}

					public void urlsResult(JSONObject faceinfo) {
						// TODO Auto-generated method stub
						JSONArray faceinfo_array;
						try {
							faceinfo_array = faceinfo.getJSONArray("face_info");
							for(int i=0;i<faceinfo_array.length();i++){
								final String url=faceinfo_array.getJSONObject(i).getString("url");
								final String starName=faceinfo_array.getJSONObject(i).getJSONArray("person").getJSONObject(0).getString("person_name");
								final int index=i;
								Log.i(Config.TAG, "url="+url);
								FindSimiliarFaceActivity.this.runOnUiThread(new Runnable() {
									
									public void run() {
										// TODO Auto-generated method stub
										mImageCacheManager.loadImage(url, imageViews[index], 0, R.drawable.error);
										textViews[index].setText(starName);
									}
								});
								
							}
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							FindSimiliarFaceActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									textView.setText("volley Error.");
								}
							});
						}
						
					}
				});
				faceppDetect.detect(img);
			}
		});
        
        imageView = (ImageView)this.findViewById(R.id.imageView1);
        imageView.setImageBitmap(img);
        
        img1 = (ImageView)this.findViewById(R.id.img1);
        img2 = (ImageView)this.findViewById(R.id.img2);
        img3 = (ImageView)this.findViewById(R.id.img3);
        
        imageViews=new ImageView[]{img1,img2,img3};
        
        tv1=(TextView)findViewById(R.id.tv1);
        tv2=(TextView)findViewById(R.id.tv2);
        tv3=(TextView)findViewById(R.id.tv3);
        textViews=new TextView[]{tv1,tv2,tv3};
        
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	
    	//the image picker callback
    	if (requestCode == PICTURE_CHOOSE) {
    		if (intent != null) {
    			//The Android api ~~~ 
    			//Log.d(TAG, "idButSelPic Photopicker: " + intent.getDataString());
    			Cursor cursor = getContentResolver().query(intent.getData(), null, null, null, null);
    			cursor.moveToFirst();
    			int idx = cursor.getColumnIndex(ImageColumns.DATA);
    			String fileSrc = cursor.getString(idx); 
    			//Log.d(TAG, "Picture:" + fileSrc);
    			
    			//just read size
    			Options options = new Options();
    			options.inJustDecodeBounds = true;
    			img = BitmapFactory.decodeFile(fileSrc, options);

    			//scale size to read
    			options.inSampleSize = Math.max(1, (int)Math.ceil(Math.max((double)options.outWidth / 1024f, (double)options.outHeight / 1024f)));
    			options.inJustDecodeBounds = false;
    			img = BitmapFactory.decodeFile(fileSrc, options);
    			textView.setText("Clik Detect. ==>");
    			
    			
    			imageView.setImageBitmap(img);
    			buttonDetect.setVisibility(View.VISIBLE);
    			
    		}
    		else {
    			Log.d(Config.TAG, "idButSelPic Photopicker canceled");
    		}
    	}
    }

    private class DetectAndSearch {
    	DetectCallback callback = null;
    	
    	public void setDetectCallback(DetectCallback detectCallback) { 
    		callback = detectCallback;
    	}

    	public void detect(final Bitmap image) {
    		
    		new Thread(new Runnable() {
				
				public void run() {
					HttpRequests httpRequests = new HttpRequests(Config.APP_KEY, Config.APP_SECRET, true, true);
		    		//Log.v(TAG, "image size : " + img.getWidth() + " " + img.getHeight());
		    		
		    		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		    		float scale = Math.min(1, Math.min(600f / img.getWidth(), 600f / img.getHeight()));
		    		Matrix matrix = new Matrix();
		    		matrix.postScale(scale, scale);

		    		Bitmap imgSmall = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, false);
		    		//Log.v(TAG, "imgSmall size : " + imgSmall.getWidth() + " " + imgSmall.getHeight());
		    		
		    		imgSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		    		byte[] array = stream.toByteArray();
		    		
		    		try {
		    			//detect
						JSONObject result = httpRequests.detectionDetect(new PostParameters().setImg(array));
						String key_face_id=result.getJSONArray("face").getJSONObject(0).getString("face_id");
						//httpRequests.facesetAddFace(new PostParameters().setFacesetName(Config.FACESET_DETECT).setFaceId(key_face_id));
						//finished , then call the callback function
						if (callback != null) {
							callback.detectResult(result);
						}
						//searching
						Log.i(Config.TAG, "searching...");
						JSONObject searchRet=httpRequests.recognitionSearch(new PostParameters().setFacesetName(Config.FACESET_NAME).setKeyFaceId(key_face_id).setCount(3));
						Log.i(Config.TAG, searchRet.toString());
						
						JSONArray candidates=searchRet.getJSONArray("candidate");
						StringBuilder face_ids=new StringBuilder();
						for(int i=0;i<candidates.length();i++){
							if(i!=candidates.length()-1){
								face_ids.append(candidates.getJSONObject(i).getString("face_id")+",");
							}else{
								face_ids.append(candidates.getJSONObject(i).getString("face_id"));
							}
						}
						
						
						//get_face_info						
						JSONObject faceinfo=httpRequests.infoGetFace(new PostParameters().setFaceId(face_ids.toString()));
						if (callback != null) {
							callback.urlsResult(faceinfo);
						}
						
						
						
					} catch (Exception e) {
						e.printStackTrace();
						FindSimiliarFaceActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								textView.setText("Network error.");
							}
						});
					}
					
				}
			}).start();
    	}
    }

    interface DetectCallback {
    	void detectResult(JSONObject rst);
    	void urlsResult(JSONObject faceinfo);
	}

}
