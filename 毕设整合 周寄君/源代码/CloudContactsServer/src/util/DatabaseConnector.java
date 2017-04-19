package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {
	
	private static MyProperty ph = MyProperty.getInstance();
	private static final String url = ph.getProperty("db_url");
	private static final String username = ph.getProperty("db_username");
	private static final String password = ph.getProperty("db_password");

	private static Connection conn = null;
	//private static Statement stmt = null;
	//private static ResultSet rs = null;

	// private PreparedStatement pstmt = null;
	// private CallableStatement cstmt = null;

	public DatabaseConnector() {
		try {
			// 加载MySql的驱动类
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("找不到驱动程序类 ，加载驱动失败！");
			e.printStackTrace();
		}

		try {
			conn = DriverManager.getConnection(url, username, password);
			// stmt = conn.createStatement();
			// pstmt = con.prepareStatement("");
			// cstmt = con.prepareCall("{CALL demoSp(? , ?)}");
		} catch (SQLException se) {
			System.out.println("数据库连接失败！");
			se.printStackTrace();
		}
	}

	public List<Object[]> qrySql(String sql) {
		//System.out.println("sql:"+sql);
		List<Object[]> list = new ArrayList<Object[]>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				Object[] objs = new Object[count];
				for(int i = 1;i<=count;i++){
					objs[i-1] = rs.getObject(i);
				}
				list.add(objs);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public int executeSql(String sql){
		try {
			Statement stmt = conn.createStatement();
			int result = stmt.executeUpdate(sql);
			stmt.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public void close() {
//		if (rs != null) { // 关闭记录集
//			try {
//				rs.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		if (stmt != null) { // 关闭声明
//			try {
//				stmt.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
		if (conn != null) { // 关闭连接对象
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
