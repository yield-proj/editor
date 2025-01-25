package com.xebisco.yieldengine.gameeditor;

import com.xebisco.yieldengine.core.io.text.Font;
import com.xebisco.yieldengine.core.io.text.FontProperties;
import com.xebisco.yieldengine.uilib.fields.BooleanField;
import com.xebisco.yieldengine.uilib.fields.FileField;
import com.xebisco.yieldengine.uilib.fields.NumberField;
import com.xebisco.yieldengine.utils.FileExtensions;

import javax.swing.*;
import java.awt.*;

public class FontField extends FileField {
    private final NumberField<Float> fontSize;
    private final BooleanField antiAlias;

    public FontField(String name, Font font, FileExtensions extensions, boolean editable) {
        super(name, font == null ? null : Main.getFile((font).getProperties().getPath()), extensions, editable);
        JPanel props = new JPanel();
        props.setLayout(new BoxLayout(props, BoxLayout.X_AXIS));
        fontSize = new NumberField<>("size", font == null ? 12f : font.getProperties().getSize(), Float.class, editable);
        props.add(fontSize);

        antiAlias = new BooleanField("AA", font != null && font.getProperties().isAntiAliasing(), editable);
        props.add(antiAlias);
        add(props, BorderLayout.EAST);
    }

    @Override
    public Font getValue() {
        if(!getFileValue().exists()) return null;
        Font font = new Font(new FontProperties(getFileValue().getPath(), fontSize.getValue(), antiAlias.getValue()));
        font.load();
        return font;
    }
}
