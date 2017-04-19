package com.taozhang.filetransition.util;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.taozhang.filetransition.bean.UserInfo;
import com.taozhang.filetransition.bean.UserInfo;
import com.taozhang.filetransition.helper.MySQLiteOpenHelper;

/**
 * ���ݿ��userInfo�����ɾ���
 * 
 * @author taozhang
 * 
 */
@SuppressLint("SdCardPath")
public class TbUserUtil {

	private static List<UserInfo> list;

	public static List<UserInfo> queryAll(Context context) {
		list = new ArrayList<UserInfo>();// ��ʼ��list
		UserInfo user;
		String account;
		String psw;
		String username;
		String sign;

		MySQLiteOpenHelper helper = MySQLiteOpenHelper.newInstance(context);
		SQLiteDatabase readDB = helper.getReadableDatabase();// ��ȡ�Ķ����ݿ����
		// ��ѯ���ֻ����name:new String []{"name"} ,null���ʾ��ѯ����
		// ��ѯ����: "name=\"У��\"" Ϊ�˷�ֹsqlע�빥��������������"name = ?"��Ȼ������һ������ new String
		// []{"С��"}
		Cursor cursor = readDB.query(Constant.TABLENAME, null, null, null,
				null, null, null);
		while (cursor.moveToNext()) {
			user = new UserInfo();
			account = cursor.getString(cursor.getColumnIndex("uid"));
			user.uid = account;
			psw = cursor.getString(cursor.getColumnIndex("psw"));
			user.psw = psw;
			username = cursor.getString(cursor.getColumnIndex("username"));
			user.username = username;
			sign = cursor.getString(cursor.getColumnIndex("sign"));
			user.sign = sign;
			System.out.println(String.format(
					"account=%s,psw=%s,username=%s,sign=%s", account, psw,
					username, sign));
			list.add(user);
		}
		return list;
	}

	/**
	 * �����û��˺ţ��ֻ��ţ���ѯ��Ϣ
	 * 
	 * @param context
	 * @param uid
	 * @return
	 */
	public static List<UserInfo> queryByAccount(Context context, String uid) {
		list = new ArrayList<UserInfo>();// ��ʼ��list
		UserInfo user;
		String psw;
		String username;
		String sign;

		MySQLiteOpenHelper helper = MySQLiteOpenHelper.newInstance(context);
		SQLiteDatabase readDB = helper.getReadableDatabase();// ��ȡ�Ķ����ݿ����
		Cursor cursor = readDB.query(Constant.TABLENAME, null, "uid = ?",
				new String[] { uid }, null, null, null);
		while (cursor.moveToNext()) {
			user = new UserInfo();
			user.uid = uid;
			psw = cursor.getString(cursor.getColumnIndex("psw"));
			user.psw = psw;
			username = cursor.getString(cursor.getColumnIndex("username"));
			user.username = username;
			sign = cursor.getString(cursor.getColumnIndex("sign"));
			user.sign = sign;

			System.out.println(String.format(
					"account=%s,psw=%s,username=%s,sign=%s", uid, psw,
					username, sign));
			list.add(user);
		}
		return list;
	}

	/**
	 * 
	 * @param context
	 * @param user
	 */
	public static long insertIntoTB(Context context, UserInfo user) {
		// ��ȡ���ݿ����
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.newInstance(context);
		SQLiteDatabase writableDatabase = helper.getWritableDatabase();
		// д������4
		ContentValues values = new ContentValues();
		values.put("uid", user.uid);
		values.put("psw", user.psw);
		values.put("username", user.username);
		values.put("sign", user.sign);
		long insert = writableDatabase.insert(Constant.TABLENAME, null, values);
		return insert;
	}

	/**
	 * ����ĳ���û���Ϣ
	 * 
	 * @param context
	 * @param user
	 * @return
	 */
	public static int updateTb(Context context, UserInfo user) {
		// ����һ��DatabaseHelper����
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.newInstance(context);
		SQLiteDatabase writableDatabase = helper.getWritableDatabase();
		// ����һ��ContentValues����
		ContentValues values = new ContentValues();
		values.put("uid", user.uid);
		values.put("psw", user.psw);
		values.put("username", user.username);
		values.put("sign", user.sign);
		// ����update����
		// ��һ������String������
		// �ڶ�������ContentValues��ContentValues����
		// ����������String��where�־䣬�൱��sql���where�������䣬������ռλ��
		// ���ĸ�����String[]��ռλ����ֵ
		int update = writableDatabase.update(Constant.TABLENAME, values,
				"uid=?", new String[] { user.uid });
		return update;
	}

	/**
	 * 
	 * @param context
	 * @param uid
	 *            �û��˺�
	 * @return
	 */
	public static int deletedatafromTb(Context context, String uid) {
		// ����һ��DatabaseHelper����
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.newInstance(context);
		SQLiteDatabase writableDatabase = helper.getWritableDatabase();
		// ����SQLiteDatabase�����delete��������ɾ������
		// ��һ������String������
		// �ڶ�������String���������
		// ����������String[]������ֵ
		int delete = writableDatabase.delete(Constant.TABLENAME, "uid=?",
				new String[] { uid });
		return delete;
	}
}
