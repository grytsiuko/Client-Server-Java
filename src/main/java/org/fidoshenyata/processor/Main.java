package org.fidoshenyata.processor;

import org.fidoshenyata.packet.Message;

import static org.fidoshenyata.packet.Message.*;

public class Main {


    public static void main(String[] args) {
        ProcessorCorrectImpl processor = new ProcessorCorrectImpl();

        Message deleteAllCategories = Message.builder()
                .userID(12)
                .commandType(COMMAND_DELETE_ALL_CATEGORIES)
                .message("")
                .build();
        Message deleteAllCategoriesResponse = processor.processMessage(deleteAllCategories);
        System.out.println(deleteAllCategoriesResponse);

        // id is set only for testing, it could be not defined when adding new entity
        Message addCategory = Message.builder()
                .userID(12)
                .commandType(COMMAND_ADD_CATEGORY)
                .message("{\"id\": 50, \"name\": \"Name\", \"description\": \"Hey\"}")
                .build();
        Message addCategoryResponse = processor.processMessage(addCategory);
        System.out.println(addCategoryResponse);

        Message updateCategory = Message.builder()
                .userID(12)
                .commandType(COMMAND_UPDATE_CATEGORY)
                .message("{\"id\": 50, \"name\": \"New Name\", \"description\": \"New Hey\"}")
                .build();
        Message updateCategoryResponse = processor.processMessage(updateCategory);
        System.out.println(updateCategoryResponse);

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
                .message("{\"name\": \"Na\"}")
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

        Message deleteCategory = Message.builder()
                .userID(12)
                .commandType(COMMAND_DELETE_CATEGORY)
                .message("{\"id\": 50}")
                .build();
        Message deleteCategoryResponse = processor.processMessage(deleteCategory);
        System.out.println(deleteCategoryResponse);

    }
}
