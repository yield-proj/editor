package com.xebisco.yieldengine.gameeditor;

import com.xebisco.yieldengine.core.Transform;
import com.xebisco.yieldengine.core.io.audio.Audio;
import com.xebisco.yieldengine.core.io.text.Font;
import com.xebisco.yieldengine.core.io.texture.Texture;
import com.xebisco.yieldengine.gameeditor.settings.Settings;
import com.xebisco.yieldengine.uilib.SettingsWindow;
import com.xebisco.yieldengine.uilib.UIUtils;
import com.xebisco.yieldengine.uilib.fields.FileField;
import com.xebisco.yieldengine.uilib.projectmng.ProjectMng;
import com.xebisco.yieldengine.utils.FileExtensions;
import com.xebisco.yieldengine.utils.ObjectUtils;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        UIUtils.setupLaf();

        UIUtils.RETURN_FIELD_MAP.put(Transform.class, (name, value, editable, _) -> new TransformField(name, (Transform) value, editable));

        UIUtils.RETURN_FIELD_MAP.put(Font.class, (name, value, editable, field) -> new FontField(name, (Font) value, field.getAnnotation(FileExtensions.class), editable));
        UIUtils.RETURN_FIELD_MAP.put(Texture.class, (name, value, editable, field) -> new FileField(name, getFile(((Texture) value).getPath()), field.getAnnotation(FileExtensions.class), editable));
        UIUtils.RETURN_FIELD_MAP.put(Audio.class, (name, value, editable, field) -> new FileField(name, getFile(((Audio) value).getPath()), field.getAnnotation(FileExtensions.class), editable));

        SwingUtilities.invokeAndWait(() -> {
            SettingsWindow.APP_NAME = "yield_editor";
            SettingsWindow.INSTANCE = new com.xebisco.yieldengine.gameeditor.settings.Settings();
            SettingsWindow.loadSettings(Settings.class);
            ProjectMng mng = new ProjectMng(GameEditor.class, GameProject.class);
            mng.setLocationRelativeTo(null);
            mng.setVisible(true);
        });
    }

    public static File getFile(String name) {
        return new File(name);
    }
}
