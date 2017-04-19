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
	private WindowManager wManager;// ���ڹ�����
	private WindowManager.LayoutParams mParams;// ���ڵ�����
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
		mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// ϵͳ��ʾwindow
		mParams.format = PixelFormat.TRANSLUCENT;// ֧��͸��
		// mParams.format = PixelFormat.RGBA_8888;
		mParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// ����
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;// ���ڵĿ�͸�
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.x = 0;// ����λ�õ�ƫ����
		mParams.y = 0;
		// mParams.alpha = 0.1f;//���ڵ�͸����
		//ͨ��ϵͳ�ṩ��ʵ�����һ��LayoutInflater����  
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		//��һ������Ϊxml�ļ���view��id���ڶ�������Ϊ��view�ĸ����������Ϊnull��android���Զ�Ѱ�����Ƿ�ӵ�и����  
		myView = inflater.inflate(R.layout.in_call_view, null); 
		icv = (InCallView) myView.findViewById(R.id.viewIcv);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i("WmService", "Start");
		String name = intent.getExtras().getString("name");
		icv.setTitleText(name+" ����");
		wManager.addView(myView, mParams);// ��Ӵ���
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("WmService", "Destroy");
		if (myView.getParent() != null){
			wManager.removeView(myView);// �Ƴ�����
			flag = false;
		}
		super.onDestroy();
	}
}
