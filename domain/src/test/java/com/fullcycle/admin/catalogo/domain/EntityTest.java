package com.fullcycle.admin.catalogo.domain;

import com.fullcycle.admin.catalogo.domain.event.DomainEvent;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.utils.InstantUtils;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest extends UnitTest {

    @Test
    void givenNullAsEvents_whenInstantiate_thenShouldBeOk() {
        // Given
        final List<DomainEvent> events = null;

        // When
        final var anEntity = new DummyEntity(new DummyID(), events);

        // Then
        assertNotNull(anEntity.getDomainEvents());
        assertTrue(anEntity.getDomainEvents().isEmpty());
    }

    @Test
    void givenEmptyDomainEvents_whenPassInConstructor_thenShouldCreateADefensiveClone() {
        // Given
        final var expectedEvents = 1;
        final List<DomainEvent> events = new ArrayList<>();
        events.add(new DummyEvent());

        // When
        final var anEntity = new DummyEntity(new DummyID(), events);

        // Then
        assertNotNull(anEntity.getDomainEvents());
        assertEquals(expectedEvents, anEntity.getDomainEvents().size());

        assertThrows(RuntimeException.class, () -> {
            final var actualEvents = anEntity.getDomainEvents();
            actualEvents.add(new DummyEvent());
        });
    }

    @Test
    void givenCallsRegisterEvent_whenCallsRegisterEvent_thenShouldAddEventToList() {
        // Given
        final var expectedEvents = 1;
        final var anEntity = new DummyEntity(new DummyID(), new ArrayList<>());

        // When
        anEntity.registerEvent(new DummyEvent());

        // Then
        assertNotNull(anEntity.getDomainEvents());
        assertEquals(expectedEvents, anEntity.getDomainEvents().size());
    }

    @Test
    void givenAFewDomainEvents_whenCallsPublishEvent_thenShouldCallPublisherAndClearTheList() {
        // Given
        final var expectedEvents = 0;
        final var expectedSentEvents = 2;
        final var counter = new AtomicInteger(0);
        final var anEntity = new DummyEntity(new DummyID(), new ArrayList<>());
        anEntity.registerEvent(new DummyEvent());
        anEntity.registerEvent(new DummyEvent());

        assertEquals(2, anEntity.getDomainEvents().size());

        // When
        anEntity.publishDomainEvents(event -> {
            counter.incrementAndGet();
        });

        // Then
        assertNotNull(anEntity.getDomainEvents());
        assertEquals(expectedEvents, anEntity.getDomainEvents().size());
        assertEquals(expectedSentEvents, counter.get());
    }

    public static class DummyEvent implements DomainEvent {

        @Override
        public Instant occurredOn() {
            return InstantUtils.now();
        }
    }

    public static class DummyID extends Identifier {

        private final String id;

        public DummyID() {
            this.id = IdUtils.uuid();
        }

        @Override
        public String getValue() {
            return id;
        }
    }

    public static class DummyEntity extends Entity<DummyID> {

        public DummyEntity() {
            this(new DummyID(), null);
        }

        public DummyEntity(
                final DummyID dummyID,
                final List<DomainEvent> domainEvents) {
            super(dummyID, domainEvents);
        }

        @Override
        public void validate(ValidationHandler handler) {

        }
    }

}