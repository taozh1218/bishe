package service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import util.DateUtils;
import net.sf.json.JSONObject;
import bean.Chat;
import bean.Contact;
import bean.Group;
import dao.CommonDao;
import des.DESUtil;

public class ContactService {
	private static ContactService instance = new ContactService();
	private CommonDao dao = CommonDao.getInstance();
	public String getContactById(){
		return null;
	}
	public String login(String username,String password){
		Contact bean = dao.login(username, password);
		JSONObject json = new JSONObject();
		if (bean==null){
			json.put("code", 0);
		}else{
			json.put("code", 1);
			json.put("msg", dao.newSessionId(bean.getContactId()));
			json.put("data", bean);
		}
		//System.out.println(json.toString());
		return json.toString();
	}

	public String sessionCheck(String sessionid) {
		// TODO Auto-generated method stub
		String decryption = DESUtil.decrypt(sessionid, DESUtil.key);
		String[] split = decryption.split(",");
		if(split.length!=2){
			JSONObject json = new JSONObject();
			json.put("code", 0);
			return json.toString();
		}
		String uid = split[0];
		String time_s = split[1];
		long time = Long.parseLong(time_s);
		Date now = new Date();
		JSONObject json = new JSONObject();
		long tmp = now.getTime()-time;
		if (tmp > 525600000){
			json.put("code", 0);
		}else{
			Boolean right = dao.sessionCheck(uid,sessionid);
			if (right){
				Contact bean = dao.getContactById(uid);
				json.put("code", 1);
				json.put("data", bean);
			}else{
				json.put("code", 0);
			}
		}
		return json.toString();
	}
	
	
	public String inGroup(String key, String id){
		
		Group group = dao.inGroup(key, id);
		JSONObject json = new JSONObject();
		if (group==null){
			json.put("code", 0);
		}else{
			json.put("code", 1);
			json.put("data",group);
		}
		return json.toString();
	}
	
	public String outGroup(String groupId, String id){
		int result = dao.outGroup(groupId, id);
		JSONObject json = new JSONObject();
		if (result==0){
			json.put("code", 0);
		}else{
			json.put("code", 1);
		}
		return json.toString();
	}
	
	public String register(String username, String password){
		JSONObject json = new JSONObject();
		String psw = passwordHandle(password);
		if (psw==null){
			json.put("code", 0);
			json.put("data", "pwd");
		}
		else{
			Contact bean = new Contact();
			bean.setUsername(username);
			bean.setPassword(psw);
			bean.setName(username);
			bean.setTel("00000000000");
			Contact result = dao.insertContact(bean);
			if (result==null){
				json.put("code", 0);
				json.put("data", "usn");
			}else{
				json.put("code", 1);
				json.put("data",bean);
				json.put("msg", dao.newSessionId(bean.getContactId()));
			}
		}
		return json.toString();
	}
	
	private static String passwordHandle(String password){
		String key = DESUtil.key;
		return DESUtil.encrypt(password, key);
	}
	public static ContactService getInstance(){
		return instance;
	}
	
	public String getMyGroups(String id) {
		// TODO Auto-generated method stub
		List<Group> result = dao.getMyGroups(id);
		JSONObject json = new JSONObject();
		if (result==null){
			json.put("code", 0);
		}else{
			json.put("code", 1);
			json.put("data",result);
		}
		return json.toString();
	}
	
	public String getGroupMemberList(String groupId, String date)
	{
		String now = String.valueOf(new Date().getTime());
		List<Contact> result =dao.getGroupMemberList(groupId,date);
		JSONObject json = new JSONObject();
		if (result==null){
			json.put("code", 0);
		}else{
			//获取组员行为与获取组在同一个activity中
			json.put("code", 11);
			json.put("data",result);
			json.put("msg", groupId+","+now);
		}
		return json.toString();
	}
	public String saveContact(Contact bean) {
		// TODO Auto-generated method stub
		Contact result =dao.updateContact(bean);
		JSONObject json = new JSONObject();
		if (result==null){
			json.put("code", 0);
		}else{
			json.put("code", 1);
			json.put("data",result);
		}
		return json.toString();
	}
	
	public String newGroup(String groupName,String ownerId){
		Group bean = new Group();
		bean.setGroupId(UUID.randomUUID().toString());
		bean.setGroupName(groupName);
		bean.setOwnerId(ownerId);
		int result =dao.insertGroup(bean,ownerId);
		JSONObject json = new JSONObject();
		if (result==0){
			json.put("code", 0);
		}else{
			json.put("code", 1);
			json.put("data",bean);
		}
		return json.toString();
	}
	public String getGroupKey(String groupid, String id) {
		// TODO Auto-generated method stub
		String result =dao.getGroupKey(groupid,id);
		JSONObject json = new JSONObject();
		if (result==null){
			json.put("code", 0);
		}else{
			json.put("code", 2);
			json.put("data",result);
		}
		return json.toString();
	}
	public String saveGroup(Group bean) {
		// TODO Auto-generated method stub
		Group result =dao.updateGroup(bean);
		JSONObject json = new JSONObject();
		if (result==null){
			json.put("code", 0);
		}else{
			json.put("code", 1);
			json.put("data",result);
		}
		return json.toString();
	}
	public String updateAvatar(String filename, String id) {
		// TODO Auto-generated method stub
		int result = dao.updateAvater(filename,id);
		JSONObject json = new JSONObject();
		if (result==0){
			json.put("code", 3);
		}else{
			json.put("code", 4);
			json.put("data",filename);
		}
		return json.toString();
	}
	
	public String say(Chat chat){
		int result = dao.say(chat.getGroupId(), chat.getContactId(), chat.getContent(), chat.getType());
		JSONObject json = new JSONObject();
		if (result==0){
			json.put("code", 0);
		}else{
			chat.setId(UUID.randomUUID().toString());
			json.put("code", 1);
			json.put("data", chat);
		}
		return json.toString();
	}
	public String getNewChats(String contactId,String time){
		//System.out.println(contactId+":"+time);
		String oldTime = DateUtils.dateToString(new Date(Long.valueOf(time)));
		Date now = new Date();
		String newTime =  DateUtils.dateToString(now);
		List<Chat> result = dao.getNewChats(contactId, oldTime, newTime);
		JSONObject json = new JSONObject();
		if (result==null){
			json.put("code", -1);
		}else{
			json.put("code", 1);
			json.put("data", result);
			json.put("msg", now.getTime());
		}
		return json.toString();
	}
	public static void main(String[] args) {
//		ContactService cs = ContactService.getInstance();
//		System.out.println(cs.getNewChats("1004", DateUtils.stringToDate("1995-03-20 12:12:12")));
	}
	public String updateFile(String work) {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		if (work==null||"".equals(work)){
			json.put("code", 3);
		}else{
			json.put("code", 4);
			json.put("data",work);
		}
		return json.toString();
	}
}
