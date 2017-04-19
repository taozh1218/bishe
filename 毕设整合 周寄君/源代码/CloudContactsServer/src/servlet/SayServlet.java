package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import bean.Chat;
import bean.Contact;
import service.ContactService;

/**
 * Servlet implementation class SayServlet
 */
@WebServlet("/SayServlet")
public class SayServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private ContactService cs = ContactService.getInstance();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SayServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub		response.setHeader("Content-type", "text/json;charset=UTF-8");  
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String sessionid = request.getParameter("sessionid");
		String groupId = request.getParameter("groupid");
		String content = request.getParameter("content");
		String type = request.getParameter("type");
		JSONObject me = (JSONObject) request.getSession().getAttribute("USER");
		String result;
		if(me!=null){
			Chat chat = new Chat();
			chat.setContactId(me.getString("contactId"));
			chat.setGroupId(groupId);
			chat.setContent(content);
			chat.setType(type);
			result = cs.say(chat);
		}else{
			result = SessionSignIn.signin(request, sessionid);
			JSONObject json = JSONObject.fromObject(result);
			if (json.get("data")!=null){
				me = (JSONObject) request.getSession().getAttribute("USER");
				Chat chat = new Chat();
				chat.setContactId(me.getString("contactId"));
				chat.setGroupId(groupId);
				chat.setContent(content);
				chat.setType(type);
				result = cs.say(chat);
			}
		}
		System.out.println(result);
		out.print(result);
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
