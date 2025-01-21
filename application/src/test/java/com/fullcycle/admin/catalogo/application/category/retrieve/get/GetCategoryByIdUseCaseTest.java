package com.fullcycle.admin.catalogo.application.category.retrieve.get;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GetCategoryByIdUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultGetCategoryByIdUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway);
    }

    @Test
    void givenAValidId_whenCallsGetCategoryById_thenShouldReturnCategory() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = aCategory.getId();

        when(categoryGateway.findById(expectedId))
                .thenReturn(Optional.of(Category.with(aCategory)));

        // When
        final var actualCategory = useCase.execute(expectedId.getValue());

        // Then
        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertEquals(aCategory.createdAt(), actualCategory.createdAt());
        assertEquals(aCategory.updatedAt(), actualCategory.updatedAt());
        assertEquals(aCategory.deletedAt(), actualCategory.deletedAt());
    }

    @Test
    void givenAnInvalidId_whenCallsGetCategoryById_thenShouldReturnNotFound() {
        // Given
        final var expectedErrorMessage = "Category with ID 123 was not found.";
        final var expectedId = CategoryID.from("123");

        when(categoryGateway.findById(expectedId))
                .thenReturn(Optional.empty());

        // When
        final var actualException = assertThrows(DomainException.class, () -> useCase.execute(expectedId.getValue()));

        // Then
        verify(categoryGateway, times(1)).findById(expectedId);
        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    void givenAValidId_whenGatewayThrowsException_thenShouldReturnException() {
        // Given
        final var expectedId = CategoryID.from("123");
        final var expectedErrorMessage = "Gateway error";

        when(categoryGateway.findById(expectedId))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        // When
        assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        // Then
        verify(categoryGateway, times(1)).findById(expectedId);
    }
}