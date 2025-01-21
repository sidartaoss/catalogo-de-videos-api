package com.fullcycle.admin.catalogo.domain.category;

import com.fullcycle.admin.catalogo.domain.UnitTest;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest extends UnitTest {

    @Test
    void givenAValidParams_whenCallNewCategory_thenInstantiateACategory() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        // When
        final var actualCategory =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        // Then
        assertNotNull(actualCategory);
        assertNotNull(actualCategory.getId());
        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertEquals(expectedIsActive, actualCategory.active());
        assertNotNull(actualCategory.createdAt());
        assertNull(actualCategory.deletedAt());
    }

    @Test
    void givenAnInvalidNullName_whenCallNewCategoryAndValidate_thenShouldReceiveError() {
        // Given
        final String expectedName = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null.";
        final var expectedDescription = "A categoria mais assistida.";
        final var expectedIsActive = true;

        // When
        final var actualCategory =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        // Then
        final var actualException = assertThrows(DomainException.class, () -> actualCategory.validate(new ThrowsValidationHandler()));
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyName_whenCallNewCategoryAndValidate_thenShouldReceiveError() {
        // Given
        final var expectedName = " ";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty.";
        final var expectedDescription = "A categoria mais assistida.";
        final var expectedIsActive = true;

        // When
        final var actualCategory =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        // Then
        final var actualException = assertThrows(DomainException.class, () -> actualCategory.validate(
                new ThrowsValidationHandler()));
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNameLengthLessThan3_whenCallNewCategoryAndValidate_thenShouldReceiveError() {
        // Given
        final var expectedName = "Fi ";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 3 and 255.";
        final var expectedDescription = "A categoria mais assistida.";
        final var expectedIsActive = true;

        // When
        final var actualCategory =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        // Then
        final var actualException = assertThrows(DomainException.class, () -> actualCategory.validate(
                new ThrowsValidationHandler()));
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNameMoreThan255_whenCallNewCategoryAndValidate_thenShouldReceiveError() {
        // Given
        final var expectedName = """
                No entanto, não podemos esquecer que a determinação clara de objetivos acarreta um processo de reformulação e modernização do sistema de formação de quadros que corresponde às necessidades.
                No entanto, não podemos esquecer que a determinação clara de objetivos acarreta um processo de reformulação e modernização do sistema de formação de quadros que corresponde às necessidades.
                """;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 3 and 255.";
        final var expectedDescription = "A categoria mais assistida.";
        final var expectedIsActive = true;

        // When
        final var actualCategory =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        // Then
        final var actualException = assertThrows(DomainException.class, () -> actualCategory.validate(
                new ThrowsValidationHandler()));
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAValidEmptyDescription_whenCallNewCategory_thenInstantiateACategory() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = " ";
        final var expectedIsActive = true;

        // When
        final var actualCategory =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        // Then
        assertDoesNotThrow(() -> actualCategory.validate(new ThrowsValidationHandler()));
        assertNotNull(actualCategory);
        assertNotNull(actualCategory.getId());
        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertEquals(expectedIsActive, actualCategory.active());
        assertNotNull(actualCategory.createdAt());
        assertNull(actualCategory.deletedAt());
    }

    @Test
    void givenAValidFalseIsActive_whenCallNewCategory_thenInstantiateACategory() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        // When
        final var actualCategory =
                Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        // Then
        assertDoesNotThrow(() -> actualCategory.validate(new ThrowsValidationHandler()));
        assertNotNull(actualCategory);
        assertNotNull(actualCategory.getId());
        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertEquals(expectedIsActive, actualCategory.active());
        assertNotNull(actualCategory.createdAt());
        assertNotNull(actualCategory.deletedAt());
    }

    @Test
    void givenAValidActiveCategory_whenCallDeactivate_thenReturnCategoryInactivated() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        // When
        final var aCategory =
                Category.newCategory(expectedName, expectedDescription, true);

        // Then
        assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));
        final var createdAt = aCategory.createdAt();
        final var updatedAt = aCategory.updatedAt();

        assertTrue(aCategory.active());
        assertNull(aCategory.deletedAt());

        final var actualCategory = aCategory.deactivate();

        assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));

        assertEquals(aCategory.getId(), actualCategory.getId());
        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertEquals(expectedIsActive, actualCategory.active());
        assertNotNull(actualCategory.createdAt());
        assertEquals(createdAt, actualCategory.createdAt());
        assertTrue(actualCategory.updatedAt().isAfter(updatedAt));
        assertNotNull(actualCategory.deletedAt());
    }

    @Test
    void givenAValidInactiveCategory_whenCallActivate_thenReturnCategoryActivated() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        // When
        final var aCategory =
                Category.newCategory(expectedName, expectedDescription, false);

        // Then
        assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));

        final var createdAt = aCategory.createdAt();
        final var updatedAt = aCategory.updatedAt();

        assertFalse(aCategory.active());
        assertNotNull(aCategory.deletedAt());

        final var actualCategory = aCategory.activate();

        assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));

        assertEquals(aCategory.getId(), actualCategory.getId());
        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertEquals(expectedIsActive, actualCategory.active());
        assertNotNull(actualCategory.createdAt());
        assertEquals(createdAt, actualCategory.createdAt());
        assertTrue(actualCategory.updatedAt().isAfter(updatedAt));
        assertNull(actualCategory.deletedAt());
    }

    @Test
    void givenAValidCategory_whenCallUpdate_thenReturnCategoryUpdated() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory =
                Category.newCategory("Film", "A categoria", false);

        assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));

        final var createdAt = aCategory.createdAt();
        final var updatedAt = aCategory.updatedAt();

        // When
        final var actualCategory = aCategory.update(expectedName, expectedDescription, expectedIsActive);

        // Then
        assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));

        assertEquals(aCategory.getId(), actualCategory.getId());
        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertEquals(expectedIsActive, actualCategory.active());
        assertNotNull(actualCategory.createdAt());
        assertEquals(createdAt, actualCategory.createdAt());
        assertTrue(actualCategory.updatedAt().isAfter(updatedAt));
        assertNull(actualCategory.deletedAt());
    }

    @Test
    void givenAValidCategory_whenCallUpdateToInactive_thenReturnCategoryUpdated() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var aCategory =
                Category.newCategory("Film", "A categoria", true);

        assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));

        assertTrue(aCategory.active());
        assertNull(aCategory.deletedAt());

        final var createdAt = aCategory.createdAt();
        final var updatedAt = aCategory.updatedAt();

        // When
        final var actualCategory = aCategory.update(expectedName, expectedDescription, expectedIsActive);

        // Then
        assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));

        assertEquals(aCategory.getId(), actualCategory.getId());
        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertFalse(aCategory.active());
        assertNotNull(aCategory.deletedAt());
        assertEquals(expectedIsActive, actualCategory.active());
        assertNotNull(actualCategory.createdAt());
        assertEquals(createdAt, actualCategory.createdAt());
        assertTrue(actualCategory.updatedAt().isAfter(updatedAt));
    }

    @Test
    void givenAValidCategory_whenCallUpdateWithInvalidParam_thenReturnCategoryUpdated() {
        // Given
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory =
                Category.newCategory("Film", "A categoria", expectedIsActive);

        assertDoesNotThrow(() -> aCategory.validate(new ThrowsValidationHandler()));

        final var createdAt = aCategory.createdAt();
        final var updatedAt = aCategory.updatedAt();

        // When
        final var actualCategory = aCategory.update(expectedName, expectedDescription, expectedIsActive);

        // Then
        assertEquals(aCategory.getId(), actualCategory.getId());
        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertNull(aCategory.deletedAt());
        assertTrue(actualCategory.active());
        assertNotNull(actualCategory.createdAt());
        assertEquals(createdAt, actualCategory.createdAt());
        assertTrue(actualCategory.updatedAt().isAfter(updatedAt));
    }
}