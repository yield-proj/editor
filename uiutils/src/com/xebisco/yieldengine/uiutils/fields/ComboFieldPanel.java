package com.xebisco.yieldengine.uiutils.fields;

import javax.swing.*;
import java.awt.*;

import static com.xebisco.yieldengine.uiutils.Lang.getString;

public class ComboFieldPanel extends FieldPanel<String> {
    private final JComboBox<String> comboBox;

    public ComboFieldPanel(String name, String selected, String[] options, boolean editable) {
        super(name, editable);
        setLayout(new BorderLayout());
        add(new JLabel(getString(name) + ": "), BorderLayout.WEST);
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
