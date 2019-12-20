package com.gbicc.mybatis.test4;

import org.apache.ibatis.session.SqlSession;
import com.gbicc.mybatis.domain.Classes;
import com.gbicc.mybatis.util.SqlSessionFactoryUtils;


public class Test {
	@org.junit.Test
	public  void selectOne(){
		SqlSession session = SqlSessionFactoryUtils.getSqlSessionFactory().openSession();
		String stament="com.gbicc.mybatis.test4.classMapper.getClass";
//		stament="com.gbicc.mybatis.test4.classMapper.getClass2";
		Classes classes = session.selectOne(stament, 1);
		session.close();
		System.out.println(classes);
	}
	
}
