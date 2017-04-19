package com.plus.cloudcontacts.activity;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.plus.cloudcontacts.R;
import com.plus.cloudcontacts.bean.Contact;
import com.plus.cloudcontacts.bean.Group;
import com.plus.cloudcontacts.http.PostWork;
import com.plus.cloudcontacts.service.GetChatDataService;
import com.plus.cloudcontacts.util.DBManager;
import com.plus.cloudcontacts.util.MyApplication;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;

public class MenuActivity extends FragmentActivity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    
    Intent service;

    ViewPager mViewPager;
    
	private DBManager dbm;
	private MyApplication app;
    
    public List<Map<String,String>> myGroups;
    
    private static final int GROUP_MESSAGE = 1;
    private static final int GROUP_MEMBERS_MESSAGE = 11;
    private static final int TO_DATABASE_FINISHED = 100;
    
//    private Handler groupListHandler;
    
//    public void setGroupListHandler(Handler handler){
//    	this.groupListHandler = handler;
//    }

	protected Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Gson gson = new Gson();
			if(msg.what==GROUP_MESSAGE){
				//myGroups = new ArrayList<Map<String,String>>();
				app.getThreadPool().submit(new GroupsToDb(gson.toJson(msg.obj)));
			}else if (msg.what==GROUP_MEMBERS_MESSAGE){
				String groupId = msg.getData().getString("msg").split(",")[0];
				String datetime = msg.getData().getString("msg").split(",")[1];
				app.getThreadPool().submit(new MembersToDb(gson.toJson(msg.obj),groupId,datetime));
//				Message refreshMsg = new Message();
//				refreshMsg.what = GroupSectionFragment.REFRESH_GROUP_LISTVIEW;
//				groupListHandler.sendMessage(refreshMsg);
			}else if (msg.what==TO_DATABASE_FINISHED){
				Intent intent = new Intent("android.intent.action.CART_BROADCAST");
				LocalBroadcastManager.getInstance(MenuActivity.this).sendBroadcast(intent);
			}
		}

		class MembersToDb implements Runnable {
			private String json;
			private String groupId;
			private String datetime;
			public MembersToDb(String json,String groupId,String datetime){
				this.json = json;
				this.groupId = groupId;
				this.datetime = datetime;
			}
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				Contact[] cs =gson.fromJson(json, Contact[].class);
				for(int i=0;i<cs.length;i++){
					int status = Integer.valueOf(cs[i].getStatus());
					if (status>0){
						dbm.saveContact(cs[i],groupId);
					}else if(status<0){
						dbm.leaveGroup(groupId,cs[i].getContactId());
					}else{
						dbm.saveContact(cs[i],null);
					}
				}
				dbm.finishGroupContactUpdate(groupId, String.valueOf(datetime));
				mHandler.sendEmptyMessage(TO_DATABASE_FINISHED);
			}
		}

		class GroupsToDb implements Runnable {
			// TODO Auto-generated method stub
			private String json;
			public GroupsToDb(String json){
				this.json = json;
			}
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				Group[] gs =gson.fromJson(json, Group[].class);
				for(int i=0;i<gs.length;i++){
					gs[i] = dbm.saveGroup(gs[i]);
				}
				for(int i=0;i<gs.length;i++){
					if (gs[i].getTime() == null){
						app.getThreadPool().submit(new GetGroupContacts(gs[i].getGroupId(),null));
					}else{
						String time = gs[i].getTime();
						app.getThreadPool().submit(new GetGroupContacts(gs[i].getGroupId(), time));
					}
				}
			}
		}
		
	};

	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if (keyCode == KeyEvent.KEYCODE_BACK) {  
            moveTaskToBack(false);  
            return true;  
        }  
        return super.onKeyDown(keyCode, event);  
    }
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        myGroups = new ArrayList<Map<String,String>>();
        dbm = new DBManager(this);
        app = (MyApplication) getApplication();
        getMyGroupsData();
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),this);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        service = new Intent(this, GetChatDataService.class);
        startService(service);
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	stopService(service);
    	super.onDestroy();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	if(resultCode==RESULT_OK){
    		String groupId = data.getStringExtra("groupId");
    		app.getThreadPool().submit(new GetGroupContacts(groupId, null));
    	}
    }
    
    public void getMyGroupsData(){
    	app.getThreadPool().submit(new GetMyGroupsData());
    }
    
    class GetMyGroupsData implements Runnable{
		// TODO Auto-generated method stub
		@Override
		public void run() {
			Map params = new HashMap();
			String session = ((MyApplication)getApplication()).getSessionId(); 
			params.put("sessionid", URLEncoder.encode(session));
			PostWork pw = new PostWork(PostWork.URL_GET_GROUPS,params,mHandler);
			pw.work();
		}
	}
    
    class GetGroupContacts implements Runnable{
    	private String groupId;
    	private String date;
    	public GetGroupContacts(String groupId, String date){
    		this.groupId = groupId;
    		this.date = date;
    	}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Map params = new HashMap();
			String session = ((MyApplication)getApplication()).getSessionId(); 
			params.put("sessionid", URLEncoder.encode(session)); 
			params.put("groupid", groupId);
			if (date!=null){
				params.put("date", date);
			}
			PostWork pw = new PostWork(PostWork.URL_GET_MEMBERS,params,mHandler);
			pw.work();
		}
	}
    
    
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    
    

}
