package org.fidoshenyata.processor;

import org.fidoshenyata.db.connection.AbstractConnectionFactory;
import org.fidoshenyata.db.connection.TestingConnectionFactory;
import org.fidoshenyata.db.model.Category;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.db.model.Product;
import org.fidoshenyata.exceptions.db.*;
import org.fidoshenyata.packet.Message;
import org.fidoshenyata.packet.Packet;
import org.fidoshenyata.processor.json.JsonReader;
import org.fidoshenyata.processor.json.JsonWriter;
import org.fidoshenyata.service.CategoryService;
import org.fidoshenyata.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

import static org.fidoshenyata.packet.Message.*;

public class Processor {

    private CategoryService categoryService;
    private ProductService productService;
    private ProcessorUtils processorUtils;
    private JsonReader jsonReader;
    private JsonWriter jsonWriter;

    public Processor(AbstractConnectionFactory connectionFactory) {
        categoryService = new CategoryService(connectionFactory);
        productService = new ProductService(connectionFactory);
        processorUtils = new ProcessorUtils();
        jsonReader = new JsonReader();
        jsonWriter = new JsonWriter();
    }

    public Processor() {
        this(new TestingConnectionFactory());
    }

    public Packet process(Packet packet) {
        Message inputMessage = packet.getUsefulMessage();
        Message answerMessage = processMessage(inputMessage);

        Packet.PacketBuilder packetBuilder = Packet.builder()
                .source(packet.getSource())
                .packetID(packet.getPacketID())
                .usefulMessage(answerMessage);
        return packetBuilder.build();
    }

