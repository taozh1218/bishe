package com.plus.cloudcontacts.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;  
import java.util.Date;
import java.util.List;  

import com.plus.cloudcontacts.bean.Chat;
import com.plus.cloudcontacts.bean.Contact;
import com.plus.cloudcontacts.bean.Group;
  
import android.content.ContentValues;  
import android.content.Context;  
import android.database.Cursor;  
import android.database.sqlite.SQLiteDatabase;  
  
public class DBManager {  
	
    private DBHelper helper;  
    private SQLiteDatabase db;  
      
    public DBManager(Context context) {  
        helper = DBHelper.getInstance(context);  
        db = helper.getWritableDatabase();  
    }  
    
    public void newSession(String sessionid){
    	db.beginTransaction();
    	db.execSQL("REPLACE INTO CLOUD_CONTACTS(NAME,VAL) VALUES('SESSIONID','"+sessionid+"')");
        //db.execSQL("UPDATE CLOUD_CONTACTS SET VAL = '"+sessionid+"' WHERE NAME = 'SESSIONID'");
    	db.setTransactionSuccessful();
    	db.endTransaction();
    }
    
    public void updateContact(Object bean){
    	
    }
    public Contact getContact(String contactid){
    	Contact bean = new Contact();
    	Cursor c = db.rawQuery("SELECT CONTACTID,NAME,TEL,AVATAR,VERSION FROM TB_CONTACT WHERE CONTACTID = '"+contactid+"'", null);
    	if(c.moveToNext()){
	    	bean.setContactId(c.getString(0));
	    	bean.setName(c.getString(1));
	    	bean.setTel(c.getString(2));
	    	bean.setAvatar(c.getString(3));
	    	bean.setVersion(c.getString(4));
	    	return bean;
    	}
    	return null;
    }
    public void saveContact(Contact bean,String groupId){
    	db.beginTransaction();
    	Object[] args = {bean.getContactId(),bean.getName(),bean.getTel(),bean.getAvatar(),bean.getVersion()};
    	db.execSQL("REPLACE INTO TB_CONTACT(contactid,name,tel,avatar,version) values(?,?,?,?,?)",args);
    	if (groupId != null){
    		db.execSQL("DELETE FROM TB_G_C WHERE contactid = '"+bean.getContactId()+"' and groupid = '"+groupId+"'");
    		db.execSQL("INSERT INTO TB_G_C(contactid,groupId) VALUES('"+bean.getContactId()+"','"+groupId+"')");
    	}
    	db.setTransactionSuccessful();
    	db.endTransaction();
    }
    public void finishGroupContactUpdate(String groupId,String datetime){
    	db.beginTransaction();
    	db.execSQL("UPDATE TB_GROUP SET TIME = '"+datetime+"' where groupid = '"+groupId+"'");
    	db.setTransactionSuccessful();
    	db.endTransaction();
    }
   
    
    public String getSession(){
    	Cursor c = db.rawQuery("SELECT VAL FROM CLOUD_CONTACTS WHERE NAME = 'SESSIONID'",null);
    	if (c.moveToNext()){
    		return c.getString(0);
    	}else{
    		return null;
    	}
    }
    
    public void closeDB() {  
        db.close();  
    }

	public Group saveGroup(Group group) {
		// TODO Auto-generated method stub
    	Cursor c = db.rawQuery("SELECT COUNT(*) FROM TB_GROUP WHERE GROUPID = '"+group.getGroupId()+"'", null);
		c.moveToNext();
    	int count = c.getInt(0);
		db.beginTransaction();
    	if (count==0){
    		Object[] args = {group.getGroupId(),group.getGroupName(),group.getOwnerId()};
    		db.execSQL("INSERT INTO TB_GROUP(groupid,groupname,ownerid) values(?,?,?)",args);
    	}else{
    		Object[] args = {group.getGroupName(),group.getOwnerId(),group.getGroupId()};
    		db.execSQL("UPDATE TB_GROUP SET groupname = ?,ownerid = ? WHERE groupid = ?",args);
    	}
    	db.setTransactionSuccessful();
    	db.endTransaction();
    	
    	c = db.rawQuery("SELECT t.GroupId,t.groupNAME,t.ownerId,t.time FROM TB_GROUP t WHERE t.GROUPID = '"+group.getGroupId()+"'", null);
		if (c.moveToNext()){
			group.setGroupId(c.getString(0));
			group.setGroupName(c.getString(1));
			group.setOwnerId(c.getString(2));
			group.setTime(c.getString(3));
	    	return group;
		}else{
			return null;
		}
	}

