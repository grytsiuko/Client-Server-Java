package org.fidoshenyata.db.DAO;

import org.fidoshenyata.exceptions.db.NameAlreadyTakenException;

import java.util.List;

public interface Dao<T> {
    T getEntity(Integer id);
    List<T> getEntities(Integer offset, Integer limit);
    Integer getCount();
    boolean insertEntity(T entity) throws NameAlreadyTakenException;
    boolean updateEntity(T entity) throws NameAlreadyTakenException;
    boolean deleteEntity(Integer id);
}
