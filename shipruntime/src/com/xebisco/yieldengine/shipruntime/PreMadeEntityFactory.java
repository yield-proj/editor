package com.xebisco.yieldengine.shipruntime;

import com.xebisco.yieldengine.core.*;
import com.xebisco.yieldengine.utils.AltArray;
import com.xebisco.yieldengine.utils.CustomAdd;
import com.xebisco.yieldengine.utils.Editable;
import com.xebisco.yieldengine.utils.Visible;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreMadeEntityFactory implements EntityFactory, Serializable, Cloneable, Comparable<EntityFactory> {
    @Visible
    @Editable
    private EntityHeader header = new EntityHeader("Entity Name");
    @Visible
    @Editable
    private Transform transform = new Transform();
    @Visible
    @Editable
    @AltArray
    @CustomAdd(addAction = "com.xebisco.yieldengine.gameeditor.AddComponent")
    private Component[] components = new Component[0];

    private List<EntityFactory> children = new ArrayList<>();
    private PreMadeEntityFactory parent;
    private int preferredIndex;

    private transient Entity tempEntity;

    @Override
    public Entity createEntity() {
        Entity e = new Entity(header, transform);
        Collections.addAll(e.getComponents(), components);
        for (EntityFactory child : children) {
            child.createEntity().addToParent(e);
        }

        return e;
    }

    @Override
    public String toString() {
        return header.getName();
    }

    public Transform getNewWorldTransform() {
        if (parent == null) return transform;
        return new Transform(transform).apply(parent.getNewWorldTransform());
    }

    public Entity getTempEntity() {
        if (tempEntity == null) tempEntity = createEntity();
        return tempEntity;
    }

    private PreMadeEntityFactory setTempEntity(Entity tempEntity) {
        this.tempEntity = tempEntity;
        return this;
    }

    public EntityHeader getHeader() {
        return header;
    }

    public PreMadeEntityFactory setHeader(EntityHeader header) {
        this.header = header;
        return this;
    }

    public PreMadeEntityFactory setTransform(Transform transform) {
        this.transform = transform;
        return this;
    }

    public PreMadeEntityFactory getParent() {
        return parent;
    }

    public PreMadeEntityFactory setParent(PreMadeEntityFactory parent) {
        this.parent = parent;
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

    public void setChildren(List<EntityFactory> children) {
        this.children = children;
    }

    @Override
    public PreMadeEntityFactory clone() {
        try {
            PreMadeEntityFactory clone = (PreMadeEntityFactory) super.clone();
            clone.setTransform(transform.clone());
            clone.setHeader(header.clone());
            clone.setComponents(components.clone());
            clone.setChildren(new ArrayList<>());
            for (EntityFactory f : children) {
                if (f instanceof PreMadeEntityFactory f1)
                    clone.getChildren().add(f1.clone());
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public int compareTo(EntityFactory o) {
        if (o instanceof PreMadeEntityFactory o1)
            return Integer.compare(o1.preferredIndex, this.preferredIndex);
        else return -1;
    }

    public int getPreferredIndex() {
        return preferredIndex;
    }

    public void setPreferredIndex(int preferredIndex) {
        this.preferredIndex = preferredIndex;
    }
}
