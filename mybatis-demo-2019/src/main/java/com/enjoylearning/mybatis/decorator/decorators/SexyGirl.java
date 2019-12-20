package com.enjoylearning.mybatis.decorator.decorators;

import com.enjoylearning.mybatis.decorator.Girl;

public class SexyGirl implements Girl {
	
	private Girl girl;
	
	

	public SexyGirl(Girl girl) {
		super();
		this.girl = girl;
	}



	@Override
	public void dance() {
		System.out.println("着装性感……");
		girl.dance();

	}

}
