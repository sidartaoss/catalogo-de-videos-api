package com.fullcycle.admin.catalogo.application.category.retrieve.get;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@IntegrationTest
class GetCategoryByIdUseCaseIT {

    @Autowired
    private GetCategoryByIdUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidId_whenCallsGetCategoryById_thenShouldReturnCategory() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = aCategory.getId();

        this.save(aCategory);

        // When
        final var actualCategory = useCase.execute(expectedId.getValue());

        // Then
        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertEquals(LocalDate.ofInstant(aCategory.createdAt(), ZoneId.from(ZoneOffset.UTC)),
                LocalDate.ofInstant(actualCategory.createdAt(), ZoneId.from(ZoneOffset.UTC)));
        assertEquals(LocalDate.ofInstant(aCategory.updatedAt(), ZoneId.from(ZoneOffset.UTC)),
                LocalDate.ofInstant(actualCategory.updatedAt(), ZoneId.from(ZoneOffset.UTC)));
        assertEquals(aCategory.deletedAt(), actualCategory.deletedAt());
    }

    @Test
    void givenAnInvalidId_whenCallsGetCategoryById_thenShouldReturnNotFound() {
        // Given
        final var expectedErrorMessage = "Category with ID 123 was not found.";
        final var expectedId = CategoryID.from("123");

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

        doThrow(new IllegalStateException(expectedErrorMessage))
                .when(categoryGateway).findById(expectedId);

        // When
        assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        // Then
        verify(categoryGateway, times(1)).findById(expectedId);
    }

    private void save(final Category... aCategory) {
        this.categoryRepository.saveAllAndFlush(
                Arrays.stream(aCategory)
                        .map(CategoryJpaEntity::from)
                        .toList()
        );
    }
}
