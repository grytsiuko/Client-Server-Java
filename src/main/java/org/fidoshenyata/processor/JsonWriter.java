package org.fidoshenyata.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.fidoshenyata.db.model.Category;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.exceptions.db.ServerSideJSONException;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

public class JsonWriter {


    public String generateSuccessMessageReply(String message) throws ServerSideJSONException {
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("message", message);

            mapper.writeValue(outputStream, rootNode);
            return outputStream.toString();
        } catch (Exception e) {
            throw new ServerSideJSONException();
        }
    }


    public String generateOneEntityReply(Category category) throws ServerSideJSONException {
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("id", category.getId());
            rootNode.put("name", category.getName());
            rootNode.put("description", category.getDescription());

            mapper.writeValue(outputStream, rootNode);
            return outputStream.toString();
        } catch (Exception e) {
            throw new ServerSideJSONException();
        }
    }


    public String generateListReply(List<Category> categories) throws ServerSideJSONException {
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode rootNode = mapper.createObjectNode();

            ArrayNode array = rootNode.putArray("content");
            for (Category category : categories) {
                ObjectNode element = array.addObject();
                element.put("id", category.getId());
                element.put("name", category.getName());
                element.put("description", category.getDescription());
            }

            mapper.writeValue(outputStream, rootNode);
            return outputStream.toString();
        } catch (Exception e) {
            throw new ServerSideJSONException();
        }
    }


    public String generatePagingReply(List<Category> categories, PagingInfo pagingInfo) throws ServerSideJSONException {
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("offset", pagingInfo.getOffset());
            rootNode.put("limit", pagingInfo.getLimit());
            rootNode.put("total", pagingInfo.getTotal());

            ArrayNode array = rootNode.putArray("content");
            for (Category category : categories) {
                ObjectNode element = array.addObject();
                element.put("id", category.getId());
                element.put("name", category.getName());
                element.put("description", category.getDescription());
            }

            mapper.writeValue(outputStream, rootNode);
            return outputStream.toString();
        } catch (Exception e) {
            throw new ServerSideJSONException();
        }
    }
}
