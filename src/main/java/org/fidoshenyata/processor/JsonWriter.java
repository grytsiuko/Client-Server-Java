package org.fidoshenyata.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.fidoshenyata.db.model.EntityDB;
import org.fidoshenyata.db.model.PagingInfo;
import org.fidoshenyata.exceptions.db.ServerSideJSONException;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
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


    public String generateOneEntityReply(EntityDB entityDB) throws ServerSideJSONException {
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode rootNode = mapper.createObjectNode();
            entityDB.populateJsonNode(rootNode);

            mapper.writeValue(outputStream, rootNode);
            return outputStream.toString();
        } catch (Exception e) {
            throw new ServerSideJSONException();
        }
    }


    public String generateCostReply(BigDecimal cost) throws ServerSideJSONException {
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("cost", cost.toString());

            mapper.writeValue(outputStream, rootNode);
            return outputStream.toString();
        } catch (Exception e) {
            throw new ServerSideJSONException();
        }
    }


    public String generateListReply(List<? extends EntityDB> entitiesDB) throws ServerSideJSONException {
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode rootNode = mapper.createObjectNode();

            ArrayNode array = rootNode.putArray("content");
            for (EntityDB entityDB : entitiesDB) {
                ObjectNode element = array.addObject();
                entityDB.populateJsonNode(element);
            }

            mapper.writeValue(outputStream, rootNode);
            return outputStream.toString();
        } catch (Exception e) {
            throw new ServerSideJSONException();
        }
    }


    public String generatePagingReply(List<? extends EntityDB> entitiesDB, PagingInfo pagingInfo)
            throws ServerSideJSONException {

        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("offset", pagingInfo.getOffset());
            rootNode.put("limit", pagingInfo.getLimit());
            rootNode.put("total", pagingInfo.getTotal());

            ArrayNode array = rootNode.putArray("content");
            for (EntityDB entityDB : entitiesDB) {
                ObjectNode element = array.addObject();
                entityDB.populateJsonNode(element);
            }

            mapper.writeValue(outputStream, rootNode);
            return outputStream.toString();
        } catch (Exception e) {
            throw new ServerSideJSONException();
        }
    }
}
