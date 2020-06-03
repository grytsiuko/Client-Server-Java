package org.fidoshenyata.db.DAO.Impl;

import org.apache.commons.dbutils.DbUtils;
import org.fidoshenyata.db.ConnectionFactory;
import org.fidoshenyata.db.DAO.IProductDao;
import org.fidoshenyata.db.constants.SqlStrings;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.db.model.Product;
import org.fidoshenyata.exceptions.db.NameAlreadyTakenException;
import org.postgresql.util.PSQLException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDao implements IProductDao {
    @Override
    public Product getEntity(Integer id) {
        Connection connection = ConnectionFactory.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs = stmt
                    .executeQuery( fillScript(SqlStrings.GET_ENTITY_BY_ID)+ id);
            if(rs.next())
            {
                return extractProductFromResultSet(rs);
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
    public List<Product> getEntities(PagingInfo pagingInfo) {
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = connection.prepareStatement(fillScript(SqlStrings.GET_ENTITIES_W_PAGING));
            ps.setInt(1, pagingInfo.getOffset());
            ps.setInt(2, pagingInfo.getLimit());
            rs = ps.executeQuery();
            List<Product> list = new ArrayList<>();
            while(rs.next()){
                list.add(extractProductFromResultSet(rs));
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
    public List<Product> getEntities(Integer categoryId, PagingInfo pagingInfo) {
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = connection.prepareStatement(fillScript(SqlStrings.GET_PRODUCTS_W_PAGING));
            ps.setInt(1, categoryId);
            ps.setInt(2, pagingInfo.getOffset());
            ps.setInt(3, pagingInfo.getLimit());
            rs = ps.executeQuery();
            List<Product> list = new ArrayList<>();
            while(rs.next()){
                list.add(extractProductFromResultSet(rs));
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
    public Integer getCount(Integer categoryId) {
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(fillScript(SqlStrings.GET_PRODUCT_COUNT_BY_CATEGORY));
            ps.setInt(1, categoryId);
            rs = ps.executeQuery();
            if(rs.next())
            {
                return rs.getInt("count");
            }
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
    public Product getEntityByName(String name) {
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(fillScript(SqlStrings.GET_ENTITY_BY_NAME));
            ps.setString(1, "%" +name+ "%");
            rs = ps.executeQuery();
            if(rs.next())
            {
                return extractProductFromResultSet(rs);
            }
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
    public Product getEntityByName(Integer categoryId, String name) {
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(SqlStrings.GET_PRODUCT_BY_NAME_N_CATEGORY);
            ps.setInt(1, categoryId);
            ps.setString(2, "%" +name+ "%");
            rs = ps.executeQuery();
            if(rs.next())
            {
                return extractProductFromResultSet(rs);
            }
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
    public BigDecimal getCost() {
        Connection connection = ConnectionFactory.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
            try {
            stmt = connection.createStatement();
            rs = stmt
                    .executeQuery(SqlStrings.GET_COST);
            if(rs.next())
            {
                return rs.getBigDecimal("cost");
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
    public BigDecimal getCost(Integer categoryId) {
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(SqlStrings.GET_COST_BY_CATEGORY);
            ps.setInt(1, categoryId);
            rs = ps.executeQuery();
            if(rs.next())
            {
                return rs.getBigDecimal("cost");
            }
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
    public boolean insertEntity(Product entity) throws NameAlreadyTakenException {
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(SqlStrings.INSERT_PRODUCT);
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getProducer());
            ps.setString(3, entity.getDescription());
            ps.setInt(4,entity.getAmount());
            ps.setBigDecimal(5, entity.getPrice());
            ps.setInt(6,entity.getCategoryId());
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
    public boolean updateEntity(Product entity) throws NameAlreadyTakenException {
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(SqlStrings.UPDATE_PRODUCT);
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getProducer());
            ps.setString(3, entity.getDescription());
            ps.setInt(4,entity.getAmount());
            ps.setBigDecimal(5, entity.getPrice());
            ps.setInt(6,entity.getCategoryId());
            ps.setInt(7, entity.getId());
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

    private Product extractProductFromResultSet(ResultSet rs) throws SQLException{
        Integer id = rs.getInt("id");
        String name =  rs.getString("name");
        String producer =  rs.getString("producer");
        String description = rs.getString("description");
        Integer amount = rs.getInt("amount");
        BigDecimal price = rs.getBigDecimal("price");
        Integer categoryId = rs.getInt("category_id");
        return new Product(id,name,producer,description,amount,price,categoryId);
    }

    private static String fillScript(String script){
        return SqlStrings.insertTableName(script, TABLE);
    }

    private static final String TABLE = "product";

    public static void main(String[] args) throws NameAlreadyTakenException {
        ProductDao p = new ProductDao();
//        System.out.println(p.getProducts(2,new PagingInfo(0,3)));
//        System.out.println(p.getProducts(new PagingInfo(0,3)));
//        System.out.println(p.getCount(4));
//        System.out.println(p.getCount());
//        System.out.println(p.getEntityByName(1,"re"));
//        System.out.println(p.getEntityByName("re"));
//        System.out.println(p.getCost());
//        System.out.println(p.getCost(2));
//        System.out.println(p.getEntity(1));
//        System.out.println(p.insertEntity(new Product(0,"nesquick","nestle",
//                null,3, new BigDecimal("1.5"), 10)));
//        System.out.println(p.updateEntity(new Product(9,"nesquik","nestle",
//                null,3, new BigDecimal("1.5"), 10)));
//        System.out.println(p.deleteEntity(9));
    }
}
