package org.fidoshenyata.db.model;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class Product {
    @NonNull
    private final Integer id;
    @NonNull
    private final String name;
    @NonNull
    private final String producer;
    private final String description;
    @NonNull
    private final Integer amount;
    @NonNull
    private final BigDecimal price;
    @NonNull
    private final Integer categoryId;
}
