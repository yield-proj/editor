package com.xebisco.yieldengine.gameeditor.editorfactories;

import com.xebisco.yieldengine.core.Entity;
import com.xebisco.yieldengine.core.EntityFactory;
import com.xebisco.yieldengine.core.Transform;
import com.xebisco.yieldengine.core.components.Rectangle;
import org.joml.Vector2f;

public class TestEF implements EntityFactory {
    @Override
    public Entity createEntity() {
        Entity e = new Entity("Test", new Transform());
        e.getComponents().add(new Rectangle(new Vector2f(2, 2)));
        return e;
    }
}
