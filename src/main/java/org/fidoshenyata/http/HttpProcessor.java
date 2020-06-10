package org.fidoshenyata.http;

import io.jsonwebtoken.ExpiredJwtException;
import org.fidoshenyata.db.connection.AbstractConnectionFactory;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.db.model.Product;
import org.fidoshenyata.exceptions.db.InternalSQLException;
import org.fidoshenyata.exceptions.db.NoEntityWithSuchIdException;
import org.fidoshenyata.exceptions.db.ServerSideJSONException;
import org.fidoshenyata.exceptions.http.NoSuchPathException;
import org.fidoshenyata.exceptions.http.NotAuthException;
import org.fidoshenyata.processor.json.JsonReader;
import org.fidoshenyata.processor.json.JsonWriter;
import org.fidoshenyata.service.CategoryService;
import org.fidoshenyata.service.ProductService;

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
                    break;
                case "PUT":
                    break;
                case "DELETE":
                    break;
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
