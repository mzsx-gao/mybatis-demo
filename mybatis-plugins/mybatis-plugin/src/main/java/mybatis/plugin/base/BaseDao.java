package mybatis.plugin.base;

import java.util.List;

public interface BaseDao<T> {
    int dynamicInsert(T t);
    int dynamicUpdate(T t);
    List<T> dynamicSelect(T t);
    int dynamicDelete(T t);
}
