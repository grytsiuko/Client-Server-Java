package org.fidoshenyata.db.DAO.Impl;

import org.fidoshenyata.db.ConnectionFactory;
import org.fidoshenyata.db.DAO.Dao;
import org.fidoshenyata.db.constants.SqlStrings;
import org.fidoshenyata.db.model.Category;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

public class CategoryDao implements Dao<Category> {
    @Override
    public Category getEntity(Integer id) {
        Connection connection = ConnectionFactory.getConnection();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(SqlStrings.GET_CATEGORY_BY_ID + id);
            if(rs.next())
            {
                return extractCategoryFromResultSet(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<Category> getEntities(Integer offset, Integer limit) {
        return null;
    }

    @Override
    public Integer getCount() {
        return null;
    }

    @Override
    public boolean insertEntity(Category entity) {
        return false;
    }

    @Override
    public boolean updateEntity(Category entity) {
        return false;
    }

    @Override
    public boolean deleteEntity(Integer id) {
        return false;
    }

    private Category extractCategoryFromResultSet(ResultSet rs) throws SQLException{
        Integer id = rs.getInt("id");
        String name =  rs.getString("name");
        String description = rs.getString("description");
        return new Category(id,name, description);
    }

    public static void main(String[] args) {
        CategoryDao c = new CategoryDao();
        System.out.println(c.getEntity(1));
    }
}
