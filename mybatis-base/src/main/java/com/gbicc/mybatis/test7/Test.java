package com.gbicc.mybatis.test7;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.gbicc.mybatis.domain.CUser;
import com.gbicc.mybatis.util.SqlSessionFactoryUtils;



public class Test {
	public static void main(String[] args) {
		/**
		 * 测试Mybatis的一级缓存
		 */
		/*SqlSession session=SqlSessionFactoryUtils.getSqlSessionFactory().openSession();
		String statement="com.gbicc.mybatis.test7.userMapper.getUser";
		CUser cuser=session.selectOne(statement, 1);
		System.out.println(cuser);
		//一级缓存默认就会被使用(不会再查一遍)
		CUser cuser2=session.selectOne(statement,1);
		System.out.println(cuser2);
		//查询数据条件变化，重新从数据库里查找
		CUser cuser3=session.selectOne(statement,2);
		System.out.println(cuser3);
		//执行了session.clearCache()操作，清理缓存(重新从数据库里查找)
		session.clearCache();
		CUser cuser4=session.selectOne(statement,2);
		System.out.println(cuser4);
		//执行过增删改操作后(重新从数据库里查找)
		session.update("com.gbicc.mybatis.test7.userMapper.updateUser",new CUser(2, "user", 23));
		CUser cuser5 = session.selectOne(statement, 2);
		System.out.println(cuser5);*/


		/**
		 * 测试Mybatis的二级缓存
		 */
		SqlSessionFactory factory=SqlSessionFactoryUtils.getSqlSessionFactory();
		SqlSession session=factory.openSession();
		String statement = "com.gbicc.mybatis.test7.userMapper.getUser";
		CUser user = session.selectOne(statement, 1);
		session.commit();
		System.out.println(user);
		
		SqlSession session2=factory.openSession();
		user = session2.selectOne(statement, 1);
		session2.commit();
		System.out.println(user);

	}
	
}
