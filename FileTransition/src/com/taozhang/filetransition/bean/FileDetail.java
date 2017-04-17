package com.taozhang.filetransition.bean;

/**
 * ���ļ��������л�ȡ���ļ���Ϣ
 * @author taozhang
 *
 */
public class FileDetail {
	
	/**
	 * �ļ���(�����ļ�����)
	 * 
	 * ��ͨ��getName()��ȡ
	 */
	private String name;
	/**
	 * �ļ�����
	 */
	private String type;
	/**
	 * ����·��
	 */
	private String path;
	public FileDetail() {
		super();
	}
	public FileDetail(String name, String type, String path) {
		super();
		this.name = name;
		this.type = type;
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@Override
	public String toString() {
		return "FileDetail [name=" + name + ", type=" + type + ", path=" + path
				+ "]";
	}

}
