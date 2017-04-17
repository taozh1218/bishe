package com.taozhang.filetransition.helper;

import com.taozhang.filetransition.util.Constant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	public final static int VERSION = 1;

	private static MySQLiteOpenHelper helper;

	private MySQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	/**
	 * 重写构造方法
	 * 
	 * @param context
	 * @param name
	 *            数据库名字
	 */
	private MySQLiteOpenHelper(Context context) {
		super(context, Constant.DATABASE_FILENAME, null, VERSION);
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
		// 当数据库创建时，执行建表操作
		db.execSQL("CREATE TABLE if not exists fileinfo("
				+ "name String DEFAULT \"\"," + // default null/ ""
				"type String DEFAULT \"\");");
		System.out.println("数据库创建成功，并且建立了fileinfo表！");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

}
