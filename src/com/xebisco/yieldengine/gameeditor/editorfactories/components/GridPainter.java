package com.xebisco.yieldengine.gameeditor.editorfactories.components;

import com.xebisco.yieldengine.core.Component;
import com.xebisco.yieldengine.core.Global;
import com.xebisco.yieldengine.core.Transform;
import com.xebisco.yieldengine.core.camera.OrthoCamera;
import com.xebisco.yieldengine.core.graphics.Graphics;
import com.xebisco.yieldengine.core.graphics.IPainter;
import com.xebisco.yieldengine.core.graphics.yldg1.Paint;
import com.xebisco.yieldengine.core.input.Input;
import com.xebisco.yieldengine.core.input.Key;
import com.xebisco.yieldengine.core.io.text.Font;
import com.xebisco.yieldengine.gameeditor.editorfactories.MousePosition;
import com.xebisco.yieldengine.utils.Color4f;
import com.xebisco.yieldengine.utils.ColorUtils;
import com.xebisco.yieldengine.utils.Editable;
import com.xebisco.yieldengine.utils.Visible;
import org.joml.Vector2f;

import static com.xebisco.yieldengine.gameeditor.editorfactories.MousePosition.lockToGrid;

public class GridPainter extends Component implements IPainter {

    @Visible
    @Editable
    private Vector2f size = new Vector2f(16, 16);

    @Visible
    @Editable
    private boolean paint = true;

    @Visible
    @Editable
    private Color4f color = ColorUtils.argb(0x60333333);

    private final Font infoFont = new Font("OpenSans-Regular.ttf", 12f, true);


    private boolean actCtrlLock;

    @Override
    public void onUpdate() {
        OrthoCamera cam = ((OrthoCamera) Global.getCurrentScene().getCamera());
        float mx = cam.getTransform().getTranslation().x() + Input.getInstance().getMousePosition().x() * cam.getViewport().x() * cam.getTransform().getScale().x() - cam.getViewport().x() / 2f * cam.getTransform().getScale().x(),
                my = cam.getTransform().getTranslation().y() + Input.getInstance().getMousePosition().y() * cam.getViewport().y() * cam.getTransform().getScale().y() - cam.getViewport().y() / 2f * cam.getTransform().getScale().y();
        if (Input.getInstance().isKeyPressed(Key.VK_G)) {
            if (!actCtrlLock) {
                lockToGrid = !lockToGrid;
                actCtrlLock = true;
            }
        } else {
            actCtrlLock = false;
        }
        MousePosition.GX = (int) ((int) (Math.abs(mx) + size.x() / 2f) / size.x()) * size.x();
        MousePosition.GY = (int) ((int) (-Math.abs(my) - size.y() / 2f) / size.y()) * size.y();
        if (mx < 0) MousePosition.GX *= -1;
        if (my > 0) MousePosition.GY *= -1;
        MousePosition.X = mx;
        MousePosition.Y = my;
    }

    @Override
    public void onPaint(Graphics g) {
        OrthoCamera camera = ((OrthoCamera) Global.getCurrentScene().getCamera());
        if (paint) {
            if (camera.getTransform().getScale().x() <= 2)
                drawGrid(g, new Vector2f(size).mul(4), camera);
            if (camera.getTransform().getScale().x() <= 6)
                drawGrid(g, size, camera);
        }
        drawAxis(g, camera);
        if (lockToGrid)
            drawMousePoint(g, camera);

        drawInfo(g, camera);
    }

    private void drawInfo(Graphics g, OrthoCamera cam) {
        String text = String.format("X: %.1f, Y: %.1f, zoom: %.2fx", cam.getTransform().getTranslation().x(), cam.getTransform().getTranslation().y(), 1 / cam.getTransform().getScale().x());
        Paint paint = new Paint();
        paint.setFont(infoFont);
        paint.setColor(ColorUtils.argb(0xEEFFFFFF));
        float width = g.getG1().stringWidth(text, paint);
        paint.setTransform(new Transform().translate(new Vector2f(cam.getViewport().x() / 2f - 25 - width / 2f, -cam.getViewport().y() / 2f + 25)));
        paint.setCamera(new OrthoCamera(new Vector2f(cam.getViewport())));

        g.getG1().drawText(text, paint);

        paint.setColor(ColorUtils.argb(0x60606060));
        g.getG1().drawRect(width + 10, infoFont.getSize() + 4, paint);
    }

