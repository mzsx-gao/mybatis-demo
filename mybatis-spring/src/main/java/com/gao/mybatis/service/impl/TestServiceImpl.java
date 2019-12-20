package com.gao.mybatis.service.impl;

import com.gao.mybatis.dao.TestMapper;
import com.gao.mybatis.entity.Test;
import com.gao.mybatis.service.ITestServcice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements ITestServcice {

    @Autowired
    private TestMapper testMapper;

    public void test() {
        Test test = testMapper.selectByPrimaryKey(1L);
        System.out.println(test);
    }
}