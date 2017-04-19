package com.taozhang.filetransition.ui.fragment;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.adapter.AdapterOfGridView;
import com.taozhang.filetransition.bean.FileBean;
import com.taozhang.filetransition.bean.FileDetail;
import com.taozhang.filetransition.helper.MySQLiteOpenHelper;
import com.taozhang.filetransition.ui.FileListActivity;
import com.taozhang.filetransition.ui.FileManagerActivity;
import com.taozhang.filetransition.ui.TypeListActivity;
import com.taozhang.filetransition.util.Constant;
import com.taozhang.filetransition.util.DBUtil;
import com.taozhang.filetransition.util.FileUtil;
import com.taozhang.filetransition.util.SplitStringUtil;

/**
 * Created by taozhang on 2016/5/10. Company:Geowind,University of South China.
 * ContactQQ:962076337
 * 
 * @updateAuthor taozhang
 * @updateDate 2016/5/10
 */
@SuppressLint({ "NewApi", "ValidFragment" })
public class HistoryFragment extends Fragment {

	private Context context;
	private View history;
	@SuppressLint("NewApi")
	private Fragment current_frag;
	private Intent intent;
	private TextView tv;
	private GridView gridview;
	private FileBean file;
	private List<FileBean> mFileBeans;

	private MySQLiteOpenHelper dbHelper;
	private SQLiteDatabase writableDatabase;
	private ContentValues contentValues;


	public static HistoryFragment computerFragment_instance = null;

	public HistoryFragment() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HistoryFragment(Context context) {
		this.context = context;
		dbHelper = MySQLiteOpenHelper.newInstance(context);
	}

	@SuppressLint("ValidFragment")
	public static HistoryFragment getInstance(Context context) {
		if (computerFragment_instance == null) {
			synchronized (HistoryFragment.class) {
				if (computerFragment_instance == null) {
					computerFragment_instance = new HistoryFragment(context);
				}
			}
		}
		return computerFragment_instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// inflate the view of fragment
		history = inflater.inflate(R.layout.frag_history, null);
		getComponent();
		return history;
	}

	private void getComponent() {
		// tv = (TextView) history.findViewById(R.id.tv_historyFrag);
		// Button btn = (Button) history
		// .findViewById(R.id.btn_historyFrag_fileManager);
		// btn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		// // intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
		// // startActivityForResult(intent, 1);
		// showFileChooser();
		// }
		//
		// });
		TextView tv_tishi = (TextView) history
				.findViewById(R.id.tv_historyFrag_tishi);
		if (Environment.getExternalStorageDirectory().getAbsoluteFile() != null) {
			tv_tishi.setText("��ܰ��ʾ�������н��յ����ļ����洢��"
					+ Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/fileTransition" + "�ļ�����");
		} else {
			tv_tishi.setText("�����н��յ����ļ����洢��" + "/fileTransition" + "�ļ�����");
		}
		gridview = (GridView) history
				.findViewById(R.id.gridView_viewPager_historyFrag);
		initBasicFiles();
		AdapterOfGridView adapterOfGridView = new AdapterOfGridView(mFileBeans,
				getActivity());
		gridview.setAdapter(adapterOfGridView);
		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO ��ת����Ӧ���͵��ļ��б�
				if (Environment.getExternalStorageDirectory().getAbsolutePath() != null) {
					Intent intent = new Intent(getActivity(),
							TypeListActivity.class);
					intent.putExtra("position", position + "");
					getActivity().startActivity(intent);
				}
			}

		});

		Button tiaozhuan = (Button) history
				.findViewById(R.id.btn_btn_historyFrag_transition);
		tiaozhuan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO ��ָ��·�����ļ�������
				// Intent intent = new Intent();
				// ComponentName comp = new ComponentName(
				// "com.mediatek.filemanager",
				// "com.mediatek.filemanager.FileManagerOperationActivity");
				// intent.setComponent(comp);
				// intent.setAction("android.intent.action.VIEW");

				// PackageManager packageManager = context.getPackageManager();
				// intent =
				// packageManager.getLaunchIntentForPackage("com.android.fileexplorer");
				//
				// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// startActivity(intent);

				Intent intent = new Intent(getActivity(),
						FileManagerActivity.class);
				startActivity(intent);

			}
		});
	}

	public void openDirectoryByPath(String path) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File(path);
		// intent.setDataAndNormalize(Uri.fromFile(file));
		// intent.setDataAndType(Uri.fromFile(file),"video/*");
		startActivity(intent);
	}

	/** �����ļ�ѡ�������ѡ���ļ� **/
	private void showFileChooser() {
		intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			// Attention startActivity һ��ҪActivity���ã�������Ч
			startActivityForResult(Intent.createChooser(intent, "��ѡ��һ��Ҫ�ϴ����ļ�"),
					Constant.FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(getActivity(), "�밲װ�ļ�������", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * ��ʼ��������6���ļ�����
	 */
	private void initBasicFiles() {
		mFileBeans = new ArrayList<FileBean>();
		file = new FileBean();
		file.setName("����");
		file.setId(R.drawable.music_red_64);
		mFileBeans.add(file);
		file = new FileBean();
		file.setName("ͼƬ");
		file.setId(R.drawable.pic_blue_64);
		mFileBeans.add(file);
		file = new FileBean();
		file.setName("��Ƶ");
//		file.setId(R.drawable.audio_red);
		file.setId(R.drawable.video_red_64);
		mFileBeans.add(file);
		file = new FileBean();
		file.setName("apk�ļ�");
		file.setId(R.drawable.apk_blue_64);
		mFileBeans.add(file);
		file = new FileBean();
		file.setName("office�ļ�");
		file.setId(R.drawable.office_red_64);
		mFileBeans.add(file);
		file = new FileBean();
		file.setName("����");
		file.setId(R.drawable.file_blue_64);
		mFileBeans.add(file);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			// Get the Uri of the selected file
			Uri uri = data.getData();
			if (uri == null) {
				return;
			}
			String path = uri.getPath();// ��ȡ·��
			System.out.println("path:" + path);
			String[] split = path.split(".");
			if (split != null) {
				System.out.println(split.length);// ����length��0������splitʧ��
				System.out.println(split[0]);
				for (String string : split) {
					System.out.println(string);
				}
			}

			// File file = new File(path);
			// String name = file.getName();
			// System.out.println("file name:"+name);

			String realFilePath = FileUtil.getRealFilePath(getActivity(), uri);
			System.out.println("realFilePath:" + realFilePath);

			String filePath = FileUtil.getFilePath(getActivity(), uri);
			System.out.println("filePath:" + filePath);

		}
	}
}
