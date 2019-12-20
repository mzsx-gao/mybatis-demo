package com.enjoylearning.mybatis.decorator.extend;

import com.enjoylearning.mybatis.decorator.WangMeiLi;

public class LovelyWangMeiLi extends WangMeiLi {
	
	@Override
	public void dance() {
		System.out.println("着装可爱……");
		super.dance();

	}

}
