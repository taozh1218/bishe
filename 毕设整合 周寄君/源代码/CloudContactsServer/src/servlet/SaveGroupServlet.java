package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import bean.Contact;
import bean.Group;
import service.ContactService;

/**
 * Servlet implementation class SaveGroupServlet
 */
@WebServlet("/SaveGroupServlet")
public class SaveGroupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private ContactService cs = ContactService.getInstance();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveGroupServlet() {
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
		String groupid = request.getParameter("groupid");
		String groupname = request.getParameter("groupname");
		JSONObject me = (JSONObject) request.getSession().getAttribute("USER");
		String result;
		if(me!=null){
			Group bean = new Group();
			bean.setGroupId(groupid);
			bean.setGroupName(groupname);
			result = cs.saveGroup(bean);
		}else{
			result = SessionSignIn.signin(request, sessionid);
			JSONObject json = JSONObject.fromObject(result);
			if (json.get("data")!=null){
				me = (JSONObject) request.getSession().getAttribute("USER");
				Group bean = new Group();
				bean.setGroupId(groupid);
				bean.setGroupName(groupname);
				result = cs.saveGroup(bean);
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
