package com.xebisco.yieldengine.gameeditor.editorfactories.components;

import com.xebisco.yieldengine.core.Component;
import com.xebisco.yieldengine.core.EntityFactory;
import com.xebisco.yieldengine.core.Global;
import com.xebisco.yieldengine.core.Transform;
import com.xebisco.yieldengine.core.camera.OrthoCamera;
import com.xebisco.yieldengine.core.graphics.Graphics;
import com.xebisco.yieldengine.core.graphics.IPainter;
import com.xebisco.yieldengine.core.graphics.yldg1.Paint;
import com.xebisco.yieldengine.gameeditor.EntityListEditor;
import com.xebisco.yieldengine.shipruntime.PreMadeEntityFactory;
import com.xebisco.yieldengine.utils.ColorUtils;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;

public class EntitiesPaintComp extends Component implements IPainter {
    private List<Vector3fc> toDrawBalls = new ArrayList<>();
    private EntityListEditor entityListEditor;

    private void paintFactory(PreMadeEntityFactory f, Graphics g, OrthoCamera cam) {
        Transform transform = new Transform(f.getNewWorldTransform());
        Paint paint = new Paint();
        paint.setCamera(cam);
        for (EntityFactory factory : f.getChildren()) {
            if (factory instanceof PreMadeEntityFactory f1) {
                paintFactory(f1, g, cam);
            }
        }
        for (Component c : f.getComponents()) {
            if (c instanceof IPainter p) {
                c.setWorldTransform(transform);
                try {
                    p.onPaint(g);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        toDrawBalls.add(transform.getTranslation());
    }

    @Override
    public void onPaint(Graphics g) {
        toDrawBalls.clear();

        OrthoCamera cam = (OrthoCamera) Global.getCurrentScene().getCamera();

        for (EntityFactory factory : entityListEditor.getScene().getEntityFactories()) {
            if (factory instanceof PreMadeEntityFactory f) {
                paintFactory(f, g, cam);
            }
        }
        Paint ballsPaint = new Paint();
        ballsPaint.setColor(ColorUtils.argb(0x50606060));
        for (Vector3fc ball : toDrawBalls) {
            ballsPaint.setTransform(new Transform());
            ballsPaint.getTransform().translate(ball);
            g.getG1().drawEllipse(8 * cam.getTransform().getScale().x(), 8 * cam.getTransform().getScale().y(), ballsPaint);
        }
    }

    public EntityListEditor getEntityListEditor() {
        return entityListEditor;
    }

    public void setEntityListEditor(EntityListEditor entityListEditor) {
        this.entityListEditor = entityListEditor;
    }
}
