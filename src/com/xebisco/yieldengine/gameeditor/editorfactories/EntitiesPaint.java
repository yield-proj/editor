package com.xebisco.yieldengine.gameeditor.editorfactories;

import com.xebisco.yieldengine.core.Entity;
import com.xebisco.yieldengine.core.EntityFactory;
import com.xebisco.yieldengine.core.Transform;
import com.xebisco.yieldengine.gameeditor.editorfactories.components.EntitiesPaintComp;

public class EntitiesPaint implements EntityFactory {
    @Override
    public Entity createEntity() {
        Entity e = new Entity("EntitiesPaint", new Transform());
        e.getComponents().add(new EntitiesPaintComp());
        return e;
    }
}
