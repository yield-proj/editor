package com.xebisco.yieldengine.gameeditor;

import com.xebisco.yieldengine.core.Component;
import com.xebisco.yieldengine.core.Entity;
import com.xebisco.yieldengine.core.EntityFactory;
import com.xebisco.yieldengine.core.Scene;
import com.xebisco.yieldengine.core.camera.OrthoCamera;
import com.xebisco.yieldengine.core.components.Rectangle;
import com.xebisco.yieldengine.gameeditor.editorfactories.MousePosition;
import com.xebisco.yieldengine.gameeditor.editorfactories.components.EntitySelectorComp;
import com.xebisco.yieldengine.shipruntime.PreMadeEntityFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class EntityListEditor extends JPanel {
    private Scene scene, sceneEditor;
    private final JToolBar toolBar;
    private final JLabel titleLabel = new JLabel();
    private final JPanel gamePanel;

    public EntityListEditor(JPanel gamePanel) {
        setLayout(new BorderLayout());
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (sceneEditor == null) return;
                updateRes();
            }
        });
        this.gamePanel = gamePanel;
        add(gamePanel);
        gamePanel.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                updateValues();
            }
        });

        toolBar = new JToolBar("ENTITY LIST TOOLBAR");
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        toolBar.add(titleLabel);
        toolBar.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        toolBar.add(Box.createHorizontalStrut(5));
        /*toolBar.add(new JButton(new AbstractAction("Grid", IconCache.get().imageIcon("/icon/grid.png", getClass())) {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIUtils.showOptions(SwingUtilities.getWindowAncestor(EntityListEditor.this), true, Objects.requireNonNull(getComponentInEntities(sceneEditor.getEntities(), GridPainter.class)));
            }
        }));*/
        //add(toolBar, BorderLayout.NORTH);
    }

    public void showNewEntityPopup(List<EntityFactory> factories) {
        JPopupMenu popupMenu = new JPopupMenu("New Entity");
        float mx = MousePosition.X;
        float my = MousePosition.Y;

        JMenu newMenu = new JMenu("New");
        newMenu.setMnemonic(KeyEvent.VK_N);
        newMenu.add(new AbstractAction("Empty Entity") {
            @Override
            public void actionPerformed(ActionEvent e) {
                PreMadeEntityFactory factory = new PreMadeEntityFactory();
                factory.getTransform().translate(mx, my);
                factories.add(factory);
            }
        });
        newMenu.add(new AbstractAction("Rectangle") {
            @Override
            public void actionPerformed(ActionEvent e) {
                PreMadeEntityFactory factory = new PreMadeEntityFactory();
                factory.getTransform().translate(mx, my);
                factory.getComponents().add(new Rectangle());
                factories.add(factory);
            }
        });

        popupMenu.add(newMenu);

        Point mouse = getMousePosition();
        popupMenu.show(this, mouse.x, mouse.y);
    }

    private void updateValues() {
        if(scene == null) return;
        titleLabel.setText(scene.getName());
        sceneEditor.setBackgroundColor(scene.getBackgroundColor());
    }

    private <T extends Component> T getComponentInEntities(Collection<Entity> entities, Class<T> componentClass) {
        for (Entity e : entities) {
            Component c;
            if ((c = e.getComponent(componentClass)) != null) {
                //noinspection unchecked
                return (T) c;
            }
        }
        return null;
    }

    public void updateRes() {
        ((OrthoCamera) sceneEditor.getCamera()).getViewport().set(gamePanel.getSize().getWidth(), gamePanel.getSize().getHeight() + 10);
        setMinimumSize(new Dimension(0, 0));
    }

    public Object getScene() {
        return scene;
    }

    public EntityListEditor setScene(Scene scene) {
        this.scene = scene;
        return this;
    }

    public Object getSceneEditor() {
        return sceneEditor;
    }

    public EntityListEditor setSceneEditor(Scene sceneEditor) {
        this.sceneEditor = sceneEditor;
        Objects.requireNonNull(getComponentInEntities(sceneEditor.getEntities(), EntitySelectorComp.class)).setEntityListEditor(this);
        updateRes();
        return this;
    }
}
