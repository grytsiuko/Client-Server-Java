package org.fidoshenyata.db.DAO;

import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.exceptions.db.NameAlreadyTakenException;

import java.util.List;

public interface Dao<T> {
    T getEntity(Integer id);
    List<T> getEntitiesByName(String name);
    List<T> getEntities(PagingInfo pagingInfo);
//    Integer getCount();
    boolean insertEntity(T entity) throws NameAlreadyTakenException;
    boolean updateEntity(T entity) throws NameAlreadyTakenException;
    boolean deleteEntity(Integer id);
    boolean deleteAll();
}
