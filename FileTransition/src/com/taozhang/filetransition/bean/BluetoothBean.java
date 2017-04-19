package com.taozhang.filetransition.bean;

public class BluetoothBean {
	private String name;
	private String address;

	public BluetoothBean() {
		super();
	}

	public BluetoothBean(String name, String address) {
		super();
		this.name = name;
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "BluetoothBean [name=" + name + ", address=" + address + "]";
	}

}
