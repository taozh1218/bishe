package dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import des.DESUtil;
import bean.Chat;
import bean.Contact;
import bean.Group;
import util.DatabaseConnector;
import util.DateUtils;

public class CommonDao {
	private static CommonDao instance = new CommonDao();
	
	private static DatabaseConnector dc = new DatabaseConnector();
	
	public Contact login(String username,String password){
		List<Object[]> result =dc.qrySql("SELECT PASSWORD FROM TB_CONTACT WHERE USERNAME = '"+username+"'");
		if (result.size()==0){
			return null;
		}
		String psw = String.valueOf(result.get(0)[0]);
		String decryptPsw =  DESUtil.decrypt(psw, DESUtil.key);
		if (password.equals(decryptPsw)){
			result = dc.qrySql("SELECT CONTACTID,NAME,TEL,ADDTIME,STATUS,AVATAR,VERSION FROM TB_CONTACT WHERE USERNAME = '"+username+"'");
			Contact bean = new Contact();
			bean.setContactId(String.valueOf(result.get(0)[0]));
			bean.setName(String.valueOf(result.get(0)[1]));
			bean.setTel(String.valueOf(result.get(0)[2]));
			bean.setAddTime(DateUtils.dateToString((java.sql.Timestamp)result.get(0)[3]));
			//bean.setStatus(String.valueOf(result.get(0)[4]));
			bean.setAvatar(String.valueOf(result.get(0)[5]));
			bean.setVersion(String.valueOf(result.get(0)[6]));
			bean.setUsername(username);
			bean.setPassword("invisible");
			return bean;
		}else{
			return null;
		}
	}
	
	public String newSessionId(String uid){
		Date now = new Date();
		String sessionId = uid+","+now.getTime();
		sessionId = DESUtil.encrypt(sessionId,DESUtil.key);
		int result = dc.executeSql("UPDATE TB_CONTACT SET SESSION = '"+sessionId+"' WHERE CONTACTID = '"+uid+"'");
		if (result>0){
			return sessionId;
		}else{
			return null;
		}
	}
	

	public Boolean sessionCheck(String uid, String sessionid) {
		// TODO Auto-generated method stub
		List<Object[]> result = dc.qrySql("SELECT COUNT(*) FROM TB_CONTACT WHERE CONTACTID = '"+uid+"' AND SESSION ='"+sessionid+"'");
		int count = Integer.valueOf((String.valueOf(result.get(0)[0])));
		if (count == 0){
			return false;
		}else{
			return true;
		}
	}
	
	public int insertGroup(Group bean,String id){
		int result = dc.executeSql("INSERT INTO TB_GROUP(GROUPID,GROUPNAME,OWNERID) VALUES('"+bean.getGroupId()+"','"+bean.getGroupName()+"','"+bean.getOwnerId()+"')");
		if(result>0){
			dc.executeSql("INSERT INTO TB_GROUP_CONTACT(GROUPID,CONTACTID) VALUES('"+bean.getGroupId()+"','"+id+"')");
		}
		return result;
	}
	
	public Group updateGroup(Group bean){
		String str = bean.getGroupName()+","+new Date().getTime();
		String key = DigestUtils.shaHex(str);
		int result = dc.executeSql("UPDATE TB_GROUP SET GROUPNAME='"+bean.getGroupName()+
				"',TB_GROUP.KEY='"+key+"' WHERE GROUPID = '"+bean.getGroupId()+"'");
		if (result > 0){
			return bean;
		}else{
			return null;
		}
	}
	
