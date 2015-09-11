package com.facepp.demo;


import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;



/**
 * Facepp SDK Android test
 * 
 * Look result at debug area.(Log cat)
 * @author moon5ckq
 */
public class MainActivity extends Activity implements OnClickListener{

	
	private ExecutorService service=Executors.newFixedThreadPool(3);//开发版并发限制数3
	
	Button create,detect,train,delete,deleteface,reset,pick,find;
	TextView status;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initView();
    }
    private void initView() {
		// TODO Auto-generated method stub
		create=(Button) findViewById(R.id.create);
		create.setOnClickListener(this);
		
		detect=(Button) findViewById(R.id.detect);
		detect.setOnClickListener(this);
		
		train=(Button) findViewById(R.id.train);
		train.setOnClickListener(this);
		
		delete=(Button) findViewById(R.id.delete);
		delete.setOnClickListener(this);
		
		deleteface=(Button) findViewById(R.id.deleteface);
		deleteface.setOnClickListener(this);
		
		reset=(Button) findViewById(R.id.reset);
		reset.setOnClickListener(this);
		
		pick=(Button) findViewById(R.id.pick);
		pick.setOnClickListener(this);
		
		find=(Button) findViewById(R.id.find);
		find.setOnClickListener(this);
		
		status=(TextView) findViewById(R.id.status);
	}
    public void onClick(View v) {
    	// TODO Auto-generated method stub
    	int viewId=v.getId();
    	switch(viewId){
	    	case R.id.create:
	    		new CreateTask().start();
	    		break;
	    	case R.id.detect:
	    		for(String star:Config.STARS){
	            	service.submit(new DetectTask(star));
	            }
	            service.shutdown();
	    		break;
	    	case R.id.train:
	    		new TrainTask().start();
	    		break;
	    	case R.id.delete:
	    		new DeleteTask().start();
	    		break;
	    	case R.id.deleteface:
	    		new DeleteFaceTask().start();
	    		break;
	    	case R.id.reset:
	    		new ResetFacesetTask().start();
	    		break;
	    	case R.id.pick:
	    		Intent itp=new Intent(MainActivity.this,PickSearchActivity.class);
	    		startActivity(itp);
	    		break;
	    	case R.id.find:
	    		Intent itf=new Intent(MainActivity.this,FindSimiliarFaceActivity.class);
	    		startActivity(itf);
	    		break;
	    	default:
	    		break;
    	}
    }
	public String getFaceId(JSONArray array){
    	String faceId="";
    	try {
    		faceId = array.getJSONObject(0).getString("face_id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return faceId;
    }
	public void setText(final String content){
		MainActivity.this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				status.setText(content);
			}
			
		});
	}
    class CreateTask extends Thread{
		 @Override
		 public void run() {
		
			 	//begin face++
			    setText("status:createing");
				HttpRequests httpRequests = new HttpRequests(Config.APP_KEY, Config.APP_SECRET, true, true);
				try {
					
					Charset.forName("UTF-8").name();
					
					httpRequests.facesetCreate(new PostParameters().setFacesetName(Config.FACESET_DETECT));
					httpRequests.facesetSetInfo(new PostParameters().setFacesetName(Config.FACESET_DETECT).setTag(Config.FACESET_DETECT));
					
					httpRequests.facesetCreate(new PostParameters().setFacesetName(Config.FACESET_NAME));
					httpRequests.facesetSetInfo(new PostParameters().setFacesetName(Config.FACESET_NAME).setTag(Config.FACESET_NAME));
					
					httpRequests.groupCreate(new PostParameters().setGroupName(Config.GROUP_NAME));
					httpRequests.groupSetInfo(new PostParameters().setGroupName(Config.GROUP_NAME).setTag(Config.GROUP_NAME));
					
					Log.i(Config.TAG, "created!");
					setText("status:created");
					
				} catch(Exception e) {
					e.printStackTrace();
					setText("status:create error\n"+e.toString());
				} 
			
		 }
   
   }
    class DeleteTask extends Thread{
		 @Override
		 public void run() {
		
			 	//begin face++
			    setText("status:deleting");
				HttpRequests httpRequests = new HttpRequests(Config.APP_KEY, Config.APP_SECRET, true, true);
				try {
					
					Charset.forName("UTF-8").name();
					
					for(String star:Config.STARS){
						httpRequests.personDelete(new PostParameters().setPersonName(star));
					}
					
					httpRequests.groupDelete(new PostParameters().setFacesetName(Config.GROUP_NAME));
					httpRequests.facesetDelete(new PostParameters().setFacesetName(Config.FACESET_NAME));
					httpRequests.facesetDelete(new PostParameters().setFacesetName(Config.FACESET_DETECT));
					
					Log.i(Config.TAG, "deleted!");
					setText("status:deleted");
				} catch(Exception e) {
					e.printStackTrace();
					setText("status:delete error\n"+e.toString());
				} 
			
		 }
  
    }
    class DeleteFaceTask extends Thread{
		 @Override
		 public void run() {
		
			 	//begin face++
			    setText("status:deleting certain faces");
				HttpRequests httpRequests = new HttpRequests(Config.APP_KEY, Config.APP_SECRET, true, true);
				try {
					
					Charset.forName("UTF-8").name();
					
					for(String face:Config.FACES_DELETED){
						httpRequests.facesetRemoveFace(new PostParameters().setFacesetName(Config.FACESET_NAME).setFaceId(face));
					}

					Log.i(Config.TAG, "deleted!");
					setText("status:deleted,press train");
				} catch(Exception e) {
					e.printStackTrace();
					setText("status:delete certain faces error\n"+e.toString());
				} 
			
		 }
 
   }
    class ResetFacesetTask extends Thread{
		 @Override
		 public void run() {
		
			 	//begin face++
			    setText("status:deleting faceset");
				HttpRequests httpRequests = new HttpRequests(Config.APP_KEY, Config.APP_SECRET, true, true);
				try {
					
					Charset.forName("UTF-8").name();
					httpRequests.facesetDelete(new PostParameters().setFacesetName(Config.FACESET_NAME));
					setText("status:creating faceset");
					httpRequests.facesetCreate(new PostParameters().setFacesetName(Config.FACESET_NAME));
					httpRequests.facesetSetInfo(new PostParameters().setFacesetName(Config.FACESET_NAME).setTag(Config.FACESET_NAME));
					
					setText("status:adding faces... ");
					for(String star:Config.STARS){
						JSONObject ret=httpRequests.personGetInfo(new PostParameters().setPersonName(star));
						JSONArray array=ret.getJSONArray("face");
						for(int i=0;i<array.length();i++){
							String face_id=array.getJSONObject(i).getString("face_id");
							JSONObject addret=httpRequests.facesetAddFace(new PostParameters().setFacesetName(Config.FACESET_NAME).setFaceId(face_id));
							Log.i(Config.TAG, star+addret.toString());
						}
					}

					
					Log.i(Config.TAG, "done!");
					setText("status: task done,press train");
				} catch(Exception e) {
					e.printStackTrace();
					setText("status:task error\n"+e.toString());
				} 
			
		 }
 
 }
    class TrainTask extends Thread{
		 @Override
		 public void run() {

				HttpRequests httpRequests = new HttpRequests(Config.APP_KEY, Config.APP_SECRET, true, true);
				try {
					
					Charset.forName("UTF-8").name();
//					setText("status:detecting...");
//					JSONObject result = httpRequests.detectionDetect(new PostParameters().setUrl(Config.PIC_URL));
//					JSONArray array=result.getJSONArray("face");
//					String key_face_id=Config.DEFAULT_FACE_ID;
//					if(array.length()>0){
//						key_face_id=getFaceId(array);
//					}else{
//						Log.e(Config.TAG, " no face detected");
//					}
//					
					
					//recognition/train
					Log.i(Config.TAG, "training...");
					JSONObject syncRet =httpRequests.trainSearch(new PostParameters().setFacesetName(Config.FACESET_NAME));	
					Log.i(Config.TAG, syncRet.toString());
										
					//recognition/search
//					Log.i(Config.TAG, "searching...");
//					JSONObject searchRet=httpRequests.recognitionSearch(new PostParameters().setFacesetName(Config.FACESET_NAME).setKeyFaceId(key_face_id).setCount(3));
//					Log.i(Config.TAG, searchRet.toString());
					setText("status:train task,wait several minutes");
					
				} catch(Exception e) {
					e.printStackTrace();
					setText("status:train error\n"+e.toString());
				} 
			
		 }
    
    }
    class DetectTask extends Thread{
    	String mStarName;
    	public DetectTask(String name){
    		mStarName=name;
    	}
		 @Override
		 public void run() {
		
			 	// get urls
			    ArrayList<String> urls=new ArrayList<String>();	
			    setText("status:getting urls...");
			 	HttpClient httpClient=new DefaultHttpClient();
			 	HttpGet request=new HttpGet(Config.GET_URL+mStarName);
	
			 	try {				
			 		HttpResponse response=httpClient.execute(request);
					if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
						Scanner scanner=new Scanner(response.getEntity().getContent(),"utf-8");
						StringBuilder sb=new StringBuilder();
						while(scanner.hasNextLine()){
							String content=scanner.nextLine();
							sb.append(content);
						}
						JSONObject ret=new JSONObject(sb.toString());
						JSONObject searchResult=ret.optJSONObject("searchResult");
						if(searchResult!=null){
							JSONArray data=searchResult.optJSONArray("data");
							for(int i=0;i<data.length();i++){
								JSONObject item=data.optJSONObject(i);
								if(item!=null){
									String url=item.optString("obj_url");
									if(url!=null && !url.equals("")){
										urls.add(url);
										Log.i(Config.TAG, url);
									}
									
								}
							}
						}
						Log.i(Config.TAG, mStarName+":get face num:"+urls.size());
					}
					setText("status:get urls over");
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					setText("status:get url error\n"+e.toString());
				} 
			 	
			 	//begin face++
			 	setText("status:detecting..."+mStarName);
				HttpRequests httpRequests = new HttpRequests(Config.APP_KEY, Config.APP_SECRET, true, true);
				try {
					
					Charset.forName("UTF-8").name();
					
					//create person	and faceset			
					httpRequests.personCreate(new PostParameters().setPersonName(mStarName));
					httpRequests.personSetInfo(new PostParameters().setPersonName(mStarName).setTag(mStarName));
					httpRequests.groupAddPerson(new PostParameters().setGroupName(Config.GROUP_NAME).setPersonName(mStarName));
					
//					httpRequests.facesetCreate(new PostParameters().setFacesetName(FACESET_NAME));
//					httpRequests.facesetSetInfo(new PostParameters().setFacesetName(FACESET_NAME).setTag(FACESET_NAME));
					
					//detect face , add face
					Log.i(Config.TAG, "detecting...");
					for(String url:urls){						
						try {
							JSONObject result = httpRequests.detectionDetect(new PostParameters().setUrl(url));
							JSONArray array=result.getJSONArray("face");
							if(array.length()>0){
								httpRequests.personAddFace(new PostParameters().setPersonName(mStarName).setFaceId(getFaceId(array)));	
								httpRequests.facesetAddFace(new PostParameters().setFacesetName(Config.FACESET_NAME).setFaceId(getFaceId(array)));
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							setText("status:upload"+mStarName+"url error at"+url+"\n"+e.toString());
						}
						setText("status:upload"+mStarName+" one url= "+url);
						
					}
	
					
//					//recognition/train
//					Log.i(TAG, "training...");
//					JSONObject syncRet =httpRequests.trainSearch(new PostParameters().setFacesetName(FACESET_NAME));	
//					Log.i(TAG, syncRet.toString());
//										
//					//recognition/search
//					Log.i(TAG, "searching...");
//					JSONObject searchRet=httpRequests.recognitionSearch(new PostParameters().setFacesetName(FACESET_NAME).setKeyFaceId(KEY_FACE_ID).setCount(3));
//					Log.i(TAG, searchRet.toString());
					
					setText("status:detecting"+mStarName+" over");
				} catch(Exception e) {
					e.printStackTrace();
					setText("status:detecting"+mStarName+" error\n"+e.toString());
				}finally{ 
					Log.i(Config.TAG, "done!");
					urls.clear();
				}
			
		 }
  }
}
