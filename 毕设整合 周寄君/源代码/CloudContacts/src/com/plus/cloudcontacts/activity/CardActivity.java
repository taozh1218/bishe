package com.plus.cloudcontacts.activity;

import java.io.File;

import com.google.gson.Gson;
import com.plus.cloudcontacts.R;
import com.plus.cloudcontacts.R.layout;
import com.plus.cloudcontacts.R.menu;
import com.plus.cloudcontacts.bean.Contact;
import com.plus.cloudcontacts.http.PostWork;
import com.plus.cloudcontacts.util.MyApplication;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CardActivity extends Activity {

	private ImageView cardAvatar;
	private TextView cardFrom;
	private TextView cardName;
	private TextView cardTel;
	private Button btnCall;
	private Button btnText;
	private String tel;
	private String name;
	private String avatar;
	private MyApplication app;
	
	private static final int IMAGE_LOAD_SUCCESS = 5;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == IMAGE_LOAD_SUCCESS){
				cardAvatar.setImageURI(Uri.fromFile(new File(app.getCache(),avatar)));
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card);
		app = (MyApplication) getApplication();
		cardAvatar = (ImageView)findViewById(R.id.cardAvatar);
		cardFrom = (TextView)findViewById(R.id.cardFrom);
		cardName = (TextView)findViewById(R.id.cardName);
		cardTel = (TextView)findViewById(R.id.cardTel);
		btnCall = (Button)findViewById(R.id.btnCall);
		btnText = (Button)findViewById(R.id.btnText);
		tel = this.getIntent().getExtras().getString("tel");
		name = this.getIntent().getExtras().getString("name");
		avatar = this.getIntent().getExtras().getString("avatar");
		String fromGroup = this.getIntent().getExtras().getString("fromGroup");
		cardFrom.setText("À´×Ô"+fromGroup+"µÄ");
		cardName.setText(name);
		cardTel.setText(tel);
		File avatarFile = new File(app.getCache(),avatar);
		if (avatarFile.exists()){
			cardAvatar.setImageURI(Uri.fromFile(avatarFile));
		}else{
			app.getThreadPool().submit(new GetImageThread());
		}
		btnCall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+tel));
				startActivity(intent);
			}
		});
		
		btnText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+tel));            
	            startActivity(intent);  
			}
		});
		
	}

	class GetImageThread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(PostWork.getImageURI(avatar, app.getCache())!=null){
				Message msg = new Message();
				msg.what = IMAGE_LOAD_SUCCESS;
				mHandler.sendMessage(msg);
			}
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.card, menu);
		return true;
	}

}
