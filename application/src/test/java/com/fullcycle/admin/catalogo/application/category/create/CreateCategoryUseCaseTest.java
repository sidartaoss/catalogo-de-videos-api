package com.fullcycle.admin.catalogo.application.category.create;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateCategoryUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultCreateCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway);
    }

    @Test
    void givenAValidCommand_whenCallsCreateCategory_thenShouldReturnCategoryId() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        // When
        final var actualOutput = useCase.execute(aCommand).get();

        // Then
        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        verify(categoryGateway, times(1)).create(argThat(aCategory ->
                Objects.equals(expectedName, aCategory.name()) &&
                        Objects.equals(expectedDescription, aCategory.description()) &&
                        Objects.equals(expectedIsActive, aCategory.active()) &&
                        Objects.nonNull(aCategory.getId()) &&
                        Objects.nonNull(aCategory.createdAt()) &&
                        Objects.nonNull(aCategory.updatedAt()) &&
                        Objects.isNull(aCategory.deletedAt())
        ));
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

        // When
        final var notification = useCase.execute(aCommand).getLeft();

        // Then
        assertNotNull(notification);
        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.getErrors().get(0).message());

        verify(categoryGateway, never()).create(any());
    }

    @Test
    void givenAValidCommandWithInactiveCategory_whenCallsCreateCategory_thenShouldReturnInactiveCategoryId() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        // When
        final var actualOutput = useCase.execute(aCommand).get();

        // Then
        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        verify(categoryGateway, times(1)).create(argThat(aCategory ->
                Objects.equals(expectedName, aCategory.name()) &&
                        Objects.equals(expectedDescription, aCategory.description()) &&
                        Objects.equals(expectedIsActive, aCategory.active()) &&
                        Objects.nonNull(aCategory.getId()) &&
                        Objects.nonNull(aCategory.createdAt()) &&
                        Objects.nonNull(aCategory.updatedAt()) &&
                        Objects.nonNull(aCategory.deletedAt())
        ));
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

        when(categoryGateway.create(any()))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        // When
        final var notification = useCase.execute(aCommand).getLeft();

        // Then
        assertNotNull(notification);
        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.getErrors().get(0).message());

        verify(categoryGateway, times(1)).create(argThat(aCategory ->
                Objects.equals(expectedName, aCategory.name()) &&
                        Objects.equals(expectedDescription, aCategory.description()) &&
                        Objects.equals(expectedIsActive, aCategory.active()) &&
                        Objects.nonNull(aCategory.getId()) &&
                        Objects.nonNull(aCategory.createdAt()) &&
                        Objects.nonNull(aCategory.updatedAt()) &&
                        Objects.isNull(aCategory.deletedAt())
        ));
    }
}
