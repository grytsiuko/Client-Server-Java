package org.fidoshenyata.db.DAO.Impl;

import org.apache.commons.dbutils.DbUtils;
import org.fidoshenyata.db.connection.AbstractConnectionFactory;
import org.fidoshenyata.db.connection.ProductionConnectionFactory;
import org.fidoshenyata.db.DAO.Dao;
import org.fidoshenyata.db.queries.SqlStrings;
import org.fidoshenyata.db.model.Category;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.exceptions.db.InternalSQLException;
import org.fidoshenyata.exceptions.db.NameAlreadyTakenException;
import org.fidoshenyata.exceptions.db.NoEntityWithSuchIdException;
import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao implements Dao<Category> {

    private AbstractConnectionFactory connectionFactory;

    public CategoryDao(AbstractConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Category getEntity(Integer id) throws NoEntityWithSuchIdException, InternalSQLException {
        Connection connection = connectionFactory.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs = stmt
                    .executeQuery(fillScript(SqlStrings.GET_ENTITY_BY_ID) + id);
            if (rs.next()) {
                return extractCategoryFromResultSet(rs);
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
    public List<Category> getEntitiesByName(String name) throws InternalSQLException {
        Connection connection = connectionFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(fillScript(SqlStrings.GET_ENTITY_BY_NAME));
            ps.setString(1, "%" + name + "%");
            rs = ps.executeQuery();
            List<Category> list = new ArrayList<>();
            while (rs.next()) {
                list.add(extractCategoryFromResultSet(rs));
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
    public List<Category> getEntities(PagingInfo pagingInfo) throws InternalSQLException {
        Connection connection = connectionFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(fillScript(SqlStrings.GET_ENTITIES_W_PAGING));
            ps.setInt(1, pagingInfo.getOffset());
            ps.setInt(2, pagingInfo.getLimit());
            rs = ps.executeQuery();
            List<Category> list = new ArrayList<>();
            while (rs.next()) {
                list.add(extractCategoryFromResultSet(rs));
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
    public boolean insertEntity(Category entity) throws NameAlreadyTakenException, InternalSQLException {
        Connection connection = connectionFactory.getConnection();
        PreparedStatement ps = null;
        try {
            if (entity.getId() == null) {
                ps = connection.prepareStatement(SqlStrings.INSERT_CATEGORY);
                ps.setString(1, entity.getName());
                ps.setString(2, entity.getDescription());
            } else {
                ps = connection.prepareStatement(SqlStrings.INSERT_CATEGORY_WITH_ID);
                ps.setInt(1, entity.getId());
                ps.setString(2, entity.getName());
                ps.setString(3, entity.getDescription());
            }
            int i = ps.executeUpdate();
            return i == 1;
        } catch (PSQLException ex) {
            if (ex.getSQLState().equals("23505"))
                throw new NameAlreadyTakenException();
            else
                throw new InternalSQLException();
        } catch (SQLException ex) {
            throw new InternalSQLException();
        } finally {
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }
    }

    @Override
    public boolean updateEntity(Category entity) throws NameAlreadyTakenException, InternalSQLException {
        Connection connection = connectionFactory.getConnection();
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(SqlStrings.UPDATE_CATEGORY);
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getDescription());
            ps.setInt(3, entity.getId());
            int i = ps.executeUpdate();
            return i == 1;
        } catch (PSQLException ex) {
            if (ex.getSQLState().equals("23505"))
                throw new NameAlreadyTakenException();
            else
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

    private Category extractCategoryFromResultSet(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        return new Category(id, name, description);
    }

    private static String fillScript(String script) {
        return SqlStrings.insertTableName(script, TABLE);
    }

    private static final String TABLE = "category";
}
