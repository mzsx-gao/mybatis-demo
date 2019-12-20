package com.enjoylearning.mybatis.decorator.extend;

import com.enjoylearning.mybatis.decorator.WangMeiLi;

public class PureWangMeiLi extends WangMeiLi {
	
	@Override
	public void dance() {
		System.out.println("着装清纯……");
		super.dance();

	}

}
