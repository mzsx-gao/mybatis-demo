package mybatis.plugin.annotation;


import mybatis.plugin.constants.IdType;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableId {
    String value() default "";

    IdType type() default IdType.NONE;
}
