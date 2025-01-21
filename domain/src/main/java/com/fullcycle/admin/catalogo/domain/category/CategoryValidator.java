package com.fullcycle.admin.catalogo.domain.category;

import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalogo.domain.validation.Validator;

public class CategoryValidator extends Validator {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 255;
    private final Category category;

    public CategoryValidator(final Category aCategory, final ValidationHandler aHandler) {
        super(aHandler);
        this.category = aCategory;
    }

    @Override
    public void validate() {
        checkNameConstraints();
    }

    private void checkNameConstraints() {
        final var name = this.category.name();
        if (name == null) {
            this.validationHandler().append(new Error("'name' should not be null."));
            return;
        }
        if (name.isBlank()) {
            this.validationHandler().append(new Error("'name' should not be empty."));
            return;
        }
        final var length = name.trim().length();
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            this.validationHandler().append(new Error("'name' must be between 3 and 255."));
        }
    }
}
