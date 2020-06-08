package org.fidoshenyata.processor;

import org.fidoshenyata.db.connection.ProductionConnectionFactory;
import org.fidoshenyata.packet.Message;

import static org.fidoshenyata.packet.Message.*;

public class Main {


    public static void main(String[] args) {
        Processor processor = new Processor(new ProductionConnectionFactory());

        // delete old entities

        Message deleteAllCategories = Message.builder()
                .userID(12)
                .commandType(COMMAND_DELETE_ALL_CATEGORIES)
                .message("")
                .build();
        Message deleteAllCategoriesResponse = processor.processMessage(deleteAllCategories);
        System.out.println(deleteAllCategoriesResponse);

        Message deleteAllProducts = Message.builder()
                .userID(12)
                .commandType(COMMAND_DELETE_ALL_PRODUCTS)
                .message("")
                .build();
        Message deleteAllProductsResponse = processor.processMessage(deleteAllProducts);
        System.out.println(deleteAllProductsResponse);

        // add entities
        // id is set only for testing, it could be not defined when adding new entity
        Message addCategory = Message.builder()
                .userID(12)
                .commandType(COMMAND_ADD_CATEGORY)
                .message("{\"id\": 50, \"name\": \"Category\"}")
                .build();
        Message addCategoryResponse = processor.processMessage(addCategory);
        System.out.println(addCategoryResponse);

        Message addProduct = Message.builder()
                .userID(12)
                .commandType(COMMAND_ADD_PRODUCT)
                .message("{\"id\": 50, \"name\": \"Product\", \"producer\": \"Producer\", " +
                        "\"amount\": \"50\", \"price\": \"30.00\", \"categoryId\": 50}")
                .build();
        Message addProductResponse = processor.processMessage(addProduct);
        System.out.println(addProductResponse);


        // update entities

        Message updateCategory = Message.builder()
                .userID(12)
                .commandType(COMMAND_UPDATE_CATEGORY)
                .message("{\"id\": 50, \"name\": \"New Category\", \"description\": \"Description\"}")
                .build();
        Message updateCategoryResponse = processor.processMessage(updateCategory);
        System.out.println(updateCategoryResponse);

        Message updateProduct = Message.builder()
                .userID(12)
                .commandType(COMMAND_UPDATE_PRODUCT)
                .message("{\"id\": 50, \"name\": \"New Product\", \"producer\": \"New Producer\", " +
                        "\"description\": \"Description\", \"price\": \"30.00\", \"categoryId\": 50}")
                .build();
        Message updateProductResponse = processor.processMessage(updateProduct);
        System.out.println(updateProductResponse);

        Message increaseProduct = Message.builder()
                .userID(12)
                .commandType(COMMAND_INCREASE_PRODUCT)
                .message("{\"id\": 50, \"amount\": 5}")
                .build();
        Message increaseProductResponse = processor.processMessage(increaseProduct);
        System.out.println(increaseProductResponse);

        Message decreaseProduct = Message.builder()
                .userID(12)
                .commandType(COMMAND_DECREASE_PRODUCT)
                .message("{\"id\": 50, \"amount\": 35}")
                .build();
        Message decreaseProductResponse = processor.processMessage(decreaseProduct);
        System.out.println(decreaseProductResponse);


        // get some lists

        Message getAllCategories = Message.builder()
                .userID(12)
                .commandType(COMMAND_GET_CATEGORIES)
                .message("{\"offset\": 0, \"limit\": 10}")
                .build();
        Message getAllCategoriesResponse = processor.processMessage(getAllCategories);
        System.out.println(getAllCategoriesResponse);

        Message getAllCategoriesByName = Message.builder()
                .userID(12)
                .commandType(COMMAND_GET_CATEGORIES_BY_NAME)
                .message("{\"name\": \"Category\"}")
                .build();
        Message getAllCategoriesByNameResponse = processor.processMessage(getAllCategoriesByName);
        System.out.println(getAllCategoriesByNameResponse);

        Message getCategoryById = Message.builder()
                .userID(12)
                .commandType(COMMAND_GET_CATEGORY_BY_ID)
                .message("{\"id\": 50}")
                .build();
        Message getCategoryByIdResponse = processor.processMessage(getCategoryById);
        System.out.println(getCategoryByIdResponse);



        Message getAllProducts = Message.builder()
                .userID(12)
                .commandType(COMMAND_GET_PRODUCTS)
                .message("{\"offset\": 0, \"limit\": 5}")
                .build();
        Message getAllProductsResponse = processor.processMessage(getAllProducts);
        System.out.println(getAllProductsResponse);

        Message getProductsByCategory = Message.builder()
                .userID(12)
                .commandType(COMMAND_GET_PRODUCTS_BY_CATEGORY)
                .message("{\"offset\": 0, \"limit\": 3, \"categoryId\": 50}")
                .build();
        Message getProductsByCategoryResponse = processor.processMessage(getProductsByCategory);
        System.out.println(getProductsByCategoryResponse);

        Message getProductsByName = Message.builder()
                .userID(12)
                .commandType(COMMAND_GET_PRODUCTS_BY_NAME)
                .message("{\"name\": \"Product\"}")
                .build();
        Message getProductsByNameResponse = processor.processMessage(getProductsByName);
        System.out.println(getProductsByNameResponse);

        Message getProductsByNameByCategory = Message.builder()
                .userID(12)
                .commandType(COMMAND_GET_PRODUCTS_BY_NAME_BY_CATEGORY)
                .message("{\"name\": \"Product\", \"categoryId\": 50}")
                .build();
        Message getProductsByNameByCategoryResponse = processor.processMessage(getProductsByNameByCategory);
        System.out.println(getProductsByNameByCategoryResponse);

        Message getProductById = Message.builder()
                .userID(12)
                .commandType(COMMAND_GET_PRODUCT_BY_ID)
                .message("{\"id\": 50}")
                .build();
        Message getProductByIdResponse = processor.processMessage(getProductById);
        System.out.println(getProductByIdResponse);


        // get some statistics

        Message getProductsCost = Message.builder()
                .userID(12)
                .commandType(COMMAND_GET_PRODUCTS_COST)
                .message("")
                .build();
        Message getProductsCostResponse = processor.processMessage(getProductsCost);
        System.out.println(getProductsCostResponse);

        Message getProductsCostByCategory = Message.builder()
                .userID(12)
                .commandType(COMMAND_GET_PRODUCTS_COST_BY_CATEGORY)
                .message("{\"categoryId\": 50}")
                .build();
        Message getProductsCostByCategoryResponse = processor.processMessage(getProductsCostByCategory);
        System.out.println(getProductsCostByCategoryResponse);



        // delete new entities

        Message deleteCategory = Message.builder()
                .userID(12)
                .commandType(COMMAND_DELETE_CATEGORY)
                .message("{\"id\": 50}")
                .build();
        Message deleteCategoryResponse = processor.processMessage(deleteCategory);
        System.out.println(deleteCategoryResponse);

        Message deleteProduct = Message.builder()
                .userID(12)
                .commandType(COMMAND_DELETE_PRODUCT)
                .message("{\"id\": 50}")
                .build();
        Message deleteProductResponse = processor.processMessage(deleteProduct);
        System.out.println(deleteProductResponse);
    }
}
