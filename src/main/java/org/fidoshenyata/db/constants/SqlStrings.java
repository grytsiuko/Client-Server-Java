package org.fidoshenyata.db.constants;

public class SqlStrings {

    public final static String GET_ENTITY_BY_ID =
            "SELECT * FROM $table WHERE id =";

    public final static String GET_ENTITY_BY_NAME =
            "SELECT * FROM $table WHERE name LIKE ?";

    public final static String GET_ENTITIES_W_PAGING =
            "SELECT * FROM $table " +
                    "ORDER BY name DESC " + " OFFSET ? " + " FETCH FIRST ? ROWS ONLY";

    public final static String GET_ENTITY_COUNT =
            "SELECT COUNT( id ) FROM $table";

    public final static String INSERT_CATEGORY =
            "INSERT INTO category (name, description) VALUES " + "(?, ?)";

    public final static String INSERT_CATEGORY_WITH_ID =
            "INSERT INTO category (id, name, description) VALUES " + "(?, ?, ?)";

    public final static String UPDATE_CATEGORY =
            "UPDATE category " + "SET name = ?, description = ? " +
                    "WHERE id = ?";

    public final static String DELETE_ENTITY_BY_ID =
            "DELETE FROM $table WHERE id=";

    public final static String DELETE_ALL_ENTITIES =
            "DELETE FROM $table";


    public final static String GET_PRODUCT_BY_NAME_N_CATEGORY =
            "SELECT * FROM product WHERE category_id = ? AND name LIKE ?";

    public final static String GET_PRODUCT_COUNT_BY_CATEGORY =
            "SELECT COUNT( id ) FROM $table WHERE category_id = ?";

    public final static String GET_PRODUCTS_W_PAGING =
            "SELECT * FROM $table " + "WHERE category_id = ? " +
                    "ORDER BY name DESC " + " OFFSET ? " + " FETCH FIRST ? ROWS ONLY";

    public final static String GET_COST =
            "SELECT SUM(amount * price) AS cost FROM product";

    public final static String GET_COST_BY_CATEGORY =
            GET_COST + " WHERE category_id = ?";

    public final static String INSERT_PRODUCT =
            "INSERT INTO product" +
                    " (name, producer, description, amount, price, category_id) " +
                    " VALUES " + "(?, ?, ?, ?, ?, ?)";

    public final static String INSERT_PRODUCT_WITH_ID =
            "INSERT INTO product" +
                    " (id, name, producer, description, amount, price, category_id) " +
                    " VALUES " + "(?, ?, ?, ?, ?, ?, ?)";

    public final static String UPDATE_PRODUCT =
            "UPDATE product " +
                    " SET name = ?, producer = ?, description = ?, " +
                    "     price = ?, category_id = ? " +
                    " WHERE id = ?";


    public final static String UPDATE_AMOUNT =
            "UPDATE product " +
                    " SET amount = ? " +
                    " WHERE id = ?";

    public final static String GET_AMOUNT =
            "SELECT amount " +
                    " FROM product " +
                    " WHERE id = ?";


    public static String insertTableName(String sqlString, String name) {
        return sqlString.replace("$table", name);
    }
}
