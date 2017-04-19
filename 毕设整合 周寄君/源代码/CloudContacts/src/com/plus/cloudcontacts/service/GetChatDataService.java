package com.plus.cloudcontacts.service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.plus.cloudcontacts.activity.GroupListActivity;
import com.plus.cloudcontacts.bean.Chat;
import com.plus.cloudcontacts.http.PostWork;
import com.plus.cloudcontacts.util.DBManager;
import com.plus.cloudcontacts.util.MyApplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

public class GetChatDataService extends Service{

	private MyApplication app;
	private DBManager dbm;
	private Thread mThread;
	private boolean running;
	private boolean isHandling;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what==1){
				//·µ»Ø
				Gson gson = new Gson();
				String json = gson.toJson(msg.obj);
				Chat[] chats = gson.fromJson(json, Chat[].class);
				for(Chat chat:chats){
					dbm.saveChat(chat);
					Intent intent = new Intent("android.intent.action.NEW_CHAT_BROADCAST");
					intent.putExtra("type", chat.getType());
					intent.putExtra("content", chat.getContent());
					intent.putExtra("avatar", chat.getAvatar());
					sendBroadcast(intent);
				}
				dbm.setChatTime(msg.getData().getString("msg"));
			}
			isHandling = false;
			super.handleMessage(msg);
		}
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		app = (MyApplication) getApplication();
		dbm = new DBManager(getApplicationContext());
		running = true;
		isHandling = false;
		mThread = new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (running){
					if(!isHandling){
						isHandling = true;
						Map<String,String> params = new HashMap<String, String>();
						params.put("sessionid", URLEncoder.encode(app.getSessionId()));
						params.put("time", dbm.getChatTime());
						PostWork pw = new PostWork(PostWork.URL_GET_CHAT_DATA, params, mHandler);
						pw.work();
					}
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				super.run();
			}
		};
		mThread.start();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		running = false;
		super.onDestroy();
	}
}
