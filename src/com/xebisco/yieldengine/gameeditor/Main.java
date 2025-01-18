package com.xebisco.yieldengine.gameeditor;

import com.xebisco.yieldengine.gameeditor.settings.Settings;
import com.xebisco.yieldengine.uilib.SettingsWindow;
import com.xebisco.yieldengine.uilib.UIUtils;
import com.xebisco.yieldengine.uilib.projectmng.ProjectMng;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        UIUtils.setupLaf();

        SwingUtilities.invokeAndWait(() -> {
            SettingsWindow.APP_NAME = "yield_editor";
            SettingsWindow.INSTANCE = new com.xebisco.yieldengine.gameeditor.settings.Settings();
            SettingsWindow.loadSettings(Settings.class);
            ProjectMng mng = new ProjectMng(GameEditor.class, GameProject.class);
            mng.setLocationRelativeTo(null);
            mng.setVisible(true);
        });
    }
}
