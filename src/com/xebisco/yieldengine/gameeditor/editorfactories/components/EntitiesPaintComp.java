package com.xebisco.yieldengine.gameeditor.editorfactories.components;

import com.xebisco.yieldengine.core.Component;
import com.xebisco.yieldengine.core.EntityFactory;
import com.xebisco.yieldengine.core.Global;
import com.xebisco.yieldengine.core.camera.OrthoCamera;
import com.xebisco.yieldengine.core.graphics.Graphics;
import com.xebisco.yieldengine.core.graphics.IPainter;
import com.xebisco.yieldengine.core.graphics.yldg1.Paint;
import com.xebisco.yieldengine.shipruntime.PreMadeEntityFactory;
import com.xebisco.yieldengine.utils.ColorUtils;

public class EntitiesPaintComp extends Component implements IPainter {
    @Override
    public void onPaint(Graphics g) {
        Paint paint = new Paint();
        OrthoCamera cam = (OrthoCamera) Global.getCurrentScene().getCamera();
        paint.setCamera(cam);
        for (EntityFactory factory : Global.getCurrentScene().getEntityFactories()) {
            if (factory instanceof PreMadeEntityFactory f) {
                for (Component c : f.getComponents()) {
                    if (c instanceof IPainter p) {
                        c.setWorldTransform(f.getTransform());
                        p.onPaint(g);
                    }
                }
                paint.setColor(ColorUtils.argb(0x50606060));
                paint.setTransform(f.getTransform());
                g.getG1().drawEllipse(8 * cam.getTransform().getScale().x(), 8 * cam.getTransform().getScale().y(), paint);
            }
        }
    }
}
