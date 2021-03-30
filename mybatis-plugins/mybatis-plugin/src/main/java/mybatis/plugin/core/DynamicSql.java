package mybatis.plugin.core;

import org.apache.ibatis.session.SqlSessionFactory;

public interface DynamicSql {
    public void handler(SqlSessionFactory sqlSessionFactory, Class genericClazz, Class<?> intfClass);
}
