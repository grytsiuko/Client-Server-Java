package org.fidoshenyata.db.DAO.Impl;

import org.apache.commons.dbutils.DbUtils;
import org.fidoshenyata.db.ConnectionFactory;
import org.fidoshenyata.db.DAO.Dao;
import org.fidoshenyata.db.constants.SqlStrings;
import org.fidoshenyata.db.model.Category;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.db.model.Product;
import org.fidoshenyata.exceptions.db.NameAlreadyTakenException;
import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao implements Dao<Category> {

    @Override
    public Category getEntity(Integer id) {
        Connection connection = ConnectionFactory.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs = stmt
                    .executeQuery( fillScript(SqlStrings.GET_ENTITY_BY_ID)+ id);
            if(rs.next())
            {
                return extractCategoryFromResultSet(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(connection);
        }
        return null;
    }

    @Override
    public List<Category> getEntityByName(String name) {
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(fillScript(SqlStrings.GET_ENTITY_BY_NAME));
            ps.setString(1, "%" +name+ "%");
            rs = ps.executeQuery();
            List<Category> list = new ArrayList<>();
            if(rs.next())
            {
                list.add(extractCategoryFromResultSet(rs));
            }
            return list;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }
        return null;
    }

    @Override
    public List<Category> getEntities(PagingInfo pagingInfo) {
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
           ps = connection.prepareStatement(fillScript(SqlStrings.GET_ENTITIES_W_PAGING));
           ps.setInt(1, pagingInfo.getOffset());
           ps.setInt(2, pagingInfo.getLimit());
           rs = ps.executeQuery();
           List<Category> list = new ArrayList<>();
           while(rs.next()){
               list.add(extractCategoryFromResultSet(rs));
           }
           return list;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }
        return null;
    }

    @Override
    public Integer getCount() {
        Connection connection = ConnectionFactory.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try{
            stmt = connection.createStatement();
            rs = stmt.executeQuery(fillScript(SqlStrings.GET_ENTITY_COUNT));
            if(rs.next()){
                return rs.getInt("count");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(connection);
        }
        return null;
    }

    @Override
    public boolean insertEntity(Category entity) throws NameAlreadyTakenException{
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(SqlStrings.INSERT_CATEGORY);
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getDescription());
            int i = ps.executeUpdate();
            if (i == 1) return true;
        } catch (PSQLException ex){
            if(ex.getSQLState().equals("23505"))
                throw new NameAlreadyTakenException();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }
        return false;
    }

    @Override
    public boolean updateEntity(Category entity) throws NameAlreadyTakenException{
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(SqlStrings.UPDATE_CATEGORY);
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getDescription());
            ps.setInt(3, entity.getId());
            int i = ps.executeUpdate();
            if (i == 1) return true;
        } catch (PSQLException ex){
            if(ex.getSQLState().equals("23505"))
                throw new NameAlreadyTakenException();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(connection);
        }
        return false;
    }

    @Override
    public boolean deleteEntity(Integer id) {
        Connection connection = ConnectionFactory.getConnection();
        Statement stmt = null;
        try{
            stmt = connection.createStatement();
            int i = stmt.executeUpdate(fillScript(SqlStrings.DELETE_ENTITY_BY_ID) + id);
            if(i == 1) return true;
        }catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DbUtils.closeQuietly(stmt);
            DbUtils.closeQuietly(connection);
        }
        return false;
    }

    private Category extractCategoryFromResultSet(ResultSet rs) throws SQLException{
        Integer id = rs.getInt("id");
        String name =  rs.getString("name");
        String description = rs.getString("description");
        return new Category(id,name, description);
    }

    private static String fillScript(String script){
        return SqlStrings.insertTableName(script, TABLE);
    }

    private static final String TABLE = "category";

    public static void main(String[] args) throws NameAlreadyTakenException {
        CategoryDao c = new CategoryDao();
//        System.out.println(c.getEntity(1));
//        System.out.println(c.getEntityByName("o"));
//        System.out.println(c.getEntities(new PagingInfo(1,3)));
//        System.out.println(c.getCount());
//        System.out.println(c.insertEntity(new Category(0,"Food", null)));
//        System.out.println(c.updateEntity(new Category(1,"Food", null)));
//        System.out.println(c.deleteEntity(2));
    }
}
