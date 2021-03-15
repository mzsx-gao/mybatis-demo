package com.gbicc.mybatis.entity;

public class User {

	
	public User(int id, String name, String sex) {
		super();
		this.id = id;
		this.name = name;
		this.sex = sex;
	}
	public User(){
		super();
	}
	private int id;
	private String name;
	private String sex;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", sex=" + sex + "]";
	}
	
	
	
}
