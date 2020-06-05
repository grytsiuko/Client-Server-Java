package org.fidoshenyata.service;

import com.sun.nio.sctp.IllegalReceiveException;
import lombok.AllArgsConstructor;
import org.fidoshenyata.db.DAO.IProductDao;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.db.model.Product;
import org.fidoshenyata.exceptions.db.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
public class ProductService {
    private final IProductDao dao;

    public Product getProduct(Integer id) throws InternalSQLException, NoEntityWithSuchIdException {
        if (id == null) throw new NullPointerException();
        return dao.getEntity(id);
    }

    public List<Product> getProducts(PagingInfo pagingInfo) throws InternalSQLException {
        if (pagingInfo == null) throw new NullPointerException();
        return dao.getEntities(pagingInfo);
    }

    public List<Product> getProducts(Integer categoryId, PagingInfo pagingInfo) throws InternalSQLException {
        if (categoryId == null || pagingInfo == null) throw new NullPointerException();
        return dao.getEntities(categoryId, pagingInfo);
    }

    public Integer getCount() throws InternalSQLException {
        return dao.getCount();
    }

    public Integer getCount(Integer categoryId) throws InternalSQLException {
        if (categoryId == null) throw new NullPointerException();
        return dao.getCount(categoryId);
    }

    public List<Product> getProductsByName(String name) throws InternalSQLException {
        if (name == null) throw new NullPointerException();
        return dao.getEntitiesByName(name);
    }

    public List<Product> getProductsByName(Integer categoryId, String name) throws InternalSQLException {
        if (categoryId == null || name == null) throw new NullPointerException();
        return dao.getEntitiesByName(categoryId, name);
    }

    public BigDecimal getCost() throws InternalSQLException {
        return dao.getCost();
    }

    public BigDecimal getCost(Integer categoryId) throws InternalSQLException {
        if (categoryId == null) throw new NullPointerException();
        return dao.getCost(categoryId);
    }

    public boolean addProduct(Product product)
            throws NameAlreadyTakenException, InternalSQLException, IllegalFieldException {

        if (product == null) throw new NullPointerException();
        if (product.getAmount() < 0)
            throw new IllegalFieldException("Negative starting amount");
        assertProduct(product);

        return dao.insertEntity(product);
    }

    public boolean updateProduct(Product product)
            throws NameAlreadyTakenException, InternalSQLException, IllegalFieldException {
        if (product == null) throw new NullPointerException();
        assertProduct(product);

        return dao.updateEntity(product);
    }

    public boolean deleteEntity(Integer id) throws InternalSQLException {
        if (id == null) throw new NullPointerException();
        return dao.deleteEntity(id);
    }

    public boolean increaseAmount(Integer id, Integer amount)
            throws NoSuchProductException, InternalSQLException, IllegalFieldException {

        if (id == null || amount == null) throw new NullPointerException();
        if (amount <= 0) throw new IllegalFieldException("Amount should be positive");

        return dao.increaseAmount(id, amount);
    }

    public boolean decreaseAmount(Integer id, Integer amount)
            throws NoSuchProductException, NotEnoughProductException, InternalSQLException, IllegalFieldException {

        if (id == null || amount == null) throw new NullPointerException();
        if (amount <= 0) throw new IllegalFieldException("Amount should be positive");

        return dao.decreaseAmount(id, amount);
    }

    public boolean deleteAllEntities() throws InternalSQLException {
        return dao.deleteAll();
    }

    private void assertProduct(Product product) throws IllegalFieldException {
        if (product.getDescription() != null && product.getDescription().length() > Product.MAX_DESCRIPTION_LENGTH)
            throw new IllegalFieldException("Too long description");
        if (product.getName() != null && product.getName().length() > Product.MAX_NAME_LENGTH)
            throw new IllegalFieldException("Too long name");
        if (product.getPrice().compareTo(new BigDecimal("0.00")) <= 0)
            throw new IllegalFieldException("Price should be positive");
        if (product.getProducer().length() > Product.MAX_PRODUCER_LENGTH)
            throw new IllegalFieldException("Too long producer");
    }

}
