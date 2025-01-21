package com.fullcycle.admin.catalogo.application.category.delete;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@IntegrationTest
class DeleteCategoryUseCaseIT {

    @Autowired
    private DeleteCategoryUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidId_whenCallsDeleteCategory_thenShouldBeOk() {
        // Given
        final var aCategory = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var expectedId = aCategory.getId();

        this.save(aCategory);

        assertEquals(1, this.categoryRepository.count());

        // When
        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        // Then
        assertEquals(0, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidId_whenCallsDeleteCategory_thenShouldBeOk() {
        // Given
        final var expectedId = CategoryID.from("123");

        assertEquals(0, this.categoryRepository.count());

        // When
        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        // Then
        assertEquals(0, this.categoryRepository.count());
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

    private void save(final Category... aCategory) {
        this.categoryRepository.saveAllAndFlush(
                Arrays.stream(aCategory)
                        .map(CategoryJpaEntity::from)
                        .toList()
        );
    }
}
