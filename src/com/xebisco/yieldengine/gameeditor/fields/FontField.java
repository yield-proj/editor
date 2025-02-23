package com.xebisco.yieldengine.gameeditor.fields;

import com.xebisco.yieldengine.core.io.text.Font;
import com.xebisco.yieldengine.core.io.text.FontProperties;
import com.xebisco.yieldengine.gameeditor.Main;
import com.xebisco.yieldengine.uilib.DirectoryRestrictedFileSystemView;
import com.xebisco.yieldengine.uilib.DocumentChangeListener;
import com.xebisco.yieldengine.uilib.UIUtils;
import com.xebisco.yieldengine.uilib.fields.BooleanField;
import com.xebisco.yieldengine.uilib.fields.FileField;
import com.xebisco.yieldengine.uilib.fields.NumberField;
import com.xebisco.yieldengine.utils.FileExtensions;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class FontField extends FileField {
    private final NumberField<Float> fontSize;
    private final BooleanField antiAlias;
    private Font font;

    public FontField(String name, Font font, FileExtensions extensions, boolean editable) {
        super(name, font == null ? null : Main.getAsset((font).getProperties().getPath()), extensions, new DirectoryRestrictedFileSystemView(Main.getAssetsFolder()), editable);
        JPanel props = new JPanel();
        props.setLayout(new BoxLayout(props, BoxLayout.X_AXIS));
        props.add(new JSeparator(SwingConstants.VERTICAL));
        props.add(Box.createHorizontalStrut(8));
        fontSize = new NumberField<>("size", font == null ? 40f : font.getProperties().getSize(), Float.class, editable) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(70, super.getPreferredSize().height);
            }
        };
        fontSize.getTextField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFont();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFont();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        props.add(fontSize, BorderLayout.WEST);
        fontSize.getTextField().getFormatter().setMinimum(4f);
        getTextField().getDocument().addDocumentListener((DocumentChangeListener) _ -> {
            updateFont();
        });
        props.add(Box.createHorizontalStrut(8));

        antiAlias = new BooleanField("AA", font == null || font.getProperties().isAntiAliasing(), editable) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(50, super.getPreferredSize().height);
            }
        };
        antiAlias.getCheckBox().addItemListener(_ -> {
            updateFont();
        });
        props.add(antiAlias, BorderLayout.EAST);
        add(props, BorderLayout.EAST);
        updateFont();
    }

    private void updateFont() {
        if (font != null) {
            font.dispose();
            font = null;
        }
        if (!getFileValue().exists() || getFileValue().isDirectory()) {
            font = null;
            fontSize.getTextField().setEnabled(false);
            antiAlias.getCheckBox().setEnabled(false);
            return;
        }
        fontSize.getTextField().setEnabled(true);
        antiAlias.getCheckBox().setEnabled(true);
        font = new Font(new FontProperties(Main.getAssetPath(getFileValue().getPath()), fontSize.getValue(), antiAlias.getValue()));
        try {
            font.load();
        } catch (Exception e) {
            UIUtils.error(e);
        }
    }

    @Override
    public Font getValue() {
        return font;
    }
}
