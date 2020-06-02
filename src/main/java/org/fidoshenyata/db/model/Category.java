package org.fidoshenyata.db.model;


import lombok.Data;
import lombok.NonNull;

@Data
public class Category {
    @NonNull
    private final Integer id;
    @NonNull
    private final String name;
    private final String description;
}
