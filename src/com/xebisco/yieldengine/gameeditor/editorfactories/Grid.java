package com.xebisco.yieldengine.gameeditor.editorfactories;

import com.xebisco.yieldengine.core.Entity;
import com.xebisco.yieldengine.core.EntityFactory;
import com.xebisco.yieldengine.core.EntityHeader;
import com.xebisco.yieldengine.core.Transform;
import com.xebisco.yieldengine.gameeditor.editorfactories.components.GridPainter;

public class Grid implements EntityFactory {
    @Override
    public Entity createEntity() {
        Entity e = new Entity(new EntityHeader("GRID"), new Transform());
        e.getComponents().add(new GridPainter());
        return e;
    }
}
