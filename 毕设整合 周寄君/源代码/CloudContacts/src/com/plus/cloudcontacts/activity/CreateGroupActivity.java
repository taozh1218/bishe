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
import android.widget.Toast;

public class CreateGroupActivity extends Activity {

	private DBManager dbm;
	private EditText edtGroupName;
	private Button btnCreate;
	private MyApplication app;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==1){
				Gson gson = new Gson();
				Group bean = gson.fromJson(gson.toJson(msg.obj), Group.class);
				dbm.saveGroup(bean);
				dbm.inGroup(bean.getGroupId(), ((MyApplication)getApplication()).getMe().getContactId());
				Intent intent = new Intent("android.intent.action.CART_BROADCAST");
				LocalBroadcastManager.getInstance(CreateGroupActivity.this).sendBroadcast(intent);
				Toast.makeText(CreateGroupActivity.this, "创建组成功", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);
		app =(MyApplication)getApplication();
		edtGroupName =(EditText) findViewById(R.id.edtGroupName);
		btnCreate = (Button)findViewById(R.id.btnCreate);
		dbm = new DBManager(this);
		btnCreate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				app.getThreadPool().submit(new CreateGroupThread());
			}
		});
	}

	class CreateGroupThread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Map params = new HashMap();
			String session = URLEncoder.encode(dbm.getSession());
			String groupName = edtGroupName.getText().toString();
			groupName = URLEncoder.encode(groupName);
			params.put("sessionid",session);
			params.put("groupname", groupName);
			PostWork pw = new PostWork(PostWork.URL_NEW_GROUP, params, mHandler);
			pw.work();
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_group, menu);
		return true;
	}

}
