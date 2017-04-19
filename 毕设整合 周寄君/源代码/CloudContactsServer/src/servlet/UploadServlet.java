package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import service.ContactService;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class FileUploadServlet
 */
@WebServlet("/UploadServlet")
@MultipartConfig
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private ContactService cs = ContactService.getInstance();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	private String work(HttpServletRequest request) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String savePath = request.getServletContext().getRealPath(
				"/WEB-INF/uploadFile");
		Collection<Part> parts = request.getParts();
		String fileName = null;
//		if (parts.size() == 1) {
//			Part part = request.getPart("file");
//			String header = part.getHeader("Content-Disposition");
//			fileName = getFileName(header);
//			part.write(savePath + File.separator + fileName);
//		} else {
			for (Part part : parts) {// 循环处理上传的文件
				String header = part.getHeader("Content-Disposition");
				fileName = getFileName(header);
				System.out.println(savePath + File.separator + fileName);
				part.write(savePath + File.separator + fileName);
			}
//		}
		return fileName;
	}

	private static String getFileName(String header) {
		Pattern pattern = Pattern.compile("(.\\w+)\"$");
		Matcher matcher = pattern.matcher(header);
		String suffix = "";
		if (matcher.find()) {
			suffix = matcher.group(1);
		}
		// TODO Auto-generated method stub
		DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		// 转换为字符串
		String formatDate = format.format(new Date());
		// 随机生成文件编号
		int random = new Random().nextInt(9000) + 1000;
		return new StringBuffer().append(formatDate).append(random)
				.append(suffix).toString();
	}

	public static void main(String[] args) {
		System.out
				.println(getFileName("{form-data,name=\"file\",filename=\"snmp4j--api.zip\"}"));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setHeader("Content-type", "text/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String sessionid = request.getParameter("sessionid");
		JSONObject me = (JSONObject) request.getSession().getAttribute("USER");
		String result;
		if(me!=null){
			
			result = cs.updateFile(work(request));
		}else{
			result = SessionSignIn.signin(request, sessionid);
			JSONObject json = JSONObject.fromObject(result);
			if (json.get("data")!=null){
				me = (JSONObject) request.getSession().getAttribute("USER");
				result = cs.updateFile(work(request));
			}
		}
		System.out.println(result);
		out.print(result);
		out.flush();
		out.close();
	}

}
