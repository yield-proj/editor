package com.xebisco.yieldengine.uiutils.fields;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldsPanel extends JPanel {
    private final FieldPanel<?>[] fieldPanels;

    public FieldsPanel(FieldPanel<?>... fieldPanels) {
        this.fieldPanels = fieldPanels;

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        for (FieldPanel<?> fieldPanel : fieldPanels) {
            add(fieldPanel);
            add(Box.createRigidArea(new Dimension(0, 5)));
        }
    }

    public static <T> FieldsPanel fromClass(Class<T> clazz, T o) {
        List<FieldPanel<?>> fieldPanels = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Visible.class)) {
                boolean editable = true;
                if (field.isAnnotationPresent(Editable.class)) {
                    editable = field.getAnnotation(Editable.class).value();
                }
                field.setAccessible(true);
                try {
                    Object fieldObject = field.get(o);
                    if (field.getType().isPrimitive()) {
                        if (field.getType().isAssignableFrom(boolean.class)) {
                            fieldPanels.add(new BooleanFieldPanel(field.getName(), (boolean) fieldObject, editable));
                        } else if (field.getType().isAssignableFrom(int.class)) {
                            fieldPanels.add(new NumberFieldPanel<>(field.getName(), (int) fieldObject, true, editable));
                        } else if (field.getType().isAssignableFrom(byte.class)) {
                            fieldPanels.add(new NumberFieldPanel<>(field.getName(), (byte) fieldObject, true, editable));
                        } else if (field.getType().isAssignableFrom(short.class)) {
                            fieldPanels.add(new NumberFieldPanel<>(field.getName(), (short) fieldObject, true, editable));
                        } else if (field.getType().isAssignableFrom(long.class)) {
                            fieldPanels.add(new NumberFieldPanel<>(field.getName(), (long) fieldObject, true, editable));
                        } else if (field.getType().isAssignableFrom(float.class)) {
                            fieldPanels.add(new NumberFieldPanel<>(field.getName(), (float) fieldObject, true, editable));
                        } else if (field.getType().isAssignableFrom(double.class)) {
                            fieldPanels.add(new NumberFieldPanel<>(field.getName(), (double) fieldObject, true, editable));
                        } else {
                            System.err.println("WARNING: " + clazz + ", " + field.getName() + ", " + field.getType() + " ignored.");
                        }
                    } else if (field.getType().isAssignableFrom(String.class)) {
                        fieldPanels.add(new StringFieldPanel(field.getName(), (String) fieldObject, editable));
                    } else if (field.getType().isAssignableFrom(File.class)) {
                        FileExtensions extensions = null;
                        if (field.isAnnotationPresent(FileExtensions.class)) {
                            extensions = field.getAnnotation(FileExtensions.class);
                        }
                        fieldPanels.add(new FilePanel(field.getName(), (File) fieldObject, extensions, editable));
                    } else {
                        System.err.println("WARNING: " + clazz + ", " + field.getName() + ", " + field.getType() + " ignored.");
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return new FieldsPanel(fieldPanels.toArray(new FieldPanel<?>[0]));
    }

    public static void saveToObject(Object o, Map<String, Serializable> fields) {
        for (String fieldName : fields.keySet()) {
            Field field;
            try {
                field = o.getClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(true);

            try {
                field.set(o, fields.get(fieldName));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static HashMap<String, Serializable> getMap(FieldPanel<?>[] fieldPanels) {
        HashMap<String, Serializable> fields = new HashMap<>();

        for (FieldPanel<?> fieldPanel : fieldPanels) {
            fields.put(fieldPanel.getName(), fieldPanel.getValue());
        }

        return fields;
    }

    public HashMap<String, Serializable> getMap() {
        return getMap(getFieldPanels());
    }

    public void saveToObject(Object o) {
        saveToObject(o, getMap());
    }

    public FieldPanel<?>[] getFieldPanels() {
        return fieldPanels;
    }
}
