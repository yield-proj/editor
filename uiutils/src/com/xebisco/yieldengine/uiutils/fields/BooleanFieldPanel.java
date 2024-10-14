package com.xebisco.yieldengine.uiutils.fields;

import com.xebisco.yieldengine.uiutils.Utils;

import javax.swing.*;
import java.awt.*;

public class BooleanFieldPanel extends FieldPanel<Boolean> {

    private final JCheckBox checkBox;

    public BooleanFieldPanel(String name, boolean value, boolean editable) {
        super(name, editable);

        setLayout(new BorderLayout());
        checkBox = new JCheckBox(Utils.prettyString(name));

        checkBox.setSelected(value);
        checkBox.setEnabled(editable);

        add(checkBox, BorderLayout.CENTER);
    }

    @Override
    public Boolean getValue() {
        return checkBox.isSelected();
    }

    @Override
    public void setValue(Boolean value) {
        checkBox.setSelected(value);
    }
}
