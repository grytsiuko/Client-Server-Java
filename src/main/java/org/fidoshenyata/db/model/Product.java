package org.fidoshenyata.db.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class Product implements EntityDB {
    private final Integer id;
    @NonNull
    private final String name;
    @NonNull
    private final String producer;
    private final String description;
    private final Integer amount;
    @NonNull
    private final BigDecimal price;
    @NonNull
    private final Integer categoryId;

    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_PRODUCER_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 255;

    @Override
    public void populateJsonNode(ObjectNode node) {
        node.put("id", getId());
        node.put("name", getName());
        node.put("producer", getProducer());
        node.put("description", getDescription());
        node.put("amount", getAmount());
        node.put("price", getPrice().toString());
        node.put("categoryId", getCategoryId());
    }
}
