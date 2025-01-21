package com.xebisco.yieldengine.gameeditor.editorfactories.components;

import com.xebisco.yieldengine.core.*;
import com.xebisco.yieldengine.core.camera.OrthoCamera;
import com.xebisco.yieldengine.core.graphics.Graphics;
import com.xebisco.yieldengine.core.graphics.IPainter;
import com.xebisco.yieldengine.core.graphics.yldg1.Paint;
import com.xebisco.yieldengine.core.input.Input;
import com.xebisco.yieldengine.core.input.MouseButton;
import com.xebisco.yieldengine.core.io.texture.Texture;
import com.xebisco.yieldengine.gameeditor.EntityListEditor;
import com.xebisco.yieldengine.gameeditor.Inspector;
import com.xebisco.yieldengine.gameeditor.editorfactories.MousePosition;
import com.xebisco.yieldengine.gameeditor.settings.Settings;
import com.xebisco.yieldengine.shipruntime.PreMadeEntityFactory;
import com.xebisco.yieldengine.utils.ColorPalette;
import org.joml.Vector3f;

public class EntitySelectorComp extends Component implements IPainter {
    private EntityListEditor entityListEditor;
    private PreMadeEntityFactory selectedFactory;

    private boolean rightClickLock, leftClickLock, blockSelection, xSel, ySel, movX, movY;
    private float clickX, clickY;

    private Texture arrowTexture = new Texture("uiimages/arrow.png");

    @Override
    public void onUpdate() {
        OrthoCamera cam = (OrthoCamera) Global.getCurrentScene().getCamera();

        if (Input.getInstance().isMouseButtonPressed(MouseButton.BUTTON_3)) {
            if (!rightClickLock) {
                rightClickLock = true;
                entityListEditor.showNewEntityPopup(Global.getCurrentScene().getEntityFactories());
            }
        } else {
            rightClickLock = false;
        }
        if (Input.getInstance().isMouseButtonPressed(MouseButton.BUTTON_1)) {
            if (!leftClickLock) {
                leftClickLock = true;
                if (!blockSelection) {
                    selectedFactory = null;
                    for (EntityFactory factory : Global.getCurrentScene().getEntityFactories()) {
                        if (factory instanceof PreMadeEntityFactory f) {
                            Transform t = f.getTransform();
                            if (t.getTranslation().distance(new Vector3f(MousePosition.X, MousePosition.Y, 0)) < Settings.getInstance().ENTITY_SELECTOR.selectorMinimumDistance * cam.getTransform().getScale().x()) {
                                selectedFactory = (PreMadeEntityFactory) factory;
                                Inspector.set(selectedFactory);
                                break;
                            }
                        }

                    }
                }
                clickX = MousePosition.X;
                clickY = MousePosition.Y;
                if (xSel && ySel) {
                    movX = true;
                    movY = true;
                }
            }
        } else {
            movX = false;
            movY = false;
            leftClickLock = false;
        }

        if (selectedFactory != null) {
            float w = arrowTexture.getWidth() * cam.getTransform().getScale().x();
            float h = arrowTexture.getHeight() * cam.getTransform().getScale().y();

            Transform entityTransform = selectedFactory.getTransform();

            //Y
            ySel = MousePosition.X >= entityTransform.getTranslation().x() - w / 4 && MousePosition.X <= entityTransform.getTranslation().x() + w / 4
                    && MousePosition.Y >= entityTransform.getTranslation().y() && MousePosition.Y <= entityTransform.getTranslation().y() + h;

            //X
            xSel = MousePosition.Y >= entityTransform.getTranslation().y() - w / 4 && MousePosition.Y <= entityTransform.getTranslation().y() + w / 4
                    && MousePosition.X >= entityTransform.getTranslation().x() && MousePosition.X <= entityTransform.getTranslation().x() + h;

        }

        blockSelection = false;

        if (xSel) {
            blockSelection = true;
            if (Input.getInstance().isMouseButtonPressed(MouseButton.BUTTON_1) && !movY) {
                movX = true;
            }
        }

        if (movX && selectedFactory != null) {
            if (MousePosition.lockToGrid) {
                selectedFactory.getTransform().translate(MousePosition.GX - selectedFactory.getTransform().getTranslation().x(), 0);
            } else {
                selectedFactory.getTransform().translate(MousePosition.X - clickX, 0);
                clickX = MousePosition.X;
            }
        }

        if (ySel) {
            blockSelection = true;
            if (Input.getInstance().isMouseButtonPressed(MouseButton.BUTTON_1) && !movX) {
                movY = true;
            }
        }

        if (movY && selectedFactory != null) {
            if (MousePosition.lockToGrid) {
                selectedFactory.getTransform().translate(0, MousePosition.GY - selectedFactory.getTransform().getTranslation().y());
            } else {
                selectedFactory.getTransform().translate(0, MousePosition.Y - clickY);
                clickY = MousePosition.Y;
            }
        }
    }

    @Override
    public void onPaint(Graphics g) {
        if (selectedFactory == null) return;

        Paint paint = new Paint();
        paint.setColor(ColorPalette.Colors.WHITE.get());
        paint.setTexture(arrowTexture);

        OrthoCamera cam = (OrthoCamera) Global.getCurrentScene().getCamera();
        float w = arrowTexture.getWidth() * cam.getTransform().getScale().x();
        float h = arrowTexture.getHeight() * cam.getTransform().getScale().y();

        Transform entityTransform = selectedFactory.getTransform();

        //Y GREEN

        paint.setTransform(new Transform(entityTransform)
                .translate(0, h / 2f));
        paint.setColor(ySel || movY ? ColorPalette.Colors.WHITE.get() : ColorPalette.Colors.GREEN.get());
        g.getG1().drawImage(w, h, paint);

        //X RED

        paint.setTransform(new Transform(entityTransform)
                .rotateZ((float) Math.toRadians(-90))
                .translate(h / 2, 0));

        paint.setColor(xSel || movX ? ColorPalette.Colors.WHITE.get() : ColorPalette.Colors.RED.get());

        g.getG1().drawImage(w, h, paint);

        paint.setTransform(entityTransform);
        paint.setColor(ColorPalette.Colors.WHITE.get());

        g.getG1().drawEllipse(8 * cam.getTransform().getScale().x(), 8 * cam.getTransform().getScale().y(), paint);
    }

    public EntityListEditor getEntityListEditor() {
        return entityListEditor;
    }

    public EntitySelectorComp setEntityListEditor(EntityListEditor entityListEditor) {
        this.entityListEditor = entityListEditor;
        return this;
    }
}
