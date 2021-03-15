package com.gbicc.mybatis.util;

import java.io.InputStream;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import com.gbicc.mybatis.test.QueryTest;

public class SqlSessionFactoryUtils {

	public static SqlSessionFactory getSqlSessionFactory(){
		InputStream is= QueryTest.class.getClassLoader().getResourceAsStream("conf.xml");
		SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(is);
		return sqlSessionFactory;
	}
	
}
