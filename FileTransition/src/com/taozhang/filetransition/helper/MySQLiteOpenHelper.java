package com.taozhang.filetransition.helper;

import com.taozhang.filetransition.util.Constant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	public final static int VERSION = 1;
	public static final String DROP_TB = "drop table if exists userInfo";

	private static MySQLiteOpenHelper helper;

	private MySQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	/**
	 * ��д���췽��
	 * 
	 * @param context
	 * @param name
	 *            ���ݿ�����
	 */
	private MySQLiteOpenHelper(Context context) {
		super(context, Constant.DATABASE_NAME, null, VERSION);
	}

	public static MySQLiteOpenHelper newInstance(Context context) {
		if (helper == null) {
			synchronized (MySQLiteOpenHelper.class) {
				if (helper == null) {
					helper = new MySQLiteOpenHelper(context);
				}
			}
		}
		return helper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// �����ݿⴴ��ʱ��ִ�н������
		db.execSQL("CREATE TABLE if not exists userInfo("
				+ "_id integer primary key autoincrement,"
				+ "uid String DEFAULT \"\"," + // default null/ ""
				"psw String DEFAULT \"\"," +
				"username String ," +
				"sign String )");
		System.out.println("���ݿⴴ���ɹ������ҽ�����userInfo��");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

}
