package com.xebisco.yieldengine.gameeditor;

import com.xebisco.yieldengine.gameeditor.settings.KeyStrokeSettings;
import com.xebisco.yieldengine.uilib.fields.StringField;

public class KStrokeField extends StringField {
    private final KeyStrokeSettings.KStroke stroke;

    public KStrokeField(KeyStrokeSettings.KStroke stroke, boolean editable) {
        super(stroke.getName(), stroke.getKeyCode(), editable);
        this.stroke = stroke;
    }

    @Override
    public KeyStrokeSettings.KStroke getValue() {
        stroke.setKeyCode((String) super.getValue());
        return stroke;
    }
}
