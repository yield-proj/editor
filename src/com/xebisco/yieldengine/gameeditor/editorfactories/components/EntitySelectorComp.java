package com.xebisco.yieldengine.gameeditor.editorfactories.components;

import com.xebisco.yieldengine.core.*;
import com.xebisco.yieldengine.core.camera.OrthoCamera;
import com.xebisco.yieldengine.core.graphics.Graphics;
import com.xebisco.yieldengine.core.graphics.IPainter;
import com.xebisco.yieldengine.core.graphics.yldg1.Paint;
import com.xebisco.yieldengine.core.input.Input;
import com.xebisco.yieldengine.core.input.MouseButton;
import com.xebisco.yieldengine.core.io.IO;
import com.xebisco.yieldengine.core.io.texture.Texture;
import com.xebisco.yieldengine.gameeditor.EntityListEditor;
import com.xebisco.yieldengine.gameeditor.Inspector;
import com.xebisco.yieldengine.gameeditor.Main;
import com.xebisco.yieldengine.gameeditor.editorfactories.MousePosition;
import com.xebisco.yieldengine.gameeditor.settings.Settings;
import com.xebisco.yieldengine.shipruntime.PreMadeEntityFactory;
import com.xebisco.yieldengine.utils.ColorPalette;
import com.xebisco.yieldengine.utils.concurrency.ASyncFunction;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.concurrent.atomic.AtomicReference;

public class EntitySelectorComp extends Component implements IPainter {
    private EntityListEditor entityListEditor;
    public static PreMadeEntityFactory selectedFactory;

    private boolean rightClickLock, leftClickLock, blockSelection, xSel, ySel, movX, movY, canSelect;
    private float clickX, clickY;
    private Vector2f startPos = new Vector2f();

    private Texture arrowTexture = IO.getInstance().loadTexture("editoruires/arrow.png");

    public PreMadeEntityFactory getFac(OrthoCamera cam) {
        for (EntityFactory factory : entityListEditor.getScene().getEntityFactories()) {
            if (factory instanceof PreMadeEntityFactory f) {
                Transform t = f.getTransform();
                if (t.getTranslation().distance(new Vector3f(MousePosition.X, MousePosition.Y, 0)) < Settings.getInstance().ENTITY_SELECTOR.selectorMinimumDistance * cam.getTransform().getScale().x()) {
                    return (PreMadeEntityFactory) factory;
                }
            }
        }
        return null;
    }

    @Override
    public void onUpdate() {
        OrthoCamera cam = (OrthoCamera) Global.getCurrentScene().getCamera();

        if (Input.getInstance().isMouseButtonPressed(MouseButton.BUTTON_3)) {
            if (!rightClickLock) {
                rightClickLock = true;
                PreMadeEntityFactory fac = getFac(cam);
                if (fac != null) {
                    entityListEditor.showNewEntityPopup(fac.getChildren(), fac);
                } else {
                    entityListEditor.showNewEntityPopup(entityListEditor.getScene().getEntityFactories(), null);
                }
            }
        } else {
            rightClickLock = false;
        }
        if (Input.getInstance().isMouseButtonPressed(MouseButton.BUTTON_1)) {
            if (!leftClickLock) {
                leftClickLock = true;
                if (!blockSelection) {
                    canSelect = false;
                    selectedFactory = null;
                    PreMadeEntityFactory fac = getFac(cam);
                    if (fac != null) {
                        Inspector.setGlobal(fac, true);
                    }
                }
                if(selectedFactory != null) {
                    startPos = new Vector2f(selectedFactory.getTransform().getTranslation());
                }
                clickX = MousePosition.X;
                clickY = MousePosition.Y;
                if (xSel && ySel) {
                    movX = true;
                    movY = true;
                }
            }
        } else {
            if (leftClickLock) {
                if (selectedFactory != null && canSelect) {
                    PreMadeEntityFactory sf = selectedFactory;
                    Vector2f pos = new Vector2f(sf.getTransform().getTranslation()), sp = new Vector2f(startPos);
                    Main.aa(new Main.AppAction(
                            "Move Entity",
                            () -> {
                                Inspector.setMovingEntity(true);
                                sf.getTransform().position(pos);
                                ASyncFunction.aSync(() -> Inspector.setMovingEntity(false), ASyncFunction.NONE, 500);
                            },
                            () -> {
                                Inspector.setMovingEntity(true);
                                sf.getTransform().position(sp);
                                ASyncFunction.aSync(() -> Inspector.setMovingEntity(false), ASyncFunction.NONE, 500);
                            }
                    ));
                }
            }
            canSelect = true;
            movX = false;
            movY = false;
            leftClickLock = false;
        }

        if (selectedFactory != null) {
            float w = arrowTexture.getWidth() * cam.getTransform().getScale().x();
            float h = arrowTexture.getHeight() * cam.getTransform().getScale().y();

            Transform entityTransform = selectedFactory.getNewWorldTransform();

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

        if (movX && selectedFactory != null && canSelect) {
            if (MousePosition.lockToGrid) {
                selectedFactory.getTransform().translate(MousePosition.GX - selectedFactory.getNewWorldTransform().getTranslation().x(), 0);
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

        if (movY && selectedFactory != null && canSelect) {
            if (MousePosition.lockToGrid) {
                selectedFactory.getTransform().translate(0, MousePosition.GY - selectedFactory.getNewWorldTransform().getTranslation().y());
            } else {
                selectedFactory.getTransform().translate(0, MousePosition.Y - clickY);
                clickY = MousePosition.Y;
            }
        }

        Inspector.setMovingEntity((movX || movY) && selectedFactory != null);
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

        Transform entityTransform = selectedFactory.getNewWorldTransform();

        //Y GREEN

        paint.setTransform(new Transform().translate(entityTransform.getTranslation())
                .translate(0, h / 2f));
        paint.setColor(ySel || movY ? ColorPalette.Colors.WHITE.get() : ColorPalette.Colors.GREEN.get());
        g.getG1().drawImage(w, h, paint);

        //X RED

        paint.setTransform(new Transform().translate(entityTransform.getTranslation())
                .rotateZ((float) Math.toRadians(-90))
                .translate(h / 2, 0));

        paint.setColor(xSel || movX ? ColorPalette.Colors.WHITE.get() : ColorPalette.Colors.RED.get());

        g.getG1().drawImage(w, h, paint);

        paint.setTransform(new Transform().translate(entityTransform.getTranslation()));
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
