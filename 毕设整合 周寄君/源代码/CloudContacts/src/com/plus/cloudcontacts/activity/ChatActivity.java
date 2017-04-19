package com.plus.cloudcontacts.activity;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.plus.cloudcontacts.R;
import com.plus.cloudcontacts.RecordButton;
import com.plus.cloudcontacts.RecordButton.OnFinishedRecordListener;
import com.plus.cloudcontacts.bean.Chat;
import com.plus.cloudcontacts.bean.ListItem;
import com.plus.cloudcontacts.http.PostWork;
import com.plus.cloudcontacts.util.DBManager;
import com.plus.cloudcontacts.util.DirUtil;
import com.plus.cloudcontacts.util.MyApplication;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
public class ChatActivity extends Activity {

	private static int RESULT_LOAD_IMAGE = 1;

	private RecordButton mRecordButton = null;
	private Button btnSwitch = null;
	private Button btnSendPicture = null;
	private Button btnSendMsg = null;
	private Button btnSendVoice = null;
	private EditText etMsg;
	private ListView lvchat;
	private MyApplication app;
	private String target;
	private static String audioresponse = null;
	private static String msgresponse = null;
	private static String picresponse = null;
	private static String avatar_target = null;
	private DBManager dbm;
	private SQLiteDatabase db = null;
//	private static String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/.jmmsrtemp/";
	ChatAdapter mAdapter_ListGroup;
	ArrayList<ListItem> list_GroupItem;
	static ChatItem citem;
	boolean isWorking; 
	MessageChangeReceiver smsBroadCastReceiver;
	
	public String meAvatar;
	
	private String groupId;
	
    public static final int LIST_VIEW_NOTIFY = 100;
	   
