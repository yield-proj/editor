package com.xebisco.yieldengine.gameeditor;

import com.xebisco.yieldengine.core.EntityFactory;
import com.xebisco.yieldengine.core.EntityHeader;
import com.xebisco.yieldengine.core.Transform;
import com.xebisco.yieldengine.core.io.audio.Audio;
import com.xebisco.yieldengine.core.io.text.Font;
import com.xebisco.yieldengine.core.io.texture.Texture;
import com.xebisco.yieldengine.gameeditor.fields.AudioField;
import com.xebisco.yieldengine.gameeditor.fields.FontField;
import com.xebisco.yieldengine.gameeditor.fields.HeaderField;
import com.xebisco.yieldengine.gameeditor.fields.TextureField;
import com.xebisco.yieldengine.gameeditor.settings.KeyStrokeSettings;
import com.xebisco.yieldengine.gameeditor.settings.Settings;
import com.xebisco.yieldengine.shipruntime.PreMadeEntityFactory;
import com.xebisco.yieldengine.uilib.SettingsWindow;
import com.xebisco.yieldengine.uilib.UIUtils;
import com.xebisco.yieldengine.uilib.fields.FileField;
import com.xebisco.yieldengine.uilib.projectmng.ProjectMng;
import com.xebisco.yieldengine.utils.FileExtensions;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Main {
    public static final ArrayList<AppAction> APP_ACTIONS = new ArrayList<>();
    private static int undoIndex = -1;

    public record AppAction(String name, Runnable action, Runnable redo) {
    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        Locale.setDefault(Locale.US);
        UIUtils.setupLaf();

        UIUtils.RETURN_FIELD_MAP.put(KeyStrokeSettings.KStroke.class, (_, value, editable, _) -> new KStrokeField(((KeyStrokeSettings.KStroke) value), editable));

        UIUtils.RETURN_FIELD_MAP.put(Transform.class, (name, value, editable, _) -> new TransformField(name, (Transform) value, editable));

        UIUtils.RETURN_FIELD_MAP.put(Font.class, (name, value, editable, field) -> new FontField(name, (Font) value, field.getAnnotation(FileExtensions.class), editable));
        UIUtils.RETURN_FIELD_MAP.put(Texture.class, (name, value, editable, field) -> new TextureField(name, (Texture) value, field.getAnnotation(FileExtensions.class), editable));
        UIUtils.RETURN_FIELD_MAP.put(Audio.class, (name, value, editable, field) -> new AudioField(name, (Audio) value, field.getAnnotation(FileExtensions.class), editable));

        UIUtils.RETURN_FIELD_MAP.put(EntityHeader.class, (_, value, _, _) -> new HeaderField((EntityHeader) value));

        SwingUtilities.invokeAndWait(() -> {
            SettingsWindow.APP_NAME = "yield_editor";
            SettingsWindow.INSTANCE = new com.xebisco.yieldengine.gameeditor.settings.Settings();
            SettingsWindow.loadSettings(Settings.class);
            ProjectMng mng = new ProjectMng(GameEditor.class, GameProject.class);
            mng.setLocationRelativeTo(null);
            mng.setVisible(true);
        });
    }

    public static void aa(AppAction action) {
        while (APP_ACTIONS.size() > undoIndex + 1) {
            APP_ACTIONS.removeLast();
        }
        undoIndex++;
        APP_ACTIONS.add(action);
        action.action().run();
    }

    public static void undo() {
        if(!canUndo()) throw new IllegalStateException("Can't undo");
        APP_ACTIONS.get(undoIndex--).redo().run();
    }

    public static boolean canUndo() {
        return undoIndex >= 0;
    }

    public static void redo() {
        if(!canRedo()) throw new IllegalStateException("Can't redo");
        APP_ACTIONS.get(++undoIndex).action().run();
    }

    public static boolean canRedo() {
        return undoIndex < APP_ACTIONS.size() - 1;
    }

    public static void processFactories(List<EntityFactory> factories) {
        factories.sort((o1, o2) -> {
            if(o1 instanceof PreMadeEntityFactory && o2 instanceof PreMadeEntityFactory) {
                return ((PreMadeEntityFactory) o2).compareTo(o1);
            }
            return -1;
        });
    }

    public static void setMappings(JComponent c) {
        ActionMap map = c.getActionMap();
        map.put(TransferHandler.getCutAction().getValue(Action.NAME),
                TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());

    }

    private static File projectFolder;

    public static void setProjectFolder(File projectFolder) {
        Main.projectFolder = projectFolder;
    }

    public static File getProjectFolder() {
        return projectFolder;
    }

    public static File getAssetsFolder() {
        File assetsFolder = new File(getProjectFolder(), "assets");
        if (!assetsFolder.isDirectory()) {
            if(!assetsFolder.mkdir()) {
                UIUtils.error(new IllegalStateException("Could not create assets folder"));
            }
        }
        return assetsFolder;
    }

    public static File getAsset(String name) {
        return new File(getAssetsFolder(), name);
    }

    public static String getAssetPath(String name) {
        return FileField.getPathInDir(name, getAssetsFolder());
    }
}
