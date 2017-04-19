package com.plus.cloudcontacts.activity;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.plus.cloudcontacts.R;
import com.plus.cloudcontacts.bean.Contact;
import com.plus.cloudcontacts.http.PostWork;
import com.plus.cloudcontacts.service.WmService;
import com.plus.cloudcontacts.util.DBManager;
import com.plus.cloudcontacts.util.MyApplication;

import com.google.gson.Gson;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {
	private DBManager dbm;
	private MyApplication app;
	private SessionThread mThread;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==0){
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Intent intent = new Intent(MainActivity.this, LoginActivity.class);
						startActivity(intent);
						finish();
					}
				}, 2000);
			}else{
				Gson gson = new Gson();
		        Contact bean = gson.fromJson(gson.toJson(msg.obj),Contact.class);
		        app.setMe(bean);
		        //bean.setContactId("ME");
		        dbm.saveContact(bean,null);
		        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
		        intent.putExtra("bean", gson.toJson(bean));
				startActivity(intent);
				finish();
			}
		}
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app =(MyApplication) getApplication();
        dbm = new DBManager(this);
        app.getThreadPool().submit(new SessionThread());
    }

    private class SessionThread implements Runnable{
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		final String session = dbm.getSession();
            app.setSessionId(session);
            if (session!=null){
            	Map<String,String> param = new HashMap<String,String>();
    			param.put("sessionid", URLEncoder.encode(session));
    			PostWork pw = new PostWork(PostWork.URL_SESSION,param,mHandler);
    			if(!pw.work()){
    		        Message msg = new Message();
    		        msg.what = 0;
    		        mHandler.handleMessage(msg);
    			}
            }else{
    	        Message msg = new Message();
    	        msg.what = 0;
    	        mHandler.handleMessage(msg);
            }
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
