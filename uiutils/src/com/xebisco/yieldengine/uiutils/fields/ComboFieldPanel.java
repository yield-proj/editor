package com.xebisco.yieldengine.uiutils.fields;

import com.xebisco.yieldengine.uiutils.Utils;

import javax.swing.*;
import java.awt.*;

public class ComboFieldPanel extends FieldPanel<String> {
    private final JComboBox<String> comboBox;

    public ComboFieldPanel(String name, String selected, String[] options, boolean editable) {
        super(name, editable);
        setLayout(new BorderLayout());
        add(new JLabel(Utils.prettyString(name) + ": "), BorderLayout.WEST);
        comboBox = new JComboBox<>(options);
        comboBox.setSelectedItem(selected);
        comboBox.setEditable(false);
        comboBox.setEnabled(editable);
        add(comboBox);
    }

    @Override
    public String getValue() {
        return (String) comboBox.getSelectedItem();
    }

    @Override
    public void setValue(String value) {
        comboBox.setSelectedItem(value);
    }
}
