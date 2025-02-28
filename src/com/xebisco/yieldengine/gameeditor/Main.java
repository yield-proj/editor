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
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
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

    public static Process executeBashCommand(String command) {
        return executeBashCommand(command, true);
    }

    public static Process executeBashCommand(String command, boolean printError) {
        System.out.println("Executing BASH command:\n   " + command);
        Runtime r = Runtime.getRuntime();
        // Use bash -c so we can handle things like multi commands separated by ; and
        // things like quotes, $, |, and \. My tests show that command comes as
        // one argument to bash, so we do not need to quote it to make it one thing.
        // Also, exec may object if it does not have an executable file as the first thing,
        // so having bash here makes it happy provided bash is installed and in path.
        String[] commands = {"bash", "-c", command};
        try {
            Process p = r.exec(commands);

            p.waitFor();
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;

            while ((line = b.readLine()) != null) {
                System.out.println(line);
            }
            b.close();

            if (printError) {
                b = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                while ((line = b.readLine()) != null) {
                    System.out.println(line);
                }
                b.close();
            }
            return p;
        } catch (Exception e) {
            System.err.println("Failed to execute bash with command: " + command);
            throw new RuntimeException(e);
        }
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
        if (!canUndo()) throw new IllegalStateException("Can't undo");
        APP_ACTIONS.get(undoIndex--).redo().run();
    }

    public static boolean canUndo() {
        return undoIndex >= 0;
    }

    public static void redo() {
        if (!canRedo()) throw new IllegalStateException("Can't redo");
        APP_ACTIONS.get(++undoIndex).action().run();
    }

    public static boolean canRedo() {
        return undoIndex < APP_ACTIONS.size() - 1;
    }

    public static void processFactories(List<EntityFactory> factories) {
        factories.sort((o1, o2) -> {
            if (o1 instanceof PreMadeEntityFactory && o2 instanceof PreMadeEntityFactory) {
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
            if (!assetsFolder.mkdir()) {
                UIUtils.error(new IllegalStateException("Could not create assets folder"));
            }
        }
        return assetsFolder;
    }

    public static File getScriptsFolder() {
        File scripsFolder = new File(getProjectFolder(), "scripts");
        if (!scripsFolder.isDirectory()) {
            if (!scripsFolder.mkdir()) {
                UIUtils.error(new IllegalStateException("Could not create scripts folder"));
            }
        }
        return scripsFolder;
    }

    public static File getBuildFolder() {
        File buildFolder = new File(getProjectFolder(), "build");
        if (!buildFolder.isDirectory()) {
            if (!buildFolder.mkdir()) {
                UIUtils.error(new IllegalStateException("Could not create build folder"));
            }
        }
        return buildFolder;
    }

    public static File getAsset(String name) {
        return new File(getAssetsFolder(), name);
    }

    public static String getAssetPath(String name) {
        return FileField.getPathInDir(name, getAssetsFolder());
    }
}
