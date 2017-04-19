package com.plus.cloudcontacts.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;

import android.app.Service;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import java.lang.reflect.Method;
import java.util.Timer;

import com.plus.cloudcontacts.service.WmService;
import com.plus.cloudcontacts.util.DBManager;

import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

public class PhoneListen extends BroadcastReceiver {

	private static boolean mIncomingFlag = false;
	private static String mIncomingNumber = null;
	private DBManager dbm;
	private View view;
	private boolean isRunning = true; 

	@Override
	public void onReceive(Context context, Intent intent) {
		if (view == null) {
			view = new View(context);
		}
		// Dial number;
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			mIncomingFlag = false;// 这个是拨号的时候采用的到的，所以这里没用
		} else {
			// Get call;
			TelephonyManager tManager = (TelephonyManager) context
					.getSystemService(Service.TELEPHONY_SERVICE);
			switch (tManager.getCallState()) {
			// 有电话打进来
			case TelephonyManager.CALL_STATE_RINGING:
				dbm = new DBManager(context);
				mIncomingFlag = true;
				// mIncomingNumber就是来电号码
				mIncomingNumber = intent.getStringExtra("incoming_number");
				String name = dbm.getNameByTel(mIncomingNumber);
				if (name != null) {
					// work here;
//					Toast.makeText(context, name+" 来电", Toast.LENGTH_LONG)
//							.show();
					Intent service_intent = new Intent(context,WmService.class);
					//service_intent.setAction("com.plus.cloudcontacts.service.WM_SERVICE");
					Bundle bundle = new Bundle();
					bundle.putString("name", name);
					service_intent.putExtras(bundle);
			        context.startService(service_intent);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				if (mIncomingFlag) {
					Intent service_intent = new Intent(context,WmService.class);
					context.stopService(service_intent);
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				if (mIncomingFlag) {
					Intent service_intent = new Intent(context,WmService.class);
					context.stopService(service_intent);
				}
				break;
			default:
				break;
			}
		}
	}
}