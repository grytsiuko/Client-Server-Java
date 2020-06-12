package org.fidoshenyata.db.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.NonNull;

@Data
public class NamedId implements EntityDB{
    private final Integer id;
    @NonNull
    private final String name;

    @Override
    public void populateJsonNode(ObjectNode node) {
        node.put("id", getId());
        node.put("name", getName());
    }
}
