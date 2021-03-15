package com.gbicc.mybatis.test;

import com.gbicc.mybatis.entity.ConditionUser;
import org.apache.ibatis.session.SqlSession;
import com.gbicc.mybatis.entity.Classes;
import com.gbicc.mybatis.util.SqlSessionFactoryUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QueryTest {
	@Test
	public  void test1(){
		SqlSession session = SqlSessionFactoryUtils.getSqlSessionFactory().openSession();
		String stament="com.gbicc.mybatis.test3.classMapper.getClass";
		stament="com.gbicc.mybatis.test3.classMapper.getClass2";
		Classes classes = session.selectOne(stament, 1);
		session.close();
		System.out.println(classes);
	}

	@Test
	public  void test2(){
		SqlSession session = SqlSessionFactoryUtils.getSqlSessionFactory().openSession();
		String stament="com.gbicc.mybatis.test4.classMapper.getClass";
//		stament="com.gbicc.mybatis.test4.classMapper.getClass2";
		Classes classes = session.selectOne(stament, 1);
		session.close();
		System.out.println(classes);
	}

	@Test
	public  void test3(){
		SqlSession session = SqlSessionFactoryUtils.getSqlSessionFactory().openSession();
		String stament="com.gbicc.mybatis.test5.userMapper.getUser";
		List<Object> list = session.selectList(stament, new ConditionUser("%a%",13,19));
		session.close();
		System.out.println(list);
	}

	@Test
	public  void test4(){
		SqlSession session = SqlSessionFactoryUtils.getSqlSessionFactory().openSession();
		Map<String, Integer> paramMap = new HashMap<>();
		paramMap.put("sex_id", 0);
		paramMap.put("user_count",-1);
		String statement="com.gbicc.mybatis.test6.userMapper.getCount";
		session.selectOne(statement, paramMap);
		Integer userCount = paramMap.get("user_count");
		System.out.println(userCount);

		session.close();
	}
	
}
