package com.fullcycle.admin.catalogo.application.category.create;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@IntegrationTest
class CreateCategoryUseCaseIT {

    @Autowired
    private CreateCategoryUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidCommand_whenCallsCreateCategory_thenShouldReturnCategoryId() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        // When
        final var actualOutput = useCase.execute(aCommand).get();

        // Then
        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        assertEquals(1, this.categoryRepository.count());

        this.categoryRepository.findById(actualOutput.id())
                .ifPresent(actualCategory -> {
                    assertEquals(expectedName, actualCategory.getName());
                    assertEquals(expectedDescription, actualCategory.getDescription());
                    assertEquals(expectedIsActive, actualCategory.isActive());
                    assertNotNull(actualCategory.getCreatedAt());
                    assertNotNull(actualCategory.getUpdatedAt());
                    assertNull(actualCategory.getDeletedAt());
                });
    }

    @Test
    void givenAInvalidName_whenCallsCreateCategory_thenShouldReturnDomainException() {
        // Given
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null.";
        final var expectedErrorCount = 1;

        final var aCommand =
                CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, this.categoryRepository.count());

        // When
        final var notification = useCase.execute(aCommand).getLeft();

        // Then
        assertNotNull(notification);
        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.getErrors().get(0).message());

        assertEquals(0, this.categoryRepository.count());

        verify(categoryGateway, never()).create(any());
    }

    @Test
    void givenAValidCommandWithInactiveCategory_whenCallsCreateCategory_thenShouldReturnInactiveCategoryId() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, this.categoryRepository.count());

        // When
        final var actualOutput = useCase.execute(aCommand).get();

        // Then
        assertEquals(1, this.categoryRepository.count());

        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        this.categoryRepository.findById(actualOutput.id())
                .ifPresent(actualCategory -> {
                    assertEquals(expectedName, actualCategory.getName());
                    assertEquals(expectedDescription, actualCategory.getDescription());
                    assertEquals(expectedIsActive, actualCategory.isActive());
                    assertNotNull(actualCategory.getCreatedAt());
                    assertNotNull(actualCategory.getUpdatedAt());
                    assertNotNull(actualCategory.getDeletedAt());
                });
    }

    @Test
    void givenAValidCommand_whenGatewayThrowsRandomException_thenShouldReturnAnException() {
        // Given
        final String expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Gateway error.";

        final var aCommand =
                CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        doThrow(new IllegalStateException(expectedErrorMessage))
                .when(categoryGateway).create(any());

        // When
        final var notification = useCase.execute(aCommand).getLeft();

        // Then
        assertNotNull(notification);
        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.getErrors().get(0).message());
    }
}
