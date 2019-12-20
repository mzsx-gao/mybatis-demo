package com.gbicc.mybatis.test5;

import java.util.List;
import org.apache.ibatis.session.SqlSession;
import com.gbicc.mybatis.domain.ConditionUser;
import com.gbicc.mybatis.util.SqlSessionFactoryUtils;


public class Test {
	@org.junit.Test
	public  void selectList(){
		SqlSession session = SqlSessionFactoryUtils.getSqlSessionFactory().openSession();
		String stament="com.gbicc.mybatis.test5.userMapper.getUser";
		List<Object> list = session.selectList(stament, new ConditionUser("%a%",13,19));
		session.close();
		System.out.println(list);
	}
	
}
