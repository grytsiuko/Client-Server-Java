package org.fidoshenyata.http;

import io.jsonwebtoken.ExpiredJwtException;
import org.fidoshenyata.db.connection.AbstractConnectionFactory;
import org.fidoshenyata.db.model.Category;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.db.model.Product;
import org.fidoshenyata.exceptions.db.*;
import org.fidoshenyata.exceptions.http.NoSuchPathException;
import org.fidoshenyata.exceptions.http.NotAuthException;
import org.fidoshenyata.processor.json.JsonReader;
import org.fidoshenyata.processor.json.JsonWriter;
import org.fidoshenyata.service.CategoryService;
import org.fidoshenyata.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

public class HttpProcessor {
    private final HttpParser hp;
    private final ResponseSender responseSender;
    private final JwtCoder jwtCoder;

    private final JsonReader jsonReader;
    private final JsonWriter jsonWriter;

    private final CategoryService categoryService;
    private final ProductService productService;

    public HttpProcessor(AbstractConnectionFactory connectionFactory,
                         HttpParser hp, ResponseSender responseSender){
        this.hp = hp;
        this.responseSender = responseSender;
        jwtCoder = new JwtCoder();
        jsonReader = new JsonReader();
        jsonWriter = new JsonWriter();
        categoryService = new CategoryService(connectionFactory);
        productService = new ProductService(connectionFactory);
    }

    public void process() {
        if (this.hp.getMethod() == null){
            responseSender.sendJsonResponse(400, "{}");
            return;
        }
        try{
            switch (this.hp.getMethod()){
                case "GET":
                    this.handleGetRequests();
                    break;
                case "POST":
                    this.handlePostRequests();
                    break;
                case "PUT":
                    this.handlePutRequests();
                    break;
                case "DELETE":
                    this.handleDeleteRequests();
                    break;
                default:
                    throw new NoSuchPathException();
            }
        }
        catch(ServerSideJSONException e){
            responseSender.sendJsonResponse(500, jsonWriter.generateErrorReply("Error while creating response"));
        }catch (NumberFormatException e){
            responseSender.sendJsonResponse(400, jsonWriter.generateErrorReply("Failed to parse expected integer"));
        }catch(IllegalArgumentException e){
            responseSender.sendJsonResponse(400, jsonWriter.generateErrorReply(e.getMessage()));
        }catch(NoSuchPathException e){
            responseSender.sendJsonResponse(405, jsonWriter.generateErrorReply("Method not allowed"));
        }catch (InternalSQLException e) {
            responseSender.sendJsonResponse(500, jsonWriter.generateErrorReply("Internal SQL error"));
        }catch (NoEntityWithSuchIdException e) {
            responseSender.sendJsonResponse(404, jsonWriter.generateErrorReply("No entity with such ID"));
        } catch(NotAuthException e ){
            responseSender.sendJsonResponse
                    (401, jsonWriter.generateErrorReply("Not authorized or incompatible token"));
        } catch (ExpiredJwtException e){
            responseSender.sendJsonResponse(401, jsonWriter.generateErrorReply("Token expired"));
        }catch (NameAlreadyTakenException e) {
            responseSender.sendJsonResponse(409, jsonWriter.generateErrorReply("Such name or id already exists"));
        }catch (AbsentFieldsJSONException | NullPointerException e) {
            responseSender.sendJsonResponse(404, jsonWriter.generateErrorReply("Some fields are absent"));
        }catch (CategoryNotExistsException e) {
            responseSender.sendJsonResponse(404, jsonWriter.generateErrorReply("Category not exists"));
        }catch (IllegalJSONException e) {
            responseSender.sendJsonResponse(400, jsonWriter.generateErrorReply("Illegal JSON"));
        }catch (IllegalFieldException e) {
            responseSender.sendJsonResponse(409, jsonWriter.generateErrorReply("Illegal value of some fields"));
        }catch (NoSuchProductException e) {
            responseSender.sendJsonResponse(404, jsonWriter.generateErrorReply("No such product"));
        } catch (NotEnoughProductException e) {
            responseSender.sendJsonResponse(409, jsonWriter.generateErrorReply("Not enough amount"));
        }

    }

    private void checkJws() throws NotAuthException, ExpiredJwtException {
        if(!jwtCoder.isJwsValid(hp.getHeader("x-auth"))) throw new NotAuthException();
    }

