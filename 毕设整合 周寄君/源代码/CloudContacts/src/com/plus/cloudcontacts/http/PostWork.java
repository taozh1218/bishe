package com.plus.cloudcontacts.http;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.plus.cloudcontacts.json.ResponseMessage;
import com.plus.cloudcontacts.util.DirUtil;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class PostWork {
	public static final int SHOW_RESPONSE = 0;
	private static final String URL_HEAD = "http://192.168.137.1:8080/CloudContactsServer/";
	public static final String URL_TEST = "TestServlet";
	public static final String URL_LOGIN = "LoginServlet";
	public static final String URL_SESSION = "SessionServlet";
	public static final String URL_IN_GROUP = "InGroupServlet";
	public static final String URL_SIGN_UP = "SignUpServlet";
	public static final String URL_GET_GROUPS = "GetGroupsServlet";
	public static final String URL_GET_MEMBERS = "GetMembersServlet";
	public static final String URL_SAVE_CONTACT = "SaveContactServlet";
	public static final String URL_AWAY = "LeaveGroupServlet";
	public static final String URL_NEW_GROUP = "NewGroupServlet";
	public static final String URL_SAVE_GROUP = "SaveGroupServlet";
	public static final String URL_GET_GROUP_KEY = "GetGroupKeyServlet";
	public static final String URL_AVATAR_UPLOAD = "AvatarUploadServlet";
	public static final String URL_GET_IMAGE = "GetImageServlet";
	public static final String URL_GET_CHAT_DATA = "GetChatDataServlet";
	public static final String URL_SAY = "SayServlet";
	public static final String URL_UPLOAD = "UploadServlet";

	private String url_s;
	private Handler mHandler;
	private static String[] httpSession;
	private Bundle bundleData;
	
	public void msgPutString(String key,String value){
		bundleData.putString(key, value);
	}

	public PostWork(String url_servlet, Map params, Handler mHandler) {
		// TODO Auto-generated constructor stub
		this.url_s = URL_HEAD + url_servlet;
		StringBuffer param_s = new StringBuffer();
		Iterator<Entry<String, String>> iter = params.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			param_s.append("&");
			param_s.append(entry.toString());
		}
		if (param_s.toString().length() > 0) {
			param_s.setCharAt(0, '?');
			url_s += param_s.toString();
		}
		bundleData = new Bundle();
		this.mHandler = mHandler;
	}

	public static Uri getImageURI(String path, File cache) {
		if (cache==null){
			cache = new File(Environment.getExternalStorageDirectory(), "cloudcontacts_cache");
			if(!cache.exists()){
	            cache.mkdirs();
	        }
		}
		File file = new File(cache, path);
		// 如果图片存在本地缓存目录，则不去服务器下载
		if (file.exists()) {
			return Uri.fromFile(file);// Uri.fromFile(path)这个方法能得到文件的URI
		} else {
			try {
				// 从网络上获取图片
				System.out.println("path:"+path);
				URL url = new URL(URL_HEAD + URL_GET_IMAGE + "?filename="
						+ path);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				if (conn.getResponseCode() == 200) {
					InputStream is = conn.getInputStream();
					FileOutputStream fos = new FileOutputStream(file);
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
					}
					is.close();
					fos.close();
					// 返回一个URI对象
					return Uri.fromFile(file);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	
	
	public boolean upload(List<File> list) {
		HttpURLConnection connection = null;
		try {
			String BOUNDARY = "---------7d4a6d158c9"; // 定义数据分隔线
			URL url = new URL(url_s);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			if (httpSession != null) {
				connection.setRequestProperty("Cookie", httpSession[0]);
			}
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(8000);
			connection.setReadTimeout(8000);

			OutputStream out = new DataOutputStream(
					connection.getOutputStream());
			byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();// 定义最后数据分隔线
			int leng = list.size();
			for (int i = 0; i < leng; i++) {
				File file = list.get(i);
				StringBuilder sb = new StringBuilder();
				sb.append("--");
				sb.append(BOUNDARY);
				sb.append("\r\n");
				sb.append("Content-Disposition: form-data;name=\"file" + i
						+ "\";filename=\"" + file.getName() + "\"\r\n");
				sb.append("Content-Type:application/octet-stream\r\n\r\n");

				byte[] data = sb.toString().getBytes();
				out.write(data);
				DataInputStream filein = new DataInputStream(
						new FileInputStream(file));
				int bytes = 0;
				byte[] bufferOut = new byte[1024];
				while ((bytes = filein.read(bufferOut)) != -1) {
					out.write(bufferOut, 0, bytes);
				}
				out.write("\r\n".getBytes()); // 多个文件时，二个文件之间加入这个
				filein.close();
			}
			out.write(end_data);
			out.flush();
			out.close();

			String session_value = connection.getHeaderField("Set-Cookie");
			if (session_value != null) {
				httpSession = session_value.split(";");
			}
			// 下面对获取到的输入流进行读取
			InputStream in = connection.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			Gson gson = new Gson();
			ResponseMessage rm = gson.fromJson(response.toString(),
					ResponseMessage.class);
			if (rm.getCode() == 0) {
				return false;
			}
			Message message = new Message();
			message.what = rm.getCode();
			message.obj = rm.getData();
//			Bundle data = new Bundle();
			bundleData.putString("msg", rm.getMsg());
			message.setData(bundleData);
			DirUtil.Copy(list.get(0).getPath(), DirUtil.getFilePath((String)message.obj));
			mHandler.sendMessage(message);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return true;
	}

	public boolean work() {
		// TODO Auto-generated method stub
		HttpURLConnection connection = null;
		try {

			URL url = new URL(url_s);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			if (httpSession != null) {
				connection.setRequestProperty("Cookie", httpSession[0]);
			}
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			connection.setConnectTimeout(8000);
			connection.setReadTimeout(8000);
			String session_value = connection.getHeaderField("Set-Cookie");
			if (session_value != null) {
				httpSession = session_value.split(";");
			}
			// 下面对获取到的输入流进行读取
			InputStream in = connection.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			Gson gson = new Gson();
			ResponseMessage rm = gson.fromJson(response.toString(),
					ResponseMessage.class);
			if (rm.getCode() == 0) {
				return false;
			}
			Message message = new Message();
			message.what = rm.getCode();
			message.obj = rm.getData();
			Bundle data = new Bundle();
			data.putString("msg", rm.getMsg());
			message.setData(data);
			mHandler.sendMessage(message);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return true;
	}

}