	public List<Group> getMyGroups(String id) {
		// TODO Auto-generated method stub
		List<Group> group = new ArrayList<Group>() ;
    	Cursor c = db.rawQuery("SELECT t.GroupId,t.groupNAME,(select count(*) from tb_g_c where groupId = t.groupId)"+
		" FROM TB_GROUP t,TB_G_C v WHERE v.CONTACTID = '"+id+"' and v.groupid = t.groupid order by t.GroupName", null);
    	while(c.moveToNext()){
    		Group bean = new Group();
	    	bean.setGroupId(c.getString(0));
	    	bean.setGroupName(c.getString(1));
	    	bean.setOwnerId(c.getString(2));
	    	group.add(bean);
    	}
    	return group;
	}  
	 public List<Contact> getMembers(String groupId){
	    	List<Contact> list = new ArrayList<Contact>();
	    	Cursor c = db.rawQuery("SELECT t.CONTACTID,t.NAME,t.TEL,t.AVATAR,t.VERSION FROM TB_CONTACT t,tb_g_c v WHERE t.CONTACTID = v.contactid and v.groupid = '"+groupId+"'", null);
	    	while(c.moveToNext()){
	    		Contact bean = new Contact();
		    	bean.setContactId(c.getString(0));
		    	bean.setName(c.getString(1));
		    	bean.setTel(c.getString(2));
		    	bean.setAvatar(c.getString(3));
		    	bean.setVersion(c.getString(4));
		    	list.add(bean);
	    	}
	    	return list;
	    }

	public void leaveGroup(String groupId,String id) {
		// TODO Auto-generated method stub
		db.beginTransaction();
    	Object[] args = {groupId,id};
    	db.execSQL("DELETE FROM TB_G_C WHERE groupid = ? AND CONTACTid = ?",args);
    	db.setTransactionSuccessful();
    	db.endTransaction();
	}

	public String getNameByTel(String mIncomingNumber) {
		// TODO Auto-generated method stub
		Cursor c = db.rawQuery("SELECT name FROM tb_contact WHERE tel = '"+mIncomingNumber+"'",null);
    	if (c.moveToNext()){
    		return c.getString(0);
    	}else{
    		return null;
    	}
	}

	public void inGroup(String groupId, String contactId) {
		// TODO Auto-generated method stub
		db.beginTransaction();
    	Object[] args = {groupId,contactId};
    	db.execSQL("INSERT INTO TB_G_C(groupid,CONTACTid) VALUES(?,?)",args);
    	db.setTransactionSuccessful();
    	db.endTransaction();
	}

	public String getChatTime() {
		// TODO Auto-generated method stub
		Cursor c = db.rawQuery("SELECT val FROM CLOUD_CONTACTS WHERE NAME = 'CHAT_TIME'",null);
    	if (c.moveToNext()){
    		return c.getString(0);
    	}else{
    		Date time = new Date();
    		String s_time = String.valueOf(time.getTime());
    		setChatTime(s_time);
    		return s_time;
    	}
	}
	
	public void setChatTime(String s_time){
		db.beginTransaction();
    	Object[] args = {"CHAT_TIME",s_time};
    	db.execSQL("REPLACE INTO CLOUD_CONTACTS(NAME,VAL) VALUES(?,?)",args);
    	db.setTransactionSuccessful();
    	db.endTransaction();
	}

	public void saveChat(Chat chat) {
		// TODO Auto-generated method stub
		db.beginTransaction();
    	Object[] args = {chat.getId(),chat.getGroupId(),chat.getContactId(),chat.getType(),chat.getContent(),chat.getTime()};
    	db.execSQL("INSERT INTO TB_CHAT(ID,GROUPID,CONTACTID,TYPE,CONTENT,TIME) VALUES(?,?,?,?,?,?)",args);
    	db.setTransactionSuccessful();
    	db.endTransaction();
	}
	public List<Chat> getGroupChats(String groupId){
		List<Chat> list = new ArrayList<Chat>();
    	Cursor c = db.rawQuery("SELECT t.ID,t.GROUPID,t.CONTACTID,t.TYPE,t.CONTENT,t.TIME,v.AVATAR FROM TB_CHAT t,TB_CONTACT v WHERE t.GROUPID = '"+groupId+"' AND T.contactId = V.contactId ORDER BY TIME", null);
    	while(c.moveToNext()){
    		Chat bean = new Chat();
    		bean.setId(c.getString(0));
    		bean.setGroupId(c.getString(1));
	    	bean.setContactId(c.getString(2));
	    	bean.setType(c.getString(3));
	    	bean.setContent(c.getString(4));
	    	bean.setTime(c.getString(5));
	    	bean.setAvatar(c.getString(6));
	    	list.add(bean);
    	}
    	return list;
	}
}  