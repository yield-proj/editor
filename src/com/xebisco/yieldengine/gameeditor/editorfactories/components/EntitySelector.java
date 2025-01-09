package com.xebisco.yieldengine.gameeditor.editorfactories.components;

import com.xebisco.yieldengine.core.Component;
import com.xebisco.yieldengine.core.Entity;
import com.xebisco.yieldengine.core.Global;
import com.xebisco.yieldengine.core.Transform;
import com.xebisco.yieldengine.core.camera.OrthoCamera;
import com.xebisco.yieldengine.core.graphics.Graphics;
import com.xebisco.yieldengine.core.graphics.IPainter;
import com.xebisco.yieldengine.core.graphics.yldg1.Paint;
import com.xebisco.yieldengine.core.input.Input;
import com.xebisco.yieldengine.core.input.MouseButton;
import com.xebisco.yieldengine.core.io.texture.Texture;
import com.xebisco.yieldengine.gameeditor.EntityListEditor;
import com.xebisco.yieldengine.utils.ColorPalette;
import com.xebisco.yieldengine.utils.ColorUtils;

public class EntitySelector extends Component implements IPainter {
    private EntityListEditor entityListEditor;
    private Entity selectedEntity;

    private boolean rightClickLock;

    private Texture arrowTexture = new Texture("uiimages/arrow.png");

    @Override
    public void onUpdate() {
        if(Input.getInstance().isMouseButtonPressed(MouseButton.BUTTON_3)) {
            if(!rightClickLock) {
                entityListEditor.showNewEntityPopup(Global.getCurrentScene().getEntityFactories());
                rightClickLock = true;
            }
        } else {
            rightClickLock = false;
        }
    }

    @Override
    public void onPaint(Graphics g) {

        Paint paint = new Paint();
        paint.setColor(ColorPalette.Colors.WHITE.get());
        paint.setTexture(arrowTexture);

        OrthoCamera cam = (OrthoCamera) Global.getCurrentScene().getCamera();
        float w = arrowTexture.getWidth() * cam.getTransform().getScale().x();
        float h = arrowTexture.getHeight() * cam.getTransform().getScale().y();

        Transform entityTransform = new Transform(); //TODO

        //Y GREEN

        paint.setTransform(new Transform(entityTransform)
                .translate(0, h / 2f));
        paint.setColor(ColorPalette.Colors.GREEN.get());
        g.getG1().drawImage(w, h, paint);

        //X RED

        paint.setTransform(new Transform(entityTransform)
                .rotateZ((float) Math.toRadians(-90))
                .translate(h / 2, 0));

        paint.setColor(ColorPalette.Colors.RED.get());

        g.getG1().drawImage(w, h, paint);

        paint.setTransform(entityTransform);
        paint.setColor(ColorPalette.Colors.WHITE.get());

        g.getG1().drawEllipse(8 * cam.getTransform().getScale().x(), 8 * cam.getTransform().getScale().y(), paint);
    }

    public EntityListEditor getEntityListEditor() {
        return entityListEditor;
    }

    public EntitySelector setEntityListEditor(EntityListEditor entityListEditor) {
        this.entityListEditor = entityListEditor;
        return this;
    }
}
