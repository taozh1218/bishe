package com.plus.cloudcontacts.service;

import com.plus.cloudcontacts.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class WmService extends Service {
	private WindowManager wManager;// 窗口管理者
	private WindowManager.LayoutParams mParams;// 窗口的属性
	private View myView;
	private InCallView icv;
	private boolean flag = true;
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.i("WmService", "Bind");
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.i("WmService", "Created");
		wManager = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		mParams = new WindowManager.LayoutParams();
		mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// 系统提示window
		mParams.format = PixelFormat.TRANSLUCENT;// 支持透明
		// mParams.format = PixelFormat.RGBA_8888;
		mParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 焦点
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;// 窗口的宽和高
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.x = 0;// 窗口位置的偏移量
		mParams.y = 0;
		// mParams.alpha = 0.1f;//窗口的透明度
		//通过系统提供的实例获得一个LayoutInflater对象  
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		//第一个参数为xml文件中view的id，第二个参数为此view的父组件，可以为null，android会自动寻找它是否拥有父组件  
		myView = inflater.inflate(R.layout.in_call_view, null); 
		icv = (InCallView) myView.findViewById(R.id.viewIcv);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i("WmService", "Start");
		String name = intent.getExtras().getString("name");
		icv.setTitleText(name+" 来电");
		wManager.addView(myView, mParams);// 添加窗口
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("WmService", "Destroy");
		if (myView.getParent() != null){
			wManager.removeView(myView);// 移除窗口
			flag = false;
		}
		super.onDestroy();
	}
}
