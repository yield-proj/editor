package com.xebisco.yieldengine.shipruntime;

import com.xebisco.yieldengine.core.Component;
import com.xebisco.yieldengine.core.Entity;
import com.xebisco.yieldengine.core.EntityFactory;
import com.xebisco.yieldengine.core.Transform;
import com.xebisco.yieldengine.utils.AltArray;
import com.xebisco.yieldengine.utils.CustomAdd;
import com.xebisco.yieldengine.utils.Editable;
import com.xebisco.yieldengine.utils.Visible;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreMadeEntityFactory implements EntityFactory, Serializable {
    @Visible
    @Editable
    private String name = "Empty Entity";
    @Visible
    @Editable
    private Transform transform = new Transform();
    @Visible
    @Editable
    @AltArray
    @CustomAdd(addAction = "com.xebisco.yieldengine.gameeditor.AddComponent")
    private Component[] components = new Component[0];

    private final List<EntityFactory> children = new ArrayList<>();
    private PreMadeEntityFactory parent;

    private transient Entity tempEntity;

    @Override
    public Entity createEntity() {
        Entity e = new Entity(name, transform);
        Collections.addAll(e.getComponents(), components);
        for(EntityFactory child : children)
            e.getChildren().add(child.createEntity());

        return e;
    }

    public Transform getNewWorldTransform() {
        if(parent == null) return transform;
        Transform worldTransform = new Transform(parent.getNewWorldTransform());
        worldTransform.getTransformMatrix().translationRotateScale(worldTransform.getTranslation(), worldTransform.getNormalizedRotation(),  worldTransform.getScale());
        return worldTransform;
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

    public Component[] getComponents() {
        return components;
    }

    public PreMadeEntityFactory setComponents(Component[] components) {
        this.components = components;
        return this;
    }

    public List<EntityFactory> getChildren() {
        return children;
    }
}
