package enjoy.plugin;

import enjoy.PageUtil;
import enjoy.bean.Page;
import enjoy.parse.CountSqlParser;
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

@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class,
        ResultHandler.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class,
        ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class PageInterceptor implements Interceptor {

    private static String pageparam_1 = "PAGEPARAM_1";
    private static String pageparam_2 = "PAGEPARAM_2";

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
     * select * from zxx where xdf=? limit ?,?
     */
    private <E> List<E> pageQuery(Invocation invocation, BoundSql boundSql) {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        RowBounds rowBounds = (RowBounds) invocation.getArgs()[2];
        ResultHandler resultHandler = (ResultHandler) invocation.getArgs()[3];
        Executor executor = (Executor) invocation.getTarget();
        CacheKey cacheKey = executor.createCacheKey(mappedStatement, parameter, rowBounds,
            mappedStatement.getBoundSql(parameter));
        createParamMappingToBoundSql(mappedStatement, boundSql, cacheKey);
        createParamObjectToBoundSql(mappedStatement, boundSql);
        String pageSql = getPageSql(boundSql);
        //创建新的BoundSql对象
        BoundSql newboundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql,
            boundSql.getParameterMappings(), boundSql.getParameterObject());
        List<E> result = null;
        try {
            boundSqlAdditionalParameterCopy(newboundSql, boundSql);
            result = executor.query(mappedStatement, parameter, rowBounds, resultHandler, cacheKey, newboundSql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getPageSql(BoundSql boundSql) {
        String sql = boundSql.getSql();
        sql += " limit ?,?";
        return sql;
    }

    private void boundSqlAdditionalParameterCopy(BoundSql newBoundSql, BoundSql oldBoundSql) throws NoSuchFieldException, IllegalAccessException {
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
        if (boundSql.getParameterMappings() != null) {
            boundSql.getParameterMappings().add(new ParameterMapping.Builder(mappedStatement.getConfiguration(),
                pageparam_1, Integer.class).build());
            boundSql.getParameterMappings().add(new ParameterMapping.Builder(mappedStatement.getConfiguration(),
                pageparam_2, Integer.class).build());
        }
    }

    private void createParamObjectToBoundSql(MappedStatement mappedStatement, BoundSql boundSql) {
        Page page = PageUtil.getLocalPage();
        Object parameterObject = boundSql.getParameterObject();
        MetaObject metaObject = mappedStatement.getConfiguration().newMetaObject(parameterObject);
        metaObject.setValue(pageparam_1, page.getStartRow());
        metaObject.setValue(pageparam_2, page.getPageSize());
    }

    private Long count(Invocation invocation, BoundSql boundSql) {
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    private MappedStatement createCountMappedStatement(MappedStatement ms) {
        MappedStatement.Builder statementBuilder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId() +
            "_COUNT", ms.getSqlSource(), ms.getSqlCommandType())
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
        List<ResultMap> resultMaps = new ArrayList<ResultMap>();
        ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), ms.getId(), Long.class,
            new ArrayList<ResultMapping>()).build();
        resultMaps.add(resultMap);
        statementBuilder.resultMaps(resultMaps);
        MappedStatement statement = statementBuilder.build();
        return statement;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}