package com.taozhang.filetransition.ui;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.taozhang.filetransition.R;
import com.taozhang.filetransition.adapter.AdapterOfGridView;
import com.taozhang.filetransition.adapter.AdapterOfGridView_operationComputer;
import com.taozhang.filetransition.adapter.AdapterOfViewPager;
import com.taozhang.filetransition.bean.FileBean;
import com.taozhang.filetransition.bean.FileDetail;
import com.taozhang.filetransition.util.Connect;

/**
 * 
 * ActivityResult:{who = android:fragment:1,request=50001,result=-1,data =
 * Intent{dat=file:// + Absoulutepath}}
 * 
 * @author taozhang
 * 
 */
public class FileListActivity extends Activity {

	private ViewPager mViewPager;
	private PagerTabStrip mPagerTabStrip;
	private String[] tab_titles;
	private List<View> views;
	private String TAG = "";
	private AdapterOfViewPager mAdapterOfViewPager;
	/**
	 * 存放基本文件类型的list
	 */
	private List<FileBean> mFileBeans = new ArrayList<FileBean>();
	private FileBean file;
	private static InetSocketAddress address ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_list);

		initComponent();
		Bundle bundleExtra = getIntent().getBundleExtra("bundle");
		address = (InetSocketAddress) bundleExtra.get("IP");  
		getServiceData(address);//获取数据
	}

	private void initComponent() {
		mViewPager = (ViewPager) findViewById(R.id.viewPager_fileListAct);
		mPagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip_fileListAct);

		tab_titles = getResources()
				.getStringArray(R.array.pagerTabStrip_titles);// 获取tab标题数组
		mPagerTabStrip.setDrawFullUnderline(false);
		// 设置tab的背景色
		// mPagerTabStrip.setBackgroundColor(getResources().getColor(
		// R.color.bg));
		// 设置当前tab页签的下划线颜色
		// mPagerTabStrip.setTabIndicatorColor(getResources().getColor(
		// R.color.red));
		// mPagerTabStrip.setTextSpacing(200);

		LayoutInflater inflater = LayoutInflater.from(this);
		views = new ArrayList<View>();
		views.add(inflater.inflate(R.layout.viewpager_first, null));
		views.add(inflater.inflate(R.layout.viewpager_second, null));
		views.add(inflater.inflate(R.layout.viewpager_third, null));

		// 设置适配器
		mAdapterOfViewPager = new AdapterOfViewPager(views, tab_titles, this);
		mViewPager.setAdapter(mAdapterOfViewPager);

		initPagerOne();
	}

	public static Connect connect;
	private ArrayList<FileDetail> mFiles;

	/**
	 * 获取服务器端的诗句
	 * 
	 * @param address
	 */
	private void getServiceData(final InetSocketAddress address) {
	
		new Thread() {
			public void run() {
				try {
					// 閾炬帴
					connect = Connect.getInstance(address);
					// 璇锋眰鏂囦欢鍒楄〃
					connect.sendMsg(1 + "");
					// 鎺ュ彈鏈嶅姟鍣ㄥ彂鏉ョ殑json鍒楄〃
					String json;
					json = connect.getMsg();
					Gson gson = new Gson();
					Log.e("json打印", json);
					mFiles = gson.fromJson(json,
							new TypeToken<ArrayList<FileDetail>>() {
							}.getType());

					Log.i("FileListActivity_files", mFiles.toString());
					adapterOfGridView.setFiles(mFiles);
					mHandler.sendEmptyMessage(1);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();

	

	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;

			switch (what) {
			case 1:
				adapterOfGridView.notifyDataSetChanged();
				break;
			case 2:
				// 跳转
				Intent intent = (Intent) msg.obj;
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	};

	private AdapterOfGridView_operationComputer adapterOfGridView;

	/**
	 * init the GridView in the firstView
	 */
	private void initPagerOne() {
		GridView gridView = (GridView) views.get(0).findViewById(
				R.id.gridView_viewPager);
		// adapterOfGridView.setFiles(mFiles);
		initBasicFiles();
		adapterOfGridView = new AdapterOfGridView_operationComputer(
				getApplicationContext());
		gridView.setAdapter(adapterOfGridView);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FileDetail bean = (FileDetail) parent
						.getItemAtPosition(position);
				final String path = bean.getPath();
				new Thread() {
					@Override
					public void run() {
						super.run();
						connect.sendMsg("path");
						connect.sendMsg(path);
						Intent intent;
						try {
							String result = connect.getMsg();
							if (result.equals("OK")) {
								intent = new Intent(FileListActivity.this,
										FileOperationActivity.class);
								Message msg = new Message();
								msg.obj = intent;
								msg.what = 2;
								mHandler.sendMessage(msg);
							} else if (result.equals("ERROR")) {
								Toast.makeText(getApplicationContext(), "出现异常", Toast.LENGTH_SHORT).show();
							}
							Log.i("FileListActivity", result);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
			}
		});
	}

	/**
	 * 初始化基本的6中文件类型
	 */
	private void initBasicFiles() {
		file = new FileBean();
		file.setName("音乐");
		file.setId(R.drawable.music_red);
		mFileBeans.add(file);
		file = new FileBean();
		file.setName("图片");
		file.setId(R.drawable.pic_grey);
		mFileBeans.add(file);
		file = new FileBean();
		file.setName("视频");
		file.setId(R.drawable.audio_black);
		mFileBeans.add(file);
		file = new FileBean();
		file.setName("apk文件");
		file.setId(R.drawable.app_blue);
		mFileBeans.add(file);
		file = new FileBean();
		file.setName("office文件");
		file.setId(R.drawable.office_red);
		mFileBeans.add(file);
		file = new FileBean();
		file.setName("其他");
		file.setId(R.drawable.file);
		mFileBeans.add(file);
	}

}
