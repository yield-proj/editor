package com.xebisco.yieldengine.gameeditor.settings;

import com.xebisco.yieldengine.uilib.BasicSettings;
import com.xebisco.yieldengine.uilib.SettingsWindow;
import com.xebisco.yieldengine.uilib.UIUtils;
import com.xebisco.yieldengine.utils.Pair;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class Settings extends BasicSettings {
    @Serial
    private static final long serialVersionUID = 3758102755696519996L;

    //EDITOR SETTINGS
    public EntitySelector ENTITY_SELECTOR;
    public CameraSettings CAMERA_SETTINGS;
    public KeyStrokeSettings KEY_STROKE_SETTINGS;

    @Override
    protected Pair<ArrayList<Runnable>, ArrayList<DefaultMutableTreeNode>> tabs() {
        Pair<ArrayList<Runnable>, ArrayList<DefaultMutableTreeNode>> tabs =  super.tabs();

        addSection(tabs, "Editor Settings", ENTITY_SELECTOR, CAMERA_SETTINGS, KEY_STROKE_SETTINGS);

        return tabs;
    }

    public static Settings getInstance() {
        return (Settings) SettingsWindow.INSTANCE;
    }
}
