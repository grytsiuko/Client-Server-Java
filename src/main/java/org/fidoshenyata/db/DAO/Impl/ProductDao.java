package org.fidoshenyata.db.DAO.Impl;

import org.fidoshenyata.db.DAO.IProductDao;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.db.model.Product;
import org.fidoshenyata.exceptions.db.NameAlreadyTakenException;

import java.math.BigDecimal;
import java.util.List;

public class ProductDao implements IProductDao {
    @Override
    public List<Product> getEntities(Integer categoryId, PagingInfo pagingInfo) {
        return null;
    }

    @Override
    public Integer getCount(Integer categoryId) {
        return null;
    }

    @Override
    public Product getEntityByName(Integer categoryId, String name) {
        return null;
    }

    @Override
    public BigDecimal getCost() {
        return null;
    }

    @Override
    public BigDecimal getCost(Integer categoryId) {
        return null;
    }

    @Override
    public Product getEntity(Integer id) {
        return null;
    }

    @Override
    public Product getEntityByName(String name) {
        return null;
    }

    @Override
    public List<Product> getEntities(PagingInfo pagingInfo) {
        return null;
    }

    @Override
    public Integer getCount() {
        return null;
    }

    @Override
    public boolean insertEntity(Product entity) throws NameAlreadyTakenException {
        return false;
    }

    @Override
    public boolean updateEntity(Product entity) throws NameAlreadyTakenException {
        return false;
    }

    @Override
    public boolean deleteEntity(Integer id) {
        return false;
    }
}
