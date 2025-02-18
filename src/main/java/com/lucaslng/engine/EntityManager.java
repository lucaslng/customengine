package com.lucaslng.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.lucaslng.engine.entities.AbstractEntityFactory;
import com.lucaslng.engine.entities.Entity;

public class EntityManager {

	private final Map<Integer, Entity> entities = new HashMap<>();
	private final Map<Class<?>, Map<Integer, Record>> components = new HashMap<>();

	public Entity buildEntity(AbstractEntityFactory entityFactory) {
		Entity entity = new Entity();
		entities.put(entity.id(), entity);
		for (Record component : entityFactory.components()) {
			addComponent(entity.id(), component);
		}
		return entity;
	}

	public <T extends Record> void addComponent(int entityId, T component) {
		assert entityExists(entityId);
		components.computeIfAbsent(component.getClass(), k -> new HashMap<>()).put(entityId, component);
	}

	public <T extends Record> T getComponent(int entityId, Class<T> componentClass) {
		assert entityExists(entityId);
		return componentClass.cast(components.getOrDefault(componentClass, new HashMap<>()).get(entityId));
	}

	public <T extends Record> boolean hasComponent(int entityId, Class<T> componentClass) {
		assert entityExists(entityId);
		return components.containsKey(componentClass) && components.get(componentClass).containsKey(entityId);
	}

	public <T extends Record> void removeComponent(int entityId, Class<T> componentClass) {
		assert entityExists(entityId);
		Map<Integer, Record> componentMap = components.get(componentClass);
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
	
}
