package com.mybatis.test;

import com.gao.mybatis.service.ITestServcice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-mybatis.xml"})
public class TestService {

    @Autowired
    private ITestServcice testServciceImpl;

    @Test
    public void test(){
        testServciceImpl.test();
    }

}