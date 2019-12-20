package com.enjoylearning.mybatis.mapper;

import com.enjoylearning.mybatis.entity.TRole;

import java.util.List;

public interface TRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TRole record);

    int insertSelective(TRole record);

    TRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TRole record);

    int updateByPrimaryKey(TRole record);
    
    List<TRole> selectRoleandUsers();
}