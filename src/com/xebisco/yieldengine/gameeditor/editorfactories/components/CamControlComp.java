package com.xebisco.yieldengine.gameeditor.editorfactories.components;

import com.xebisco.yieldengine.core.Component;
import com.xebisco.yieldengine.core.Global;
import com.xebisco.yieldengine.core.Time;
import com.xebisco.yieldengine.core.camera.OrthoCamera;
import com.xebisco.yieldengine.core.input.Axis;
import com.xebisco.yieldengine.core.input.Input;
import com.xebisco.yieldengine.core.input.Key;
import com.xebisco.yieldengine.core.input.MouseButton;
import com.xebisco.yieldengine.gameeditor.settings.Settings;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class CamControlComp extends Component {

    Axis back = new Axis(Key.VK_BACK_SPACE);
    private float lastMouseX, lastMouseY;

    @Override
    public void onUpdate() {
        OrthoCamera cam = ((OrthoCamera) Global.getCurrentScene().getCamera());
        float mouseX = Input.getInstance().getMousePosition().x() * cam.getViewport().x() * cam.getTransform().getScale().x(), mouseY = Input.getInstance().getMousePosition().y() * cam.getViewport().y() * cam.getTransform().getScale().y();

        if (lastMouseX > 0 && lastMouseY > 0 && Input.getInstance().isMouseButtonPressed(MouseButton.BUTTON_2)) {
            cam.getTransform().translate(new Vector2f(lastMouseX - mouseX, lastMouseY - mouseY));
        }

        lastMouseX = mouseX;
        lastMouseY = mouseY;

        cam.getTransform().translate(
                        new Vector2f(Axis.HORIZONTAL.getValue(), Axis.VERTICAL.getValue())
                                .mul(Settings.getInstance().CAMERA_SETTINGS.cameraSpeed * Time.getDeltaTime() * cam.getTransform().getScale().x()))
                .scale(new Vector3f(-Input.getInstance().getScrollWheel() * Time.getDeltaTime() * Settings.getInstance().CAMERA_SETTINGS.zoomIntensity, -Input.getInstance().getScrollWheel() * Time.getDeltaTime() * Settings.getInstance().CAMERA_SETTINGS.zoomIntensity, 0));
        if (Input.getInstance().isKeyPressed(Key.VK_BACK_SPACE))
            cam.getTransform().scale(new Vector3f(back.getValue() * Time.getDeltaTime(), back.getValue() * Time.getDeltaTime(), 0));

    }

    @Override
    public void onLateUpdate() {
    }
}
