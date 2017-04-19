package com.plus.cloudcontacts.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.plus.cloudcontacts.R;
import com.plus.cloudcontacts.bean.ListItem;
import com.plus.cloudcontacts.http.PostWork;
import com.plus.cloudcontacts.util.DirUtil;
import com.plus.cloudcontacts.util.MyApplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter {
	private List<ListItem> mData;
	private LayoutInflater mInflater;
	private ArrayList<Integer> TypeList = new ArrayList<Integer>();

	public void AddType(int mResource) {
		TypeList.add(mResource);
	}

	public ChatAdapter(Context context, List<ListItem> data) {
		mData = data;
		mInflater = LayoutInflater.from(context);
		// mInflater =
		// (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getItemViewType(int position) {
		return mData.get(position).mType;
	}

	@Override
	public int getViewTypeCount() {
		if (TypeList.size() == 0)
			return 1;
		else
			return TypeList.size();
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	public ListItem getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
		// return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		int type = getItemViewType(position);

		if (convertView == null) {
			holder = new ViewHolder();
			ListItem item = getItem(position);
			convertView = mInflater.inflate(TypeList.get(type), null);
			// convertView = mInflater.inflate(type,parent,false);
			for (Iterator<Integer> it = item.mMap.keySet().iterator(); it
					.hasNext();) { // 遍历
				int id = it.next();
				Object obj = convertView.findViewById(id);
				if (obj != null) {
					holder.List_Object.add(obj);
					holder.List_id.add(id);
				}
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.SetValue(mData.get(position));
		return convertView;
	}

	public static class ViewHolder {
		ArrayList<Object> List_Object = new ArrayList<Object>();
		ArrayList<Integer> List_id = new ArrayList<Integer>();
		MediaPlayer mPlayer = null;

		private final class AsyncImageTask extends AsyncTask<String, Integer, Uri> {

			private ImageView iv_header;

			public AsyncImageTask(ImageView iv_header) {
				this.iv_header = iv_header;
			}

			// 后台运行的子线程子线程
			@Override
			protected Uri doInBackground(String... params) {
				try {
					return PostWork.getImageURI(params[0],null);
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
		
		public boolean SetValue(ListItem item) {
			int i = 0;
			Object oV;
			for (Object obj : List_Object) {
				{
					int id = List_id.get(i);
					oV = item.mMap.get(id);

					if (obj.getClass().equals(TextView.class)) {
						if ("null".equals(oV)) {
							((TextView) obj).setVisibility(8);
						} else {
							((TextView) obj).setVisibility(0);
							((TextView) obj).setText(oV.toString());
						}
					}

					if (obj.getClass().equals(ImageView.class)) {
						ImageView iv = ((ImageView) obj);
//						System.out.println(oV);
						if ("null".equals(oV)) {
							((ImageView) obj).setVisibility(8);
						} else {
							((ImageView) obj).setVisibility(0);
							
//							Bitmap bmp = BitmapFactory
//									.decodeFile(oV.toString());
//							((ImageView) obj).setImageBitmap(bmp);
							AsyncImageTask task = new AsyncImageTask((ImageView) obj);
							task.execute(oV.toString());
//							(ImageView) obj).setImageBitmap((Bitmap) attentionList)
						}
						if (iv.getId()==R.id.chat_avatar){
							iv.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									
								}
							});
						}
					}

					if (obj.getClass().equals(Button.class)) {
						if ("null".equals(oV)) {
							((Button) obj).setVisibility(8);

						} else {
							((Button) obj).setVisibility(0);
							final String filename = oV.toString();
							((Button) obj)
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View arg0) {
											// TODO Auto-generated method stub
											if (mPlayer != null) {
												mPlayer.stop();
											}
											new Thread() {
												public void run() {
													PostWork.getImageURI(filename, null);
													try {
														mPlayer = new MediaPlayer();
														mPlayer.setDataSource(DirUtil.getFilePath(filename));
														mPlayer.prepare();
														mPlayer.start();
													} catch (IOException e) {
														Log.e("media",
																"播放失败");
													}
												};
											}.start();
										}
									});
						}
					}
				}

				i++;
			}
			return false;
		}
	}



}
