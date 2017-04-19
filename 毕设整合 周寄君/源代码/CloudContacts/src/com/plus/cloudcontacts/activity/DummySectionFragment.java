package com.plus.cloudcontacts.activity;

//import com.google.gson.Gson;
//import com.plus.cloudcontacts.R;
//import com.plus.cloudcontacts.bean.Contact;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import com.plus.cloudcontacts.activity.CardActivity.GetImageThread;
import com.plus.cloudcontacts.bean.Contact;
import com.plus.cloudcontacts.http.PostWork;
import com.plus.cloudcontacts.util.DBManager;
import com.plus.cloudcontacts.util.MyApplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DummySectionFragment extends Fragment implements OnClickListener {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";

	private View rootView;

	private ImageView cardAvatar;
	private TextView cardFrom;
	private EditText cardName;
	private EditText cardTel;
	private Button cardCancel;
	private Button cardSave;

	private Contact bean;

	private DBManager dbm;
	private boolean working = false;
	private MyApplication app;

	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	
	private static final int SERVER_ERROR = 0;
	private static final int MODIFY_SUCCESS = 1;
	private static final int IMAGE_UPLOAD_FAILED = 3;
	private static final int IMAGE_UPLOAD_SUCCESS = 4;
	private static final int IMAGE_LOAD_SUCCESS = 5;
	// 创建一个以当前时间为名称的文件
	File tempFile = new File(Environment.getExternalStorageDirectory(),
			getPhotoFileName());

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == SERVER_ERROR) {
				cardName.setText(bean.getName());
				cardTel.setText(bean.getTel());
				Toast.makeText(getActivity(), "修改个人信息失败", Toast.LENGTH_SHORT)
						.show();
				working = false;
			} else if (msg.what == MODIFY_SUCCESS) {
				bean.setName(cardName.getText().toString());
				bean.setTel(cardTel.getText().toString());
				dbm.saveContact(bean, null);
				Toast.makeText(getActivity(), "修改个人信息成功", Toast.LENGTH_SHORT)
						.show();
				working = false;
			}  else if (msg.what == IMAGE_UPLOAD_FAILED) {
				Toast.makeText(getActivity(), "上传头像失败", Toast.LENGTH_SHORT)
						.show();
			} else if (msg.what == IMAGE_UPLOAD_SUCCESS) {
				bean.setAvatar((String) msg.obj);
				Toast.makeText(getActivity(), "上传头像成功", Toast.LENGTH_SHORT)
						.show();
			} else if (msg.what == IMAGE_LOAD_SUCCESS){
				cardAvatar.setImageURI(Uri.fromFile(new File(app.getCache(),bean.getAvatar())));
			}
			else {
				//其他消息
				Toast.makeText(getActivity(), "修改个人信息失败", Toast.LENGTH_SHORT)
						.show();
				working = false;
			}
		}
	};

	public DummySectionFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_main_dummy, container,
				false);
		dbm = new DBManager(rootView.getContext());
		app = (MyApplication) getActivity().getApplication();
		// TextView dummyTextView = (TextView)
		// rootView.findViewById(R.id.section_label);
		// dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
		cardAvatar = (ImageView) rootView.findViewById(R.id.cardAvatar);
		cardFrom = (TextView) rootView.findViewById(R.id.cardFrom);
		cardName = (EditText) rootView.findViewById(R.id.cardName);
		cardTel = (EditText) rootView.findViewById(R.id.cardTel);
		cardCancel = (Button) rootView.findViewById(R.id.cardCancel);
		cardSave = (Button) rootView.findViewById(R.id.cardSave);
		String json = this.getArguments().getString("json");
		// String fromGroup = this.getArguments().getString("fromGroup");
		Gson gson = new Gson();
		bean = gson.fromJson(json, Contact.class);

		// cardFrom.setText(fromGroup);
		cardName.setText(bean.getName());
		cardTel.setText(bean.getTel());

		cardSave.setOnClickListener(this);
		cardCancel.setOnClickListener(this);
		cardAvatar.setOnClickListener(this);
		
		if(bean.getAvatar()==null){
			bean.setAvatar("default.jpg");
		}
		File avatarFile = new File(app.getCache(),bean.getAvatar());
		if (avatarFile.exists()){
			cardAvatar.setImageURI(Uri.fromFile(avatarFile));
		}else{
			app.getThreadPool().submit(new GetImageThread());
		}

		return rootView;
	}
	
	class GetImageThread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(PostWork.getImageURI(bean.getAvatar(), app.getCache())!=null){
				Message msg = new Message();
				msg.what = IMAGE_LOAD_SUCCESS;
				mHandler.sendMessage(msg);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cardCancel:
			dbm.newSession("");
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
			getActivity().finish();
			break;
		case R.id.cardSave:
			if (!working) {
				working = true;
				app.getThreadPool().submit(new SaveCardThread());
			} 
			break;
		case R.id.cardAvatar:
			showDialog();
			break;
		}
	}

	class SaveCardThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Map params = new HashMap();
			String session = URLEncoder.encode(dbm.getSession());
			String name = URLEncoder.encode(cardName.getText()
					.toString());
			params.put("sessionid", session);
			params.put("name", name);
			params.put("tel", cardTel.getText().toString());
			PostWork pw = new PostWork(PostWork.URL_SAVE_CONTACT,
					params, mHandler);
			if (!pw.work()) {
				Message msg = new Message();
				msg.what = -1;
				mHandler.handleMessage(msg);
			}
		}
		
	}
	
	// 提示对话框方法
	private void showDialog() {
		new AlertDialog.Builder(this.getActivity())
				.setTitle("头像设置")
				.setPositiveButton("拍照", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						// 调用系统的拍照功能
						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						// 指定调用相机拍照后照片的储存路径
						intent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(tempFile));
						startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
					}
				})
				.setNegativeButton("相册", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_PICK, null);
						intent.setDataAndType(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								"image/*");
						startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
					}
				}).show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:
			startPhotoZoom(Uri.fromFile(tempFile), 150);
			break;

		case PHOTO_REQUEST_GALLERY:
			if (data != null)
				startPhotoZoom(data.getData(), 150);
			break;

		case PHOTO_REQUEST_CUT:
			if (data != null){
				setPicToView(data);
				uploadAvatar(data);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void uploadAvatar(Intent picdata) {
		// TODO Auto-generated method stub
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			Bitmap bmp = bundle.getParcelable("data");
			File f = new File(Environment.getExternalStorageDirectory(),
					"tmp.jpg");
			if (f.exists()) {
				f.delete();
			}
			try {
				FileOutputStream out = new FileOutputStream(f);
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
				out.flush();
				out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final List list = new ArrayList();
			list.add(f);
			app.getThreadPool().submit(new UploadThread(list));
		}
	}
	class UploadThread implements Runnable{
		private List list;
		UploadThread(List list){
			this.list = list;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Map params = new HashMap();
			String session = app.getSessionId();
			params.put("sessionid", URLEncoder.encode(session));
			PostWork pw = new PostWork(PostWork.URL_AVATAR_UPLOAD,
					params, mHandler);
			pw.upload(list);
		}
		
	}
	
	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);

		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	// 将进行剪裁后的图片显示到UI界面上
	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			Bitmap photo = bundle.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			cardAvatar.setImageDrawable(drawable);
			// cardAvatar.setBackgroundDrawable(drawable);
		}
	}

	// 使用系统当前日期加以调整作为照片的名称
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

}