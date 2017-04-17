package com.taozhang.filetransition.util;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/**
 * 锟斤拷锟接凤拷锟斤拷锟斤拷锟斤拷锟斤拷
 * 
 * @author Administrator
 *
 */
public class Connect {
	private volatile static Connect con;
	private Socket socket;
	public DataOutputStream dOps;
	public DataInputStream dIps;


	private Connect(String address) {

		try {
			socket = new Socket(address,10088);
			dOps = new DataOutputStream(socket.getOutputStream());
			dIps = new DataInputStream(socket.getInputStream());
			Log.i("socket", "杩炴帴鎴愬姛");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	
	public static Connect getInstance(InetSocketAddress address) {
		if (con == null) {
			// 锟斤拷锟斤拷
			synchronized (Connect.class) {
				if (con == null) {
					con = new Connect(address.getAddress().getHostAddress());
				}
			}
		}

		return con;
	}

	/**
	 * 锟斤拷锟斤拷锟斤拷息锟侥凤拷锟斤拷
	 * 
	 * @param msg
	 */
	public void sendMsg(String msg) {
		try {
			
			dOps.writeUTF(msg);
			dOps.flush();// 寮哄埗杈撳嚭
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * TODO:涓嶆噦涓轰粈涔堜竴瀹氳鏄暟缁�
	 * 
	 * @return
	 * @throws IOException
	 */
	public String getMsg() throws IOException {
		 String readUTF =  dIps.readUTF();
		return readUTF;
		}

}
