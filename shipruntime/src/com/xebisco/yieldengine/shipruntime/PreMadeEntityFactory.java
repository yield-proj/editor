package com.xebisco.yieldengine.shipruntime;

import com.xebisco.yieldengine.core.Component;
import com.xebisco.yieldengine.core.Entity;
import com.xebisco.yieldengine.core.EntityFactory;
import com.xebisco.yieldengine.core.Transform;
import com.xebisco.yieldengine.utils.Editable;
import com.xebisco.yieldengine.utils.Visible;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PreMadeEntityFactory implements EntityFactory, Serializable {
    @Visible
    @Editable
    private String name = "Empty Entity";
    @Visible
    private final Transform transform = new Transform();
    @Visible
    @Editable
    private final List<Component> components = new ArrayList<>();

    private final List<EntityFactory> children = new ArrayList<>();

    private transient Entity tempEntity;

    @Override
    public Entity createEntity() {
        Entity e = new Entity(name, transform);
        e.getComponents().addAll(components);
        for(EntityFactory child : children)
            e.getChildren().add(child.createEntity());

        return e;
    }

    public Entity getTempEntity() {
        if(tempEntity == null) tempEntity = createEntity();
        return tempEntity;
    }

    private PreMadeEntityFactory setTempEntity(Entity tempEntity) {
        this.tempEntity = tempEntity;
        return this;
    }

    public String getName() {
        return name;
    }

    public PreMadeEntityFactory setName(String name) {
        this.name = name;
        return this;
    }

    public Transform getTransform() {
        return transform;
    }

    public List<Component> getComponents() {
        return components;
    }

    public List<EntityFactory> getChildren() {
        return children;
    }
}
