package com.taozhang.filetransition.ui;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.adapter.AdapterOfFileDetail;
import com.taozhang.filetransition.bean.FileDetail;
import com.taozhang.filetransition.helper.MySQLiteOpenHelper;
import com.taozhang.filetransition.util.DBUtil;

public class MusicListActivity extends Activity {
	
	private List<FileDetail> musicList;
	MySQLiteOpenHelper helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_list);
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.music_list, menu);
		return true;
	}
	
	private void init() {
		ListView lv = (ListView) findViewById(R.id.lv_MusicListAct_files);
//		Collection<File> listFiles = FileUtils.listFiles(Environment.getRootDirectory(), new String[]{".txt"}, true);
		//set Adapter 
		AdapterOfFileDetail adapter = new AdapterOfFileDetail(getApplicationContext());
		
//		ArrayList<FileDetail> searchFileByType = DBUtil.searchFileByType("docx");
//		adapter.setList(searchFileByType);
		List<FileDetail> list = DBUtil.queryAll(getApplicationContext());
		adapter.setList(list);
		
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
			}
		});
		
	}	

}
