package com.taozhang.filetransition.ui;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.taozhang.filetransition.R;
import com.taozhang.filetransition.adapter.AdapterOfGridView_operationComputer;
import com.taozhang.filetransition.adapter.AdapterOfViewPager;
import com.taozhang.filetransition.adapter.PCGridViewAdapter;
import com.taozhang.filetransition.base.App;
import com.taozhang.filetransition.bean.FileBean;
import com.taozhang.filetransition.bean.FileDetail;
import com.taozhang.filetransition.bean.FileInfo;
import com.taozhang.filetransition.impl.FileSelecedImpl;
import com.taozhang.filetransition.util.Connect;
import com.taozhang.filetransition.util.Constant;
import com.taozhang.filetransition.util.MessageUtil;

/**
 * 
 * ActivityResult:{who = android:fragment:1,request=50001,result=-1,data =
 * Intent{dat=file:// + Absoulutepath}}
 * 
 * @author taozhang
 * 
 */
public class FileListActivity extends Activity implements FileSelecedImpl, OnClickListener, OnItemLongClickListener{

	private ViewPager mViewPager;
	private PagerTabStrip mPagerTabStrip;
	private String[] tab_titles;
	private List<View> views;
	private AdapterOfViewPager mAdapterOfViewPager;
	/**
	 * ��Ż����ļ����͵�list
	 */
	private List<FileBean> mFileBeans = new ArrayList<FileBean>();
	private FileBean file;
	public  static InetSocketAddress address ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.computer_file_layout);
		initComponent();
		Bundle bundleExtra = getIntent().getBundleExtra("bundle");
		address = (InetSocketAddress) bundleExtra.get("IP");  
		getServiceData(address);//��ȡ����
	}

	public void initComponent() {
		mHandler = new FileHandler(this);
		initPagerOne();
	}

	public static Connect connect;
	private ArrayList<FileDetail> mFiles;

	/**
	 * ��ȡ�������˵�����
	 * 
	 * @param address
	 */
	private boolean hasConnectPC = false;
	public  void getServiceData(final InetSocketAddress address) {
	
		new Thread() {
			public void run() {
				try {
					// 链接
					connect = Connect.getInstance();
					if(!hasConnectPC){
						connect.connectServer(address,Constant.PCPORT);
						hasConnectPC = true;
					}
					// 请求文件列表
					connect.sendMsg(1 + "");
					// 接受服务器发来的json列表
					String json;
					json = connect.getMsg();
					Gson gson = new Gson();
					Log.e("json��ӡ", json);
					mFiles = gson.fromJson(json,
							new TypeToken<ArrayList<FileDetail>>() {
							}.getType());

					Log.i("FileListActivity_files", mFiles.toString());
					adapter.setData(mFiles);
					mHandler.sendEmptyMessage(1);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();

	

	}
	private static class FileHandler extends Handler{
		private WeakReference<FileListActivity> weakRef;
		private FileListActivity activity;
		public FileHandler(FileListActivity weakRef) {
			super();
			this.weakRef = new WeakReference<FileListActivity>(weakRef);
			activity = this.weakRef.get();
		}
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;

			switch (what) {
			case 1:
				activity.adapter.notifyDataSetChanged();
				activity.progress.setVisibility(View.GONE);
				activity.sendButton.setVisibility(View.VISIBLE);
				break;
			case 2:
				// ��ת
				Intent intent = (Intent) msg.obj;
				activity.startActivity(intent);
				break;
			case 3:
				String str = (String) msg.obj;
				Toast.makeText(activity.getApplicationContext(), str, Toast.LENGTH_SHORT).show();
				break;
			
			default:
				break;
			}
		}
	}
	public FileHandler mHandler;
	public  PCGridViewAdapter adapter;

	/**
	 * init the GridView in the firstView
	 */
	private View progress;
	private GridView gridView;
	private Button sendButton;
	private void initPagerOne() {
		// �ײ��ķ��Ͱ�ť
		sendButton =  (Button) findViewById(R.id.computer_bottom_send);
		sendButton.setOnClickListener(this);
		progress = findViewById(R.id.computer_file_progress);
		// adapterOfGridView.setFiles(mFiles);
		initBasicFiles();
		adapter = new PCGridViewAdapter(App.context);
		gridView = (GridView) findViewById(R.id.computer_gridview);
		gridView.setAdapter(adapter);
		gridView.setOnItemLongClickListener(this);
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
						Message msg = new Message();
						try {
							String result = connect.getMsg();
							if (result.equals("OK")) {
								intent = new Intent(FileListActivity.this,
										FileOperationActivity.class);
								
								msg.obj = intent;
								msg.what = 2;
								mHandler.sendMessage(msg);
							} else if (result.equals("ERROR")) {
								msg.obj = "�����쳣";
								msg.what = 3;
								mHandler.sendMessage(msg);
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
	 * ��ʼ��������6���ļ�����
	 */
	private void initBasicFiles() {
		file = new FileBean();
		file.setName("����");
		file.setId(R.drawable.music_red);
		mFileBeans.add(file);
		file = new FileBean();
		file.setName("ͼƬ");
		file.setId(R.drawable.pic_grey);
		mFileBeans.add(file);
		file = new FileBean();
		file.setName("��Ƶ");
		file.setId(R.drawable.audio_black);
		mFileBeans.add(file);
		file = new FileBean();
		file.setName("apk�ļ�");
		file.setId(R.drawable.app_blue);
		mFileBeans.add(file);
		file = new FileBean();
		file.setName("office�ļ�");
		file.setId(R.drawable.office_red);
		mFileBeans.add(file);
		file = new FileBean();
		file.setName("����");
		file.setId(R.drawable.file);
		mFileBeans.add(file);
	}

	@Override
	public void updata(int count) {
		// TODO
	}

	@Override
	public void onsendFilesToPC() {
//		new Thread(){
//			public void run() {
//				if (!hasConnectPC) {
//					connect.connectServer(address, Constant.PCPORT);
//				}
//				// ��������
//				connect.sendMsg(Constant.REQUEST_SEND_FEILS_TO_PC);
//				// ��������
//				int count = AdapterOfGridView_operationComputer.getCheckedFilesSize();
//				connect.sendMsg(count + "");
//				// �����ļ�
//				for(FileInfo info : AdapterOfGridView_operationComputer.getCheckedFiles()){
//					MessageUtil.sendFile(info.filePath, connect.dOps);
//				}
//				
//			};
//		}.start();
	
		
	}

	@Override
	public void onClick(View arg0) {
		// ��ת���ļ�ѡ��acitivity
		Intent intent = new Intent(FileListActivity.this,FileSelectedTransmitToPC.class);
		
	
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View convertView, int position,
			long id) {
		final AdapterView<?> mparent  = parent;
		final int mPostin = position;
		new AlertDialog.Builder(this).setItems(new String[]{"���ش��ļ�"} ,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				new Thread(){
					public void run() {
						FileDetail item = (FileDetail) mparent.getItemAtPosition(mPostin);
						// ��������
						connect.sendMsg(Constant.REQUEST_DOWNLOAD_FROM_PC);
						//  �����ļ�Ŀ¼
						connect.sendMsg(item.getPath());
						// ���ܷ�����ask
						Message msg = new Message();
						msg.what =3;
						try {
							String ask = connect.getMsg();
							if(Constant.ERROR.equals(ask)){
								
								msg.obj = "�Ҳ������ļ�";
								mHandler.sendMessage(msg);
								
							}else if(Constant.EXIST.equals(ask)){
								// �����ļ�
								MessageUtil.getFile(connect.dIps);
								msg.obj = "�ļ��������";
								mHandler.sendMessage(msg);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					};
				}.start();
				
			}
		}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialong, int id) {
				dialong.dismiss();
			}
		}).show();
		
		return false;
	}

}