	public Contact insertContact(Contact bean){
		String sql = "SELECT COUNT(*) FROM TB_CONTACT WHERE USERNAME = '"+bean.getUsername()+"'";
		List<Object[]> result = dc.qrySql(sql);
		int count = Integer.valueOf(String.valueOf(result.get(0)[0]));
		if (count==0){
			int ins = dc.executeSql("INSERT INTO TB_CONTACT(USERNAME,PASSWORD,NAME,TEL,ADDTIME,STATUS) VALUES('"+bean.getUsername()+"','"+bean.getPassword()+"','"+bean.getName()+"','"+bean.getTel()+"',NOW(),'1')");
			if (ins>0){
				result = dc.qrySql("SELECT CONTACTID,NAME,TEL,ADDTIME,STATUS,AVATAR,VERSION FROM TB_CONTACT WHERE USERNAME = '"+bean.getUsername()+"'");
				bean.setContactId(String.valueOf(result.get(0)[0]));
				bean.setName(String.valueOf(result.get(0)[1]));
				bean.setTel(String.valueOf(result.get(0)[2]));
				bean.setAddTime(DateUtils.dateToString((java.sql.Timestamp)result.get(0)[3]));
				//bean.setStatus(String.valueOf(result.get(0)[4]));
				bean.setAvatar(String.valueOf(result.get(0)[5]));
				bean.setVersion(String.valueOf(result.get(0)[6]));
				bean.setPassword("invisible");
				return bean;
			}
		}
		return null;
	}
	
	public Contact updateContact(Contact bean){
		int result = dc.executeSql("UPDATE TB_CONTACT SET NAME='"+bean.getName()+
				"',TEL='"+bean.getTel()+"',VERSION=VERSION+1 WHERE CONTACTID = '"+bean.getContactId()+"'");
		if(result>0){
			dc.executeSql("INSERT INTO TB_CONTACT_LOG(OPT,CONTACTID,DATE) VALUES('MODIFY','"+bean.getContactId()+"',NOW())");
			return getContactById(bean.getContactId());
		}else{
			return null;
		}
	}
	
	public int updateAvater(String filename, String id) {
		// TODO Auto-generated method stub
		int result = dc.executeSql("UPDATE TB_CONTACT SET avatar='"+filename+
				"' WHERE CONTACTID = '"+id+"'");
		if(result>0){
			dc.executeSql("INSERT INTO TB_CONTACT_LOG(OPT,CONTACTID,DATE) VALUES('AVATAR','"+id+"',NOW())");
			return 1;
		}else{
			return 0;
		}
	}
	
	
	public Contact getContactById(String id){
		List<Object[]> result = dc.qrySql("SELECT CONTACTID,NAME,TEL,ADDTIME,STATUS,AVATAR,VERSION FROM TB_CONTACT WHERE CONTACTID = '"+id+"'");
		if (result.size()==0){
			return null;
		}else{
			Contact bean = new Contact();
			bean.setContactId(String.valueOf(result.get(0)[0]));
			bean.setName(String.valueOf(result.get(0)[1]));
			bean.setTel(String.valueOf(result.get(0)[2]));
			bean.setAddTime(DateUtils.dateToString((java.sql.Timestamp)result.get(0)[3]));
			//bean.setStatus(String.valueOf(result.get(0)[4]));
			bean.setAvatar(String.valueOf(result.get(0)[5]));
			bean.setVersion(String.valueOf(result.get(0)[6]));
			bean.setUsername("invisible");
			bean.setPassword("invisible");
			return bean;
		}
	}
	

	public String getGroupKey(String groupid, String id) {
		// TODO Auto-generated method stub
		System.out.println("SELECT COUNT(*) FROM TB_GROUP_CONTACT WHERE GROUPID ='"+groupid+"' AND CONTACTID = '"+id+"'");
		List<Object[]> result = dc.qrySql("SELECT COUNT(*) FROM TB_GROUP_CONTACT WHERE GROUPID ='"+groupid+"' AND CONTACTID = '"+id+"'");
		int count = Integer.valueOf((String.valueOf(result.get(0)[0])));
		if (count != 0){
			result = dc.qrySql("SELECT TB_GROUP.key FROM TB_GROUP WHERE GROUPID = '"+groupid+"'");
			if (result.size()>0){
				return (String) result.get(0)[0];
			}
		}
		return null;
	}
	
