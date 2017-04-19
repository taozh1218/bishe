package com.plus.cloudcontacts.util;

import java.io.File;

import com.plus.cloudcontacts.bean.Contact;
import com.plus.cloudcontacts.thread.MyThreadPool;

import android.app.Application;
import android.os.Environment;

public class MyApplication extends Application{
	
	private String sessionId;
	private Contact me;
	private File cache;
	private MyThreadPool threadPool;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		cache = new File(Environment.getExternalStorageDirectory(), "cloudcontacts_cache");
        if(!cache.exists()){
            cache.mkdirs();
        }
        threadPool = new MyThreadPool();
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Contact getMe() {
		return me;
	}

	public void setMe(Contact me) {
		this.me = me;
	}

	public File getCache() {
		return cache;
	}

	public void setCache(File cache) {
		this.cache = cache;
	}

	public MyThreadPool getThreadPool() {
		return threadPool;
	}

	public void setThreadPool(MyThreadPool threadPool) {
		this.threadPool = threadPool;
	}
}