    private void handleGetRequests()
            throws ServerSideJSONException, IllegalArgumentException, NoSuchPathException,
            InternalSQLException, NoEntityWithSuchIdException, NotAuthException {
        if(hp.urlContains("login")) {
            this.handleLoginPath();
            return;
        }
        checkJws();
        if(hp.urlContains("api")){
            if(hp.urlContains("good")) this.handleProductGetPath();
            else if (hp.urlContains("cost")) this.handleCostPath();
            else if (hp.urlContains("category")) this.handleCategoryGetPath();
            else throw new NoSuchPathException();
        }
        else throw new NoSuchPathException();
    }

    private void handlePutRequests() throws NotAuthException, NoSuchPathException, NameAlreadyTakenException,
            AbsentFieldsJSONException, CategoryNotExistsException, IllegalFieldException,
            InternalSQLException, IllegalJSONException, ServerSideJSONException {
        checkJws();
        if(hp.urlContains("api")){
            if(hp.urlContains("good")) this.processAddProduct(hp.getBody());
            else if (hp.urlContains("category")) this.processAddCategory(hp.getBody());
            else throw new NoSuchPathException();
        }
        else throw new NoSuchPathException();
    }

    private void handlePostRequests() throws NotAuthException, NoSuchPathException, NameAlreadyTakenException,
            AbsentFieldsJSONException, CategoryNotExistsException, IllegalFieldException,
            InternalSQLException, IllegalJSONException, ServerSideJSONException,
            NoSuchProductException, NotEnoughProductException {
        checkJws();
        if(hp.urlContains("api")){
            if(hp.urlContains("good")) this.handleProductPostPath();
            else if(hp.urlContains("category")) this.processUpdateCategory(hp.getBody());
            else throw new NoSuchPathException();
        }else throw new NoSuchPathException();
    }

    private void handleDeleteRequests() throws NotAuthException, NoSuchPathException,
            InternalSQLException, ServerSideJSONException {
        checkJws();
        if(hp.urlContains("api")){
            if(hp.urlContains("good")) this.handleProductDeletePath();
            else if(hp.urlContains("category")) this.handleCategoryDeletePath();
            else throw new NoSuchPathException();
        }else throw new NoSuchPathException();
    }

    private void handleCostPath() throws NumberFormatException, InternalSQLException,
            ServerSideJSONException, NoSuchPathException {
        if(hp.getUrlPartsLength() == 3){
            int categoryId = Integer.parseInt(hp.getUrlParts().get(2));
            this.processGetProductsCostByCategory(categoryId);
        }else if(hp.getUrlPartsLength() == 2){
            this.processGetProductsCost();
        }
        else throw new NoSuchPathException();
    }

    private void handleProductGetPath() throws NumberFormatException, InternalSQLException, NoEntityWithSuchIdException
            , ServerSideJSONException, NoSuchPathException {
        if(hp.getUrlPartsLength() == 3){
            this.processGetProductById();
        }else if (hp.getUrlPartsLength() == 2){
            if(hp.getParam("name")== null){
                int offset = Integer.parseInt(hp.getParam("offset"));
                int limit = Integer.parseInt(hp.getParam("limit"));
                PagingInfo pagingInfo = new PagingInfo(offset,limit);
                if(hp.getParam("categoryId") == null) this.processGetProducts(pagingInfo);
                else {
                    int categoryId = Integer.parseInt(hp.getParam("categoryId"));
                    this.processGetProductsByCategory(pagingInfo, categoryId);
                }
            } else if (hp.getParam("categoryId") == null){
                this.processGetProductsByName(hp.getParam("name"));
            } else {
                int categoryId = Integer.parseInt(hp.getParam("categoryId"));
                this.processGetProductsByNameByCategory(hp.getParam("name"),
                        categoryId);
            }
        } else throw new NoSuchPathException();
    }

    private void handleCategoryGetPath() throws NumberFormatException, ServerSideJSONException,
            InternalSQLException, NoEntityWithSuchIdException, NoSuchPathException {
        if(hp.getUrlPartsLength() == 3){
            int id = Integer.parseInt(hp.getUrlParts().get(2));
            this.processGetCategoryById(id);
        }else if(hp.getUrlPartsLength() == 2){
            if (hp.getParam("name") != null) {
                this.processGetCategoriesByName(hp.getParam("name"));
                return;
            }
            int offset = Integer.parseInt(hp.getParam("offset"));
            int limit = Integer.parseInt(hp.getParam("limit"));
            PagingInfo pagingInfo = new PagingInfo(offset,limit);
            this.processGetCategories(pagingInfo);
        }  else throw new NoSuchPathException();
    }

