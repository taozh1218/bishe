package com.taozhang.filetransition.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.adapter.AdapterOfGridView_operationComputer;
import com.taozhang.filetransition.adapter.AdapterOfListViewFileManager;
import com.taozhang.filetransition.base.App;
import com.taozhang.filetransition.bean.FileInfo;
import com.taozhang.filetransition.helper.MySQLiteOpenHelper;
import com.taozhang.filetransition.ui.swipemenulistview.SwipeMenu;
import com.taozhang.filetransition.ui.swipemenulistview.SwipeMenuCreator;
import com.taozhang.filetransition.ui.swipemenulistview.SwipeMenuItem;
import com.taozhang.filetransition.ui.swipemenulistview.SwipeMenuListView;
import com.taozhang.filetransition.ui.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.taozhang.filetransition.util.Constant;
import com.taozhang.filetransition.util.SplitStringUtil;
import com.taozhang.filetransition.util.simplifymi.FileOperationHelper;
import com.taozhang.filetransition.util.simplifymi.FileOperationHelper.IOperationProgressListener;
import com.taozhang.filetransition.util.simplifymi.InformationDialog;
import com.taozhang.filetransition.util.simplifymi.IntentBuilder;
import com.taozhang.filetransition.util.simplifymi.TextInputDialog;
import com.taozhang.filetransition.util.simplifymi.TextInputDialog.OnFinishListener;
import com.taozhang.filetransition.util.simplifymi.Util;

/**
 * ��ѯ��Ӧ���͵��ļ��б�
 * 
 * @author taozhang
 * 
 */
