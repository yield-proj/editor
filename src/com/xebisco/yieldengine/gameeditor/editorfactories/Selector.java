package com.xebisco.yieldengine.gameeditor.editorfactories;

import com.xebisco.yieldengine.core.Entity;
import com.xebisco.yieldengine.core.EntityFactory;
import com.xebisco.yieldengine.core.EntityHeader;
import com.xebisco.yieldengine.core.Transform;
import com.xebisco.yieldengine.gameeditor.editorfactories.components.EntitySelectorComp;

public class Selector implements EntityFactory {
    @Override
    public Entity createEntity() {
        Entity e = new Entity(new EntityHeader("SELECTOR"), new Transform());
        e.getComponents().add(new EntitySelectorComp());
        return e;
    }
}
