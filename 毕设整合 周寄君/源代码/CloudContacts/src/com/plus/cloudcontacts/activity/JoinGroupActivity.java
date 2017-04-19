package com.plus.cloudcontacts.activity;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.plus.cloudcontacts.R;
import com.plus.cloudcontacts.R.layout;
import com.plus.cloudcontacts.R.menu;
import com.plus.cloudcontacts.bean.Group;
import com.plus.cloudcontacts.http.PostWork;
import com.plus.cloudcontacts.util.DBManager;
import com.plus.cloudcontacts.util.MyApplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class JoinGroupActivity extends Activity {

	private EditText groupCode;
	private Button btnMatch;
	private TextView newGroup1;
	private TextView newGroup2;
	
	private MyApplication app;
	private DBManager dbm;
	private boolean working = false;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what==1){
				Gson gs = new Gson();
				Group group = gs.fromJson(gs.toJson(msg.obj), Group.class);
				dbm.inGroup(group.getGroupId(),app.getMe().getContactId());
				Intent intent = new Intent("android.intent.action.CART_BROADCAST");
				LocalBroadcastManager.getInstance(JoinGroupActivity.this).sendBroadcast(intent);
				Toast.makeText(JoinGroupActivity.this, "加入组成功", Toast.LENGTH_SHORT).show();
				Bundle data = new Bundle();
				getIntent().putExtra("groupId",group.getGroupId());
				setResult(RESULT_OK, getIntent());
				finish();
			}else{
				Toast.makeText(JoinGroupActivity.this, "没有匹配到任何组", Toast.LENGTH_SHORT).show();
			}
			working = false;
		}
	};
	
	private OnClickListener myOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(JoinGroupActivity.this,CreateGroupActivity.class);
			startActivity(intent);
			finish();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_group);
		
		app = (MyApplication) getApplication();
		dbm = new DBManager(this);
		groupCode = (EditText)findViewById(R.id.groupCode);
		newGroup1 = (TextView)findViewById(R.id.newGroup1);
		newGroup2 = (TextView)findViewById(R.id.newGroup2);
		
		newGroup1.setOnClickListener(myOnClickListener);
		newGroup2.setOnClickListener(myOnClickListener);
		
		btnMatch = (Button) findViewById(R.id.btnMatch);
		
		btnMatch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (working){
					return;
				}
				working = true;
				app.getThreadPool().submit(new MatchThread());
			}
		});
	}

	class MatchThread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Map params = new HashMap();
			String gCode = groupCode.getText().toString();
			String session = app.getSessionId(); 
			params.put("sessionid", URLEncoder.encode(session));
			params.put("groupcode", gCode);
			PostWork pw = new PostWork(PostWork.URL_IN_GROUP, params, mHandler);
			pw.work();
		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.join_group, menu);
		return true;
	}

}