public class TypeListActivity extends Activity implements
		IOperationProgressListener {
	private static final String LOG_TAG = "TypeListActivity";
	private AdapterOfListViewFileManager adapter;
	private AlertDialog dialog;
	int position_creator;
	private SwipeMenu menu;
	int index;
	/**
	 * ��������ͣ����λ�ã�
	 */
	private String positionS;
	/**
	 * ѡ�е�λ�ã�itemLongClick��position)
	 */
	private int mListViewSelectedItem;
	private FileOperationHelper mFileOperationHelper;
	private FileInfo mListViewSelectedFileInfo;
	private LinearLayout ll_parent;
	private String mCurrentPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_list);

		mFileOperationHelper = new FileOperationHelper(this);
		positionS = getIntent().getStringExtra("position");
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.music_list, menu);
		return true;
	}

	public String getCurrentPath() {
		return mCurrentPath;
	}

	public void setCurrentPath(String path) {
		mCurrentPath = path;
	}

	private void init() {
		ImageView img_back = (ImageView) findViewById(R.id.img_typeListAct_back);
		img_back.setOnClickListener(onClickListener);
		SwipeMenuListView lv = (SwipeMenuListView) findViewById(R.id.lv_MusicListAct_files);
		// Collection<File> listFiles =
		// FileUtils.listFiles(Environment.getRootDirectory(), new
		// String[]{".txt"}, true);
		// set Adapter
		adapter = new AdapterOfListViewFileManager(TypeListActivity.this);
		// ArrayList<FileDetail> searchFileByType =
		// DBUtil.searchFileByType("docx");
		// adapter.setList(searchFileByType);
		// TODO ��ʼ������
		// List<FileDetail> list = DBUtil.queryAll(getApplicationContext());
		int parseInt = Integer.parseInt(positionS);
		List<FileInfo> filesByPath = new ArrayList<FileInfo>();
		switch (parseInt) {
		case 0:
			mCurrentPath = Constant.BASEPATH_MUSIC;
			filesByPath = new ArrayList<FileInfo>();
			break;
		case 1:
			mCurrentPath = Constant.BASEPATH_PIC;
			filesByPath = new ArrayList<FileInfo>();
			break;
		case 2:
			mCurrentPath = Constant.BASEPATH_VIDEO;
			filesByPath = new ArrayList<FileInfo>();
			break;
		case 3:
			mCurrentPath = Constant.BASEPATH_APK;
			filesByPath = new ArrayList<FileInfo>();
			break;
		case 4:
			mCurrentPath = Constant.BASEPATH_OFFICE;
			filesByPath = new ArrayList<FileInfo>();
			break;
		case 5:
			mCurrentPath = Constant.BASEPATH_ELSE;
			filesByPath = new ArrayList<FileInfo>();
			break;
		default:
			break;
		}
		filesByPath = getFilesByPath(mCurrentPath);
		adapter.setList(filesByPath);

		lv.setAdapter(adapter);
		lv.setMenuCreator(creator);
		lv.setOnMenuItemClickListener(onMenuItemClickListener);
		lv.setOnItemClickListener(mOnItemClickListener);
		lv.setOnItemLongClickListener(mItemLongClickListener);

		ll_parent = (LinearLayout) findViewById(R.id.ll_typeListAct_parent);
		if (App.isCopy) {
			ll_parent.setVisibility(View.VISIBLE);
		}

		LinearLayout ll_zhantie = (LinearLayout) findViewById(R.id.ll_typeListAct_zhantie);
		LinearLayout ll_cancel = (LinearLayout) findViewById(R.id.ll_typeListAct_cancel);
		ll_zhantie.setOnClickListener(onClickListener);
		ll_cancel.setOnClickListener(onClickListener);
	}

	/**
	 * ����·����ȡ�ļ��б�
	 * 
	 * @param path
	 * @return
	 */
	public List<FileInfo> getFilesByPath(String path) {
		List<FileInfo> list = new ArrayList<FileInfo>();
		File file;
		File[] listFiles;
		FileInfo fileInfo;
		file = new File(path);
		listFiles = file.listFiles();
		if (listFiles != null) {
			for (File file2 : listFiles) {
				fileInfo = new FileInfo();
				fileInfo.fileName = file2.getName();
				fileInfo.filePath = file2.getAbsolutePath();
				fileInfo.IsDir = file2.isDirectory();
				List<String> arrayByStringTokenizer = SplitStringUtil
						.getArrayByStringTokenizer(fileInfo.fileName, ".");
				fileInfo.fileType = arrayByStringTokenizer
						.get(arrayByStringTokenizer.size() - 1);
				fileInfo.fileSize = file2.length();
				fileInfo.ModifiedDate = file2.lastModified();
				fileInfo.canRead = file2.canRead();
				fileInfo.canWrite = file2.canWrite();
				fileInfo.isHidden = file2.isHidden();
				System.out.println(fileInfo.toString());
				list.add(fileInfo);
			}
		}
		return list;
	}

	// ����ɾ��ͼ��
	SwipeMenuCreator creator = new SwipeMenuCreator() {

		@Override
		public void create(SwipeMenu menu) {
			// create "open" item
			SwipeMenuItem item = new SwipeMenuItem(TypeListActivity.this);
			// item.setBackground(Color.RED);
			item.setBackground(new ColorDrawable(Color.rgb(0xff, 0x00, 0x00)));
			item.setWidth(150);
			item.setIcon(R.drawable.icon_delete_white);
			menu.addMenuItem(item);
		}
	};

	/**
	 * creator�ļ���������������ӵ�listview�ϵ�
	 */
	OnMenuItemClickListener onMenuItemClickListener = new OnMenuItemClickListener() {

		/**
		 * ��һ����item��position���ڶ�����creator��position
		 */
		@Override
		public void onMenuItemClick(int position, SwipeMenu menu, int index) {
			showDialog();
			mListViewSelectedFileInfo = adapter.getList().get(position);// ��ѡ�е�file��ֵ
			TypeListActivity.this.position_creator = position;
			TypeListActivity.this.menu = menu;
			TypeListActivity.this.index = index;
		}
	};

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			List<FileInfo> filelist = adapter.getList();
			FileInfo fileDetail = filelist.get(position);
			if (fileDetail != null) {
				// ===TODO=============ע�⣬�����path����Ϊ����/�������ݿ�ʧ��==========
				String path = fileDetail.filePath;
				// �������������������������������¼��ַ�ʽ������=========
				// Intent openFile = OpenFileUtil.openFile(path);
				// startActivity(openFile);

				// OpenDocumentUtil.openFile(new File(path),
				// getApplicationContext());

				File file = new File(path);
				openFile(file);

			}
		}
	};

	OnItemLongClickListener mItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View convertView,
				int position, long id) {
			// ���õ�ǰѡ�е��ļ����Լ�λ��
			mListViewSelectedItem = position;
			mListViewSelectedFileInfo = (FileInfo) adapter.getItem(position);
			App.fileInfo = mListViewSelectedFileInfo;

			Log.i("FileManager longClick", "onItemLongClick");

			// ����dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(
					TypeListActivity.this);
			// ��ȡ�Զ���Ĳ���
			View alertDialogView = View.inflate(TypeListActivity.this,
					R.layout.dialog_filemanager, null);

			dialog = builder.create();
			dialog.setView(alertDialogView, 0, 0, 0, 0);

			// find component
			TextView tv_copy = (TextView) alertDialogView
					.findViewById(R.id.tv_fileMngdialog_copy);
			TextView tv_move = (TextView) alertDialogView
					.findViewById(R.id.tv_fileMngdialog_move);
			TextView tv_zhantie = (TextView) alertDialogView
					.findViewById(R.id.tv_fileMngdialog_zhantie);
			TextView tv_reaname = (TextView) alertDialogView
					.findViewById(R.id.tv_fileMngdialog_rename);
			TextView tv_copyPath = (TextView) alertDialogView
					.findViewById(R.id.tv_fileMngdialog_copyPath);
			TextView tv_send = (TextView) alertDialogView
					.findViewById(R.id.tv_fileMngdialog_send);
			TextView tv_sendByInternet = (TextView) alertDialogView
					.findViewById(R.id.tv_fileMngdialog_sendByInternet);
			TextView tv_detail = (TextView) alertDialogView
					.findViewById(R.id.tv_fileMngdialog_detail);
			tv_copy.setOnClickListener(onClickListener);
			tv_move.setOnClickListener(onClickListener);
			tv_zhantie.setOnClickListener(onClickListener);
			tv_reaname.setOnClickListener(onClickListener);
			tv_copyPath.setOnClickListener(onClickListener);
			tv_send.setOnClickListener(onClickListener);
			tv_sendByInternet.setOnClickListener(onClickListener);
			tv_detail.setOnClickListener(onClickListener);
			// ��ʾdialog
			dialog.show();

			return true;
		}
	};

	// ���ļ��Ĳ���
	android.view.View.OnClickListener onClickListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.img_typeListAct_back:
				TypeListActivity.this.finish();
				break;
			case R.id.tv_fileMngdialog_copy:
				dialog.dismiss();
				App.isCopy = true;
				ll_parent.setVisibility(View.VISIBLE);
				onOperationCopy();
				Toast.makeText(getApplicationContext(), "���Ƴɹ�,��ȥճ����",
						Toast.LENGTH_SHORT).show();
				break;
			case R.id.tv_fileMngdialog_move:
				dialog.dismiss();
				App.fileInfo = mListViewSelectedFileInfo;
				App.cutPosition = mListViewSelectedItem;
				App.isCopy = true;
				App.isMove = true;
				onOperationMove();
				Toast.makeText(getApplicationContext(), "���гɹ�,��ȥճ����",
						Toast.LENGTH_SHORT).show();
				ll_parent.setVisibility(View.VISIBLE);
				break;
			case R.id.tv_fileMngdialog_rename:
				dialog.dismiss();
				onOperationRename();
				break;
			case R.id.tv_fileMngdialog_copyPath:
				dialog.dismiss();
				Util.copyPath(mListViewSelectedFileInfo.filePath,
						getApplicationContext());
				Toast.makeText(getApplicationContext(), "·�����Ƴɹ���",
						Toast.LENGTH_SHORT).show();
				break;
			case R.id.tv_fileMngdialog_send:
				dialog.dismiss();
				onOperationSend();
				break;
			case R.id.tv_fileMngdialog_sendByInternet:
				dialog.dismiss();
				onOperationSendByInternet();
				break;
			case R.id.tv_fileMngdialog_detail:
				dialog.dismiss();
				onOperationInfo();
				break;
			case R.id.ll_typeListAct_cancel:
				App.isCopy = false;
				ll_parent.setVisibility(View.GONE);
				mFileOperationHelper.clear();
				break;
			case R.id.ll_typeListAct_zhantie:
				App.isCopy = false;
				ll_parent.setVisibility(View.GONE);
				if (mFileOperationHelper.canPaste()) {
					onOperationPaste();
				} else {
					Toast.makeText(getApplicationContext(), "ճ��ʧ�ܣ������Ƶ��ļ�Ϊ��~",
							Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}

	};

	private void onOperationSendByInternet() {
		if (mListViewSelectedFileInfo.IsDir) {// �ļ���
			AlertDialog dialog = new AlertDialog.Builder(TypeListActivity.this)
					.setMessage("sorry,�޷������ļ���").setPositiveButton("ȷ��", null)
					.create();
			dialog.show();
			return;
		}
		// ͨ��������APP����
		Intent buildSendFile = IntentBuilder
				.buildSendFile(mListViewSelectedFileInfo);
		if (buildSendFile != null) {
			try {
				startActivity(buildSendFile);
			} catch (ActivityNotFoundException e) {
				Log.e(LOG_TAG, "fail to view file: " + e.toString());
			}
		}
	}

	/**
	 * �����ļ����ļ����޷����ͣ�
	 */
	private void onOperationSend() {
		if (mListViewSelectedFileInfo.IsDir) {// �ļ���
			AlertDialog dialog = new AlertDialog.Builder(TypeListActivity.this)
					.setMessage("sorry,�޷������ļ���").setPositiveButton("ȷ��", null)
					.create();
			dialog.show();
			return;
		}

		// Intent intent = new
		// Intent(TypeListActivity.this,BluetoothActivity.class);
		// intent.putExtra(Constant.FILEPATH,
		// mListViewSelectedFileInfo.filePath);
		// startActivity(intent);
		// TODO ����
		// ���������
		AdapterOfGridView_operationComputer.fileInfos.clear();
		AdapterOfGridView_operationComputer.fileInfos = new ArrayList<FileInfo>();
		AdapterOfGridView_operationComputer.fileInfos
				.add(mListViewSelectedFileInfo);

		Intent intent = new Intent(TypeListActivity.this, SendVoiceActivity.class);
		startActivity(intent);

	}

	public void onOperationInfo() {
		if (mListViewSelectedFileInfo == null)
			return;

		InformationDialog dialog = new InformationDialog(TypeListActivity.this,
				mListViewSelectedFileInfo);
		dialog.show();
	}

	/**
	 * ��ճ������ɾ��,ɾ���ڻص�����
	 */
	private void onOperationMove() {
		App.cutPath = mCurrentPath;
		mFileOperationHelper.Copy(mListViewSelectedFileInfo);
		// deleteFile(mListViewSelectedItem);
	}

	private void onOperationRename() {
		int pos = mListViewSelectedItem;
		if (pos == -1)
			return;

		if (mListViewSelectedFileInfo == null)
			return;
		// mListViewSelectedFileInfo

		TextInputDialog dialog = new TextInputDialog(TypeListActivity.this,
				"������", "������������", mListViewSelectedFileInfo.fileName,
				new OnFinishListener() {
					@Override
					public boolean onFinish(String text) {
						return doRename(mListViewSelectedFileInfo, text);
					}

				});

		dialog.show();
	}

	private boolean doRename(final FileInfo f, String text) {
		if (TextUtils.isEmpty(text))
			return false;

		if (mFileOperationHelper.Rename(f, text)) {
			f.fileName = text;
			// mFileViewListener.onDataChanged();
			List<FileInfo> filesByPath = getFilesByPath(mCurrentPath);
			adapter.setList(filesByPath);
			adapter.notifyDataSetChanged();
		} else {
			new AlertDialog.Builder(TypeListActivity.this).setMessage("������ʧ��")
					.setPositiveButton("ȷ��", null).create().show();
			return false;
		}

		return true;
	}

	private ProgressDialog progressDialog;

	private void onOperationPaste() {
		if (App.isMove) {// �ж��ǲ��Ǽ���
			if (App.cutPath.equals(mCurrentPath)) {
				Toast.makeText(getApplicationContext(), "����ʧ�ܣ�����·����ԭ·��һ�£�",
						Toast.LENGTH_LONG).show();
				return;
			}
		}
		if (mFileOperationHelper.Paste(mCurrentPath)) {
			showProgress("���ڸ�����...");
		}
	}

	private void showProgress(String msg) {
		progressDialog = new ProgressDialog(TypeListActivity.this);
		// dialog.setIcon(R.drawable.icon);
		progressDialog.setMessage(msg);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	/**
	 * �����ļ�������mFileOperator��list
	 */
	private void onOperationCopy() {
		mFileOperationHelper.Copy(mListViewSelectedFileInfo);
	}

	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				TypeListActivity.this);
		builder.setTitle("ע��");
		builder.setMessage("��ͬʱɾ�������ļ���ȷ��Ҫɾ����");
		builder.setCancelable(true);
		builder.setNegativeButton("ȡ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("ȷ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				deleteFile(position_creator, menu, index);
			}
		});
		builder.create().show();
	}

	/**
	 * ɾ�������ļ�
	 * 
	 * @param position
	 * @param menu
	 * @param index
	 */
	public void deleteFile(int position, SwipeMenu menu, int index) {
		// ɾ����Ӧ��item
		List<FileInfo> list = adapter.getList();
		list.remove(position);
		adapter.setList(list);
		adapter.notifyDataSetChanged();

		// ɾ�������ļ�
		onOperationDelete();
	}

	/**
	 * ɾ�������ļ�
	 * 
	 * @param position
	 * @param menu
	 * @param index
	 */
	public void deleteFile(FileInfo fileInfo) {
		// ɾ�������ļ�
		doOperationDelete(fileInfo);
	}

	public void onOperationDelete() {
		doOperationDelete(mListViewSelectedFileInfo);
	}

	/**
	 * ɾ��
	 * 
	 * @param selectedFile
	 */
	private void doOperationDelete(final FileInfo selectedFile) {
		if (mFileOperationHelper.Delete(selectedFile)) {
			showProgress("����ɾ��...");
		}
	}

	// ���ļ�
	private void openFile(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = getMIMEType(file);
		intent.setDataAndType(Uri.fromFile(file), type);
		startActivity(intent);
	}

	// ��ȡ�ļ�mimetype
	private String getMIMEType(File file) {
		String type = "";
		String name = file.getName();
		// �ļ���չ��
		String end = name.substring(name.lastIndexOf(".") + 1, name.length())
				.toLowerCase();
		if (end.equals("m4a") || end.equals("mp3") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("mp4") || end.equals("3gp")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("png") || end.equals("jpeg")
				|| end.equals("bmp") || end.equals("gif")) {
			type = "image";
		} else {
			// ����޷�ֱ�Ӵ򿪣������б����û�ѡ��
			type = "*";
		}
		type += "/*";
		return type;
	}

	@Override
	public void onFinish() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	@Override
	public void onFileChanged(String path) {
		if (mCurrentPath != null) {
			List<FileInfo> filesByPath = new ArrayList<FileInfo>();
			filesByPath = getFilesByPath(mCurrentPath);
			adapter.setList(filesByPath);
			handler.sendEmptyMessage(1);
		}
		if (App.isMove) {// ����Ǽ���
			App.isMove = false;
			handler.sendEmptyMessage(2);
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			if (progressDialog != null) {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
			switch (what) {
			case 1:// notify
				adapter.notifyDataSetChanged();
				break;
			case 2:// ɾ���ļ�
				deleteFile(App.fileInfo);// ��Ϊ֮ǰ��Activity�Ѿ����٣�����ֻ��ɾ����������notifyadapter
				App.fileInfo = null;
			default:
				break;
			}
		};
	};
}
