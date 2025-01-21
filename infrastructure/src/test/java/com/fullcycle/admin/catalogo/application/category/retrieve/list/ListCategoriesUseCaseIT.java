package com.fullcycle.admin.catalogo.application.category.retrieve.list;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
class ListCategoriesUseCaseIT {

    @Autowired
    private ListCategoriesUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;


    @BeforeEach
    void mockUp() {
        final var categories = List.of(
                Category.newCategory("Filmes", null, true),
                Category.newCategory("Netflix Originals", "Títulos de autoria da Netflix", true),
                Category.newCategory("Amazon Originals", "Títulos de autoria da Amazon Prime", true),
                Category.newCategory("Documentários", null, true),
                Category.newCategory("Sports", "", true),
                Category.newCategory("Kids", "Categoria para crianças", true),
                Category.newCategory("Series", "", true)
        );
        this.categoryRepository.saveAllAndFlush(categories.stream()
                .map(CategoryJpaEntity::from).toList());
    }

    @Test
    void givenAValidTerm_whenTermDoesntMatchPrePersisted_thenShouldReturnEmptyPage() {
        // Given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "invalid";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 0;
        final var expectedTotal = 0;

        final var aQuery = new SearchQuery(
                expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // When
        final var actualResult = useCase.execute(aQuery);

        // Then
        assertEquals(expectedItemsCount, actualResult.items().size());
        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
    }

    @ParameterizedTest
    @CsvSource({
            "fil,0,10,1,1,Filmes",
            "net,0,10,1,1,Netflix Originals",
            "ZON,0,10,1,1,Amazon Originals",
            "KI,0,10,1,1,Kids",
            "crianças,0,10,1,1,Kids",
            "da Amazon,0,10,1,1,Amazon Originals",
    })
    void givenAValidTerm_whenCallsListCategories_thenShouldReturnCategoriesFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final int expectedTotal,
            final String expectedCategoryName
    ) {
        // Given
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery = new SearchQuery(
                expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // When
        final var actualResult = useCase.execute(aQuery);

        // Then
        assertEquals(expectedItemsCount, actualResult.items().size());
        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedCategoryName, actualResult.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,7,7,Amazon Originals",
            "name,desc,0,10,7,7,Sports",
            "createdAt,asc,0,10,7,7,Filmes",
            "createdAt,desc,0,10,7,7,Series",
    })
    void givenAValidSortAndDirection_thenCallsListCategories_thenShouldReturnCategoriesOrdered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final int expectedTotal,
            final String expectedCategoryName
    ) {
        // Given
        final var expectedTerms = "";

        final var aQuery = new SearchQuery(
                expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // When
        final var actualResult = useCase.execute(aQuery);

        // Then
        assertEquals(expectedItemsCount, actualResult.items().size());
        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());
        assertEquals(expectedCategoryName, actualResult.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,7,Amazon Originals;Documentários",
            "1,2,2,7,Filmes;Kids",
            "2,2,2,7,Netflix Originals;Series",
            "3,2,1,7,Sports",
    })
    void givenAValidPage_thenCallsListCategories_thenShouldReturnCategoriesPaginated(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final int expectedTotal,
            final String expectedCategoriesName
    ) {
        // Given
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery = new SearchQuery(
                expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // When
        final var actualResult = useCase.execute(aQuery);

        // Then
        assertEquals(expectedItemsCount, actualResult.items().size());
        assertEquals(expectedPage, actualResult.currentPage());
        assertEquals(expectedPerPage, actualResult.perPage());
        assertEquals(expectedTotal, actualResult.total());

        int index = 0;
        for (final String expectedName : expectedCategoriesName.split(";")) {
            assertEquals(expectedName, actualResult.items().get(index).name());
            index++;
        }
    }
}
