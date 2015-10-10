package com.facepp.demo;


import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
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

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

	
	private ExecutorService service=Executors.newFixedThreadPool(8);//开发版并发限制数3
	
	Button create,detect,upload,train,delete,get_size,change_name,deleteface,reset,pick,find;
	TextView status;

	//添加人时替换
	private String[] ADD_CHANGE_PERSON_SET =Config.TOADD; //Config.STARS;
	private int ADD_CHANGE_FACESET_INIT_INDEX=7100;        //0 8415
	
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

		upload=(Button) findViewById(R.id.upload);
		upload.setOnClickListener(this);

		
		train=(Button) findViewById(R.id.train);
		train.setOnClickListener(this);
		
		delete=(Button) findViewById(R.id.delete);
		delete.setOnClickListener(this);


		get_size=(Button) findViewById(R.id.get_size);
		get_size.setOnClickListener(this);

		change_name=(Button) findViewById(R.id.change_name);
		change_name.setOnClickListener(this);
		
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
	    		for(String star: ADD_CHANGE_PERSON_SET){
	            	service.submit(new DetectTask(star));
	            }
	            service.shutdown();
	    		break;
			case R.id.upload:
				new UploadTask().start();
				break;
	    	case R.id.train:
	    		new TrainTask().start();
	    		break;
	    	case R.id.delete:
	    		new DeleteTask().start();
	    		break;
			case R.id.get_size:
				new GetInfoTask().start();
				break;
			case R.id.change_name:
				new ChangNameTask().start();
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
	class GetInfoTask extends Thread{
		@Override
		public void run() {

			//begin face++
			setText("status:getting faces num...");
			HttpRequests httpRequests = new HttpRequests(Config.APP_KEY, Config.APP_SECRET, true, true);
			try {

				Charset.forName("UTF-8").name();
				int total=0;
				JSONObject faces=httpRequests.facesetGetInfo(new PostParameters().setFacesetName(Config.FACESET_NAME));
				JSONArray faceArray=faces.getJSONArray("face");

				total=faceArray.length();

				Log.i(Config.TAG, "get info");
				setText("status:faceset size= "+total);
			} catch(Exception e) {
				e.printStackTrace();
				setText("status:get info error\n"+e.toString());
			}

		}

	}
	class ChangNameTask extends Thread{
		@Override
		public void run() {

			//begin face++
			setText("status:change name...");

			HttpRequests httpRequests = new HttpRequests(Config.APP_KEY, Config.APP_SECRET, true, true);
			try {

				Charset.forName("UTF-8").name();

				httpRequests.personSetInfo(new PostParameters().setPersonName("米兰达可儿").setName("米兰·达可儿"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("艾玛沃特森").setName("艾玛·沃特森"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("伊娃格林").setName("伊娃·格林"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("奥黛丽赫本").setName("奥黛丽·赫本"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("斯威夫特").setName("泰勒·斯威夫特"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("塞弗里德").setName("阿曼达·塞弗里德"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("达科塔范宁").setName("达科塔·范宁"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("卡拉迪瓦伊").setName("卡拉·迪瓦伊"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("玛丽莲梦露").setName("玛丽莲·梦露"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("罗伯特帕丁森").setName("罗伯特·帕丁森"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("布拉德皮特").setName("布拉德·皮特"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("约翰尼德普").setName("约翰·尼德普"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("迈克尔杰克逊").setName("迈克尔·杰克逊"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("汤姆克鲁斯").setName("汤姆·克鲁斯"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("范迪塞尔").setName("范·迪塞尔"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("小罗伯特唐尼").setName("小罗伯特·唐尼"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("马特达蒙").setName("马特·达蒙"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("额勒贝格道尔吉").setName("查希亚·额勒贝格道尔吉"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("海尔马里亚姆").setName("海尔马里亚姆·德萨莱尼"));
				httpRequests.personSetInfo(new PostParameters().setPersonName("小罗伯特唐尼").setName("小罗伯特·唐尼"));


				Log.i(Config.TAG, "change name");
				setText("status:name changed");
			} catch(Exception e) {
				e.printStackTrace();
				setText("status:change name error\n"+e.toString());
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
					//httpRequests.facesetDelete(new PostParameters().setFacesetName(Config.FACESET_NAME_TMP));
					setText("status:creating faceset tmp");
					httpRequests.facesetCreate(new PostParameters().setFacesetName(Config.FACESET_NAME_TMP));
					httpRequests.facesetSetInfo(new PostParameters().setFacesetName(Config.FACESET_NAME_TMP).setTag(Config.FACESET_NAME));
					
					setText("status:adding faces... ");
					for(String star:Config.STARS){
						JSONObject ret=httpRequests.personGetInfo(new PostParameters().setPersonName(star));
						JSONArray array=ret.getJSONArray("face");
						StringBuilder face_ids=new StringBuilder();
						for(int i=0;i<array.length();i++){
							String face_id=array.getJSONObject(i).getString("face_id");
							if(i!=array.length()-1){
								face_ids.append(array.getJSONObject(i).getString("face_id")+",");
							}else{
								face_ids.append(array.getJSONObject(i).getString("face_id"));
							}
//							JSONObject addret=httpRequests.facesetAddFace(new PostParameters().setFacesetName(Config.FACESET_NAME).setFaceId(face_id));
//							Log.i(Config.TAG, star+addret.toString());
						}
						try {
							JSONObject addret=httpRequests.facesetAddFace(new PostParameters().setFacesetName(Config.FACESET_NAME_TMP).setFaceId(face_ids.toString()));
							Log.i(Config.TAG, star+addret.toString());
						} catch (FaceppParseException e) {
							e.printStackTrace();
							setText("status:add face error at " + star+"\n" + e.toString());
						}
					}
					setText("status:deleting faceset old... ");
					httpRequests.facesetDelete(new PostParameters().setFacesetName(Config.FACESET_NAME));
					setText("status:rename faceset tmp... ");
					httpRequests.facesetSetInfo(new PostParameters().setFacesetName(Config.FACESET_NAME_TMP).setName(Config.FACESET_NAME));

					
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
	class UploadTask extends Thread{
		@Override
		public void run() {

			//begin face++
			setText("status:upload urls");
			HttpRequests httpRequests = new HttpRequests(Config.APP_KEY, Config.APP_SECRET, true, true);

			HttpClient uploadClient=new DefaultHttpClient();
			HttpGet uploadGet=null;
			HttpResponse response=null;

			try {

				Charset.forName("UTF-8").name();

				JSONObject faces=httpRequests.facesetGetInfo(new PostParameters().setFacesetName(Config.FACESET_NAME));
				JSONArray faceArray=faces.getJSONArray("face");
				for(int i=ADD_CHANGE_FACESET_INIT_INDEX;i<faceArray.length();i++){
					int total=faceArray.length();
					String face_id=faceArray.getJSONObject(i).getString("face_id");
					JSONObject faceInfo=httpRequests.infoGetFace(new PostParameters().setFaceId(face_id));
					JSONArray faceInfo_array = faceInfo.getJSONArray("face_info");
					for(int j=0;j<faceInfo_array.length();j++) {
						final String url = faceInfo_array.getJSONObject(j).getString("url");
						//setText("status:upload url=" + url);
						//Log.i(Config.TAG, "url=" + url);

						uploadGet=new HttpGet(Config.UPLOAD_URL+url);
						try {
							response=uploadClient.execute(uploadGet);
							if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
                                setText("status:upload "+i+"/"+total+" success url=" + url);
                                Log.i(Config.TAG, "success url=" + url);
                            }else{
                                setText("status:upload "+i+"/"+total+" error url=" + url);
                                Log.e(Config.TAG, "error url=" + url);
                            }
						} catch (IOException e) {
							setText("status:upload mob error\n"+e.toString());
							e.printStackTrace();
							continue;
						}
					}
				}


				Log.i(Config.TAG, "uploaded");
				setText("status:task done!");


			} catch(Exception e) {
				e.printStackTrace();
				setText("status:upload error\n"+e.toString());
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
					
					//create person
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
