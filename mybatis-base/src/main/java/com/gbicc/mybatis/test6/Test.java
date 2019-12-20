package com.gbicc.mybatis.test6;

import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import com.gbicc.mybatis.util.SqlSessionFactoryUtils;


public class Test {
	@org.junit.Test
	public  void selectList(){
		SqlSession session = SqlSessionFactoryUtils.getSqlSessionFactory().openSession();
		Map<String, Integer> paramMap = new HashMap<String,Integer>();
		paramMap.put("sex_id", 0);
		paramMap.put("user_count",-1);
		String statement="com.gbicc.mybatis.test6.userMapper.getCount";
		session.selectOne(statement, paramMap);
		Integer userCount = paramMap.get("user_count");
		System.out.println(userCount);

		session.close();
	}
	
}
