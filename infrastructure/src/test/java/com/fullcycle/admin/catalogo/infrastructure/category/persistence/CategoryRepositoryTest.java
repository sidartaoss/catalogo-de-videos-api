package com.fullcycle.admin.catalogo.infrastructure.category.persistence;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.MySQLGatewayTest;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

@MySQLGatewayTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void givenAnInvalidNullName_whenCallsSave_thenShouldReturnError() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var anEntity = CategoryJpaEntity.from(aCategory);

        anEntity.setName(null);

        final var expectedPropertyName = "name";
        final var expectedErrorMessage =
                "not-null property references a null or transient value : com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity.name";

        // When
        final var actualException =
                assertThrows(DataIntegrityViolationException.class, () -> this.categoryRepository.save(anEntity));

        // Then
        final var actualCause =
                assertInstanceOf(PropertyValueException.class, actualException.getCause());

        assertEquals(expectedPropertyName, actualCause.getPropertyName());
        assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullCreatedAt_whenCallsSave_thenShouldReturnError() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var anEntity = CategoryJpaEntity.from(aCategory);

        anEntity.setCreatedAt(null);

        final var expectedPropertyName = "createdAt";
        final var expectedErrorMessage =
                "not-null property references a null or transient value : com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity.createdAt";

        // When
        final var actualException =
                assertThrows(DataIntegrityViolationException.class, () -> this.categoryRepository.save(anEntity));

        // Then
        final var actualCause =
                assertInstanceOf(PropertyValueException.class, actualException.getCause());

        assertEquals(expectedPropertyName, actualCause.getPropertyName());
        assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullUpdatedAt_whenCallsSave_thenShouldReturnError() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var anEntity = CategoryJpaEntity.from(aCategory);

        anEntity.setUpdatedAt(null);

        final var expectedPropertyName = "updatedAt";
        final var expectedErrorMessage =
                "not-null property references a null or transient value : com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity.updatedAt";

        // When
        final var actualException =
                assertThrows(DataIntegrityViolationException.class, () -> this.categoryRepository.save(anEntity));

        // Then
        final var actualCause =
                assertInstanceOf(PropertyValueException.class, actualException.getCause());

        assertEquals(expectedPropertyName, actualCause.getPropertyName());
        assertEquals(expectedErrorMessage, actualCause.getMessage());
    }
}