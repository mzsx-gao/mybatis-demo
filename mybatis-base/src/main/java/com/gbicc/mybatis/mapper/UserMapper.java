package com.gbicc.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gbicc.mybatis.entity.CUser;
import com.gbicc.mybatis.entity.User;

public interface UserMapper {

	@Insert("insert into users(name,age) values(#{name},#{age})")
	public int add(CUser user);
	@Delete("delete from users where id=#{id}")
	public int deleteById(int id);
	@Update("update users set name=#{name},age=#{age} where id=#{id}")
	public int update(User user);
	@Select("select * from users where id=#{id}")
	public int getById(int id);
	@Select("select * from users")
	public List<User> getAllUsers();
}
