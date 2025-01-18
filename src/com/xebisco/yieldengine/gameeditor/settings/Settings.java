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
    public final EntitySelector ENTITY_SELECTOR = new EntitySelector();
    public final CameraSettings CAMERA_SETTINGS = new CameraSettings();

    @Override
    protected Pair<ArrayList<Runnable>, ArrayList<DefaultMutableTreeNode>> tabs() {
        Pair<ArrayList<Runnable>, ArrayList<DefaultMutableTreeNode>> tabs =  super.tabs();

        //EDITOR SETTINGS
        Pair<Runnable, DefaultMutableTreeNode> editorSettingsTab = editorSettingsTab();
        UIUtils.depthPanel(editorSettingsTab.second(), "Editor Settings");

        tabs.second().add(editorSettingsTab.second());
        tabs.first().add(editorSettingsTab.first());



        return tabs;
    }


    protected Pair<Runnable, DefaultMutableTreeNode> entitySelectorTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setName("Entity Selector");
        Pair<Runnable, JPanel> v = UIUtils.getObjectsFieldsPanel(new Object[]{ENTITY_SELECTOR});
        p.add(v.second());
        return new Pair<>(v.first(), new DefaultMutableTreeNode(p));
    }

    protected Pair<Runnable, DefaultMutableTreeNode> cameraSettingsTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setName("Camera Settings");
        Pair<Runnable, JPanel> v = UIUtils.getObjectsFieldsPanel(new Object[]{CAMERA_SETTINGS});
        p.add(v.second());
        return new Pair<>(v.first(), new DefaultMutableTreeNode(p));
    }

    protected Pair<Runnable, DefaultMutableTreeNode> editorSettingsTab() {
        DefaultMutableTreeNode editorSettings = new DefaultMutableTreeNode();

        List<Runnable> applyList = new ArrayList<>();

        Pair<Runnable, DefaultMutableTreeNode> entitySelectorTab = entitySelectorTab();
        editorSettings.add(entitySelectorTab.second());
        applyList.add(entitySelectorTab.first());

        Pair<Runnable, DefaultMutableTreeNode> cameraSettingsTab = cameraSettingsTab();
        editorSettings.add(cameraSettingsTab.second());
        applyList.add(cameraSettingsTab.first());

        return new Pair<>(() -> applyList.forEach(Runnable::run), editorSettings);
    }

    public static Settings getInstance() {
        return (Settings) SettingsWindow.INSTANCE;
    }
}
