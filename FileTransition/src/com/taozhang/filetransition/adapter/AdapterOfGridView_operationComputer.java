package com.taozhang.filetransition.adapter;

import java.util.List;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.adapter.AdapterOfGridView_operationComputer.ViewHolder;
import com.taozhang.filetransition.bean.FileDetail;
import com.taozhang.filetransition.bean.FileDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AdapterOfGridView_operationComputer extends BaseAdapter {

	 private List<FileDetail> files;
	    private Context mContext;
	    private FileDetail file;
	    /**
	     * Get view from xml layout.
	     */
	    private LayoutInflater mInflater = null;


	    public AdapterOfGridView_operationComputer(Context context) {
	        mContext = context;
	        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }


	    public AdapterOfGridView_operationComputer(List<FileDetail> files, Context context) {
	        this.files = files;
	        mContext = context;
	        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }


	    public List<FileDetail> getFiles() {
	        return files;
	    }

	    public void setFiles(List<FileDetail> files) {
	        this.files = files;
	    }

	    public FileDetail getFile() {
	        return file;
	    }

	    public void setFile(FileDetail file) {
	        this.file = file;
	    }

	    public Context getContext() {
	        return mContext;
	    }

	    public void setContext(Context context) {
	        mContext = context;
	    }


	    @Override
	    public int getCount() {
	        return files == null ? 0 : files.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        return files == null ? null : files.get(position);
	    }

	    @Override
	    public long getItemId(int position) {
	        return position;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        final ViewHolder holder;
	        if (convertView == null) {
	            holder = new ViewHolder();
	            //映射出convertView
	            convertView = mInflater.inflate(R.layout.gridviewitem_filelist, parent, false);
	            holder.img = (ImageView) convertView.findViewById(R.id.img_gridViewItem_fileList);
	            holder.tv = (TextView) convertView.findViewById(R.id.tv_gridViewItem_fileList);
	            //            holder.tv_filePath = (TextView) convertView.findViewById(R.id.tv_gridViewItem_fileList_filePath);
	            //            holder.tv_fileType = (TextView) convertView.findViewById(R.id.tv_gridViewItem_fileList_fileType);
	                convertView.setTag(holder);
	        } else {
	            holder = (ViewHolder) convertView.getTag();
	        }
	        FileDetail file = (FileDetail) getItem(position);
	        if (file == null) {//判断获取到的是否为空
	            Toast.makeText(mContext, "该路径下无文件", Toast.LENGTH_SHORT).show();
	            return convertView;
	        }
	        holder.img.setScaleType(ImageView.ScaleType.FIT_CENTER);
//	        holder.img.setImageResource(file.getId());
	        //TODO:
	        
	        holder.tv.setText(file.getName());

	        return convertView;
	    }

	    public static class ViewHolder {
	        public ImageView img;
	        public TextView tv;
	        //        public TextView tv_filePath;
	        //        public TextView tv_fileType;
	    }
}
