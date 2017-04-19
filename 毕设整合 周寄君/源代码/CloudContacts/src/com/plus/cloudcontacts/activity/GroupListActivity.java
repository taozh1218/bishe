package com.plus.cloudcontacts.activity;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.plus.cloudcontacts.bean.Contact;
import com.plus.cloudcontacts.http.PostWork;
import com.plus.cloudcontacts.util.CharacterParser;
import com.plus.cloudcontacts.util.ChineseCharToEn;
import com.plus.cloudcontacts.util.DBManager;
import com.plus.cloudcontacts.util.MyApplication;
import com.plus.cloudcontacts.util.SystemContact;
import com.plus.cloudcontacts.R;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AlphabetIndexer;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.SimpleAdapter.ViewBinder;

public class GroupListActivity extends Activity {

	private ListView groupList;
	private List<Map<String, String>> listData;

	private List<Contact> members;
	private String groupName;
	private String groupId;
	private Button btnViewGroup;
	private Button btnChat;
	private Button btnSendMsg;
//	private Button btnAway;
	private Button btnImport;
	private SimpleAdapter sa;
	
    private HashMap<String, Integer> selector;// 存放含有索引字母的位置  
    private LinearLayout layoutIndex;  
    private TextView tv_show;  
    private String[] indexStr = { "#", "A", "B", "C", "D", "E", "F", "G", "H",  
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",  
            "V", "W", "X", "Y", "Z" };  
    private int height;// 字体高度  
    private boolean flag = false; 

	private MyApplication app;
	
	private List data;
	private String otherTel;

	private boolean importing = false;

	DBManager dbm;
	
	public static final int DATABASE_QUERY_FINISHED = 100;
	public static int CHANGE_GROUP_NAME = 200;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 0) {

			} else if (msg.what == 1) {
				String myid = ((MyApplication) getApplication()).getMe()
						.getContactId();
				dbm.leaveGroup(groupId, myid);
				Intent intent = new Intent(
						"android.intent.action.CART_BROADCAST");
				LocalBroadcastManager.getInstance(GroupListActivity.this)
						.sendBroadcast(intent);
				Toast.makeText(GroupListActivity.this, "已离开组",
						Toast.LENGTH_SHORT).show();
				finish();
			} else if (msg.what == 2) {
				importing = false;
				Toast.makeText(GroupListActivity.this, "导入成功",
						Toast.LENGTH_SHORT).show();
			} else if (msg.what == 3) {
				Toast.makeText(GroupListActivity.this, "导入中..",
						Toast.LENGTH_SHORT).show();
			} else if (msg.what == DATABASE_QUERY_FINISHED) {
				sa.notifyDataSetChanged();
			}
		}
	};

	class DbThread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			members = dbm.getMembers(groupId);
			getHashMap(data);
			Message msg = new Message();
			msg.what = DATABASE_QUERY_FINISHED;
			mHandler.sendMessage(msg);
		}
	}
	
	class NetThread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Map params = new HashMap();
			String session = URLEncoder.encode(dbm.getSession());
			params.put("sessionid", session);
			params.put("groupid", groupId);
			PostWork pw = new PostWork(PostWork.URL_AWAY,params, mHandler);
			pw.work();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_list);
		groupList = (ListView) findViewById(R.id.groupList);
		dbm = new DBManager(this);
		app = (MyApplication) getApplication();
		Intent intent = this.getIntent();
		groupId = intent.getStringExtra("groupId");
		groupName = intent.getStringExtra("groupName");
		setTitle(groupName);
		app.getThreadPool().submit(new DbThread());
		data = new ArrayList();
		sa = new SimpleAdapter(GroupListActivity.this, data,
				R.layout.list_item, new String[] { "name" },
				new int[] { R.id.groupName }) {
			@Override
			public View getView(int position, View convertView,
					ViewGroup parent) {
				// TODO Auto-generated method stub
				View view = super
						.getView(position, convertView, parent);
				ImageView avatar = (ImageView) view
						.findViewById(R.id.imageView1);
				if (listData.get(position).get("avatar") == null) {
					listData.get(position).put("avatar", "default.jpg");
				}
				String imageName = listData.get(position).get("avatar");
				String name = listData.get(position).get("name");
				if (name == "") {
					FirstWordAsyncTask task = new FirstWordAsyncTask(avatar);
					task.execute(imageName);
				} else {
					AsyncImageTask task = new AsyncImageTask(avatar);
					task.execute(imageName);
				}
				return view;
			}
		};
		sa.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view,
					Object attentionList, String textRepresentation) {
				// TODO Auto-generated method stub
				if (view instanceof ImageView
						&& attentionList instanceof Bitmap) {
					ImageView iv = (ImageView) view;
					iv.setImageBitmap((Bitmap) attentionList);
					return true;
				} else {
					return false;
				}
			}
		});
		groupList.setAdapter(sa);
