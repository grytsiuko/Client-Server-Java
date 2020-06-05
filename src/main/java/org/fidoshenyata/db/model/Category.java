package org.fidoshenyata.db.model;


import lombok.Data;
import lombok.NonNull;

@Data
public class Category {
    private final Integer id;
    @NonNull
    private final String name;
    private final String description;

    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 255;
}
