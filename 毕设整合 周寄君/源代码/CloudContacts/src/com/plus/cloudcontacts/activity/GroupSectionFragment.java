package com.plus.cloudcontacts.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.plus.cloudcontacts.R;
import com.plus.cloudcontacts.bean.Group;
import com.plus.cloudcontacts.util.DBManager;
import com.plus.cloudcontacts.util.MyApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class GroupSectionFragment extends Fragment implements OnRefreshListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    private ListView groupList;    
    private View rootView;
    private ArrayList<String> nameList;
    private ArrayList<String> sizeList;
    private ArrayList<String> groupMem;
    private DBManager dbm;
    private MyApplication app;
    private List<Group> groups;
    private SwipeRefreshLayout mSwipeLayout;
    private List hashMap;
    private SimpleAdapter sa;
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver mItemViewListClickReceiver;
    
    public static final int DATABASE_QUERY_FINISHED = 100;
//    
    private Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		if (msg.what==DATABASE_QUERY_FINISHED){
    			hashMap.removeAll(hashMap);
    			hashMap = getHashMap(hashMap);
    			sa.notifyDataSetChanged();
    		}
    	};
    };
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	broadcastManager = LocalBroadcastManager.getInstance(getActivity());
    	IntentFilter intentFilter = new IntentFilter();
    	intentFilter.addAction("android.intent.action.CART_BROADCAST");
    	mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
                app.getThreadPool().submit(new GroupsThread());
            }
    	 };
    	 broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    	 
    };
    
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	broadcastManager.unregisterReceiver(mItemViewListClickReceiver);
    	super.onDestroy();
    }
    
//	@Override
//    public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		MenuActivity mActivity = (MenuActivity) activity;
//		mActivity.setGroupListHandler(mHandler);
//    };
	
    public GroupSectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_group, container, false);
        //TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
        //dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
        groupList = (ListView) rootView.findViewById(R.id.groupList);
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_ly);
        mSwipeLayout.setOnRefreshListener(this);  
        mSwipeLayout.setColorScheme(android.R.color.darker_gray,android.R.color.white,android.R.color.darker_gray,android.R.color.white);
        dbm = new DBManager(rootView.getContext());
        app = (MyApplication) getActivity().getApplication();
        groups = new ArrayList<Group>();
        app.getThreadPool().submit(new GroupsThread());
        hashMap = new ArrayList();
        sa = new SimpleAdapter(rootView.getContext(), getHashMap(hashMap), R.layout.list_group,new String[]{"name","size"}, new int[]{R.id.groupName,R.id.groupSize});
        sa.setViewBinder(new ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Object attentionList, String textRepresentation) {
                // TODO Auto-generated method stub
                if(view instanceof ImageView && attentionList instanceof Bitmap){
                        ImageView iv=(ImageView)view;
                        iv.setImageBitmap((Bitmap) attentionList);
                        return true;
                }else{
                        return false;
                }	
			}
		});
        
        
        
        groupList.setAdapter(sa);
        groupList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (arg2!=0){
					Intent intent = new Intent(getActivity(),GroupListActivity.class);
					intent.putExtra("groupId", groups.get(arg2-1).getGroupId());
					intent.putExtra("groupName", groups.get(arg2-1).getGroupName());
					startActivity(intent);
				}else{
					Intent intent = new Intent(getActivity(),JoinGroupActivity.class);
					getActivity().startActivityForResult(intent, 0);
				}
			}
			
		});
        groupList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				//Toast.makeText(rootView.getContext(), arg2, 2000).show();
				return false;
			}
		});
        return rootView;
    }
    
	class GroupsThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			groups = dbm.getMyGroups(app.getMe().getContactId());
    		Message msg = new Message();
    		msg.what = DATABASE_QUERY_FINISHED;
    		mHandler.sendMessage(msg);
		}
	}

	private List<? extends Map<String, ?>> getHashMap(List data) {
		// TODO Auto-generated method stub
//		nameList = this.getArguments().getStringArrayList("groupName");
//		sizeList = this.getArguments().getStringArrayList("groupSize");
//      groupMem = this.getArguments().getStringArrayList("groupMem");
		Map join = new HashMap();
		join.put("name", "+ 加入一个组");
		data.add(join);
		for(int i=0;i<groups.size();i++){
			Map map = new HashMap();
			map.put("name", groups.get(i).getGroupName());
			map.put("size", groups.get(i).getOwnerId()+"个人");
			data.add(map);
		}
		return data;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
			((MenuActivity)this.getActivity()).getMyGroupsData();
			mSwipeLayout.setRefreshing(false);
	}
	
	
}