    private void handleProductPostPath() throws NameAlreadyTakenException, AbsentFieldsJSONException,
            CategoryNotExistsException, IllegalFieldException, InternalSQLException, IllegalJSONException,
            ServerSideJSONException, NoSuchPathException, NumberFormatException, NoSuchProductException,
            NotEnoughProductException {
        if(hp.getUrlPartsLength() == 2){
            this.processUpdateProduct(hp.getBody());
        }else if (hp.getUrlPartsLength() == 3){
            String command = hp.getUrlParts().get(2).toLowerCase();
            if(command.equals("dec")) this.processDecreaseProduct(hp.getBody());
            else if(command.equals("inc")) this.processIncreaseProduct(hp.getBody());
            else throw new NoSuchPathException();
        } else throw new NoSuchPathException();
    }

    private void handleProductDeletePath() throws NumberFormatException,
            InternalSQLException, ServerSideJSONException, NoSuchPathException {

        if(hp.getUrlPartsLength() == 3){
            int id = Integer.parseInt(hp.getUrlParts().get(2));
            productService.deleteEntity(id);
            responseSender.sendJsonResponse(200,
                    jsonWriter.generateSuccessMessageReply("Successfully deleted product"));
        }else if(hp.getUrlPartsLength() == 2){
            productService.deleteAllEntities();
            responseSender.sendJsonResponse(200,
                    jsonWriter.generateSuccessMessageReply("Successfully deleted all products"));
        } else throw new NoSuchPathException();
    }
    private void handleCategoryDeletePath() throws InternalSQLException, ServerSideJSONException, NoSuchPathException {
        if(hp.getUrlPartsLength() == 3){
            int id = Integer.parseInt(hp.getUrlParts().get(2));
            categoryService.deleteCategory(id);
            responseSender.sendJsonResponse(200,
                    jsonWriter.generateSuccessMessageReply("Successfully deleted category"));
        }else if(hp.getUrlPartsLength() == 2){
            categoryService.deleteAllEntities();
            responseSender.sendJsonResponse(200,
                    jsonWriter.generateSuccessMessageReply("Successfully deleted all categories"));
        } else throw new NoSuchPathException();
    }

    private void processUpdateCategory(String body)
            throws IllegalJSONException, InternalSQLException, ServerSideJSONException,
            AbsentFieldsJSONException, NameAlreadyTakenException, IllegalFieldException,
            CategoryNotExistsException {

        Category category = jsonReader.extractCategory(body);
        categoryService.updateCategory(category);
        responseSender.sendJsonResponse(200,
                jsonWriter.generateSuccessMessageReply("Successfully updated category"));
    }

    private void processIncreaseProduct(String body)
            throws InternalSQLException, ServerSideJSONException, IllegalJSONException,
            IllegalFieldException, NoSuchProductException {

        Integer amount = jsonReader.extractAmount(body);
        Integer id = jsonReader.extractId(body);
        productService.increaseAmount(id, amount);
        responseSender.sendJsonResponse(200,
                jsonWriter.generateSuccessMessageReply("Successfully increased amount of product"));
    }

    private void processDecreaseProduct(String body)
            throws InternalSQLException, ServerSideJSONException, IllegalJSONException,
            IllegalFieldException, NoSuchProductException, NotEnoughProductException {
        Integer amount = jsonReader.extractAmount(body);
        Integer id = jsonReader.extractId(body);
        productService.decreaseAmount(id, amount);
        responseSender.sendJsonResponse(200,
                jsonWriter.generateSuccessMessageReply("Successfully decreased amount of product"));
    }

    private void processUpdateProduct(String body)
            throws InternalSQLException, ServerSideJSONException, IllegalJSONException,
            AbsentFieldsJSONException, NameAlreadyTakenException, IllegalFieldException, CategoryNotExistsException {
        Product product = jsonReader.extractProduct(body);
        productService.updateProduct(product);
        responseSender.sendJsonResponse(200,jsonWriter.generateSuccessMessageReply("Successfully updated product"));
    }

