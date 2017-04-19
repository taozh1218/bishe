package servlet;

import javax.servlet.http.HttpServletRequest;

import service.ContactService;
import net.sf.json.JSONObject;

public class SessionSignIn {
	public static String signin(HttpServletRequest request,String sessionid){
		ContactService cs = ContactService.getInstance();
//		System.out.println("sessionid:"+sessionid);
		cs.sessionCheck(sessionid);
		String result = cs.sessionCheck(sessionid);
		JSONObject json = JSONObject.fromObject(result);
		if (json.get("data")!=null){
			request.getSession().setAttribute("USER", json.get("data"));
		}
		return result;
	}
}
