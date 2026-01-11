package com.lucaslng.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.entities.Entity;

public class EntityManager {

	private final Map<Integer, Entity> entities = new ConcurrentHashMap<>();
	private final Map<Class<?>, Map<Integer, Object>> components = new ConcurrentHashMap<>(); // Map<Component.class, Map<EntityId,
																																									// Component>>

	public Entity buildEntity(AbstractEntityFactory entityFactory) {
		Entity entity = new Entity();
		entities.put(entity.id(), entity);
		for (Object component : entityFactory.components()) {
			addComponent(entity.id(), component);
		}
		return entity;
	}

	public void removeEntity(int entityId) {
		assert entityExists(entityId);
		for (Map<Integer, Object> componentMap : components.values())
			componentMap.remove(entityId);
		entities.remove(entityId);
	}

	public <T> void addComponent(int entityId, T component) {
		assert entityExists(entityId);
		components.computeIfAbsent(component.getClass(), k -> new HashMap<>()).put(entityId, component);
	}

	public <T> T getComponent(int entityId, Class<T> componentClass) {
		assert entityExists(entityId);
		return componentClass.cast(components.getOrDefault(componentClass, new HashMap<>()).get(entityId));
	}

	public <T> boolean hasComponent(int entityId, Class<T> componentClass) {
		assert entityExists(entityId);
		return components.containsKey(componentClass) && components.get(componentClass).containsKey(entityId);
	}

	public <T> void removeComponent(int entityId, Class<T> componentClass) {
		assert entityExists(entityId);
		Map<Integer, Object> componentMap = components.get(componentClass);
		if (componentMap != null) {
			componentMap.remove(entityId);
		}
	}

	public Set<Integer> getEntitiesWith(Class<?>... componentClasses) {
		Set<Integer> result = new HashSet<>(entities.keySet());
		for (Class<?> componentClass : componentClasses) {
			result.retainAll(components.getOrDefault(componentClass, Collections.emptyMap()).keySet());
		}
		return result;
	}

	public boolean entityExists(int entityId) {
		return entities.containsKey(entityId);
	}

	public Map<Integer, Entity> entities() {
		return entities;
	}

}
