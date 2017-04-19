package com.plus.cloudcontacts.util;

import android.content.Context;
import android.database.sqlite.*;

public class DBHelper extends SQLiteOpenHelper{
	private static DBHelper instance;
	private static final String DATABASE_NAME = "cloudcontacts.db";  
    private static final int DATABASE_VERSION = 1;  
      
    public static DBHelper getInstance(Context context){
    	if (instance==null){
    		instance = new DBHelper(context);
    	}
    	return instance;
    }
    
    private DBHelper(Context context) {  
        //CursorFactory����Ϊnull,ʹ��Ĭ��ֵ  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }  
  
    //���ݿ��һ�α�����ʱonCreate�ᱻ����  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        db.execSQL("CREATE TABLE IF NOT EXISTS CLOUD_CONTACTS" +  
                "(NAME VARCHAR PRIMARY KEY , VAL VARCHAR)"); 
        
        db.execSQL("CREATE TABLE IF NOT EXISTS TB_CONTACT("+
        		   "contactId VARCHAR PRIMARY KEY,"+
        		   "NAME VARHCAR,"+
        		   "TEL VARCHAR,"+
        		   "AVATAR VARCHAR,"+
        		   "VERSION VARCHAR)");
        
        db.execSQL("CREATE TABLE IF NOT EXISTS TB_GROUP("+
     		   "GROUPID VARCHAR PRIMARY KEY,"+
     		   "GROUPNAME VARHCAR,"+
     		   "OWNERID VARCHAR," +
     		   "TIME DATETIME)");
        
        db.execSQL("CREATE TABLE IF NOT EXISTS TB_G_C("+
     		   "GROUPID VARCHAR,"+
    		   "contactId VARCHAR," +
    		   "primary key(GROUPID,contactId))");
        
        db.execSQL("CREATE TABLE IF NOT EXISTS TB_CHAT("+
       		   "ID VARCHAR,"+
      		   "GROUPID VARCHAR,"+
      		   "CONTACTID VARCHAR,"+
     		   "CONTENT VARCHAR," +
     		   "TYPE VARCHAR," +
     		   "TIME VARCHAR," +
     		   "primary key(ID))");
    }  
  
    //���DATABASE_VERSIONֵ����Ϊ2,ϵͳ�����������ݿ�汾��ͬ,�������onUpgrade  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
        //db.execSQL("");  
    }  
}
