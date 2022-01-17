package com.gao.mybatis.springboot.mapper;

import com.gao.mybatis.springboot.entity.TUser;

import java.util.List;
import java.util.Map;

public interface TUserMapper {

    List<TUser> selectByEmailAndSex1(Map<String, Object> param);

}