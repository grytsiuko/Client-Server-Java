package org.fidoshenyata.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.fidoshenyata.db.model.Category;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.exceptions.db.AbsentFieldsJSONException;
import org.fidoshenyata.exceptions.db.IllegalJSONException;
import org.fidoshenyata.exceptions.db.ServerSideJSONException;
import org.fidoshenyata.packet.Message;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

import static org.fidoshenyata.packet.Message.RESPONSE_ERROR;
import static org.fidoshenyata.packet.Message.RESPONSE_OK;

public class ProcessorUtils {


    public PagingInfo extractPagingInfo(String json) throws IllegalJSONException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readValue(json, JsonNode.class);

            int offset = root.get("offset").asInt();
            int limit = root.get("limit").asInt();

            return new PagingInfo(offset, limit);
        } catch (JsonProcessingException e) {
            throw new IllegalJSONException();
        }
    }


    public Category extractCategory(String json) throws IllegalJSONException, AbsentFieldsJSONException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readValue(json, JsonNode.class);
            System.out.println(json);

            String name = root.get("name").asText();
            String description = getStringFieldIfExists(root, "description");

            return new Category(null, name, description);
        } catch (JsonProcessingException  e) {
            throw new IllegalJSONException();
        } catch (NullPointerException e){
            throw new AbsentFieldsJSONException();
        }
    }

    private String getStringFieldIfExists(JsonNode parent, String field){
        JsonNode jsonNode = parent.get(field);
        if(jsonNode == null)
            return null;
        return jsonNode.asText();
    }


    public String extractName(String json) throws IllegalJSONException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readValue(json, JsonNode.class);

            return root.get("name").asText();
        } catch (JsonProcessingException e) {
            throw new IllegalJSONException();
        }
    }


    public Integer extractId(String json) throws IllegalJSONException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readValue(json, JsonNode.class);

            return root.get("id").asInt();
        } catch (JsonProcessingException e) {
            throw new IllegalJSONException();
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


    public Message buildErrorMessage(String message, int userID) {
        return Message.builder()
                .userID(userID)
                .commandType(RESPONSE_ERROR)
                .message(message)
                .build();
    }


    public Message buildSuccessMessage(String message, int userID) {
        return Message.builder()
                .userID(userID)
                .commandType(RESPONSE_OK)
                .message(message)
                .build();
    }
}
