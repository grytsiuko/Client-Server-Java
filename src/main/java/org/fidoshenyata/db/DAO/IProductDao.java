package org.fidoshenyata.db.DAO;

import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.db.model.Product;
import org.fidoshenyata.exceptions.db.InternalSQLException;
import org.fidoshenyata.exceptions.db.NoSuchProductException;
import org.fidoshenyata.exceptions.db.NotEnoughProductException;

import java.math.BigDecimal;
import java.util.List;

public interface IProductDao extends Dao<Product> {

    List<Product> getEntities(Integer categoryId, PagingInfo pagingInfo)
            throws InternalSQLException;

    Integer getCount(Integer categoryId)
            throws InternalSQLException;

    List<Product> getEntitiesByName(Integer categoryId, String name)
            throws InternalSQLException;

    BigDecimal getCost()
            throws InternalSQLException;

    BigDecimal getCost(Integer categoryId)
            throws InternalSQLException;

    Boolean increaseAmount(Integer productId, Integer amount)
            throws NoSuchProductException, InternalSQLException;

    Boolean decreaseAmount(Integer productId, Integer amount)
            throws NoSuchProductException, NotEnoughProductException, InternalSQLException;
}
