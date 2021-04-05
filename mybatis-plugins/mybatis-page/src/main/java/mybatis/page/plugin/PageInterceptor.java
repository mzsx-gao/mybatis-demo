package mybatis.page.plugin;

import lombok.extern.slf4j.Slf4j;
import mybatis.page.PageUtil;
import mybatis.page.bean.Page;
import mybatis.page.parse.CountSqlParser;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 分页插件
 */
@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class,
        ResultHandler.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class,
        ResultHandler.class, CacheKey.class, BoundSql.class})
})
@Slf4j
public class PageInterceptor implements Interceptor {

    private static String PAGE_NUM = "PAGE_NUM";
    private static String PAGE_SIZE = "PAGE_SIZE";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Page<Object> localPage = PageUtil.getLocalPage();
        if (localPage == null) {
            return invocation.proceed();
        }
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        //1、先查询总数
        Long count = count(invocation, boundSql);
        if (count <= 0) {
            return new ArrayList<>();
        }
        localPage.setTotal(count);

        //2、分页查询
        List<Object> list = pageQuery(invocation, boundSql);
        if (list != null) {
            localPage.addAll(list);
        }
        return localPage;
    }

    /**
     * 查询总数
     * sql示例:SELECT count(0) FROM t_user a WHERE a.email LIKE CONCAT('%', ?, '%') AND a.sex = ?
     */
    private Long count(Invocation invocation, BoundSql boundSql) {
        log.info("查询总数......");
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        RowBounds rowBounds = (RowBounds) invocation.getArgs()[2];
        ResultHandler resultHandler = (ResultHandler) invocation.getArgs()[3];
        Executor executor = (Executor) invocation.getTarget();
        CacheKey cacheKey = executor.createCacheKey(mappedStatement, parameter, RowBounds.DEFAULT, boundSql);
        CountSqlParser countSqlParser = new CountSqlParser();
        String countSql = countSqlParser.getSmartCountSql(boundSql.getSql());
        //创建count查询的新BoundSql对象
        BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql,
            boundSql.getParameterMappings(), parameter);
        try {
            //添加count BoundSql的实参，就是从查询中来的
            boundSqlAdditionalParameterCopy(countBoundSql, boundSql);
            List<Object> results = executor.query(createCountMappedStatement(mappedStatement), parameter, rowBounds,
                resultHandler, cacheKey, countBoundSql);
            return (Long) results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 分页查询
     * select * from t_user a where a.email like CONCAT('%', ?, '%') and a.sex = ? limit ?,?
     */
    private <E> List<E> pageQuery(Invocation invocation, BoundSql boundSql) {
        log.info("分页查询开始......");
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        RowBounds rowBounds = (RowBounds) invocation.getArgs()[2];
        ResultHandler resultHandler = (ResultHandler) invocation.getArgs()[3];
        Executor executor = (Executor) invocation.getTarget();
        CacheKey cacheKey = executor.createCacheKey(mappedStatement, parameter, rowBounds,
            mappedStatement.getBoundSql(parameter));
        //修改parameterMappings加两个参数(pageNum和pageSize)
        createParamMappingToBoundSql(mappedStatement, boundSql, cacheKey);
        //修改parameterObject加两个参数值（pageNum和pageSize）
        createParamObjectToBoundSql(mappedStatement, boundSql);
        //修改boundSql，加上limit ?,?
        String pageSql = getPageSql(boundSql);
        //创建新的BoundSql对象
        BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql,
            boundSql.getParameterMappings(), boundSql.getParameterObject());
        List<E> result = null;
        try {
            boundSqlAdditionalParameterCopy(newBoundSql, boundSql);
            result = executor.query(mappedStatement, parameter, rowBounds, resultHandler, cacheKey, newBoundSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private MappedStatement createCountMappedStatement(MappedStatement ms) {
        //修改MappedStatement的id
        MappedStatement.Builder statementBuilder = new MappedStatement.Builder(ms.getConfiguration(),
            ms.getId() + "_COUNT", ms.getSqlSource(), ms.getSqlCommandType())
            .resource(ms.getResource())
            .fetchSize(ms.getFetchSize())
            .timeout(ms.getTimeout())
            .statementType(ms.getStatementType())
            .keyGenerator(ms.getKeyGenerator())
            .databaseId(ms.getDatabaseId())
            .lang(ms.getLang())
            .resultSetType(ms.getResultSetType())
            .flushCacheRequired(ms.isFlushCacheRequired())
            .useCache(ms.isUseCache());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            statementBuilder.keyProperty(keyProperties.toString());
        }
        List<ResultMap> resultMaps = new ArrayList<>();
        //修改resultMap的type属性为long
        ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), ms.getId(), Long.class, new ArrayList<>()).build();
        resultMaps.add(resultMap);
        statementBuilder.resultMaps(resultMaps);
        MappedStatement statement = statementBuilder.build();
        return statement;
    }

    private void boundSqlAdditionalParameterCopy(BoundSql newBoundSql, BoundSql oldBoundSql)
        throws NoSuchFieldException, IllegalAccessException {
        Field additionalParametersF = oldBoundSql.getClass().getDeclaredField("additionalParameters");
        additionalParametersF.setAccessible(true);
        Map<String, Object> additionalParameters = (Map<String, Object>) additionalParametersF.get(oldBoundSql);
        for (String key : additionalParameters.keySet()) {
            newBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
        }
    }

    private void createParamMappingToBoundSql(MappedStatement mappedStatement, BoundSql boundSql, CacheKey cacheKey) {
        Page page = PageUtil.getLocalPage();
        cacheKey.update(page.getStartRow());
        cacheKey.update(page.getPageSize());
        //parameterMappings加两个参数
        if (boundSql.getParameterMappings() != null) {
            boundSql.getParameterMappings().add(
                new ParameterMapping.Builder(mappedStatement.getConfiguration(), PAGE_NUM, Integer.class).build());
            boundSql.getParameterMappings().add(
                new ParameterMapping.Builder(mappedStatement.getConfiguration(), PAGE_SIZE, Integer.class).build());
        }
    }

    private void createParamObjectToBoundSql(MappedStatement mappedStatement, BoundSql boundSql) {
        Page page = PageUtil.getLocalPage();
        Object parameterObject = boundSql.getParameterObject();
        MetaObject metaObject = mappedStatement.getConfiguration().newMetaObject(parameterObject);
        metaObject.setValue(PAGE_NUM, page.getStartRow());
        metaObject.setValue(PAGE_SIZE, page.getPageSize());
    }

    private String getPageSql(BoundSql boundSql) {
        String sql = boundSql.getSql();
        sql += " limit ?,?";
        return sql;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}