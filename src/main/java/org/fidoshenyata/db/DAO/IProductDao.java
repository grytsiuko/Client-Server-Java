package org.fidoshenyata.db.DAO;

import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.db.model.Product;
import org.fidoshenyata.exceptions.db.NoSuchProductException;
import org.fidoshenyata.exceptions.db.NotEnoughProductException;

import java.math.BigDecimal;
import java.util.List;

public interface IProductDao extends Dao<Product> {
    List<Product> getEntities(Integer categoryId,PagingInfo pagingInfo);
    Integer getCount(Integer categoryId);
    List<Product> getEntitiesByName(Integer categoryId, String name);
    BigDecimal getCost();
    BigDecimal getCost(Integer categoryId);
    Boolean increaseAmount(Integer productId, Integer amount) throws NoSuchProductException;
    Boolean decreaseAmount(Integer productId, Integer amount) throws NoSuchProductException, NotEnoughProductException;
}
