package com.fullcycle.admin.catalogo.application.category.delete;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DeleteCategoryUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultDeleteCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway);
    }

    @Test
    void givenAValidId_whenCallsDeleteCategory_thenShouldBeOk() {
        // Given
        final var aCategory = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var expectedId = aCategory.getId();

        doNothing()
                .when(categoryGateway).deleteById(expectedId);

        // When
        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        // Then
        verify(categoryGateway, times(1)).deleteById(expectedId);
    }

    @Test
    void givenAnInvalidId_whenCallsDeleteCategory_thenShouldBeOk() {
        // Given
        final var expectedId = CategoryID.from("123");

        doNothing()
                .when(categoryGateway).deleteById(expectedId);

        // When
        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        // Then
        verify(categoryGateway, times(1)).deleteById(expectedId);
    }

    @Test
    void givenAValidId_whenGatewayThrowsError_thenShouldReturnException() {
        // Given
        final var expectedId = CategoryID.from("123");
        final var expectedErrorMessage = "Gateway error.";

        doThrow(new IllegalStateException(expectedErrorMessage))
                .when(categoryGateway).deleteById(expectedId);

        // When
        assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        // Then
        verify(categoryGateway, times(1)).deleteById(expectedId);
    }
}