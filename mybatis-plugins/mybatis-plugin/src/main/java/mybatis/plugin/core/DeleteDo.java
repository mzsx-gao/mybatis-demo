package mybatis.plugin.core;

import mybatis.plugin.annotation.Ignore;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.*;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DeleteDo extends AbstractBuilder {
    @Override
    public String getId(Class intfClass) {
        return intfClass.getCanonicalName() + ".dynamicDelete";
    }

    @Override
    public SqlCommandType getSqlCommandType() {
        return SqlCommandType.DELETE;
    }

    private String getIfContent(Field field) {
        String ifContent = " and " + getTableColumn(field) + " = #{" + field.getName() + "}";
        return ifContent;
    }

    public SqlNode handlerIfMixedSqlNode(Class clazz) {
        List<SqlNode> contents = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Ignore.class)) {
                continue;
            }
            List<SqlNode> ifChildContents = new ArrayList<>();
            ifChildContents.add(new StaticTextSqlNode(getIfContent(field)));
            MixedSqlNode ifMixedSqlNode = new MixedSqlNode(ifChildContents);
            IfSqlNode ifSqlNode = new IfSqlNode(ifMixedSqlNode, field.getName() + " != null and " + field.getName() + " != ''");
            contents.add(ifSqlNode);
        }
        return new MixedSqlNode(contents);
    }

    private SqlNode getTrimSqlNode(SqlSessionFactory sqlSessionFactory, Class clazz) {
        SqlNode sqlNode = handlerIfMixedSqlNode(clazz);
        return new TrimSqlNode(sqlSessionFactory.getConfiguration(), sqlNode, "WHERE", "AND|OR", null, null);
    }

    public StringBuffer handlerSql(Class clazz) {
        StringBuffer sql = new StringBuffer("delete from ");
        sql.append(getTableName(clazz));
        return sql;
    }

    @Override
    public SqlSource sqlSource(SqlSessionFactory sqlSessionFactory, Class genericClazz) {
        StringBuffer sb = handlerSql(genericClazz);
        List<SqlNode> contents = new ArrayList<>();
        contents.add(new StaticTextSqlNode(sb.toString()));
        contents.add(getTrimSqlNode(sqlSessionFactory,genericClazz));
        MixedSqlNode mixedSqlNode = new MixedSqlNode(contents);
        SqlSource sqlSource = new DynamicSqlSource(sqlSessionFactory.getConfiguration(), mixedSqlNode);
        return sqlSource;
    }

    @Override
    public List<ResultMap> getResultMaps(SqlSessionFactory sqlSessionFactory, Class genericClazz, Class<?> intfClass) {
        return new ArrayList<>();
    }
}