    private void drawMousePoint(Graphics g, OrthoCamera cam) {
        Paint paint = new Paint()
                .setColor(ColorUtils.argb(0x90FF0000))
                .setTransform(new Transform().translate(MousePosition.GX, MousePosition.GY));
        g.getG1().drawEllipse(14 * cam.getTransform().getScale().x(), 14 * cam.getTransform().getScale().y(), paint);
    }

    private void drawGrid(Graphics g, Vector2f size, OrthoCamera camera) {
        Paint paint = new Paint()
                .setStrokeSize(camera.getTransform().getScale().x() * 1.5f)
                .setColor(color)
                .setTransform(getEntity().getWorldTransform());
        float xs = camera.getViewport().x() * camera.getTransform().getScale().x();
        float ys = camera.getViewport().y() * camera.getTransform().getScale().y();
        float sx = camera.getTransform().getTranslation().x();
        while (sx > size.x) sx -= size.x;
        while (sx < -size.x) sx += size.x;
        float sy = camera.getTransform().getTranslation().y();
        while (sy > size.y) sy -= size.y;
        while (sy < -size.y) sy += size.y;


        for (int i = 0; i <= Math.ceil(ys / 2f / size.y()); i++) {
            g.getG1().drawLine(-xs / 2f + camera.getTransform().getTranslation().x(), i * size.y() + camera.getTransform().getTranslation().y() - sy, xs / 2f + camera.getTransform().getTranslation().x(), i * size.y() + camera.getTransform().getTranslation().y() - sy, paint);
        }
        for (int i = 1; i <= Math.ceil(ys / 2f / size.y()); i++) {
            g.getG1().drawLine(-xs / 2f + camera.getTransform().getTranslation().x(), -i * size.y() + camera.getTransform().getTranslation().y() - sy, xs / 2f + camera.getTransform().getTranslation().x(), -i * size.y() + camera.getTransform().getTranslation().y() - sy, paint);
        }

        for (int i = 0; i <= Math.ceil(xs / 2f / size.x()); i++) {
            g.getG1().drawLine(i * size.x() + camera.getTransform().getTranslation().x() - sx, -ys / 2f + camera.getTransform().getTranslation().y(), i * size.x() + camera.getTransform().getTranslation().x() - sx, ys / 2f + camera.getTransform().getTranslation().y(), paint);
        }
        for (int i = 1; i <= Math.ceil(xs / 2f / size.x()); i++) {
            g.getG1().drawLine(-i * size.x() + camera.getTransform().getTranslation().x() - sx, -ys / 2f + camera.getTransform().getTranslation().y(), -i * size.x() + camera.getTransform().getTranslation().x() - sx, ys / 2f + camera.getTransform().getTranslation().y(), paint);
        }
    }

    private void drawAxis(Graphics g, OrthoCamera camera) {
        float xs = camera.getViewport().x() * camera.getTransform().getScale().x();
        float ys = camera.getViewport().y() * camera.getTransform().getScale().y();
        Paint paint = new Paint()
                .setStrokeSize(camera.getTransform().getScale().x() * 1.5f)
                .setTransform(getEntity().getWorldTransform());
        paint.setColor(ColorUtils.argb(0x60FF0000));
        //X
        g.getG1().drawLine(-xs / 2f + camera.getTransform().getTranslation().x(), 0, xs / 2f + camera.getTransform().getTranslation().x(), 0, paint);

        paint.setColor(ColorUtils.argb(0x6000FF00));
        //Y
        g.getG1().drawLine(0, -ys / 2f + camera.getTransform().getTranslation().y(), 0, ys / 2f + camera.getTransform().getTranslation().y(), paint);
    }
}
