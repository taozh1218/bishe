package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetImageServlet
 */
@WebServlet("/GetImageServlet")
public class GetImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetImageServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String savePath = request.getServletContext().getRealPath(
				"/WEB-INF/uploadFile");      
		String filename = request.getParameter("filename");
		File file = new File(savePath+File.separator+filename);
        FileInputStream fis = new FileInputStream(file);  
        int size =fis.available(); //得到文件大小   
        byte data[]=new byte[size];   
        fis.read(data);  //读数据   
        fis.close();   
//        response.setContentType("image/jpeg"); //设置返回的文件类型   
        response.addHeader("Content-Length",String.valueOf(file.length()));//设置下载文件大小
        response.setContentType("application/x-tar");//设置文件类型
        response.setHeader("Content-Disposition", "attachment;fileName="+filename);
        OutputStream os = response.getOutputStream();  
        os.write(data);  
        os.flush();  
        os.close();          
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
