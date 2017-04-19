package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.ContactService;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class GetMembersServlet
 */
@WebServlet("/GetMembersServlet")
public class GetMembersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private ContactService cs = ContactService.getInstance();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetMembersServlet() {
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
		String groupId = request.getParameter("groupid");
		String date = request.getParameter("date");
		JSONObject me = (JSONObject) request.getSession().getAttribute("USER");
		String result;
		if(me!=null){
			result = cs.getGroupMemberList(groupId,date);
		}else{
			System.out.println(1);
			result = SessionSignIn.signin(request, sessionid);
			JSONObject json = JSONObject.fromObject(result);
			if (json.get("data")!=null){
				me = (JSONObject) request.getSession().getAttribute("USER");
				result = cs.getGroupMemberList(groupId,date);
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
