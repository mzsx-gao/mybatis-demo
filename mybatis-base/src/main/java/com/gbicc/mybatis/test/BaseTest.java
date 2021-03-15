package com.gbicc.mybatis.test;

import com.gbicc.mybatis.entity.CUser;
import com.gbicc.mybatis.entity.User;
import com.gbicc.mybatis.mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class BaseTest {
	@Test
	public  void selectOne(){
//		Reader reader=Resources.getResourceAsReader("conf.xml");
//		SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(reader);
		InputStream is= BaseTest.class.getClassLoader().getResourceAsStream("conf.xml");
		SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(is);
		SqlSession session=sqlSessionFactory.openSession();
		String stament="com.gbicc.mybatis.test1.userMapper.getUser";
		User user=session.selectOne(stament, 2);
		session.close();
		System.out.println(user);
	}
	@Test
	public void testAdd(){
		InputStream is= BaseTest.class.getClassLoader().getResourceAsStream("conf.xml");
		SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(is);
		SqlSession session=sqlSessionFactory.openSession(true);
		String stament="com.gbicc.mybatis.test1.userMapper.addUser";
		int insert=session.insert(stament, new CUser(-1, "高书电", 23));
		session.close();
		System.out.println(insert);
	}
	@Test
	public void testUpdate(){
		InputStream is= BaseTest.class.getClassLoader().getResourceAsStream("conf.xml");
		SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(is);
		SqlSession session=sqlSessionFactory.openSession(true);
		String stament="com.gbicc.mybatis.test1.userMapper.updateUser";
		int update=session.update(stament, new CUser(2, "书电2", 24));
		session.close();
		System.out.println(update);
	}
    @Test
	public void testDelete(){
		InputStream is= BaseTest.class.getClassLoader().getResourceAsStream("conf.xml");
		SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(is);
		SqlSession session=sqlSessionFactory.openSession(true);
		String stament="com.gbicc.mybatis.test1.userMapper.deleteUser";
		int delete=session.delete(stament, 3);
		session.close();
		System.out.println(delete);
	}
    @Test
	public void selectAll(){
		InputStream is= BaseTest.class.getClassLoader().getResourceAsStream("conf.xml");
		SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(is);
		SqlSession session=sqlSessionFactory.openSession(true);
		String stament="com.gbicc.mybatis.test1.userMapper.getAllUsers";
		List<User> list = session.selectList(stament);
		session.close();
		System.out.println(list);
	}
    @Test
	public void testAdd2(){
		InputStream is= BaseTest.class.getClassLoader().getResourceAsStream("conf.xml");
		SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(is);
		SqlSession session=sqlSessionFactory.openSession(true);
		UserMapper userMapper = session.getMapper(UserMapper.class);
		int add=userMapper.add(new CUser(-1,"gaoshudian",23));
		session.close();
		System.out.println(add);
	}
	
}
