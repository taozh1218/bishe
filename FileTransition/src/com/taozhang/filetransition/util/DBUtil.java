package com.taozhang.filetransition.util;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.taozhang.filetransition.bean.FileDetail;
import com.taozhang.filetransition.helper.MySQLiteOpenHelper;

/**
 * ���ݿ�ģ��
 * 
 * ֻ�ܲ��벻��/���ļ����Լ��ļ�����
 * 
 * @author taozhang
 * 
 */
@SuppressLint("SdCardPath")
public class DBUtil {

	private static List<FileDetail> list;

	public static List<FileDetail> queryAll(Context context) {
		list = new ArrayList<FileDetail>();// ��ʼ��list
		FileDetail file;
		String name;
		String type;

		MySQLiteOpenHelper helper = MySQLiteOpenHelper.newInstance(context);
		SQLiteDatabase readDB = helper.getReadableDatabase();// ��ȡ�Ķ����ݿ����
		// ��ѯ���ֻ����name:new String []{"name"} ,null���ʾ��ѯ����
		// ��ѯ����: "name=\"У��\"" Ϊ�˷�ֹsqlע�빥��������������"name = ?"��Ȼ������һ������ new String
		// []{"С��"}
		Cursor cursor = readDB.query(Constant.TABLENAME, null, null, null,
				null, null, null);
		while (cursor.moveToNext()) {
			file = new FileDetail();
			name = cursor.getString(cursor.getColumnIndex("name"));
			file.setName(name);
			type = cursor.getString(cursor.getColumnIndex("type"));
			file.setType(type);
			System.out.println(String.format("name=%s,type=%s", name, type));
			list.add(file);
		}
		return list;
	}

	public static List<FileDetail> queryByType(Context context, String type) {
		list = new ArrayList<FileDetail>();// ��ʼ��list
		FileDetail file;
		String name;

		MySQLiteOpenHelper helper = MySQLiteOpenHelper.newInstance(context);
		SQLiteDatabase readDB = helper.getReadableDatabase();// ��ȡ�Ķ����ݿ����
		Cursor cursor = readDB.query(Constant.TABLENAME, null, "name = ?",
				new String[] { type }, null, null, null);
		while (cursor.moveToNext()) {
			file = new FileDetail();
			name = cursor.getString(cursor.getColumnIndex("name"));
			file.setName(name);
			type = cursor.getString(cursor.getColumnIndex("type"));
			file.setType(type);
			System.out.println(String.format("name=%s,type=%s", name, type));
			list.add(file);
		}
		return list;
	}
}
