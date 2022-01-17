package com.gao.mybatis.springboot.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 名称: MybatisConfig
 * 描述: mybatis配置类
 *
 * @author gaoshudian
 * @date 1/17/22 5:18 PM
 */

@MapperScan("com.gao.mybatis.springboot.mapper")
@Configuration
public class MybatisConfig {
}
