package com.fullcycle.admin.catalogo.application.category.create;

import com.fullcycle.admin.catalogo.domain.category.Category;

public record CreateCategoryOutput(
        String id
) {
    public static CreateCategoryOutput from(
            final String aCategoryID) {
        return new CreateCategoryOutput(aCategoryID);
    }

    public static CreateCategoryOutput from(
            final Category aCategory) {
        return new CreateCategoryOutput(aCategory.getId().getValue());
    }
}