//		btnAway = (Button) findViewById(R.id.btnAway);
		btnSendMsg = (Button) findViewById(R.id.btnSendMsg);
		btnChat = (Button) findViewById(R.id.btnChat);
		btnImport = (Button) findViewById(R.id.btnImport);
		btnViewGroup = (Button) findViewById(R.id.btnViewGroup);
        
		layoutIndex = (LinearLayout) this.findViewById(R.id.layout);  
        layoutIndex.setBackgroundColor(Color.parseColor("#00ffffff"));   
        tv_show = (TextView) findViewById(R.id.tv);  
        tv_show.setVisibility(View.GONE);  
         
		
		groupList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// Toast.makeText(GroupListActivity.this, listData.get(arg2),
				// 2000).show();
				String name = listData.get(arg2).get("name");
				if (name != "") {
					Intent intent = new Intent(GroupListActivity.this,
							CardActivity.class);
					intent.putExtra("fromGroup", groupName);
					intent.putExtra("name", name);
					System.out.println(listData.get(arg2).get("avatar"));
					intent.putExtra("avatar", listData.get(arg2).get("avatar"));
					intent.putExtra("tel", listData.get(arg2).get("tel"));
					startActivity(intent);
				}
			}
		});

		btnChat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(GroupListActivity.this,
						ChatActivity.class);
				intent.putExtra("groupId", groupId);
				intent.putExtra("groupName", groupName);
				startActivity(intent);
			}
		});
		btnSendMsg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+otherTel));            
	            startActivity(intent);
			}
		});
