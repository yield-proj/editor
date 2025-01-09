package com.xebisco.yieldengine.gameeditor.editorfactories;

import com.xebisco.yieldengine.core.Entity;
import com.xebisco.yieldengine.core.EntityFactory;
import com.xebisco.yieldengine.core.Transform;
import com.xebisco.yieldengine.gameeditor.editorfactories.components.CamControlComp;

public class CamControl implements EntityFactory {
    @Override
    public Entity createEntity() {
        Entity e = new Entity("CamControl", new Transform());
        e.getComponents().add(new CamControlComp());
        return e;
    }
}
