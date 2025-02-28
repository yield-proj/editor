package com.xebisco.yieldengine.gameeditor.settings;

import com.xebisco.yieldengine.utils.Editable;
import com.xebisco.yieldengine.utils.Visible;

import javax.swing.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KeyStrokeSettings implements Serializable {
    @Serial
    private static final long serialVersionUID = 7950115555768281386L;

    private final static List<KStroke> DEFAULT_KEY_STROKES = List.of(
            new KStroke("Undo", "control Z"),
            new KStroke("Redo", "control shift Z"),
            new KStroke("Copy", "control C"),
            new KStroke("Cut", "control X"),
            new KStroke("Paste",  "control V"),
            new KStroke("Save", "control S")
    );

    public KeyStrokeSettings() {
        initStrokes();
    }

    @Serial
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        initStrokes();
    }

    private void initStrokes() {
        List<KStroke> keyStrokesList = new ArrayList<>();
        if(keyStrokes.length > 0) keyStrokesList.addAll(List.of(keyStrokes));
        List<KStroke> toRemove = new ArrayList<>();
        for(KStroke s : keyStrokesList) {
            if(!DEFAULT_KEY_STROKES.contains(s)) {
                toRemove.add(s);
            }
        }
        keyStrokesList.removeAll(toRemove);

        for(KStroke s : DEFAULT_KEY_STROKES) {
            if(!keyStrokesList.contains(s)) {
                keyStrokesList.add(s);
            }
        }
        keyStrokes = keyStrokesList.toArray(new KStroke[0]);
    }

    @Visible
    public KStroke[] keyStrokes = new KStroke[0];

    public KStroke getKeyStroke(String name) {
        for(KStroke s : keyStrokes) {
            if(s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    public static class KStroke implements Serializable {
        @Serial
        private static final long serialVersionUID = 6196657330214605138L;
        private final String name;
        private String keyCode;

        public KStroke(String name, String keyCode) {
            this.name = name;
            this.keyCode = keyCode;
        }

        public String getName() {
            return name;
        }

        public KeyStroke getStroke() {
            return KeyStroke.getKeyStroke(keyCode);
        }

        public String getKeyCode() {
            return keyCode;
        }

        public void setKeyCode(String keyCode) {
            this.keyCode = keyCode;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            KStroke kStroke = (KStroke) o;
            return Objects.equals(name, kStroke.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }
}
