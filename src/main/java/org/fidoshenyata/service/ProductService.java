package org.fidoshenyata.service;

import lombok.AllArgsConstructor;
import org.fidoshenyata.db.DAO.IProductDao;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.db.model.Product;
import org.fidoshenyata.exceptions.db.NameAlreadyTakenException;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
public class ProductService {
    private final IProductDao dao;

    public Product getProduct(Integer id) throws NullPointerException {
        if (id == null) throw new NullPointerException();
        return dao.getEntity(id);
    }

    public List<Product> getProducts(PagingInfo pagingInfo) throws NullPointerException {
        if (pagingInfo == null) throw new NullPointerException();
        return dao.getEntities(pagingInfo);
    }

    public List<Product> getProducts(Integer categoryId, PagingInfo pagingInfo) throws NullPointerException {
        if (categoryId == null || pagingInfo == null) throw new NullPointerException();
        return dao.getEntities(categoryId, pagingInfo);
    }

    public Integer getCount() {
        return dao.getCount();
    }

    public Integer getCount(Integer categoryId) throws NullPointerException {
        if (categoryId == null) throw new NullPointerException();
        return dao.getCount(categoryId);
    }

    public List<Product> getProductByName(String name) throws NullPointerException {
        if (name == null) throw new NullPointerException();
        return dao.getEntityByName(name);
    }

    public List<Product> getProductByName(Integer categoryId, String name) throws NullPointerException {
        if (categoryId == null || name == null) throw new NullPointerException();
        return dao.getEntityByName(categoryId, name);
    }

    public BigDecimal getCost() {
        return dao.getCost();
    }

    public BigDecimal getCost(Integer categoryId) throws NullPointerException {
        if (categoryId == null) throw new NullPointerException();
        return dao.getCost(categoryId);
    }

    public boolean addProduct(Product product) throws NameAlreadyTakenException, NullPointerException {
        if (product == null) throw new NullPointerException();
        return dao.insertEntity(product);
    }

    public boolean updateProduct(Product product) throws NameAlreadyTakenException, NullPointerException {
        if (product == null) throw new NullPointerException();
        return dao.updateEntity(product);
    }

    public boolean deleteEntity(Integer id) throws NullPointerException {
        if (id == null) throw new NullPointerException();
        return dao.deleteEntity(id);
    }

}
