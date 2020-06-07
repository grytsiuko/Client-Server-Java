package org.fidoshenyata.db.DAO.Impl;

import org.apache.commons.dbutils.DbUtils;
import org.fidoshenyata.db.connection.AbstractConnectionFactory;
import org.fidoshenyata.db.connection.ProductionConnectionFactory;
import org.fidoshenyata.db.DAO.IProductDao;
import org.fidoshenyata.db.queries.SqlStrings;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.db.model.Product;
import org.fidoshenyata.exceptions.db.*;
import org.postgresql.util.PSQLException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDao implements IProductDao {

    private AbstractConnectionFactory connectionFactory;

    public ProductDao(AbstractConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Product getEntity(Integer id) throws NoEntityWithSuchIdException, InternalSQLException {
        Connection connection = connectionFactory.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs = stmt
                    .executeQuery(fillScript(SqlStrings.GET_ENTITY_BY_ID) + id);
            if (rs.next()) {
                return extractProductFromResultSet(rs);
            } else {
                throw new NoEntityWithSuchIdException();
            }
        } catch (SQLException ex) {
            throw new InternalSQLException();
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(connection);
        }
    }

    @Override
    public List<Product> getEntities(PagingInfo pagingInfo) throws InternalSQLException {
        Connection connection = connectionFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(fillScript(SqlStrings.GET_ENTITIES_W_PAGING));
            ps.setInt(1, pagingInfo.getOffset());
            ps.setInt(2, pagingInfo.getLimit());
            rs = ps.executeQuery();
            List<Product> list = new ArrayList<>();
            while (rs.next()) {
                list.add(extractProductFromResultSet(rs));
            }
            return list;
        } catch (SQLException ex) {
            throw new InternalSQLException();
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }
    }

    @Override
    public List<Product> getEntities(Integer categoryId, PagingInfo pagingInfo) throws InternalSQLException {
        Connection connection = connectionFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(fillScript(SqlStrings.GET_PRODUCTS_W_PAGING));
            ps.setInt(1, categoryId);
            ps.setInt(2, pagingInfo.getOffset());
            ps.setInt(3, pagingInfo.getLimit());
            rs = ps.executeQuery();
            List<Product> list = new ArrayList<>();
            while (rs.next()) {
                list.add(extractProductFromResultSet(rs));
            }
            return list;
        } catch (SQLException ex) {
            throw new InternalSQLException();
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }
    }

    @Override
    public Integer getCount() throws InternalSQLException {
        Connection connection = connectionFactory.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(fillScript(SqlStrings.GET_ENTITY_COUNT));
            rs.next();
            return rs.getInt("count");
        } catch (SQLException ex) {
            throw new InternalSQLException();
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(connection);
        }
    }

    @Override
    public Integer getCount(Integer categoryId) throws InternalSQLException {
        Connection connection = connectionFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(fillScript(SqlStrings.GET_PRODUCT_COUNT_BY_CATEGORY));
            ps.setInt(1, categoryId);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt("count");
        } catch (SQLException ex) {
            throw new InternalSQLException();
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }
    }

    @Override
    public List<Product> getEntitiesByName(String name) throws InternalSQLException {
        Connection connection = connectionFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(fillScript(SqlStrings.GET_ENTITY_BY_NAME));
            ps.setString(1, "%" + name + "%");
            rs = ps.executeQuery();
            List<Product> list = new ArrayList<>();
            while (rs.next()) {
                list.add(extractProductFromResultSet(rs));
            }
            return list;
        } catch (SQLException ex) {
            throw new InternalSQLException();
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }
    }

    @Override
    public List<Product> getEntitiesByName(Integer categoryId, String name) throws InternalSQLException {
        Connection connection = connectionFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(SqlStrings.GET_PRODUCT_BY_NAME_N_CATEGORY);
            ps.setInt(1, categoryId);
            ps.setString(2, "%" + name + "%");
            rs = ps.executeQuery();
            List<Product> list = new ArrayList<>();
            while (rs.next()) {
                list.add(extractProductFromResultSet(rs));
            }
            return list;
        } catch (SQLException ex) {
            throw new InternalSQLException();
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }
    }

    @Override
    public BigDecimal getCost() throws InternalSQLException {
        Connection connection = connectionFactory.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs = stmt
                    .executeQuery(SqlStrings.GET_COST);
            rs.next();
            return rs.getBigDecimal("cost");
        } catch (SQLException ex) {
            throw new InternalSQLException();
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(connection);
        }
    }

    @Override
    public BigDecimal getCost(Integer categoryId) throws InternalSQLException {
        Connection connection = connectionFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(SqlStrings.GET_COST_BY_CATEGORY);
            ps.setInt(1, categoryId);
            rs = ps.executeQuery();
            rs.next();
            return rs.getBigDecimal("cost");
        } catch (SQLException ex) {
            throw new InternalSQLException();
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }
    }

    @Override
    public boolean insertEntity(Product entity)
            throws NameAlreadyTakenException, InternalSQLException, CategoryNotExistsException {

        Connection connection = connectionFactory.getConnection();
        PreparedStatement ps = null;
        try {
            if (entity.getId() == null) {
                ps = connection.prepareStatement(SqlStrings.INSERT_PRODUCT);
                ps.setString(1, entity.getName());
                ps.setString(2, entity.getProducer());
                ps.setString(3, entity.getDescription());
                ps.setInt(4, entity.getAmount());
                ps.setBigDecimal(5, entity.getPrice());
                ps.setInt(6, entity.getCategoryId());
            } else {
                ps = connection.prepareStatement(SqlStrings.INSERT_PRODUCT_WITH_ID);
                ps.setInt(1, entity.getId());
                ps.setString(2, entity.getName());
                ps.setString(3, entity.getProducer());
                ps.setString(4, entity.getDescription());
                ps.setInt(5, entity.getAmount());
                ps.setBigDecimal(6, entity.getPrice());
                ps.setInt(7, entity.getCategoryId());
            }
            int i = ps.executeUpdate();
            return i == 1;
        } catch (PSQLException ex) {
            if (ex.getSQLState().equals("23505"))
                throw new NameAlreadyTakenException();
            if (ex.getSQLState().equals("23503"))
                throw new CategoryNotExistsException();
            throw new InternalSQLException();
        } catch (SQLException ex) {
            throw new InternalSQLException();
        } finally {
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }
    }

    @Override
    public boolean updateEntity(Product entity)
            throws NameAlreadyTakenException, InternalSQLException, CategoryNotExistsException {

        Connection connection = connectionFactory.getConnection();
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(SqlStrings.UPDATE_PRODUCT);
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getProducer());
            ps.setString(3, entity.getDescription());
            ps.setBigDecimal(4, entity.getPrice());
            ps.setInt(5, entity.getCategoryId());
            ps.setInt(6, entity.getId());
            int i = ps.executeUpdate();
            return i == 1;
        } catch (PSQLException ex) {
            if (ex.getSQLState().equals("23505"))
                throw new NameAlreadyTakenException();
            if (ex.getSQLState().equals("23503"))
                throw new CategoryNotExistsException();
            throw new InternalSQLException();
        } catch (SQLException ex) {
            throw new InternalSQLException();
        } finally {
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }
    }

    @Override
    public boolean deleteEntity(Integer id) throws InternalSQLException {
        Connection connection = connectionFactory.getConnection();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            int i = stmt.executeUpdate(fillScript(SqlStrings.DELETE_ENTITY_BY_ID) + id);
            return i == 1;
        } catch (SQLException ex) {
            throw new InternalSQLException();
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(connection);
        }
    }

    @Override
    public boolean deleteAll() throws InternalSQLException {
        Connection connection = connectionFactory.getConnection();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            int i = stmt.executeUpdate(fillScript(SqlStrings.DELETE_ALL_ENTITIES));
            return i == 1;
        } catch (SQLException ex) {
            throw new InternalSQLException();
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(connection);
        }
    }

    @Override
    public Boolean increaseAmount(Integer productId, Integer amount) throws NoSuchProductException, InternalSQLException {
        Connection connection = connectionFactory.getConnection();
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        try {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            ps1 = connection.prepareStatement(SqlStrings.GET_AMOUNT);
            ps1.setInt(1, productId);

            rs = ps1.executeQuery();
            int amountOld;
            if (rs.next()) {
                amountOld = rs.getInt("amount");
            } else {
                throw new NoSuchProductException();
            }

            int amountNew = amountOld + amount;

            ps2 = connection.prepareStatement(SqlStrings.UPDATE_AMOUNT);
            ps2.setInt(1, amountNew);
            ps2.setInt(2, productId);
            int i = ps2.executeUpdate();
            return i == 1;
        } catch (SQLException ex) {
            throw new InternalSQLException();
        } finally {
            DbUtils.closeQuietly(ps1);
            DbUtils.closeQuietly(ps2);
            DbUtils.closeQuietly(connection);
        }
    }

    @Override
    public Boolean decreaseAmount(Integer productId, Integer amount) throws NoSuchProductException, NotEnoughProductException, InternalSQLException {
        Connection connection = connectionFactory.getConnection();
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        try {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            ps1 = connection.prepareStatement(SqlStrings.GET_AMOUNT);
            ps1.setInt(1, productId);

            rs = ps1.executeQuery();
            int amountOld;
            if (rs.next()) {
                amountOld = rs.getInt("amount");
            } else {
                throw new NoSuchProductException();
            }

            int amountNew = amountOld - amount;

            if (amountNew < 0){
                throw new NotEnoughProductException();
            }

            ps2 = connection.prepareStatement(SqlStrings.UPDATE_AMOUNT);
            ps2.setInt(1, amountNew);
            ps2.setInt(2, productId);
            int i = ps2.executeUpdate();
            return i == 1;
        } catch (SQLException ex) {
            throw new InternalSQLException();
        } finally {
            DbUtils.closeQuietly(ps1);
            DbUtils.closeQuietly(ps2);
            DbUtils.closeQuietly(connection);
        }
    }

    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        String producer = rs.getString("producer");
        String description = rs.getString("description");
        Integer amount = rs.getInt("amount");
        BigDecimal price = rs.getBigDecimal("price");
        Integer categoryId = rs.getInt("category_id");
        return new Product(id, name, producer, description, amount, price, categoryId);
    }

    private static String fillScript(String script) {
        return SqlStrings.insertTableName(script, TABLE);
    }

    private static final String TABLE = "product";
}