    private void processAddCategory(String body) throws IllegalJSONException, AbsentFieldsJSONException,
            ServerSideJSONException, NameAlreadyTakenException, IllegalFieldException,
            InternalSQLException, CategoryNotExistsException {
        Category category = jsonReader.extractCategory(body);
        categoryService.addCategory(category);
        responseSender.sendJsonResponse(201,jsonWriter.generateSuccessMessageReply("Successfully added category"));
    }

    private void processAddProduct(String body) throws IllegalJSONException, AbsentFieldsJSONException,
            NameAlreadyTakenException, IllegalFieldException, InternalSQLException,
            CategoryNotExistsException, ServerSideJSONException {
        Product product = jsonReader.extractProduct(body);
        productService.addProduct(product);
        responseSender.sendJsonResponse(201,jsonWriter.generateSuccessMessageReply("Successfully added product"));
    }

    private void processGetCategories(PagingInfo pagingInfo)
            throws InternalSQLException, ServerSideJSONException {
        List<Category> categories = categoryService.getCategories(pagingInfo);
        pagingInfo.setTotal(categoryService.getCount());
        responseSender.sendJsonResponse(200, jsonWriter.generatePagingReply(categories, pagingInfo));
    }

    private void processGetCategoriesByName(String name) throws InternalSQLException, ServerSideJSONException {
        List<Category> categories = categoryService.getCategoriesByName(name);
        responseSender.sendJsonResponse(200, jsonWriter.generateListReply(categories));
    }

    private void processGetCategoryById(int id) throws ServerSideJSONException, InternalSQLException,
            NoEntityWithSuchIdException {
        Category category = categoryService.getCategory(id);
        responseSender.sendJsonResponse(200, jsonWriter.generateOneEntityReply(category));
    }

    private void processGetProductsCost() throws InternalSQLException, ServerSideJSONException {
        BigDecimal cost = productService.getCost();
        responseSender.sendJsonResponse(200, jsonWriter.generateCostReply(cost));
    }

    private void processGetProductsCostByCategory(int categoryId) throws InternalSQLException, ServerSideJSONException {
        BigDecimal cost = productService.getCost(categoryId);
        responseSender.sendJsonResponse(200, jsonWriter.generateCostReply(cost));
    }

    private void processGetProductsByNameByCategory(String name, int categoryId) throws InternalSQLException, ServerSideJSONException {
        List<Product> products = productService.getProductsByName(categoryId, name);
        responseSender.sendJsonResponse(200, jsonWriter.generateListReply(products));
    }

    private void processGetProductsByCategory(PagingInfo pagingInfo, int categoryId) throws InternalSQLException, ServerSideJSONException {
        List<Product> products = productService.getProducts(categoryId, pagingInfo);
        pagingInfo.setTotal(productService.getCount(categoryId));
        responseSender.sendJsonResponse(200, jsonWriter.generatePagingReply(products, pagingInfo));
    }

    private void processGetProductsByName(String name) throws InternalSQLException, ServerSideJSONException {
        List<Product> products = productService.getProductsByName(name);
        responseSender.sendJsonResponse(200, jsonWriter.generateListReply(products));
    }

    private void processGetProductById() throws InternalSQLException, NoEntityWithSuchIdException, ServerSideJSONException {
        int id = Integer.parseInt(hp.getUrlParts().get(2));
        Product product = productService.getProduct(id);
        responseSender.sendJsonResponse(200, jsonWriter.generateOneEntityReply(product));
    }

    private void processGetProducts(PagingInfo pagingInfo) throws InternalSQLException, ServerSideJSONException {
        List<Product> products = productService.getProducts(pagingInfo);
        pagingInfo.setTotal(productService.getCount());
        String body = jsonWriter.generatePagingReply(products, pagingInfo);
        responseSender.sendJsonResponse(200, body);
    }

    private void handleLoginPath() throws ServerSideJSONException, IllegalArgumentException  {
        String login= hp.getParam("login");
        String password = hp.getParam("password");
        if(password == null || login == null){ throw new IllegalArgumentException("password or login is null"); }
        String body = jsonWriter.generateTokenReply(jwtCoder.generateJws(login,password));
        responseSender.sendJsonResponse(200, body);
    }
}
