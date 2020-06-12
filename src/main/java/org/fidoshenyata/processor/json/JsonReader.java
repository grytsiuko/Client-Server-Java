package org.fidoshenyata.processor.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fidoshenyata.db.model.Category;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.db.model.Product;
import org.fidoshenyata.exceptions.db.AbsentFieldsJSONException;
import org.fidoshenyata.exceptions.db.IllegalJSONException;

import java.math.BigDecimal;

public class JsonReader {


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


    public Integer extractId(String json) throws IllegalJSONException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readValue(json, JsonNode.class);

            return root.get("id").asInt();
        } catch (JsonProcessingException e) {
            throw new IllegalJSONException();
        }
    }


    public Integer extractCategoryId(String json) throws IllegalJSONException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readValue(json, JsonNode.class);

            return root.get("categoryId").asInt();
        } catch (JsonProcessingException e) {
            throw new IllegalJSONException();
        }
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


    public Integer extractAmount(String json) throws IllegalJSONException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readValue(json, JsonNode.class);

            return root.get("amount").asInt();
        } catch (JsonProcessingException e) {
            throw new IllegalJSONException();
        }
    }


    public Category extractCategory(String json) throws IllegalJSONException, AbsentFieldsJSONException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readValue(json, JsonNode.class);

            Integer id = getIntFieldIfExists(root, "id");
            String name = root.get("name").asText();
            String description = getStringFieldIfExists(root, "description");

            return new Category(id, name, description);
        } catch (JsonProcessingException e) {
            throw new IllegalJSONException();
        } catch (NullPointerException e) {
            throw new AbsentFieldsJSONException();
        }
    }


    public Product extractProduct(String json) throws IllegalJSONException, AbsentFieldsJSONException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readValue(json, JsonNode.class);

            Integer id = getIntFieldIfExists(root, "id");
            String name = root.get("name").asText();
            String producer = root.get("producer").asText();
            String description = getStringFieldIfExists(root, "description");
            Integer amount = getIntFieldIfExists(root, "amount");
            BigDecimal price = new BigDecimal(root.get("price").asText());
            Integer categoryId = root.get("categoryId").asInt();

            return new Product(id, name, producer, description, amount, price, categoryId);
        } catch (JsonProcessingException | NumberFormatException e) {
            throw new IllegalJSONException();
        } catch (NullPointerException e) {
            throw new AbsentFieldsJSONException();
        }
    }

    private String getStringFieldIfExists(JsonNode parent, String field) {
        JsonNode jsonNode = parent.get(field);
        if (jsonNode == null || jsonNode.isNull())
            return null;
        return jsonNode.asText();
    }

    private Integer getIntFieldIfExists(JsonNode parent, String field) {
        JsonNode jsonNode = parent.get(field);
        if (jsonNode == null || jsonNode.isNull())
            return null;
        return jsonNode.asInt();
    }
}
