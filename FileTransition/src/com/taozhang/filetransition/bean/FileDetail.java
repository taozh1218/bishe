package com.taozhang.filetransition.bean;

/**
 * 从文件管理器中获取的文件信息
 * @author taozhang
 *
 */
public class FileDetail {
	
	/**
	 * 文件名(包含文件类型)
	 * 
	 * 即通过getName()获取
	 */
	private String name;
	/**
	 * 文件类型
	 */
	private String type;
	/**
	 * 绝对路径
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
