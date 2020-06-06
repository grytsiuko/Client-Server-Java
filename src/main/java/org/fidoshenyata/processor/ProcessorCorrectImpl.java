package org.fidoshenyata.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fidoshenyata.db.DAO.Impl.CategoryDao;
import org.fidoshenyata.db.DAO.Impl.ProductDao;
import org.fidoshenyata.db.model.Category;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.exceptions.db.*;
import org.fidoshenyata.packet.Message;
import org.fidoshenyata.packet.Packet;
import org.fidoshenyata.service.CategoryService;
import org.fidoshenyata.service.ProductService;

import java.util.List;

import static org.fidoshenyata.packet.Message.*;

public class ProcessorCorrectImpl implements Processor {

    private CategoryService categoryService;
    private ProductService productService;
    private ProcessorUtils processorUtils;
    private JsonReader jsonReader;
    private JsonWriter jsonWriter;

    public ProcessorCorrectImpl() {
        categoryService = new CategoryService(new CategoryDao());
        productService = new ProductService(new ProductDao());
        processorUtils = new ProcessorUtils();
        jsonReader = new JsonReader();
        jsonWriter = new JsonWriter();
    }

    @Override
    public Packet process(Packet packet) {
        Message inputMessage = packet.getUsefulMessage();
        Message answerMessage = processMessage(inputMessage);

        Packet.PacketBuilder packetBuilder = Packet.builder()
                .source(packet.getSource())
                .packetID(packet.getPacketID())
                .usefulMessage(answerMessage);
        return packetBuilder.build();
    }

    private Message processMessage(Message messageBlock) {

        int userID = messageBlock.getUserID();
        int commandType = messageBlock.getCommandType();
        String message = messageBlock.getMessage();
        String response = null;

        try {

            switch (commandType) {

                case COMMAND_GET_CATEGORIES:
                    response = processGetCategories(message);
                    break;

                case COMMAND_GET_CATEGORIES_BY_NAME:
                    response = processGetCategoriesByName(message);
                    break;

                case COMMAND_GET_CATEGORY_BY_ID:
                    response = processGetCategoryById(message);
                    break;

                case COMMAND_ADD_CATEGORY:
                    response = processAddCategory(message);
                    break;
            }

        } catch (IllegalJSONException e) {
            return processorUtils.buildErrorMessage("Illegal JSON", userID);
        } catch (InternalSQLException e) {
            return processorUtils.buildErrorMessage("Internal SQL error", userID);
        } catch (ServerSideJSONException e) {
            return processorUtils.buildErrorMessage("Error while creating response", userID);
        } catch (NoEntityWithSuchIdException e) {
            return processorUtils.buildErrorMessage("No entity with such ID", userID);
        } catch (AbsentFieldsJSONException e) {
            return processorUtils.buildErrorMessage("Some fields are absent", userID);
        } catch (NameAlreadyTakenException e) {
            return processorUtils.buildErrorMessage("Such name already exists", userID);
        } catch (IllegalFieldException e) {
            return processorUtils.buildErrorMessage("Illegal value of some fields", userID);
        }

        if (response == null) {
            return processorUtils.buildErrorMessage("No such command", userID);
        } else {
            return processorUtils.buildSuccessMessage(response, userID);
        }

    }

    private String processGetCategories(String message)
            throws IllegalJSONException, InternalSQLException, ServerSideJSONException {

        PagingInfo pagingInfo = jsonReader.extractPagingInfo(message);
        List<Category> categories = categoryService.getCategories(pagingInfo);
        pagingInfo.setTotal(categoryService.getCount());
        return jsonWriter.generatePagingReply(categories, pagingInfo);
    }

    private String processGetCategoriesByName(String message)
            throws IllegalJSONException, InternalSQLException, ServerSideJSONException {

        String name = jsonReader.extractName(message);
        List<Category> categories = categoryService.getCategoriesByName(name);
        return jsonWriter.generateListReply(categories);
    }

    private String processGetCategoryById(String message)
            throws IllegalJSONException, InternalSQLException, ServerSideJSONException, NoEntityWithSuchIdException {

        Integer id = jsonReader.extractId(message);
        Category category = categoryService.getCategory(id);
        return jsonWriter.generateOneEntityReply(category);
    }

    private String processAddCategory(String message)
            throws IllegalJSONException, InternalSQLException, ServerSideJSONException, AbsentFieldsJSONException, NameAlreadyTakenException, IllegalFieldException {

        Category category = jsonReader.extractCategory(message);
        categoryService.addCategory(category);
        return jsonWriter.generateSuccessMessageReply("Successfully added category");
    }


    public static void main(String[] args) {
        ProcessorCorrectImpl processor = new ProcessorCorrectImpl();

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
                .message("{\"name\": \"egory 7\"}")
                .build();
        Message getAllCategoriesByNameResponse = processor.processMessage(getAllCategoriesByName);
        System.out.println(getAllCategoriesByNameResponse);

        Message getCategoryById = Message.builder()
                .userID(12)
                .commandType(COMMAND_GET_CATEGORY_BY_ID)
                .message("{\"id\": 103}")
                .build();
        Message getCategoryByIdResponse = processor.processMessage(getCategoryById);
        System.out.println(getCategoryByIdResponse);

        Message addCategory = Message.builder()
                .userID(12)
                .commandType(COMMAND_ADD_CATEGORY)
                .message("{\"name\": \"New New Name\", \"description\": \"Hey\"}")
                .build();
        Message addCategoryResponse = processor.processMessage(addCategory);
        System.out.println(addCategoryResponse);

    }
}
