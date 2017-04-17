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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
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
import com.taozhang.filetransition.ui.MusicListActivity;
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
	private List<FileDetail> musicList;
	private List<FileDetail> picList;
	private List<FileDetail> videoList;
	private List<FileDetail> apkList;
	private List<FileDetail> officeList;
	private List<FileDetail> elseList;

	private MySQLiteOpenHelper dbHelper;
	private SQLiteDatabase writableDatabase;
	private ContentValues contentValues;

	private static HistoryFragment computerFragment_instance = null;

	private HistoryFragment(Context context) {
		this.context = context;
		dbHelper =  MySQLiteOpenHelper.newInstance(context);
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
		// TODO
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
				// Toast.makeText(getActivity(),
				// mFileBeans.get(position).getName(), Toast.LENGTH_SHORT)
				// .show();

				// FragmentManager fragmentManager = getActivity()
				// .getFragmentManager();
				// FragmentTransaction fragmentTransaction = fragmentManager
				// .beginTransaction();
				// FileListFrag instance =
				// FileListFrag.getInstance(getActivity());
				// // TODO 测试完删除
				// FileDetail bean = new FileDetail();
				// List<String> arrayBySplit =
				// SplitStringUtil.getArrayByStringTokenizer(
				// "/storage/sdcard0/1.OGNL.mp4", ".");
				// List<String> arrayBySplit1 =
				// SplitStringUtil.getArrayByStringTokenizer(
				// "/storage/sdcard0/1.OGNL.mp4", "/");
				// current_frag = instance;
				// fragmentTransaction.add(R.id.frameLayout_MainAct,
				// current_frag);
				// fragmentTransaction.addToBackStack(null);
				// fragmentTransaction.commit();

				test();

				Intent intent = new Intent(getActivity(),
						MusicListActivity.class);
				getActivity().startActivity(intent);

			}

		});

	}

	// TODO 测试完成删除
	private void test() {
		// DBUtil.insert("1.OGNL.mp4", "mp4");
		// DBUtil.insert("360sicheck.txt", "txt");
		// DBUtil.insert("信息检索实验报告20134350215张涛.docx", "docx");
		// DBUtil.insert("StrangerToFriend.apk", "apk");
		writableDatabase = dbHelper.getWritableDatabase();
		contentValues = new ContentValues();
		contentValues.put("name", "1.OGNL.mp4");
		contentValues.put("type", "mp4");
		writableDatabase.insert(Constant.TABLENAME, null, contentValues);

		contentValues = new ContentValues();
		contentValues.put("name", "360sicheck.txt");
		contentValues.put("type", "txt");
		writableDatabase.insert(Constant.TABLENAME, null, contentValues);
		writableDatabase.close();
	}

	/** 调用文件选择软件来选择文件 **/
	private void showFileChooser() {
		intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			// Attention startActivity 一定要Activity调用，否则无效
			startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
					Constant.FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(getActivity(), "请安装文件管理器", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * 初始化基本的6中文件类型
	 */
	private void initBasicFiles() {
		mFileBeans = new ArrayList<FileBean>();
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
		file.setId(R.drawable.file_blue_32);
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
			String path = uri.getPath();// 获取路径
			System.out.println("path:" + path);
			String[] split = path.split(".");
			if (split != null) {
				System.out.println(split.length);// 发现length是0，所以split失败
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
