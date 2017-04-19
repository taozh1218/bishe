package com.plus.cloudcontacts.bean;

import java.util.Date;
import java.util.List;

public class Group {
	private String groupId;
	private String groupName;
	private String ownerId;
	private String time;
	private List<Contact> members;
	
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public List<Contact> getMembers() {
		return members;
	}
	public void setMembers(List<Contact> members) {
		this.members = members;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
