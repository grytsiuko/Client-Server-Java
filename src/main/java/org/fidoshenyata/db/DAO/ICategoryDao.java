package org.fidoshenyata.db.DAO;

import org.fidoshenyata.db.model.Category;
import org.fidoshenyata.db.model.NamedId;
import org.fidoshenyata.exceptions.db.InternalSQLException;

import java.util.List;

public interface ICategoryDao extends Dao<Category>{

    List<NamedId> getEntities()
            throws InternalSQLException;
}