	public Group getGroupById(String id){
		List<Object[]> groupRes = dc.qrySql("SELECT GROUPID,GROUPNAME,OWNERID FROM TB_GROUP WHERE GROUPID = '"+id+"'");
		if (groupRes.size()==0){
			return null;
		}else{
			Group group = new Group();
			group.setGroupId(String.valueOf(groupRes.get(0)[0]));
			group.setGroupName(String.valueOf(groupRes.get(0)[1]));
			group.setOwnerId(String.valueOf(groupRes.get(0)[2]));
			return group;
		}
	}
	
	public List<Group> getMyGroups(String myId){
		List<Object[]> groupRes = dc.qrySql("SELECT GROUPID FROM TB_GROUP_CONTACT WHERE CONTACTID = '"+myId+"'");
		if (groupRes.size()==0){
			return null;
		}else{
			List<Group> groups = new ArrayList<Group>();
			for(int i = 0;i<groupRes.size();i++){
				String groupId = String.valueOf(groupRes.get(i)[0]);
				Group group = getGroupById(groupId);
//				if(group!=null){
//					group.setMembers(getGroupMemberList(groupId));
//				}
				groups.add(group);
			}
			return groups;
		}
	}
	
	public Group inGroup(String key, String id){
		String groupId = null;
		List<Object[]> result = dc.qrySql("SELECT GROUPID FROM TB_GROUP WHERE TB_GROUP.KEY = '"+key+"'");
		if (result.size()>0){
			groupId = (String) result.get(0)[0];
		}else{
			return null;
		}
		result = dc.qrySql("SELECT COUNT(*) FROM TB_GROUP_CONTACT WHERE GROUPID = '"+groupId+"' AND CONTACTID = '"+id+"'");
		int count = Integer.valueOf((String.valueOf(result.get(0)[0])));
		if (count == 0){
			int ins = dc.executeSql("INSERT INTO TB_GROUP_CONTACT(GROUPID,CONTACTID) VALUES('"+groupId+"','"+id+"')");
			if (ins >0){
				dc.executeSql("INSERT INTO TB_GROUP_LOG(OPT,GROUPID,CONTACTID,DATE) VALUES('IN','"+groupId+"','"+id+"',str_to_date('"+
					DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss")+"','%Y-%m-%d %H:%i:%s'))");
			}
			return getGroupById(groupId);
		}else{
			return null;
		}
	}
	
	
	public int outGroup(String groupId ,String id){
		int result = dc.executeSql("DELETE FROM TB_GROUP_CONTACT WHERE GROUPID = '"+groupId+"' AND CONTACTID = '"+id+"'");
		if (result>0){
			dc.executeSql("INSERT INTO TB_GROUP_LOG(OPT,GROUPID,CONTACTID,DATE) VALUES('OUT','"+groupId+"','"+id+"',str_to_date('"+
				DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss")+"','%Y-%m-%d %H:%i:%s'))");
		}
		return result;
	}
	
	public List<Contact> getGroupMemberList(String GroupId, String date){
		String sql = "SELECT t.CONTACTID,t.NAME,t.TEL,t.ADDTIME,'1',t.AVATAR,t.VERSION FROM TB_CONTACT t,TB_GROUP_CONTACT g WHERE t.contactId = g.contactId and GROUPID = '"+GroupId+"'";
		if (date != null){
			long date_long = Long.valueOf(date);
//			sql = "SELECT t.CONTACTID,t.NAME,t.TEL,t.ADDTIME,g.opt,t.AVATAR,t.VERSION FROM TB_CONTACT t,(select contactId,sum(case opt when 'IN' then 1 when 'out' then -1 END )opt from tb_group_log where groupId = '"+
//					GroupId+"' and date > str_to_date('"+DateUtils.dateToString(new Date(date_long))+"','%Y-%m-%d %H:%i:%s')  GROUP BY contactId) g where g.contactId = t.contactId";
			sql = "SELECT t.CONTACTID,t.NAME,t.TEL,t.ADDTIME,g.opt,t.AVATAR,t.VERSION FROM TB_CONTACT t,(select contactId,sum(case opt when 'IN' then 1 when 'out' then -1 END )opt from tb_group_log where groupId = '"+
					GroupId+"' and date > str_to_date('"+DateUtils.dateToString(new Date(date_long))+"','%Y-%m-%d %H:%i:%s')  GROUP BY contactId union select distinct s.contactId,'0' from tb_contact_log s,tb_group_contact v  where s.date > str_to_date('"+
					DateUtils.dateToString(new Date(date_long))+"','%Y-%m-%d %H:%i:%s') and s.contactId = v.contactId and v.groupId = '"+GroupId+"') g where g.contactId = t.contactId";
			//System.out.println(sql);
		}
		List<Object[]> result = dc.qrySql(sql);
		if (result.size()==0){
			return null;
		}else{
			List<Contact> members = new ArrayList<Contact>();
			for(int i = 0;i<result.size();i++){
				Contact bean = new Contact();
				bean.setContactId(String.valueOf(result.get(i)[0]));
				bean.setName(String.valueOf(result.get(i)[1]));
				bean.setTel(String.valueOf(result.get(i)[2]));
				bean.setAddTime(DateUtils.dateToString((java.sql.Timestamp)result.get(i)[3]));
				bean.setStatus(String.valueOf(result.get(i)[4]));
				bean.setAvatar(String.valueOf(result.get(i)[5]));
				bean.setVersion(String.valueOf(result.get(i)[6]));
				members.add(bean);
			}
			return members;
		}
	}
	
	public int say(String groupId,String contactId, String content,String type){
		String sql = "INSERT INTO TB_GROUP_CHAT(GROUPID,CONTACTID,CONTENT,TYPE,TIME) VALUES('"+groupId+"','"+contactId+"','"+content+"','"+type+"',now())";
		int result = dc.executeSql(sql);
		return result;
	}
	
	public List<Chat> getNewChats(String contactId,String time,String newTime){
		String sql = "SELECT t.ID,t.GROUPID,t.CONTACTID,t.CONTENT,t.TYPE,t.TIME,v.avatar FROM TB_GROUP_CHAT t,TB_CONTACT v WHERE TIME > str_to_date('"+
					time+"','%Y-%m-%d %H:%i:%s') AND TIME <= str_to_date('"+
					newTime+"','%Y-%m-%d %H:%i:%s') AND t.contactId = v.contactId AND t.GROUPID IN (SELECT GROUPID FROM TB_GROUP_CONTACT WHERE CONTACTID = t.CONTACTID)";
		List<Object[]> result = dc.qrySql(sql);
		if (result.size()==0){
			return null;
		}else{
			List<Chat> chats = new ArrayList<>();
			for(int i = 0;i<result.size();i++){
				Chat chat = new Chat();
				chat.setId(String.valueOf(result.get(i)[0]));
				chat.setGroupId(String.valueOf(result.get(i)[1]));
				chat.setContactId(String.valueOf(result.get(i)[2]));
				chat.setContent(String.valueOf(result.get(i)[3]));
				chat.setType(String.valueOf(result.get(i)[4]));
				chat.setTime(String.valueOf(DateUtils.stringToDate(String.valueOf(result.get(i)[5])).getTime()));
				chat.setAvatar(String.valueOf(result.get(i)[6]));
				chats.add(chat);
			}
			return chats;
		}
	} 
	
	public static CommonDao getInstance(){
		return instance;
	}
	public static void main(String[] args) {
//		CommonDao dao = CommonDao.getInstance();
//		dao.say("2","1000","Hello World!","Text");
//		List<Chat> chats = dao.getNewChat("1004", DateUtils.stringToDate("1995-03-20 12:12:12"),new Date());
	}

}
