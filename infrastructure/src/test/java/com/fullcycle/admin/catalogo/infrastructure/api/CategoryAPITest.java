package com.fullcycle.admin.catalogo.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalogo.ApiTest;
import com.fullcycle.admin.catalogo.ControllerTest;
import com.fullcycle.admin.catalogo.application.category.create.CreateCategoryOutput;
import com.fullcycle.admin.catalogo.application.category.create.CreateCategoryUseCase;
import com.fullcycle.admin.catalogo.application.category.delete.DeleteCategoryUseCase;
import com.fullcycle.admin.catalogo.application.category.retrieve.get.CategoryOutput;
import com.fullcycle.admin.catalogo.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.fullcycle.admin.catalogo.application.category.retrieve.list.CategoryListOutput;
import com.fullcycle.admin.catalogo.application.category.retrieve.list.ListCategoriesUseCase;
import com.fullcycle.admin.catalogo.application.category.update.UpdateCategoryOutput;
import com.fullcycle.admin.catalogo.application.category.update.UpdateCategoryUseCase;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;
import com.fullcycle.admin.catalogo.infrastructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalogo.infrastructure.category.models.UpdateCategoryRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static com.fullcycle.admin.catalogo.ApiTest.CATEGORIES_JWT;
import static io.vavr.API.Left;
import static io.vavr.API.Right;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = CategoryAPI.class)
class CategoryAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateCategoryUseCase createCategoryUseCase;

    @MockBean
    private GetCategoryByIdUseCase getCategoryByIdUseCase;

    @MockBean
    private UpdateCategoryUseCase updateCategoryUseCase;

    @MockBean
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @MockBean
    private ListCategoriesUseCase listCategoriesUseCase;

    @Test
    void givenAValidRequest_whenCallsCreateCategory_thenShouldReturnCategoryId() throws Exception {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCreateCategoryRequest = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        when(this.createCategoryUseCase.execute(any()))
                .thenReturn(Right(new CreateCategoryOutput("123")));

        final var request = post("/categories")
                .with(CATEGORIES_JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aCreateCategoryRequest))
                .accept(MediaType.APPLICATION_JSON);

        // When
        final var aResult = this.mockMvc.perform(request)
                .andDo(print());

        // Then
        aResult
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/categories/123"))
                .andExpect(jsonPath("$.id", equalTo("123")));

        verify(createCategoryUseCase, times(1)).execute(argThat(command ->
                Objects.equals(expectedName, command.name()) &&
                        Objects.equals(expectedDescription, command.description()) &&
                        Objects.equals(expectedIsActive, command.isActive())
        ));
    }

    @Test
    void givenAnInvalidName_whenCallsCreateCategory_thenShouldReturnNotification() throws Exception {
        // Given
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCreateCategoryRequest = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var expectedErrorMessage = "'name' should not be null.";

        when(this.createCategoryUseCase.execute(any()))
                .thenReturn(Left(Notification.create(new Error(expectedErrorMessage))));

        final var request = post("/categories")
                .with(CATEGORIES_JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aCreateCategoryRequest))
                .accept(MediaType.APPLICATION_JSON);

        // When
        final var response = this.mockMvc.perform(request)
                .andDo(print());

        // Then
        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(createCategoryUseCase, times(1)).execute(argThat(command ->
                Objects.equals(expectedName, command.name()) &&
                        Objects.equals(expectedDescription, command.description()) &&
                        Objects.equals(expectedIsActive, command.isActive())
        ));
    }

    @Test
    void givenAnInvalidRequest_whenCallsCreateCategory_thenShouldReturnDomainException() throws Exception {
        // Given
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCreateCategoryRequest = new CreateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var expectedErrorMessage = "'name' should not be null.";

        when(this.createCategoryUseCase.execute(any()))
                .thenThrow(DomainException.with(new Error(expectedErrorMessage)));

        final var request = post("/categories")
                .with(CATEGORIES_JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aCreateCategoryRequest))
                .accept(MediaType.APPLICATION_JSON);

        // When
        final var response = this.mockMvc.perform(request)
                .andDo(print());

        // Then
        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(createCategoryUseCase, times(1)).execute(argThat(command ->
                Objects.equals(expectedName, command.name()) &&
                        Objects.equals(expectedDescription, command.description()) &&
                        Objects.equals(expectedIsActive, command.isActive())
        ));
    }

    @Test
    void givenAValidId_whenCallsGetCategoryById_thenShouldReturnCategory() throws Exception {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = aCategory.getId().getValue();

        when(this.getCategoryByIdUseCase.execute(any()))
                .thenReturn(CategoryOutput.from(aCategory));

        final var request = get("/categories/{id}", expectedId)
                .with(CATEGORIES_JWT)
                .accept(MediaType.APPLICATION_JSON);

        // When
        final var response = this.mockMvc.perform(request)
                .andDo(print());

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(expectedId)))
                .andExpect(jsonPath("$.name", equalTo(expectedName)))
                .andExpect(jsonPath("$.description", equalTo(expectedDescription)))
                .andExpect(jsonPath("$.is_active", equalTo(expectedIsActive)))
                .andExpect(jsonPath("$.created_at", equalTo(aCategory.createdAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(aCategory.updatedAt().toString())))
                .andExpect(jsonPath("$.deleted_at", equalTo(aCategory.deletedAt())));

        verify(getCategoryByIdUseCase, times(1)).execute(expectedId);
    }

    @Test
    void givenAnInvalidId_whenCallsGetCategoryById_thenShouldReturnNotFound() throws Exception {
        // Given
        final var expectedErrorMessage = "Category with ID 123 was not found.";
        final var expectedId = CategoryID.from("123");

        when(getCategoryByIdUseCase.execute(any()))
                .thenThrow(NotFoundException.with(Category.class, expectedId));

        final var request = get("/categories/{id}", expectedId.getValue())
                .with(CATEGORIES_JWT)
                .accept(MediaType.APPLICATION_JSON);

        // When
        final var response = this.mockMvc.perform(request)
                .andDo(print());

        // Then
        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(getCategoryByIdUseCase, times(1)).execute(expectedId.getValue());
    }

    @Test
    void givenAValidCommand_whenCallsUpdateCategory_thenShouldReturnCategoryId() throws Exception {
        // Given
        final var expectedId = "123";
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        when(this.updateCategoryUseCase.execute(any()))
                .thenReturn(Right(UpdateCategoryOutput.from(expectedId)));

        final var updateCategoryRequest = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var request = put("/categories/{id}", expectedId)
                .with(CATEGORIES_JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(updateCategoryRequest));

        // When
        final var response = this.mockMvc.perform(request)
                .andDo(print());

        // Then
        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(updateCategoryUseCase, times(1)).execute(argThat(command ->
                Objects.equals(expectedId, command.id()) &&
                        Objects.equals(expectedName, command.name()) &&
                        Objects.equals(expectedDescription, command.description()) &&
                        Objects.equals(expectedIsActive, command.isActive())
        ));
    }

    @Test
    void givenAnInvalidName_whenCallsUpdateCategory_thenShouldReturnDomainException() throws Exception {
        // Given
        final var expectedId = "123";
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var expectedErrorMessage = "'name' should not be null.";
        final var expectedErrorsCount = 1;

        when(updateCategoryUseCase.execute(any()))
                .thenReturn(Left(Notification.create(new Error(expectedErrorMessage))));

        final var updateCategoryRequest = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var request = put("/categories/{id}", expectedId)
                .with(CATEGORIES_JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(updateCategoryRequest));

        // When
        final var response = this.mockMvc.perform(request)
                .andDo(print());

        // Then
        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(expectedErrorsCount)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(updateCategoryUseCase, times(1)).execute(argThat(command ->
                Objects.equals(expectedId, command.id()) &&
                        Objects.equals(expectedName, command.name()) &&
                        Objects.equals(expectedDescription, command.description()) &&
                        Objects.equals(expectedIsActive, command.isActive())
        ));
    }

    @Test
    void givenACommandWithInvalidID_whenCallsUpdateCategory_thenShouldReturnNotFoundException() throws Exception {
        // Given
        final var expectedId = "not-found";
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var expectedErrorMessage = "Category with ID not-found was not found.";

        when(updateCategoryUseCase.execute(any()))
                .thenThrow(NotFoundException.with(Category.class, CategoryID.from(expectedId)));

        final var updateCategoryRequest = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var request = put("/categories/{id}", expectedId)
                .with(CATEGORIES_JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(updateCategoryRequest));

        // When
        final var response = this.mockMvc.perform(request)
                .andDo(print());

        // Then
        response.andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(updateCategoryUseCase, times(1)).execute(argThat(command ->
                Objects.equals(expectedId, command.id()) &&
                        Objects.equals(expectedName, command.name()) &&
                        Objects.equals(expectedDescription, command.description()) &&
                        Objects.equals(expectedIsActive, command.isActive())
        ));
    }

    @Test
    void givenAValidId_whenCallsDeleteCategory_thenShouldReturnNoContent() throws Exception {
        // Given
        final var expectedId = "123";

        doNothing()
                .when(this.deleteCategoryUseCase).execute(any());

        final var request = delete("/categories/{id}", expectedId)
                .with(CATEGORIES_JWT)
                .accept(MediaType.APPLICATION_JSON);

        // When
        final var response = this.mockMvc.perform(request)
                .andDo(print());

        // Then
        response.andExpect(status().isNoContent());

        verify(deleteCategoryUseCase, times(1)).execute(expectedId);
    }

    @Test
    void givenAnInvalidId_whenCallsDeleteCategory_thenShouldReturnNoContent() throws Exception {
        // Given
        final var expectedId = "123";

        doNothing()
                .when(this.deleteCategoryUseCase).execute(any());

        final var request = delete("/categories/{id}", expectedId)
                .with(CATEGORIES_JWT)
                .accept(MediaType.APPLICATION_JSON);

        // When
        final var response = this.mockMvc.perform(request)
                .andDo(print());

        // Then
        response.andExpect(status().isNoContent());

        verify(deleteCategoryUseCase, times(1)).execute(expectedId);
    }

    @Test
    void givenValidParams_whenCallsListCategories_shouldReturnCategories() throws Exception {
        // Given
        final var aCategory = Category.newCategory("Movies", null, true);

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "movies";
        final var expectedSort = "description";
        final var expectedDirection = "desc";
        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(CategoryListOutput.from(aCategory));

        when(listCategoriesUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        // When
        final var request = get("/categories")
                .with(CATEGORIES_JWT)
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .queryParam("search", expectedTerms)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mockMvc.perform(request)
                .andDo(print());

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(aCategory.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(aCategory.name())))
                .andExpect(jsonPath("$.items[0].description", equalTo(aCategory.description())))
                .andExpect(jsonPath("$.items[0].is_active", equalTo(aCategory.active())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(aCategory.createdAt().toString())))
                .andExpect(jsonPath("$.items[0].deleted_at", equalTo(aCategory.deletedAt())));

        verify(listCategoriesUseCase, times(1)).execute(argThat(query ->
                Objects.equals(expectedPage, query.page())
                        && Objects.equals(expectedPerPage, query.perPage())
                        && Objects.equals(expectedDirection, query.direction())
                        && Objects.equals(expectedSort, query.sort())
                        && Objects.equals(expectedTerms, query.terms())
        ));
    }
}