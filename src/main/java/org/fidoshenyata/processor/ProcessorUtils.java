package org.fidoshenyata.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.fidoshenyata.db.model.Category;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.exceptions.db.IllegalJSONException;
import org.fidoshenyata.exceptions.db.ServerSideJSONException;
import org.fidoshenyata.packet.Message;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

import static org.fidoshenyata.packet.Message.RESPONSE_ERROR;
import static org.fidoshenyata.packet.Message.RESPONSE_OK;

public class ProcessorUtils {


    public PagingInfo extractPagingInfo(String message) throws IllegalJSONException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readValue(message, JsonNode.class);

            int offset = root.get("offset").asInt();
            int limit = root.get("limit").asInt();

            return new PagingInfo(offset, limit);
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
