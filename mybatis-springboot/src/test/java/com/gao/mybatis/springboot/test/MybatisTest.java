package com.gao.mybatis.springboot.test;

import com.gao.mybatis.springboot.entity.TUser;
import com.gao.mybatis.springboot.mapper.TUserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 名称: MybatisTest
 * 描述: 测试类
 *
 * @author gaoshudian
 * @date 1/17/22 5:19 PM
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisTest {

    @Autowired
    TUserMapper userMapper;

    @Test
    public void testManyParamQuery() {

        String email = "qq.com";
        Byte sex = 1;

        // 第一种方式使用map
		Map<String, Object> params = new HashMap<>();
		params.put("email", email);
		params.put("sex", sex);
		List<TUser> list1 = userMapper.selectByEmailAndSex1(params);
		System.out.println(list1.size());

    }
}
