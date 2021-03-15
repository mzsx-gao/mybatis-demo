package com.gbicc.mybatis.plugin;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.type.Alias;

//@Alias("PagePlugin")
public class PagePlugin implements Interceptor {

    private String name;
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return null;
    }
}