	private Handler mHandler = new Handler(){ 
	    @Override
	    public void handleMessage(Message msg)
	    {
	        if(msg.what == LIST_VIEW_NOTIFY)
	        {
	        	String type = msg.getData().getString("type");
	        	String avatar = meAvatar;//DirUtil.getFilePath(meAvatar);
	        	msg.getData().putString(type, (String)msg.obj);
	        	if(msg.getData().getString("avatar").equals(avatar)){
	        	list_GroupItem.add(new ListItem(1, getHashMap1(msg.getData().getString("avatar"),
	        			msg.getData().getString("text"),
	        			msg.getData().getString("image"),
	        			msg.getData().getString("voice"))));
	        	mAdapter_ListGroup.notifyDataSetChanged();
	        	}else{
		        	list_GroupItem.add(new ListItem(0, getHashMap0(msg.getData().getString("avatar"),
		        			msg.getData().getString("text"),
		        			msg.getData().getString("image"),
		        			msg.getData().getString("voice"))));
		        	mAdapter_ListGroup.notifyDataSetChanged();
	        	}
	        	ListView listView = (ListView)findViewById(R.id.lvchat);
	        	listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	        }else if(msg.what == 4){
	        	app.getThreadPool().submit(new sendmsg(msg.getData().getString("type"),(String) msg.obj));
	        }
	        else if(msg.what == 1){
	        	Gson gson = new Gson();
	        	Chat chat = gson.fromJson(gson.toJson(msg.obj), Chat.class);
	        	Message subMsg = new Message();
	        	subMsg.what = LIST_VIEW_NOTIFY;
	        	subMsg.obj = chat.getContent();
				Bundle data = new Bundle();
//				data.putString("avatar", DirUtil.getFilePath(meAvatar));
				data.putString("avatar", meAvatar);
				data.putString("text", "null");
				data.putString("image", "null");
				data.putString("voice", "null");
				data.putString("type", chat.getType());
				subMsg.setData(data);
				mHandler.sendMessage(subMsg);
		        etMsg.setText("");
	        }
	    }
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		app = (MyApplication)getApplication();
		dbm = new DBManager(this);
        setTitle(getIntent().getStringExtra("groupName"));
        lvchat = (ListView)findViewById(R.id.lvchat);
        etMsg = (EditText)findViewById(R.id.et_msg_tbs);
        btnSwitch = (Button)findViewById(R.id.set_input);
        btnSendPicture = (Button)findViewById(R.id.select_picture);
        btnSendMsg = (Button)findViewById(R.id.btn_sendmsg);
        btnSendVoice = (Button)findViewById(R.id.record_button);
        
        groupId = getIntent().getStringExtra("groupId");
        meAvatar = app.getMe().getAvatar();

        smsBroadCastReceiver = new MessageChangeReceiver(meAvatar);    
        //实例化过滤器并设置要过滤的广播    
        IntentFilter intentFilter = new IntentFilter();    
        intentFilter.addAction("android.intent.action.NEW_CHAT_BROADCAST");    
        //注册广播    
        this.registerReceiver(smsBroadCastReceiver, intentFilter); 
        
        list_GroupItem = new ArrayList<ListItem>();
        mAdapter_ListGroup = new ChatAdapter(this, list_GroupItem);
		mAdapter_ListGroup.AddType(R.layout.item_msg);
		mAdapter_ListGroup.AddType(R.layout.item_msg_me);
		
        initListView();
        citem = new ChatItem();
        isWorking = true;
        new Thread(){
        	public void run() {
        		while(isWorking){
        			if(citem.isNew){
        				Message msg = new Message();
        				msg.what = LIST_VIEW_NOTIFY; 
        				Bundle data = new Bundle();
						data.putString("avatar", citem.avatar);
						data.putString("text",citem.content);
						data.putString("voice", citem.voice);
						data.putString("image", citem.image);
						msg.setData(data);
						mHandler.sendMessage(msg);
						citem.isNew = false;
        			}
        		}
        	};
        }.start();
        
        mRecordButton = (RecordButton)findViewById(R.id.record_button);
		String path = DirUtil.getFilePath("mmmm.amr");
		
		
		mRecordButton.setSavePath(path);
		mRecordButton.setOnFinishedRecordListener(new OnFinishedRecordListener() {
					@Override
					public void onFinishedRecord(final String audioPath) {
						Log.i("RECORD!!!", "finished!!!!!!!!!! save to " + audioPath);
						new Thread(){
			            	public void run() {
			            		File file =  new File(audioPath);
			            		List<File> list = new ArrayList<File>();
			            		list.add(file);
			        			Map params = new HashMap();
			        			String session = ((MyApplication)getApplication()).getSessionId(); 
			        			params.put("sessionid", URLEncoder.encode(session));
			            		PostWork pw = new PostWork(PostWork.URL_UPLOAD, params, mHandler);
			            		pw.msgPutString("type", "voice");
//			            		pw.msgPutString("avatar",DirUtil.getFilePath(meAvatar));
			            		pw.msgPutString("avatar", meAvatar);
			    	        	pw.msgPutString("text", "null");
			            		pw.msgPutString("voice", "null");
			            		pw.msgPutString("image", "null");
			            		pw.upload(list);
			            	};
			            }.start();
					}
				});
		btnSendPicture.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
	        Uri selectedImage = data.getData();
	        String[] filePathColumn = { MediaStore.Images.Media.DATA };
	 
	        Cursor cursor = getContentResolver().query(selectedImage,
	                filePathColumn, null, null, null);
	        cursor.moveToFirst();
	 
	        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	        final String picturePath = cursor.getString(columnIndex);
	        cursor.close();
	        new Thread(){
	        	public void run() {
	        		File picture = new File(picturePath);
	        		List<File> list = new ArrayList<File>();
            		list.add(picture);
					Map<String,String> params = new HashMap<String, String>();
					params.put("sessionid", URLEncoder.encode(app.getSessionId()));
            		PostWork pw = new PostWork(PostWork.URL_UPLOAD, params, mHandler);
            		pw.msgPutString("type", "image");
//            		pw.msgPutString("avatar",DirUtil.getFilePath(meAvatar));
            		pw.msgPutString("avatar", meAvatar);
    	        	pw.msgPutString("text", "null");
            		pw.msgPutString("voice", "null");
            		pw.msgPutString("image", "null");
            		pw.upload(list); 
	        	};
	        }.start();
		}
	}
	
	
	public void initListView(){
		
		String uid = app.getMe().getContactId();
		List<Chat> chats = dbm.getGroupChats(groupId);
		for (Chat chat : chats){
	        Message msg = new Message();
	        msg.what = LIST_VIEW_NOTIFY;
			Bundle data = new Bundle();
//			data.putString("avatar", DirUtil.getFilePath(chat.getAvatar()));
			data.putString("avatar", chat.getAvatar());
			data.putString("text", "null");
			data.putString("voice", "null");
			data.putString("image", "null");
			data.putString(chat.getType(), chat.getContent());
			msg.setData(data);
			mHandler.sendMessage(msg);
		}
		
        lvchat.setAdapter(mAdapter_ListGroup);
        lvchat.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        lvchat.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
            @Override  
            public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) { 
            	//((ListView)v).get
            } 
        });  

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}
	public void btnSendmsgClicked(View v){
		//listItem.clear();
		String text = etMsg.getText().toString();
		if(!(text==null)&&!text.equals("")){	
	        app.getThreadPool().submit(new sendmsg("text",etMsg.getText().toString()));
		}
	}
	public void switchInput(View view){
		InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		// 显示或者隐藏输入法
		String value = btnSwitch.getText().toString();
		if (value.equals("+")){
			imm.hideSoftInputFromWindow(ChatActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			btnSwitch.setText("-");
			btnSendPicture.setVisibility(0);
			btnSendVoice.setVisibility(0);
			btnSendMsg.setVisibility(4);
			etMsg.setVisibility(4);
			//etMsg.setFocusableInTouchMode(focusableInTouchMode)
		}else if (value.equals("-")){
			btnSwitch.setText("+");
			btnSendPicture.setVisibility(4);
			btnSendVoice.setVisibility(4);
			btnSendMsg.setVisibility(0);
			etMsg.setVisibility(0);
		}
	}
	
	private class sendmsg implements Runnable{
		private final String type;
		private final String content;
		public sendmsg(String type,String content) {
			// TODO Auto-generated constructor stub
			this.type = type;
			this.content = content;
		}
		@Override
		public void run() {
			Map<String, String> params = new HashMap<String, String>();
			params.put("sessionid", URLEncoder.encode(app.getSessionId()));
			params.put("groupid", groupId);
			params.put("type", type);
			params.put("content", URLEncoder.encode(content));
			PostWork pw = new PostWork(PostWork.URL_SAY, params, mHandler);
			pw.work();
		};
	}
	private HashMap<Integer, String> getHashMap0(String avatar,String content,String image,String voice) {	
		HashMap<Integer, String> map1 = new HashMap<Integer, String>();
//		if (!"null".equals(image)){
//			image = DirUtil.getFilePath(image);
//		}
		map1.put(R.id.chat_avatar, avatar);
		map1.put(R.id.chat_msg, content);
		map1.put(R.id.chat_image, image);
		map1.put(R.id.btn_play, voice);
		return map1;
	}
	private HashMap<Integer, String> getHashMap1(String avatar,String content,String image,String voice) {	
		HashMap<Integer, String> map1 = new HashMap<Integer, String>();
//		if (!"null".equals(image)){
//			image = DirUtil.getFilePath(image);
//		}
		map1.put(R.id.chat_avatar_me, avatar);
		map1.put(R.id.chat_msg_me, content);
		map1.put(R.id.chat_image_me, image);
		map1.put(R.id.btn_play_me, voice);
		return map1;
	}
	public static class MessageChangeReceiver extends BroadcastReceiver {  
		private String meAvatar;
		public MessageChangeReceiver(String meAvatar){
			this.meAvatar = meAvatar;
		}
        @Override  
        public void onReceive(Context context, Intent intent) {  
            // TODO Auto-generated method stub  
			String avatar = intent.getStringExtra("avatar");
        	if (!meAvatar.equals(avatar)){
//				String avatar = intent.getStringExtra("avatar");
	        	String type = intent.getStringExtra("type");
	        	String content = intent.getStringExtra("content");
				String voice = "null";
				String image = "null";
		        if(type.equals("image")){
		        	image = content;
		        	content = "null";
		        }
		        else if(type.equals("voice")){
		        	voice = content;
		        	content = "null";
		        }
		        citem.avatar = avatar;//DirUtil.getFilePath(avatar);
		        citem.voice = voice;
		        citem.image = image;
		        citem.content = content;
		        citem.isNew = true;
        	}
        }  
    }  
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(smsBroadCastReceiver); 
		System.out.println("destroy");
        isWorking = false;
		super.onDestroy();
	}

	public class ChatItem{
		public String content;
		public String avatar;
		public String voice;
		public String image;
		public boolean isNew = false;
	}
}
