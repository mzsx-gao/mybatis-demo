package mybatis.plugin.core;

import mybatis.plugin.base.BaseDao;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.session.SqlSessionFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ServiceLoader;
import java.util.Set;

public class DynamicSqlByBean {

    public static void main(String[] args) {
        new DynamicSqlByBean().scanClass(null, "cn.enjoy");
    }

    public void enable(SqlSessionFactory sqlSessionFactory, String packageName) {
        scanClass(sqlSessionFactory, packageName);
    }

    private void scanClass(SqlSessionFactory sqlSessionFactory, String packageName) {
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
        resolverUtil.find(new ResolverUtil.IsA(BaseDao.class), packageName);
        Set<Class<? extends Class<?>>> handlerSet = resolverUtil.getClasses();
        ServiceLoader<DynamicSql> load = ServiceLoader.load(DynamicSql.class);
        for (Class<? extends Class<?>> aClass : handlerSet) {
            if (aClass.getSimpleName().equalsIgnoreCase("BaseDao")) {
                continue;
            }
            Class genericType = handlerIntfGenericType(aClass);
            for (DynamicSql dynamicSql : load) {
                dynamicSql.handler(sqlSessionFactory,genericType,aClass);
            }
        }
    }

    private Class handlerIntfGenericType(Class clazz) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (((ParameterizedTypeImpl) genericInterface).getRawType().isAssignableFrom(BaseDao.class)) {
                ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                Type[] types = parameterizedType.getActualTypeArguments();
                return (Class<?>) types[0];
            }
        }
        return null;
    }
}