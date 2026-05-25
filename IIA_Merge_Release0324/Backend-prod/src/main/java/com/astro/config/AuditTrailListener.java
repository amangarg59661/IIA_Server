package com.astro.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class AuditTrailListener implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;
    private final ObjectMapper objectMapper;

    private static final Set<String> SKIP_FIELDS = Set.of(
            "createdBy", "updatedBy",
            "createDate", "createdDate", "updateDate", "updatedDate", "modifyDate"
    );

    private static final String INSERT_SQL =
            "INSERT INTO audit_trail (entity_name, entity_id, action, changed_by, changed_at, changes_json) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    public AuditTrailListener(DataSource dataSource, EntityManagerFactory entityManagerFactory) {
        this.dataSource = dataSource;
        this.entityManagerFactory = entityManagerFactory;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @PostConstruct
    public void register() {
        SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry()
                .getService(EventListenerRegistry.class);
        registry.appendListeners(EventType.POST_INSERT, this);
        registry.appendListeners(EventType.POST_UPDATE, this);
        registry.appendListeners(EventType.POST_DELETE, this);
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (isAuditTrailEntity(event.getEntity())) return;

        String[] propertyNames = event.getPersister().getPropertyNames();
        Object[] state = event.getState();
        Map<String, Object> changes = new LinkedHashMap<>();

        for (int i = 0; i < propertyNames.length; i++) {
            if (SKIP_FIELDS.contains(propertyNames[i])) continue;
            if (state[i] != null) {
                changes.put(propertyNames[i], Map.of("old", "null", "new", serialize(state[i])));
            }
        }

        saveAuditRecord(
                event.getEntity().getClass().getSimpleName(),
                String.valueOf(event.getId()),
                "CREATE",
                changes
        );
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if (isAuditTrailEntity(event.getEntity())) return;

        String[] propertyNames = event.getPersister().getPropertyNames();
        Object[] oldState = event.getOldState();
        Object[] newState = event.getState();

        if (oldState == null) return;

        Map<String, Object> changes = new LinkedHashMap<>();
        for (int i = 0; i < propertyNames.length; i++) {
            if (SKIP_FIELDS.contains(propertyNames[i])) continue;
            if (!Objects.equals(oldState[i], newState[i])) {
                changes.put(propertyNames[i], Map.of(
                        "old", serialize(oldState[i]),
                        "new", serialize(newState[i])
                ));
            }
        }

        if (!changes.isEmpty()) {
            saveAuditRecord(
                    event.getEntity().getClass().getSimpleName(),
                    String.valueOf(event.getId()),
                    "UPDATE",
                    changes
            );
        }
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        if (isAuditTrailEntity(event.getEntity())) return;

        String[] propertyNames = event.getPersister().getPropertyNames();
        Object[] state = event.getDeletedState();
        Map<String, Object> changes = new LinkedHashMap<>();

        for (int i = 0; i < propertyNames.length; i++) {
            if (SKIP_FIELDS.contains(propertyNames[i])) continue;
            if (state[i] != null) {
                changes.put(propertyNames[i], Map.of("old", serialize(state[i]), "new", "null"));
            }
        }

        saveAuditRecord(
                event.getEntity().getClass().getSimpleName(),
                String.valueOf(event.getId()),
                "DELETE",
                changes
        );
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }

    private boolean isAuditTrailEntity(Object entity) {
        return entity instanceof com.astro.entity.AuditTrail;
    }

    private String serialize(Object value) {
        if (value == null) return "null";
        if (value instanceof String || value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof LocalDateTime) return value.toString();
        if (value instanceof LocalDate) return value.toString();
        if (value instanceof BigDecimal) return ((BigDecimal) value).toPlainString();
        if (value instanceof Enum) return ((Enum<?>) value).name();
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return value.toString();
        }
    }

    private void saveAuditRecord(String entityName, String entityId, String action, Map<String, Object> changes) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

            String changesJson = objectMapper.writeValueAsString(changes);

            ps.setString(1, entityName);
            ps.setString(2, entityId);
            ps.setString(3, action);
            ps.setString(4, UserContextHolder.get());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(6, changesJson);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("[AUDIT] Failed to save audit record for " + entityName + "#" + entityId + ": " + e.getMessage());
        }
    }
}
