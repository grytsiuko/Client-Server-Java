package org.fidoshenyata.db.DAO;

import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.exceptions.db.CategoryNotExistsException;
import org.fidoshenyata.exceptions.db.InternalSQLException;
import org.fidoshenyata.exceptions.db.NameAlreadyTakenException;
import org.fidoshenyata.exceptions.db.NoEntityWithSuchIdException;

import java.util.List;

public interface Dao<T> {

    T getEntity(Integer id)
            throws NoEntityWithSuchIdException, InternalSQLException;

    List<T> getEntitiesByName(String name)
            throws InternalSQLException;

    List<T> getEntities(PagingInfo pagingInfo)
            throws InternalSQLException;

    Integer getCount()
            throws InternalSQLException;

    boolean insertEntity(T entity)
            throws NameAlreadyTakenException, InternalSQLException, CategoryNotExistsException;

    boolean updateEntity(T entity)
            throws NameAlreadyTakenException, InternalSQLException, CategoryNotExistsException;

    boolean deleteEntity(Integer id)
            throws InternalSQLException;

    boolean deleteAll()
            throws InternalSQLException;
}
