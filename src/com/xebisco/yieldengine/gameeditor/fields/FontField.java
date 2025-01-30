package com.xebisco.yieldengine.gameeditor.fields;

import com.xebisco.yieldengine.core.io.text.Font;
import com.xebisco.yieldengine.core.io.text.FontProperties;
import com.xebisco.yieldengine.gameeditor.Main;
import com.xebisco.yieldengine.uilib.DirectoryRestrictedFileSystemView;
import com.xebisco.yieldengine.uilib.DocumentChangeListener;
import com.xebisco.yieldengine.uilib.fields.BooleanField;
import com.xebisco.yieldengine.uilib.fields.FileField;
import com.xebisco.yieldengine.uilib.fields.NumberField;
import com.xebisco.yieldengine.utils.FileExtensions;

import javax.swing.*;
import java.awt.*;

public class FontField extends FileField {
    private final NumberField<Float> fontSize;
    private final BooleanField antiAlias;
    private Font font;

    public FontField(String name, Font font, FileExtensions extensions, boolean editable) {
        super(name, font == null ? null : Main.getAsset((font).getProperties().getPath()), extensions, new DirectoryRestrictedFileSystemView(Main.getAssetsFolder()), editable);
        JPanel props = new JPanel();
        props.setLayout(new BoxLayout(props, BoxLayout.X_AXIS));
        fontSize = new NumberField<>("size", font == null ? 12f : font.getProperties().getSize(), Float.class, editable);
        props.add(fontSize);
        getTextField().getDocument().addDocumentListener((DocumentChangeListener) _ -> {
            updateFont();
        });

        antiAlias = new BooleanField("AA", font != null && font.getProperties().isAntiAliasing(), editable);
        props.add(antiAlias);
        add(props, BorderLayout.EAST);
        updateFont();
    }

    private void updateFont() {
        if(font != null) {
            font.dispose();
            font = null;
        }
        if(!getFileValue().exists() || getFileValue().isDirectory()) {
            font = null;
            return;
        }
        font = new Font(new FontProperties(Main.getAssetPath(getFileValue().getPath()), fontSize.getValue(), antiAlias.getValue()));
        font.load();
    }

    @Override
    public Font getValue() {
        return font;
    }
}
