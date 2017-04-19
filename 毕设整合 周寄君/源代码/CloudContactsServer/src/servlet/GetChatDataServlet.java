package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import service.ContactService;

/**
 * Servlet implementation class GetChatDataServlet
 */
@WebServlet("/GetChatDataServlet")
public class GetChatDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private ContactService cs = ContactService.getInstance();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetChatDataServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setHeader("Content-type", "text/json;charset=UTF-8");  
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String sessionid = request.getParameter("sessionid");
		String time = request.getParameter("time");
		System.out.println(time);
		JSONObject me = (JSONObject) request.getSession().getAttribute("USER");
		String result;
		if(me!=null){
			String contactId = me.getString("contactId");
			result = cs.getNewChats(contactId, time);
		}else{
			result = SessionSignIn.signin(request, sessionid);
			JSONObject json = JSONObject.fromObject(result);
			if (json.get("data")!=null){
				me = (JSONObject) request.getSession().getAttribute("USER");
				String contactId = me.getString("contactId");
				result = cs.getNewChats(contactId, time);
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
