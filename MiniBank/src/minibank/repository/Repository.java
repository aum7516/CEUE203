package minibank.repository;

import minibank.model.annotation.Id;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic In-Memory Repository pattern implementation.
 * Reflectively locates entity keys marked with @Id annotation.
 * Includes bounded generic utilities.
 *
 * @param <T> Entity type managed by this repository.
 */
public class Repository<T> {
    protected final Map<String, T> storage = new HashMap<>();

    /**
     * Saves an entity using its reflectively discovered @Id field as the map key.
     */
    public synchronized T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null.");
        }
        String id = extractId(entity);
        if (id == null) {
            throw new IllegalStateException("Entity of type " + entity.getClass().getSimpleName() + " has no field annotated with @Id or id value is null.");
        }
        storage.put(id, entity);
        return entity;
    }

    public synchronized T findById(String id) {
        return storage.get(id);
    }

    public synchronized ArrayList<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public synchronized boolean delete(String id) {
        return storage.remove(id) != null;
    }

    public synchronized int count() {
        return storage.size();
    }

    /**
     * Safe varargs batch persistence method.
     */
    @SafeVarargs
    public final synchronized void saveAll(T... items) {
        if (items != null) {
            for (T item : items) {
                save(item);
            }
        }
    }

    /**
     * Generic bounded method calculating arithmetic average of any Number array.
     */
    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) {
            return 0.0;
        }
        double sum = 0.0;
        for (N val : values) {
            sum += val.doubleValue();
        }
        return sum / values.length;
    }

    private String extractId(T entity) {
        Class<?> clazz = entity.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    try {
                        Object val = field.get(entity);
                        return val != null ? val.toString() : null;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Cannot access @Id field '" + field.getName() + "'", e);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
}