    Message processMessage(Message messageBlock) {

        int userID = messageBlock.getUserID();
        int commandType = messageBlock.getCommandType();
        String message = messageBlock.getMessage();
        String response = null;

        try {

            switch (commandType) {

                case PING:
                    response = "PONG";
                    break;


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

                case COMMAND_UPDATE_CATEGORY:
                    response = processUpdateCategory(message);
                    break;

                case COMMAND_DELETE_CATEGORY:
                    response = processDeleteCategory(message);
                    break;

                case COMMAND_DELETE_ALL_CATEGORIES:
                    response = processDeleteAllCategories();
                    break;


                case COMMAND_GET_PRODUCTS:
                    response = processGetProducts(message);
                    break;

                case COMMAND_GET_PRODUCTS_BY_CATEGORY:
                    response = processGetProductsByCategory(message);
                    break;

                case COMMAND_GET_PRODUCTS_BY_NAME:
                    response = processGetProductsByName(message);
                    break;

                case COMMAND_GET_PRODUCTS_BY_NAME_BY_CATEGORY:
                    response = processGetProductsByNameByCategory(message);
                    break;

                case COMMAND_GET_PRODUCT_BY_ID:
                    response = processGetProductById(message);
                    break;

                case COMMAND_GET_PRODUCTS_COST:
                    response = processGetProductsCost();
                    break;

                case COMMAND_GET_PRODUCTS_COST_BY_CATEGORY:
                    response = processGetProductsCostByCategory(message);
                    break;

                case COMMAND_ADD_PRODUCT:
                    response = processAddProduct(message);
                    break;

                case COMMAND_UPDATE_PRODUCT:
                    response = processUpdateProduct(message);
                    break;

                case COMMAND_INCREASE_PRODUCT:
                    response = processIncreaseProduct(message);
                    break;

                case COMMAND_DECREASE_PRODUCT:
                    response = processDecreaseProduct(message);
                    break;

                case COMMAND_DELETE_PRODUCT:
                    response = processDeleteProduct(message);
                    break;

                case COMMAND_DELETE_ALL_PRODUCTS:
                    response = processDeleteAllProducts();
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
        } catch (AbsentFieldsJSONException | NullPointerException e) {
            return processorUtils.buildErrorMessage("Some fields are absent", userID);
        } catch (NameAlreadyTakenException e) {
            return processorUtils.buildErrorMessage("Such name or id already exists", userID);
        } catch (IllegalFieldException e) {
            return processorUtils.buildErrorMessage("Illegal value of some fields", userID);
        } catch (NoSuchProductException e) {
            return processorUtils.buildErrorMessage("No such product", userID);
        } catch (NotEnoughProductException e) {
            return processorUtils.buildErrorMessage("Not enough amount", userID);
        } catch (CategoryNotExistsException e) {
            return processorUtils.buildErrorMessage("Category not exists", userID);
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
            throws IllegalJSONException, InternalSQLException, ServerSideJSONException, AbsentFieldsJSONException, NameAlreadyTakenException, IllegalFieldException, CategoryNotExistsException {

        Category category = jsonReader.extractCategory(message);
        categoryService.addCategory(category);
        return jsonWriter.generateSuccessMessageReply("Successfully added category");
    }

    private String processUpdateCategory(String message)
            throws IllegalJSONException, InternalSQLException, ServerSideJSONException, AbsentFieldsJSONException, NameAlreadyTakenException, IllegalFieldException, CategoryNotExistsException {

        Category category = jsonReader.extractCategory(message);
        categoryService.updateCategory(category);
        return jsonWriter.generateSuccessMessageReply("Successfully updated category");
    }

    private String processDeleteCategory(String message)
            throws IllegalJSONException, InternalSQLException, ServerSideJSONException {

        Integer categoryId = jsonReader.extractId(message);
        categoryService.deleteCategory(categoryId);
        return jsonWriter.generateSuccessMessageReply("Successfully deleted category");
    }

    private String processDeleteAllCategories()
            throws InternalSQLException, ServerSideJSONException {

        categoryService.deleteAllEntities();
        return jsonWriter.generateSuccessMessageReply("Successfully deleted all categories");
    }

    private String processGetProducts(String message)
            throws InternalSQLException, ServerSideJSONException, IllegalJSONException {

        PagingInfo pagingInfo = jsonReader.extractPagingInfo(message);
        List<Product> products = productService.getProducts(pagingInfo);
        pagingInfo.setTotal(productService.getCount());
        return jsonWriter.generatePagingReply(products, pagingInfo);
    }

    private String processGetProductsByCategory(String message)
            throws InternalSQLException, ServerSideJSONException, IllegalJSONException {

        PagingInfo pagingInfo = jsonReader.extractPagingInfo(message);
        Integer categoryId = jsonReader.extractCategoryId(message);
        List<Product> products = productService.getProducts(categoryId, pagingInfo);
        pagingInfo.setTotal(productService.getCount(categoryId));
        return jsonWriter.generatePagingReply(products, pagingInfo);
    }

    private String processGetProductsByName(String message)
            throws InternalSQLException, ServerSideJSONException, IllegalJSONException {

        String name = jsonReader.extractName(message);
        List<Product> products = productService.getProductsByName(name);
        return jsonWriter.generateListReply(products);
    }

    private String processGetProductsByNameByCategory(String message)
            throws InternalSQLException, ServerSideJSONException, IllegalJSONException {

        String name = jsonReader.extractName(message);
        Integer categoryId = jsonReader.extractCategoryId(message);
        List<Product> products = productService.getProductsByName(categoryId, name);
        return jsonWriter.generateListReply(products);
    }

    private String processGetProductById(String message)
            throws IllegalJSONException, InternalSQLException, ServerSideJSONException, NoEntityWithSuchIdException {

        Integer id = jsonReader.extractId(message);
        Product product = productService.getProduct(id);
        return jsonWriter.generateOneEntityReply(product);
    }

    private String processGetProductsCost()
            throws InternalSQLException, ServerSideJSONException {

        BigDecimal cost = productService.getCost();
        return jsonWriter.generateCostReply(cost);
    }

    private String processGetProductsCostByCategory(String message)
            throws InternalSQLException, ServerSideJSONException, IllegalJSONException {

        Integer categoryId = jsonReader.extractCategoryId(message);
        BigDecimal cost = productService.getCost(categoryId);
        return jsonWriter.generateCostReply(cost);
    }

    private String processAddProduct(String message)
            throws InternalSQLException, ServerSideJSONException, IllegalJSONException, AbsentFieldsJSONException, NameAlreadyTakenException, IllegalFieldException, CategoryNotExistsException {

        Product product = jsonReader.extractProduct(message);
        productService.addProduct(product);
        return jsonWriter.generateSuccessMessageReply("Successfully added product");
    }

    private String processUpdateProduct(String message)
            throws InternalSQLException, ServerSideJSONException, IllegalJSONException, AbsentFieldsJSONException, NameAlreadyTakenException, IllegalFieldException, CategoryNotExistsException {

        Product product = jsonReader.extractProduct(message);
        productService.updateProduct(product);
        return jsonWriter.generateSuccessMessageReply("Successfully updated product");
    }

    private String processIncreaseProduct(String message)
            throws InternalSQLException, ServerSideJSONException, IllegalJSONException, IllegalFieldException, NoSuchProductException {

        Integer amount = jsonReader.extractAmount(message);
        Integer id = jsonReader.extractId(message);
        productService.increaseAmount(id, amount);
        return jsonWriter.generateSuccessMessageReply("Successfully increased product");
    }

    private String processDecreaseProduct(String message)
            throws InternalSQLException, ServerSideJSONException, IllegalJSONException, IllegalFieldException, NoSuchProductException, NotEnoughProductException {

        Integer amount = jsonReader.extractAmount(message);
        Integer id = jsonReader.extractId(message);
        productService.decreaseAmount(id, amount);
        return jsonWriter.generateSuccessMessageReply("Successfully decreased product");
    }

    private String processDeleteProduct(String message)
            throws InternalSQLException, ServerSideJSONException, IllegalJSONException {

        Integer id = jsonReader.extractId(message);
        productService.deleteEntity(id);
        return jsonWriter.generateSuccessMessageReply("Successfully deleted product");
    }

    private String processDeleteAllProducts()
            throws InternalSQLException, ServerSideJSONException {

        productService.deleteAllEntities();
        return jsonWriter.generateSuccessMessageReply("Successfully deleted all products");
    }
}
