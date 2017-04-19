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
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class ViewGroupActivity extends Activity {

	private EditText viewGroupName;
	private Button btnModify;
	private TextView txtGroupCode;
	private String groupId;
	private DBManager dbm;
	private MyApplication app;
	
	public static int CHANGE_GROUP_NAME = 200;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what==2){
				String key = (String) msg.obj;
				txtGroupCode.setText(key);
			}else if (msg.what==1){
				Gson gson = new Gson();
				Group group =gson.fromJson(gson.toJson(msg.obj), Group.class);
				dbm.saveGroup(group);
				Intent intent = new Intent();
				intent.putExtra("groupName", group.getGroupName());
				ViewGroupActivity.this.setResult(CHANGE_GROUP_NAME, intent);
				app.getThreadPool().submit(new GetGroupKeyThread());
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_group);
		viewGroupName = (EditText) findViewById(R.id.viewGroupName);
		btnModify = (Button) findViewById(R.id.btnModify);
		txtGroupCode = (TextView) findViewById(R.id.txtGroupCode);
		Intent intent = this.getIntent();
		groupId = intent.getStringExtra("groupId");
		viewGroupName.setText(intent.getStringExtra("groupName"));
		dbm = new DBManager(this);
		app = (MyApplication) getApplication();
		txtGroupCode.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				
				PopupMenu popup = new PopupMenu(ViewGroupActivity.this, txtGroupCode);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                    .inflate(R.menu.copy_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                    	Context context = ViewGroupActivity.this;
                    	ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    	cmb.setText(txtGroupCode.getText().toString().trim()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
                    	//cmb.getText();//获取粘贴信息
                        return true;
                    }
                });
                popup.show(); //showing popup menu 
				return false;
			}
		});
		btnModify.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				app.getThreadPool().submit(new ModifyThread());
			}
		});
		app.getThreadPool().submit(new GetGroupKeyThread());
	}

	class ModifyThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Map params = new HashMap();
			String session = ((MyApplication)getApplication()).getSessionId(); 
			params.put("sessionid", URLEncoder.encode(session));
			params.put("groupid", groupId);
			params.put("groupname", URLEncoder.encode(viewGroupName.getText().toString()));
			PostWork pw = new PostWork(PostWork.URL_SAVE_GROUP, params, mHandler);
			pw.work();
		}
	}
	
	class GetGroupKeyThread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Map params = new HashMap();
			String session = ((MyApplication)getApplication()).getSessionId(); 
			params.put("sessionid", URLEncoder.encode(session));
			params.put("groupid", groupId);
			PostWork pw = new PostWork(PostWork.URL_GET_GROUP_KEY, params, mHandler);
			pw.work();
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_group, menu);
		return true;
	}

}
