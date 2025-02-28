package com.xebisco.yieldengine.gameeditor.fields;

import com.xebisco.yieldengine.core.EntityHeader;
import com.xebisco.yieldengine.core.Global;
import com.xebisco.yieldengine.uilib.fields.*;

import javax.swing.*;
import java.awt.*;

public class HeaderField extends EditableField {

    private final EntityHeader header;
    private final StringField nameField;
    private final MultiComboField tagField;
    private final ComboField layerField;
    private final BooleanField enabledField;

    public HeaderField(EntityHeader header) {
        this.header = header;
        setLayout(new BorderLayout(0, 5));

        nameField = new StringField("name", header.getName(), true);
        add(nameField);

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
        add(bottom, BorderLayout.SOUTH);

        try {
            tagField = new MultiComboField("tags", header.getTags(), Global.class.getMethod("getTags"), true);
            bottom.add(tagField);
            bottom.add(Box.createHorizontalStrut(10));
            layerField = new ComboField("layer", header.getLayer(), Global.class.getMethod("getLayers"), true);
            bottom.add(layerField);
            bottom.add(Box.createHorizontalStrut(10));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        enabledField = new BooleanField("enabled", header.isEnabled(), true) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(90, super.getPreferredSize().height);
            }
        };
        bottom.add(enabledField);
    }

    @Override
    public EntityHeader getValue() {
        header.setName((String) nameField.getValue());
        header.setTags(tagField.getValue());
        header.setLayer(layerField.getValue());
        header.setEnabled(enabledField.getValue());
        return header;
    }
}
