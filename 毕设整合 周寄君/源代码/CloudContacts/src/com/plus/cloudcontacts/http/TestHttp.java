package com.plus.cloudcontacts.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Handler;
import android.os.Message;

public class TestHttp implements Runnable{
	public static final int SHOW_RESPONSE=0;
	
	private String url_s = "http://192.16.0.108:8080/CloudContactsServer/TestServlet";
	private Handler mHandler;
	
	public TestHttp(Handler mHandler) {
		// TODO Auto-generated constructor stub
		this.mHandler = mHandler;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		HttpURLConnection connection=null;
        
        try {
            URL url=new URL(url_s);
            connection =(HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            InputStream in=connection.getInputStream();
            //下面对获取到的输入流进行读取
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));
            StringBuilder response=new StringBuilder();
            String line;
            while((line=reader.readLine())!=null){
                response.append(line);
            }
            Message message=new Message();
            message.what=SHOW_RESPONSE;
            message.obj=response.toString();
            mHandler.sendMessage(message);
             
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(connection!=null){
                connection.disconnect();
            }
        }
	}
	
}
