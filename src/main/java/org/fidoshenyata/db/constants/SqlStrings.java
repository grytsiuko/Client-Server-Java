package org.fidoshenyata.db.constants;

public class SqlStrings {
    public final static String GET_CATEGORY_BY_ID = "SELECT * FROM category WHERE id=";
    public final static String GET_CATEGORIES_W_PAGING = "SELECT * FROM category " +
            "ORDER BY name DESC " + " OFFSET ? " + " FETCH FIRST ? ROWS ONLY";
    public final static String GET_CATEGORY_COUNT = "SELECT COUNT( id ) FROM category";
    public final static String INSERT_CATEGORY = "INSERT INTO category (name, description) VALUES " + "(?, ?)";
    public final static String UPDATE_CATEGORY = "UPDATE category " + "SET name = ?, description = ? "
            + "WHERE id = ?";
    public final static String DELETE_CATEGORY_BY_ID = "DELETE FROM category WHERE id=";
}
