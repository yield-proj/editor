package com.xebisco.yieldengine.gameeditor.editorfactories;

import com.xebisco.yieldengine.core.Entity;
import com.xebisco.yieldengine.core.EntityFactory;
import com.xebisco.yieldengine.core.Transform;
import com.xebisco.yieldengine.gameeditor.editorfactories.components.EntitySelector;

public class Selector implements EntityFactory {
    @Override
    public Entity createEntity() {
        Entity e = new Entity("SELECTOR", new Transform());
        e.getComponents().add(new EntitySelector());
        return e;
    }
}
