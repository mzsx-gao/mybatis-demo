package com.enjoylearning.mybatis.decorator.decorators;

import com.enjoylearning.mybatis.decorator.Girl;

public class LovelyGirl implements Girl {
	
	private Girl girl;
	
	

	public LovelyGirl(Girl girl) {
		super();
		this.girl = girl;
	}



	@Override
	public void dance() {
		System.out.println("着装可爱……");
		girl.dance();

	}

}
