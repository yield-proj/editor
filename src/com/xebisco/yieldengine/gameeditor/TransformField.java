package com.xebisco.yieldengine.gameeditor;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.xebisco.yieldengine.core.Transform;
import com.xebisco.yieldengine.uilib.UIUtils;
import com.xebisco.yieldengine.uilib.fields.EditableField;
import com.xebisco.yieldengine.uilib.fields.NumberField;
import com.xebisco.yieldengine.uilib.fields.Vector2Field;
import org.joml.AxisAngle4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javax.swing.*;
import java.awt.*;

public class TransformField extends EditableField {
    private final Vector2Field position, scale;
    private final NumberField<Float> rotation;

    public TransformField(String name, Transform value, boolean editable) {
        setLayout(new BorderLayout());
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = UIUtils.nameLabel(name);
        titlePanel.setBorder(new FlatLineBorder(new Insets(3, 5, 3, 5), UIManager.getColor("Component.borderColor"), 100, 8));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));

        mainPanel.add(position = new Vector2Field("position", new Vector2f(value.getTranslation()), editable));
        mainPanel.add(Box.createVerticalStrut(3));
        mainPanel.add(rotation = new NumberField<>("rotation", (float) Math.toDegrees(value.getEulerAngles().z()), Float.class, editable));
        mainPanel.add(Box.createVerticalStrut(3));
        mainPanel.add(scale = new Vector2Field("scale", new Vector2f(value.getScale()), editable));

        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public Transform getValue() {
        Transform transform = new Transform();
        transform.translate(position.getValue());
        transform.scale(new Vector2f(scale.getValue().x - 1, scale.getValue().y - 1));
        transform.rotateZ((float) Math.toRadians(rotation.getValue()));
        return transform;
    }
}
