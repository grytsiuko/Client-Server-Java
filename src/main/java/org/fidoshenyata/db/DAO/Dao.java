package org.fidoshenyata.db.DAO;

import java.util.Set;

public interface Dao<T> {
    T getEntity(Integer id);
    Set<T> getEntities(Integer offset, Integer limit);
    Integer getCount();
    boolean insertEntity(T entity);
    boolean updateEntity(T entity);
    boolean deleteEntity(Integer id);
}
