package mybatis.plugin.core;

import mybatis.plugin.annotation.Ignore;
import mybatis.plugin.annotation.TableField;
import mybatis.plugin.annotation.TableId;
import mybatis.plugin.annotation.TableName;
import mybatis.plugin.constants.IdType;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.session.SqlSessionFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public abstract class AbstractBuilder implements DynamicSql {
    @Override
    public void handler(SqlSessionFactory sqlSessionFactory, Class genericClazz, Class<?> intfClass) {
        if(!sqlSessionFactory.getConfiguration().hasResultMap(getResultId(genericClazz,intfClass))) {
            resultMap(sqlSessionFactory,genericClazz,intfClass);
        }
        mappedStatement(sqlSessionFactory, genericClazz, intfClass);
        if (!sqlSessionFactory.getConfiguration().hasMapper(intfClass)) {
            sqlSessionFactory.getConfiguration().addMapper(intfClass);
        }
    }

    public String getResultId(Class genericClazz, Class<?> intfClass) {
        return intfClass.getCanonicalName() + "." + genericClazz.getSimpleName() + "dynamic";
    }

    private Class<?> resolveResultJavaType(Class<?> resultType, String property, Class<?> javaType, SqlSessionFactory sqlSessionFactory) {
        if (javaType == null && property != null) {
            try {
                MetaClass metaResultType = MetaClass.forClass(resultType, sqlSessionFactory.getConfiguration().getReflectorFactory());
                javaType = metaResultType.getSetterType(property);
            } catch (Exception e) {
                //ignore, following null check statement will deal with the situation
            }
        }
        if (javaType == null) {
            javaType = Object.class;
        }
        return javaType;
    }

    private ResultMap resultMap(SqlSessionFactory sqlSessionFactory, Class clazz, Class<?> intfClass) {
        List<ResultMapping> resultMappings = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Ignore.class)) {
                continue;
            }
            List<ResultFlag> flags = new ArrayList<>();
            if (field.isAnnotationPresent(TableId.class)) {
                flags.add(ResultFlag.ID);
            }
            resultMappings.add(new ResultMapping.Builder(sqlSessionFactory.getConfiguration(), field.getName(), getTableColumn(field), resolveResultJavaType(clazz,field.getName(),null,sqlSessionFactory))
                    .jdbcType(null)
                    .nestedQueryId(null)
                    .nestedResultMapId(null)
                    .resultSet(null)
                    .typeHandler(null)
                    .flags(flags == null ? new ArrayList<>() : flags)
                    .composites(Collections.emptyList())
                    .notNullColumns(new HashSet<>())
                    .columnPrefix(null)
                    .foreignColumn(null)
                    .lazy(false)
                    .build());
        }
        String id = getResultId(clazz,intfClass);
        ResultMap resultMap = new ResultMap.Builder(sqlSessionFactory.getConfiguration(), id, clazz, resultMappings, null)
                .discriminator(null)
                .build();
        sqlSessionFactory.getConfiguration().addResultMap(resultMap);
        return resultMap;
    }

    public MappedStatement mappedStatement(SqlSessionFactory sqlSessionFactory, Class genericClazz, Class<?> intfClass) {
        boolean isAuto = false;
        String keyProperty = "";
        for (Field field : genericClazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(TableId.class)) {
                TableId annotation = field.getAnnotation(TableId.class);
                IdType type = annotation.type();
                isAuto = type == IdType.AUTO;
                keyProperty = field.getName();
                break;
            }
        }
        MappedStatement.Builder statementBuilder = new MappedStatement.Builder(sqlSessionFactory.getConfiguration(), getId(intfClass), sqlSource(sqlSessionFactory, genericClazz), getSqlCommandType())
                .resource(null)
                .fetchSize(null)
                .timeout(null)
                .statementType(StatementType.PREPARED)
                .keyGenerator(isAuto ? Jdbc3KeyGenerator.INSTANCE : NoKeyGenerator.INSTANCE)
                .keyProperty(isAuto ? keyProperty : null)
                .keyColumn(null)
                .databaseId(null)
                .lang(sqlSessionFactory.getConfiguration().getLanguageDriver(null))
                .resultOrdered(false)
                .resultSets(null)
                .resultMaps(getResultMaps(sqlSessionFactory,genericClazz,intfClass))
                .resultSetType(null)
                .flushCacheRequired(true)
                .useCache(false)
                .cache(null);
        MappedStatement statement = statementBuilder.build();
        sqlSessionFactory.getConfiguration().addMappedStatement(statement);
        return statement;
    }

    public String getTableName(Class aClass) {
        TableName tableAn = (TableName) aClass.getAnnotation(TableName.class);
        if (tableAn != null) {
            return tableAn.value();
        }
        return aClass.getSimpleName();
    }

    protected String getTableColumn(Field field) {
        String idColumnName = handlerIdColumn(field);
        if (StringUtils.isNotEmpty(idColumnName)) {
            return idColumnName;
        }
        TableField tableFieldAn = field.getAnnotation(TableField.class);
        if (tableFieldAn != null) {
            return tableFieldAn.value();
        }
        return field.getName();
    }

    protected String handlerIdColumn(Field field) {
        String columnName = "";
        TableId tableIdAn = field.getAnnotation(TableId.class);
        if (tableIdAn == null) {
            return columnName;
        }
        switch (tableIdAn.type()) {
            case AUTO:
                break;
            default:
                columnName = StringUtils.isNotEmpty(tableIdAn.value()) ? tableIdAn.value() : field.getName();
        }
        return columnName;
    }

    protected String getTableFields(Class aClass) {
        String fieldsStr = "(";
        for (Field field : aClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Ignore.class)) {
                continue;
            }
            fieldsStr += "#{" + field.getName() + "},";
        }
        fieldsStr = fieldsStr.substring(0, fieldsStr.lastIndexOf(",")) + ")";
        return fieldsStr;
    }

    protected String getTableColumns(Class aClass) {
        String fieldsStr = "(";
        for (Field field : aClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Ignore.class)) {
                continue;
            }
            fieldsStr += getTableColumn(field) + ",";
        }
        fieldsStr = fieldsStr.substring(0, fieldsStr.lastIndexOf(",")) + ")";
        return fieldsStr;
    }

    protected String getQTableColumns(Class aClass) {
        String fieldsStr = "";
        for (Field field : aClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Ignore.class)) {
                continue;
            }
            fieldsStr += getTableColumn(field) + ",";
        }
        fieldsStr = fieldsStr.substring(0, fieldsStr.lastIndexOf(","));
        return fieldsStr;
    }

    public abstract String getId(Class intfClass);
    public abstract SqlCommandType getSqlCommandType();
    public abstract SqlSource sqlSource(SqlSessionFactory sqlSessionFactory, Class genericClazz);
    public abstract List<ResultMap> getResultMaps(SqlSessionFactory sqlSessionFactory, Class genericClazz, Class<?> intfClass);
}
