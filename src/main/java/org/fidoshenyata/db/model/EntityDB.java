package org.fidoshenyata.db.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface EntityDB {

    void populateJsonNode(ObjectNode node);

}
