package org.fidoshenyata.db.DAO;

import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.db.model.Product;

import java.math.BigDecimal;
import java.util.List;

public interface IProductDao extends Dao<Product> {
    List<Product> getEntities(Integer categoryId,PagingInfo pagingInfo);
    Integer getCount(Integer categoryId);
    List<Product> getEntitiesByName(Integer categoryId, String name);
    BigDecimal getCost();
    BigDecimal getCost(Integer categoryId);
}
