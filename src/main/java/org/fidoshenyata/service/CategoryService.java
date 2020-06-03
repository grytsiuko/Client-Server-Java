package org.fidoshenyata.service;

import lombok.AllArgsConstructor;
import org.fidoshenyata.db.DAO.Dao;
import org.fidoshenyata.db.model.Category;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.exceptions.db.NameAlreadyTakenException;

import java.util.List;

@AllArgsConstructor
public class CategoryService {
    private final Dao<Category> dao;

    public Category getCategory(Integer id) throws NullPointerException{
        if(id == null) throw new NullPointerException();
        return dao.getEntity(id);
    }

    public List<Category> getCategoryByName(String name) throws NullPointerException{
        if(name == null) throw new NullPointerException();
        return dao.getEntityByName(name);
    }

    public List<Category> getCategories(PagingInfo pagingInfo) throws NullPointerException{
        if(pagingInfo == null) throw new NullPointerException();
        return dao.getEntities(pagingInfo);
    }

    public Integer getCount(){
        return dao.getCount();
    }

    public boolean addCategory(Category category) throws NameAlreadyTakenException, NullPointerException{
        if(category == null) throw new NullPointerException();
        return dao.insertEntity(category);
    }

    public boolean updateCategory(Category category) throws NameAlreadyTakenException, NullPointerException{
        if(category == null) throw new NullPointerException();
        return dao.updateEntity(category);
    }

    public boolean deleteCategory(Integer id) throws NullPointerException{
        if(id == null) throw new NullPointerException();
        return dao.deleteEntity(id);
    }
}
