package com.taozhang.filetransition.bean;

import java.io.Serializable;

/**
 * ���ݿ����û���Ϣ
 * 
 * @author taozhang
 * 
 */
public class UserInfo implements Serializable {

	public Integer id;
	public String uid;
	public String psw;
	public String username;
	/**
	 * ǩ��
	 */
	public String sign;

	@Override
	public String toString() {
		return "UserInfo [id=" + id + ", account=" + uid + ", psw=" + psw
				+ ", username=" + username + ", sign=" + sign + "]";
	}

}
