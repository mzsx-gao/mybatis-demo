package com.enjoylearning.mybatis.decorator.extend;

import com.enjoylearning.mybatis.decorator.WangMeiLi;

public class LovelyPureSexWangMeiLi extends WangMeiLi {
	
	@Override
	public void dance() {
		System.out.println("着装可爱……");
		System.out.println("着装清纯……");
		System.out.println("着装性感……");
		super.dance();

	}

}
