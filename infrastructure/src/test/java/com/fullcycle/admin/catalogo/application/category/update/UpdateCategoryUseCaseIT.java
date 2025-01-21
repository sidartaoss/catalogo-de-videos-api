package com.fullcycle.admin.catalogo.application.category.update;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@IntegrationTest
public class UpdateCategoryUseCaseIT {

    @Autowired
    private UpdateCategoryUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidCommand_whenCallsUpdateCategory_thenShouldReturnCategoryId() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory("film", null, true);
        final var expectedId = aCategory.getId();

        this.save(aCategory);

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);

        assertEquals(1, this.categoryRepository.count());

        // When
        final var actualOutput = useCase.execute(aCommand).get();

        // Then
        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        this.categoryRepository.findById(actualOutput.id())
                .ifPresent(actualCategory -> {
                    assertEquals(expectedName, actualCategory.getName());
                    assertEquals(expectedDescription, actualCategory.getDescription());
                    assertEquals(expectedIsActive, actualCategory.isActive());
                    assertEquals(LocalDate.ofInstant(aCategory.createdAt(), ZoneId.from(ZoneOffset.UTC)),
                            LocalDate.ofInstant(actualCategory.getCreatedAt(), ZoneId.from(ZoneOffset.UTC)));
                    assertEquals(LocalDate.ofInstant(aCategory.updatedAt(), ZoneId.from(ZoneOffset.UTC)),
                            LocalDate.ofInstant(actualCategory.getUpdatedAt(), ZoneId.from(ZoneOffset.UTC)));
                    assertTrue(aCategory.updatedAt().isBefore(actualCategory.getUpdatedAt()));
                    assertEquals(aCategory.deletedAt(), actualCategory.getDeletedAt());
                    assertNull(actualCategory.getDeletedAt());
                });
    }

    @Test
    void givenAnInvalidName_whenCallsUpdateCategory_thenShouldReturnDomainException() {
        // Given
        final var aCategory = Category.newCategory("film", null, true);
        final var expectedId = aCategory.getId();

        this.save(aCategory);

        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var expectedErrorMessage = "'name' should not be null.";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);

        // When
        final var notification = useCase.execute(aCommand).getLeft();

        // Then
        assertNotNull(notification);
        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.getErrors().get(0).message());

        verify(categoryGateway, never()).update(any());
    }

    @Test
    void givenAValidCommandWithInactiveCategory_whenCallsUpdateCategory_thenShouldReturnInactiveCategoryId() {
        // Given
        final var aCategory = Category.newCategory("film", null, true);
        final var expectedId = aCategory.getId();

        this.save(aCategory);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);

        // When
        final var actualOutput = useCase.execute(aCommand).get();

        // Then
        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        this.categoryRepository.findById(actualOutput.id())
                .ifPresent(actualCategory -> {
                    assertEquals(expectedName, actualCategory.getName());
                    assertEquals(expectedDescription, actualCategory.getDescription());
                    assertEquals(expectedIsActive, actualCategory.isActive());
                    assertEquals(LocalDate.ofInstant(aCategory.createdAt(), ZoneId.from(ZoneOffset.UTC)),
                            LocalDate.ofInstant(actualCategory.getCreatedAt(), ZoneId.from(ZoneOffset.UTC)));
                    assertEquals(LocalDate.ofInstant(aCategory.updatedAt(), ZoneId.from(ZoneOffset.UTC)),
                            LocalDate.ofInstant(actualCategory.getUpdatedAt(), ZoneId.from(ZoneOffset.UTC)));
                    assertTrue(aCategory.updatedAt().isBefore(actualCategory.getUpdatedAt()));
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

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = aCategory.getId();

        this.save(aCategory);

        final var aCommand =
                UpdateCategoryCommand.with(
                        expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        doThrow(new IllegalStateException(expectedErrorMessage))
                .when(categoryGateway).update(any());

        // When
        final var notification = useCase.execute(aCommand).getLeft();

        // Then
        assertNotNull(notification);
        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.getErrors().get(0).message());

        this.categoryRepository.findById(expectedId.getValue())
                .ifPresent(actualCategory -> {
                    assertEquals(expectedName, actualCategory.getName());
                    assertEquals(expectedDescription, actualCategory.getDescription());
                    assertEquals(expectedIsActive, actualCategory.isActive());
                    assertEquals(LocalDate.ofInstant(aCategory.createdAt(), ZoneId.from(ZoneOffset.UTC)),
                            LocalDate.ofInstant(actualCategory.getCreatedAt(), ZoneId.from(ZoneOffset.UTC)));
                    assertEquals(LocalDate.ofInstant(aCategory.updatedAt(), ZoneId.from(ZoneOffset.UTC)),
                            LocalDate.ofInstant(actualCategory.getUpdatedAt(), ZoneId.from(ZoneOffset.UTC)));
                    assertNull(actualCategory.getDeletedAt());
                });
    }

    @Test
    void givenACommandWithInvalidID_whenCallsUpdateCategory_thenShouldReturnNotFoundException() {
        // Given
        final var expectedId = "123";

        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var expectedErrorMessage = "Category with ID %s was not found.".formatted(expectedId);

        final var aCommand = UpdateCategoryCommand.with(
                expectedId,
                expectedName,
                expectedDescription,
                expectedIsActive);

        // When
        final var actualException = assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        // Then
        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    private void save(final Category... aCategory) {
        this.categoryRepository.saveAllAndFlush(
                Arrays.stream(aCategory)
                        .map(CategoryJpaEntity::from)
                        .toList()
        );
    }
}
