package com.fullcycle.admin.catalogo.infrastructure.category;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.MySQLGatewayTest;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MySQLGatewayTest
class CategoryMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void cleanUp() {
        categoryRepository.deleteAll();
    }

    @Test
    void givenAValidCategory_whenCallsCreate_thenShouldReturnANewCategory() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0L, this.categoryRepository.count());

        // When
        final var actualCategory = this.categoryGateway.create(aCategory);

        // Then
        assertEquals(1L, this.categoryRepository.count());
        assertEquals(aCategory.getId().getValue(), actualCategory.getId().getValue());
        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertEquals(expectedIsActive, actualCategory.active());
        assertEquals(aCategory.createdAt(), actualCategory.createdAt());
        assertEquals(aCategory.updatedAt(), actualCategory.updatedAt());
        assertEquals(aCategory.deletedAt(), actualCategory.deletedAt());
        assertNull(actualCategory.deletedAt());

        this.categoryRepository.findById(aCategory.getId().getValue())
                .ifPresent(categoryJpaEntity -> {
                    assertEquals(aCategory.getId().getValue(), categoryJpaEntity.getId());
                    assertEquals(expectedName, categoryJpaEntity.getName());
                    assertEquals(expectedDescription, categoryJpaEntity.getDescription());
                    assertEquals(expectedIsActive, categoryJpaEntity.isActive());
                    assertEquals(aCategory.createdAt(), categoryJpaEntity.getCreatedAt());
                    assertEquals(aCategory.updatedAt(), categoryJpaEntity.getUpdatedAt());
                    assertNull(categoryJpaEntity.getDeletedAt());
                });
    }

    @Test
    void givenAValidCategory_whenCallsUpdate_thenShouldReturnACategoryUpdated() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory("Film", "A mais", expectedIsActive);

        assertEquals(0L, this.categoryRepository.count());

        this.categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        assertEquals(1L, this.categoryRepository.count());

        final var anUpdatedCategory = aCategory.clone()
                .update(expectedName, expectedDescription, expectedIsActive);

        // When
        final var actualCategory = this.categoryGateway.update(anUpdatedCategory);

        // Then
        assertEquals(1L, this.categoryRepository.count());

        assertEquals(aCategory.getId().getValue(), actualCategory.getId().getValue());
        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertEquals(expectedIsActive, actualCategory.active());
        assertEquals(aCategory.createdAt(), actualCategory.createdAt());
        assertTrue(aCategory.updatedAt().isBefore(actualCategory.updatedAt()));
        assertEquals(aCategory.deletedAt(), actualCategory.deletedAt());
        assertNull(actualCategory.deletedAt());

        this.categoryRepository.findById(aCategory.getId().getValue())
                .ifPresent(categoryJpaEntity -> {
                    assertEquals(aCategory.getId().getValue(), categoryJpaEntity.getId());
                    assertEquals(expectedName, categoryJpaEntity.getName());
                    assertEquals(expectedDescription, categoryJpaEntity.getDescription());
                    assertEquals(expectedIsActive, categoryJpaEntity.isActive());
                    assertEquals(aCategory.createdAt(), categoryJpaEntity.getCreatedAt());
                    assertTrue(aCategory.updatedAt().isBefore(categoryJpaEntity.getUpdatedAt()));
                    assertNull(categoryJpaEntity.getDeletedAt());
                });
    }

    @Test
    void givenAPrePersistedCategoryAndValidCategoryId_whenTryToDeleteIt_thenShouldDeleteCategory() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = aCategory.getId();

        assertEquals(0L, this.categoryRepository.count());

        this.categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        assertEquals(1L, this.categoryRepository.count());

        // When
        this.categoryGateway.deleteById(expectedId);

        // Then
        assertEquals(0L, this.categoryRepository.count());
    }

    @Test
    void givenAnInvalidCategoryId_whenTryToDeleteIt_thenShouldDeleteCategory() {
        // Given
        assertEquals(0L, this.categoryRepository.count());

        // When
        this.categoryGateway.deleteById(CategoryID.from("invalid"));

        // Then
        assertEquals(0L, this.categoryRepository.count());
    }

    @Test
    void givenAPrePersistedCategoryAndValidCategoryId_whenCallsFindById_thenShouldReturnACategory() {
        // Given
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = aCategory.getId();

        assertEquals(0L, this.categoryRepository.count());

        this.categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        assertEquals(1L, this.categoryRepository.count());

        // When
        final var actualCategory = this.categoryGateway.findById(expectedId);

        // Then
        assertEquals(1L, this.categoryRepository.count());

        actualCategory.ifPresent(category -> {
            assertEquals(aCategory.getId().getValue(), category.getId().getValue());
            assertEquals(expectedName, category.name());
            assertEquals(expectedDescription, category.description());
            assertEquals(expectedIsActive, category.active());
            assertEquals(aCategory.createdAt(), category.createdAt());
            assertEquals(aCategory.updatedAt(), category.updatedAt());
            assertEquals(aCategory.deletedAt(), category.deletedAt());
            assertNull(category.deletedAt());
        });
    }

    @Test
    void givenAValidCategoryIdNotStored_whenCallsFindById_thenShouldReturnEmpty() {
        // Given
        assertEquals(0L, this.categoryRepository.count());

        // When
        final var actualCategory = this.categoryGateway.findById(CategoryID.from("empty"));

        // Then
        assertTrue(actualCategory.isEmpty());
    }

    @Test
    void givenPrePersistedCategories_whenCallsFindAll_thenShouldReturnPaginated() {
        // Given
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var series = Category.newCategory("Séries", "A categoria muito assistida", true);
        final var documentarios = Category.newCategory("Documentários", "A categoria assistida", true);

        assertEquals(0, this.categoryRepository.count());

        this.categoryRepository.saveAllAndFlush(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        assertEquals(3, this.categoryRepository.count());

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, "", "name", "asc");

        // When
        final var actualResult = this.categoryGateway.findAll(aQuery);

        // Then
        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedPerPage, actualResult.items().size());
        assertEquals(documentarios.getId(), actualResult.items().get(0).getId());
    }

    @Test
    void givenEmptyCategoriesTable_whenCallsFindAll_thenShouldReturnEmptyPage() {
        // Given
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 0;

        assertEquals(0, this.categoryRepository.count());

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, "", "name", "asc");

        // When
        final var actualResult = this.categoryGateway.findAll(aQuery);

        // Then
        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedTotal, actualResult.items().size());
    }

    @Test
    void givenFollowPagination_whenCallsFindAllWithPage1_thenShouldReturnEmptyPage() {
        // Given
        var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var series = Category.newCategory("Séries", "A categoria muito assistida", true);
        final var documentarios = Category.newCategory("Documentários", "A categoria assistida", true);

        assertEquals(0, this.categoryRepository.count());

        this.categoryRepository.saveAllAndFlush(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        assertEquals(3, this.categoryRepository.count());

        var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, "", "name", "asc");

        // When
        var actualResult = this.categoryGateway.findAll(aQuery);

        // Then
        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedPerPage, actualResult.items().size());
        assertEquals(documentarios.getId(), actualResult.items().get(0).getId());

        expectedPage = 1;
        aQuery =
                new SearchQuery(expectedPage, expectedPerPage, "", "name", "asc");
        actualResult = this.categoryGateway.findAll(aQuery);

        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedPerPage, actualResult.items().size());
        assertEquals(filmes.getId(), actualResult.items().get(0).getId());

        expectedPage = 2;
        aQuery =
                new SearchQuery(expectedPage, expectedPerPage, "", "name", "asc");
        actualResult = this.categoryGateway.findAll(aQuery);

        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedPerPage, actualResult.items().size());
        assertEquals(series.getId(), actualResult.items().get(0).getId());
    }

    @Test
    void givenPrePersistedCategoriesAndDocAsTerms_whenCallsFindAllAndTermsMatchesCategoryName_thenShouldReturnPaginated() {
        // Given
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var expectedTerms = "doc";

        final var filmes = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var series = Category.newCategory("Séries", "A categoria muito assistida", true);
        final var documentarios = Category.newCategory("Documentários", "A categoria assistida", true);

        assertEquals(0, this.categoryRepository.count());

        this.categoryRepository.saveAllAndFlush(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        assertEquals(3, this.categoryRepository.count());

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, "name", "asc");

        // When
        final var actualResult = this.categoryGateway.findAll(aQuery);

        // Then
        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedPerPage, actualResult.items().size());
        assertEquals(documentarios.getId(), actualResult.items().get(0).getId());
    }

    @Test
    void givenPrePersistedCategoriesAndMaisAssistidaAsTerms_whenCallsFindAllAndTermsMatchesCategoryDescription_thenShouldReturnPaginated() {
        // Given
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var expectedTerms = "MAIS ASSISTIDA";

        final var filmes = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var series = Category.newCategory("Séries", "Uma categoria muito assistida", true);
        final var documentarios = Category.newCategory("Documentários", "Uma categoria assistida", true);

        assertEquals(0, this.categoryRepository.count());

        this.categoryRepository.saveAllAndFlush(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        assertEquals(3, this.categoryRepository.count());

        final var aQuery =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, "name", "asc");

        // When
        final var actualResult = this.categoryGateway.findAll(aQuery);

        // Then
        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedPerPage, actualResult.items().size());
        assertEquals(filmes.getId(), actualResult.items().get(0).getId());
    }
}