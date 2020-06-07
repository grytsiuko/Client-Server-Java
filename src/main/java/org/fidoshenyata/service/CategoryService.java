package org.fidoshenyata.service;

import lombok.AllArgsConstructor;
import org.fidoshenyata.db.DAO.Dao;
import org.fidoshenyata.db.model.Category;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.exceptions.db.*;

import java.util.List;

@AllArgsConstructor
public class CategoryService {
    private final Dao<Category> dao;

    public Category getCategory(Integer id) throws InternalSQLException, NoEntityWithSuchIdException {
        if (id == null) throw new NullPointerException();
        return dao.getEntity(id);
    }

    public List<Category> getCategoriesByName(String name) throws InternalSQLException {
        if (name == null) throw new NullPointerException();
        return dao.getEntitiesByName(name);
    }

    public List<Category> getCategories(PagingInfo pagingInfo) throws InternalSQLException {
        if (pagingInfo == null) throw new NullPointerException();
        return dao.getEntities(pagingInfo);
    }

    public Integer getCount() throws InternalSQLException {
        return dao.getCount();
    }

    public boolean addCategory(Category category)
            throws NameAlreadyTakenException, InternalSQLException, IllegalFieldException, CategoryNotExistsException {
        if (category == null) throw new NullPointerException();
        assertCategory(category);

        return dao.insertEntity(category);
    }

    public boolean updateCategory(Category category)
            throws NameAlreadyTakenException, InternalSQLException, IllegalFieldException, CategoryNotExistsException {
        if (category == null) throw new NullPointerException();
        assertCategory(category);

        return dao.updateEntity(category);
    }

    public boolean deleteCategory(Integer id) throws InternalSQLException {
        if (id == null) throw new NullPointerException();
        return dao.deleteEntity(id);
    }

    public boolean deleteAllEntities() throws InternalSQLException {
        return dao.deleteAll();
    }

    private void assertCategory(Category category) throws IllegalFieldException {
        if (category.getDescription() != null && category.getDescription().length() > Category.MAX_DESCRIPTION_LENGTH)
            throw new IllegalFieldException("Too long description");
        if (category.getName() != null && category.getName().length() > Category.MAX_NAME_LENGTH)
            throw new IllegalFieldException("Too long name");
    }
}
