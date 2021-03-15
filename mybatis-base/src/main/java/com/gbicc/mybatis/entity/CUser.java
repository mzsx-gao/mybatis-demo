package com.gbicc.mybatis.entity;

import java.io.Serializable;

public class CUser implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public CUser(int id, String name, int age) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
	}
	public CUser(){
		super();
	}
	private int id;
	private String name;
	private int age;
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
	
	public int getAge() {
		return age;
	}
	public void setSex(int age) {
		this.age = age;
	}
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", age=" + age + "]";
	}
	
	
	
}