//		btnAway.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				new AlertDialog.Builder(GroupListActivity.this)
//						.setTitle("你确定要退出组吗？")
//						.setPositiveButton("是",
//								new DialogInterface.OnClickListener() {
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										// TODO Auto-generated method stub
//										dialog.dismiss();
//										app.getThreadPool().submit(new NetThread());
//									}
//								})
//						.setNegativeButton("否",
//								new DialogInterface.OnClickListener() {
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										// 
//										dialog.dismiss();
//									}
//								}).show();
//			}
//		});
		
		btnViewGroup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GroupListActivity.this,
						ViewGroupActivity.class);
				intent.putExtra("groupId", groupId);
				intent.putExtra("groupName", groupName);
				startActivityForResult(intent, 0);
			}
		});
		btnImport.setVisibility(View.INVISIBLE);
		// btnImport.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// if(!importing){
		// importing = true;
		// new Thread(){
		// public void run() {
		// for(int i=0;i<groupList.getCount();i++){
		// LinearLayout linearlayout =
		// (LinearLayout)groupList.getAdapter().getView(i, null, null);
		// ImageView avatar = (ImageView)linearlayout.getChildAt(0);
		// TextView tv_name = (TextView)linearlayout.getChildAt(1);
		// String name = tv_name.getText().toString();
		// if (name!=""){
		// String tel = listData.get(i).get("tel");
		// SystemContact.insert(linearlayout.getContext(), name, tel, "", "");
		// }
		// }
		// Message msg = new Message();
		// msg.what = 2;
		// mHandler.handleMessage(msg);
		// };
		// }.start();
		// }else{
		// Message msg = new Message();
		// msg.what = 3;
		// mHandler.handleMessage(msg);
		// }
		// }
		// });
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_away:
			new AlertDialog.Builder(GroupListActivity.this)
			.setTitle("你确定要退出组吗？")
			.setPositiveButton("是",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							app.getThreadPool().submit(new NetThread());
						}
					})
			.setNegativeButton("否",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					}).show();
			break;

		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == CHANGE_GROUP_NAME){
			this.setTitle(data.getStringExtra("groupName"));
			Intent intent = new Intent(
					"android.intent.action.CART_BROADCAST");
			LocalBroadcastManager.getInstance(GroupListActivity.this)
					.sendBroadcast(intent);
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private List<? extends Map<String, ?>> getHashMap(List data) {
		// TODO Auto-generated method stub
		Object[] array_mem = members.toArray();
		listData = new ArrayList<Map<String, String>>();
		ChChCom ccc = new ChChCom();
		Arrays.sort(array_mem, ccc);
		String py = "";
		StringBuffer telBuffer = new StringBuffer();
		for (int i = 0; i < array_mem.length; i++) {
			Contact obj = (Contact) array_mem[i];
			String tpy = ChineseCharToEn.getFirstWordLetter(obj.getName())
					.toUpperCase();
			if (!py.equals(tpy)) {
				py = tpy;
				Map map = new HashMap();
				map.put("name", "");
				map.put("pyname",py);
				data.add(map);
				map.put("avatar", py);
				map.put("tel", "");
				listData.add(map);
			}
			Map map = new HashMap();
			// BitmapDrawable bd = (BitmapDrawable)
			// getResources().getDrawable(R.drawable.ic_launcher);
			// Bitmap bitmap = bd.getBitmap();
			map.put("name", obj.getName());
			data.add(map);
			map.put("avatar", obj.getAvatar());
			map.put("tel", obj.getTel());
			listData.add(map);
			telBuffer.append(","+obj.getTel());
		}
		otherTel = telBuffer.substring(1);
		selector = new HashMap<String, Integer>();  
        for (int j = 0; j < indexStr.length; j++) {// 循环字母表，找出newPersons中对应字母的位置  
            for (int i = 0; i < listData.size(); i++) {  
                if (indexStr[j].equals(listData.get(i).get("pyname"))) {  
                    selector.put(indexStr[j], i);  
                }  
            }  
  
        }  
		return data;
	}

	 public void getIndexView() {  
	        LinearLayout.LayoutParams params = new LayoutParams(  
	                LayoutParams.WRAP_CONTENT, height);  
	        for (int i = 0; i < indexStr.length; i++) {  
	            final TextView tv = new TextView(this);  
	            tv.setLayoutParams(params);  
	            tv.setText(indexStr[i]);  
	            tv.setPadding(10, 0, 10, 0);  
	            layoutIndex.addView(tv);  
	            layoutIndex.setOnTouchListener(new OnTouchListener() {  
	  
	                @Override  
	                public boolean onTouch(View v, MotionEvent event)  
	  
	                {  
	                    float y = event.getY();  
	                    int index = (int) (y / height);  
	                    if (index > -1 && index < indexStr.length) {// 防止越界  
	                        String key = indexStr[index];  
	                        if (selector.containsKey(key)) {  
	                            int pos = selector.get(key);  
	                            if (groupList.getHeaderViewsCount() > 0) {// 防止ListView有标题栏。  
	                            	groupList.setSelectionFromTop(  
	                                        pos + groupList.getHeaderViewsCount(), 0);  
	                            } else {  
	                            	groupList.setSelectionFromTop(pos, 0);// 滑动到第一项  
	                            }  
	                            tv_show.setVisibility(View.VISIBLE);  
	                            tv_show.setText(indexStr[index]);  
	                        }  
	                    }  
	                    switch (event.getAction()) {  
	                    case MotionEvent.ACTION_DOWN:  
	                        layoutIndex.setBackgroundColor(Color  
	                                .parseColor("#939393"));  
	                        break;  
	  
	                    case MotionEvent.ACTION_MOVE:  
	  
	                        break;  
	                    case MotionEvent.ACTION_UP:  
	                        layoutIndex.setBackgroundColor(Color  
	                                .parseColor("#00ffffff"));  
	                        tv_show.setVisibility(View.GONE);  
	                        break;  
	                    }  
	                    return true;  
	                }  
	            });  
	        }  
	    }  
	
	@Override  
    public void onWindowFocusChanged(boolean hasFocus) {  
         if (!flag) {
            height = layoutIndex.getMeasuredHeight() / indexStr.length;  
            getIndexView();  
            flag = true;  
        }  
    }  
	
	private final class FirstWordAsyncTask extends AsyncTask<String, Integer, Bitmap> {

		private ImageView avatar;

		public FirstWordAsyncTask(ImageView avatar) {
			this.avatar = avatar;
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			Bitmap bmp = Bitmap.createBitmap(80, 80,
					Config.ARGB_8888);
			Canvas canvas = new Canvas(bmp);
			Paint paint = new Paint();
			paint.setColor(Color.rgb(0, 162, 232));
			canvas.drawCircle(45, 40, 35, paint);
			paint.setColor(Color.WHITE);
			paint.setTextSize(40);
			Rect rect = new Rect();
			paint.getTextBounds(params[0], 0, 1, rect);
			rect.height();
			// canvas.drawColor(Color.WHITE);
			canvas.drawText(params[0], (90 - rect.width()) / 2,
					(80 + rect.height()) / 2, paint);
			return bmp;
		}
		
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (avatar != null && result != null) {
				avatar.setImageBitmap(result);
			}
		}
	}
	
	private final class AsyncImageTask extends AsyncTask<String, Integer, Uri> {

		private ImageView iv_header;

		public AsyncImageTask(ImageView iv_header) {
			this.iv_header = iv_header;
		}

		// 后台运行的子线程子线程
		@Override
		protected Uri doInBackground(String... params) {
			try {
				return PostWork.getImageURI(params[0],
						GroupListActivity.this.app.getCache());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		// 这个放在在ui线程中执行
		@Override
		protected void onPostExecute(Uri result) {
			super.onPostExecute(result);
			// 完成图片的绑定
			if (iv_header != null && result != null) {
				iv_header.setImageURI(result);
			}
		}
	}

	public class ChChCom implements Comparator {
		@Override
		public int compare(Object a, Object b) {
			// TODO Auto-generated method stub
			String s1 = ((Contact) a).getName();
			String s2 = ((Contact) b).getName();
			s1 = CharacterParser.getInstance().getSelling(s1);
			s2 = CharacterParser.getInstance().getSelling(s2);
			// String s1 = ChineseCharToEn.getAllFirstLetter(((Contact)
			// a).getName());
			// String s2 = ChineseCharToEn.getAllFirstLetter(((Contact)
			// b).getName());
			return s1.compareTo(s2);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group_list, menu);
		return true;
	}

}
