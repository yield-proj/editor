package com.xebisco.yieldengine.gameeditor;

import com.xebisco.yieldengine.core.Component;
import com.xebisco.yieldengine.core.Entity;
import com.xebisco.yieldengine.core.EntityFactory;
import com.xebisco.yieldengine.core.Scene;
import com.xebisco.yieldengine.core.camera.OrthoCamera;
import com.xebisco.yieldengine.core.components.*;
import com.xebisco.yieldengine.core.components.Rectangle;
import com.xebisco.yieldengine.gameeditor.editorfactories.MousePosition;
import com.xebisco.yieldengine.gameeditor.editorfactories.components.EntitiesPaintComp;
import com.xebisco.yieldengine.gameeditor.editorfactories.components.EntitySelectorComp;
import com.xebisco.yieldengine.glimpl.window.OGLPanel;
import com.xebisco.yieldengine.shipruntime.PreMadeEntityFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class EntityListEditor extends JPanel {
    private Scene scene, sceneEditor;
    private final JToolBar toolBar;
    private final JLabel titleLabel = new JLabel();
    private final OGLPanel gamePanel;
    private final JPanel entityListPanel = new JPanel(new BorderLayout());
    private static EntityTree entityTree;
    //private PreMadeEntityFactory copyFac;
    //private boolean cutFac;

    public EntityListEditor(OGLPanel gamePanel) {
        setLayout(new BorderLayout());
        gamePanel.getContentPane().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (sceneEditor == null) return;
                updateRes();
                revalidate();
            }
        });
        this.gamePanel = gamePanel;
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setRightComponent(gamePanel.getContentPane());
        entityListPanel.setMinimumSize(new Dimension(300, 300));

        entityListPanel.add(entityTree = new EntityTree());


        splitPane.setLeftComponent(new JScrollPane(entityListPanel));
        splitPane.setResizeWeight(0);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);
        gamePanel.getContentPane().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                updateValues();
            }
        });
        gamePanel.getCanvas().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                gamePanel.getContentPane().requestFocus();
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

    public void showNewEntityPopup(List<EntityFactory> factories, PreMadeEntityFactory parent) {
        JPopupMenu popupMenu = new JPopupMenu("Entity Popup");
        float mx = MousePosition.X;
        float my = MousePosition.Y;

        if (parent != null) {
            JLabel entityLabel = new JLabel(parent.getHeader().getName());
            entityLabel.setFont(entityLabel.getFont().deriveFont(Font.BOLD));
            entityLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            popupMenu.add(entityLabel);
            popupMenu.addSeparator();
        }

        JMenu newMenu = new JMenu("New");
        newMenu.setMnemonic(KeyEvent.VK_N);
        newMenu.add(new AbstractAction("Empty Entity") {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEntity(factories, mx, my, parent);
            }
        });
        newMenu.addSeparator();
        newMenu.add(new AbstractAction("Sprite") {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEntity(factories, mx, my, parent, new Sprite());
            }
        });
        newMenu.add(new AbstractAction("Rectangle") {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEntity(factories, mx, my, parent, new Rectangle());
            }
        });
        newMenu.add(new AbstractAction("Ellipse") {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEntity(factories, mx, my, parent, new Ellipse());
            }
        });
        newMenu.add(new AbstractAction("Text") {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEntity(factories, mx, my, parent, new Text());
            }
        });
        newMenu.add(new AbstractAction("Line") {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEntity(factories, mx, my, parent, new Line());
            }
        });

        popupMenu.add(newMenu);
        popupMenu.addSeparator();

        /*
        JMenuItem copyItem = new JMenuItem(new AbstractAction("Copy") {
            @Override
            public void actionPerformed(ActionEvent e) {
                assert parent != null;
                copyFac = parent.clone();
                cutFac = false;
            }
        });
        copyItem.setEnabled(parent != null);
        copyItem.setMnemonic(KeyEvent.VK_C);
        popupMenu.add(copyItem);

        JMenuItem cutItem = new JMenuItem(new AbstractAction("Cut") {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyFac = parent;
                cutFac = true;
            }
        });
        cutItem.setEnabled(parent != null);
        if (copyFac == parent && cutFac) {
            cutItem.setEnabled(false);
        }
        cutItem.setMnemonic(KeyEvent.VK_X);
        popupMenu.add(cutItem);

        JMenuItem pasteItem = new JMenuItem(new AbstractAction("Paste") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cutFac) {
                    if (copyFac.getParent() != null)
                        copyFac.getParent().getChildren().remove(copyFac);
                    else scene.getEntityFactories().remove(copyFac);
                }

                addEntity(factories, parent, copyFac);
                copyFac = null;
            }
        });
        pasteItem.setEnabled(copyFac != null);
        pasteItem.setMnemonic(KeyEvent.VK_P);
        popupMenu.add(pasteItem); */


        popupMenu.addSeparator();

        JMenuItem deleteMenu = new JMenuItem("Delete");
        deleteMenu.setEnabled(false);
        deleteMenu.setMnemonic(KeyEvent.VK_D);
        popupMenu.add(deleteMenu);

        if (parent != null) {
            deleteMenu.setEnabled(true);
            deleteMenu.addActionListener(_ -> {
                if (parent.getParent() != null) {
                    parent.getParent().getChildren().remove(parent);
                } else {
                    scene.getEntityFactories().remove(parent);
                }
                if (Inspector.getEntity() == parent) Inspector.setGlobal(null, true);
                resetTree();
            });
        }

        Point mouse = getMousePosition();
        popupMenu.show(this, mouse.x, mouse.y);
    }

    private void addEntity(List<EntityFactory> factories, float mx, float my, PreMadeEntityFactory parent, Component... components) {
        PreMadeEntityFactory factory = new PreMadeEntityFactory();
        factory.setParent(parent);
        factory.getTransform().translate(mx, my);
        factory.setComponents(components);
        factory.setPreferredIndex(factories.size());
        factories.add(factory);
        resetTree();
    }

    private void addEntity(List<EntityFactory> factories, PreMadeEntityFactory parent, PreMadeEntityFactory factory) {
        factory.setParent(parent);
        factory.setPreferredIndex(factories.size());
        factories.add(factory);
        resetTree();
    }

    private void updateValues() {
        if (scene == null) return;
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

    public static void resetTree() {
        entityTree.reset();
    }

    private static int retEntity(DefaultMutableTreeNode node, PreMadeEntityFactory factory, int row) {
        int ret = -1;
        if (node.getUserObject() instanceof PreMadeEntityFactory a) {
            if (a == factory) {
                return row;
            }
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            TreeNode child = node.getChildAt(i);
            ret = retEntity((DefaultMutableTreeNode) child, factory, i + row);
            if (ret != -1) break;
        }
        return ret;
    }

    public static void setSelected(PreMadeEntityFactory entity) {
        int row = -1;

        for (int i = 1; i < entityTree.getRowCount(); i++) {
            row = retEntity(((DefaultMutableTreeNode) entityTree.getPathForRow(i).getLastPathComponent()), entity, i);
            if (row != -1) break;
        }

        if (row != -1)
            entityTree.setSelectionRow(row);
    }

    public void updateRes() {
        ((OrthoCamera) sceneEditor.getCamera()).getViewport().set(gamePanel.getCanvas().getSize().getWidth(), gamePanel.getCanvas().getHeight() + 10);
        gamePanel.getContentPane().setMinimumSize(new Dimension(200, 200));
    }

    class EntityTree extends JTree {
        private boolean created;

        public Scene getScene() {
            return scene;
        }

        public EntityTree() {
            reset();
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    reset();
                }
            });
            addTreeSelectionListener(_ -> {
                if (getSelectionRows() != null && getSelectionRows().length == 1) {
                    Object fac = ((DefaultMutableTreeNode) Objects.requireNonNull(getSelectionPath()).getLastPathComponent()).getUserObject();
                    if (fac instanceof PreMadeEntityFactory fac1) {
                        Inspector.setGlobal(fac1, false);
                    }
                }
            });
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        TreePath path = getPathForLocation(e.getX(), e.getY());
                        if (path != null) {
                            Object fac = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                            if (fac instanceof PreMadeEntityFactory fac1) {
                                showNewEntityPopup(fac1.getChildren(), fac1);
                            } else {
                                showNewEntityPopup(scene.getEntityFactories(), null);
                            }
                        }
                    }
                }
            });
        }

        public void reset() {
            if (sceneEditor == null) {
                DefaultMutableTreeNode root = new DefaultMutableTreeNode();
                root.setUserObject("No Scene Loaded");
                DefaultTreeModel model = new DefaultTreeModel(root);
                setModel(model);
                return;
            }
            String expState = null;
            if (created) {
                expState = TreeExpansionUtil.getExpansionState(this);
            }
            DefaultMutableTreeNode root = new DefaultMutableTreeNode();
            root.setUserObject("Entity Tree");
            DefaultTreeModel model = new DefaultTreeModel(root);
            Main.processFactories(scene.getEntityFactories());
            addA(root, scene.getEntityFactories());

            setModel(model);

            if (expState != null && !expState.isEmpty()) {
                TreeExpansionUtil.setExpansionState(this, expState);
            }
            entityTree.setTransferHandler(new TreeTransferHandler());
            entityTree.setDragEnabled(true);
            entityTree.setDropMode(DropMode.ON);

            Main.setMappings(entityTree);

            created = true;
        }

        public void addA(DefaultMutableTreeNode node, List<EntityFactory> factories) {
            for (EntityFactory f : factories) {
                if (f instanceof PreMadeEntityFactory pf) {
                    Main.processFactories(pf.getChildren());
                    if (pf.getChildren().isEmpty()) {
                        node.add(new DefaultMutableTreeNode(f));
                    } else {
                        DefaultMutableTreeNode fn = new DefaultMutableTreeNode(f);
                        node.add(fn);
                        addA(fn, pf.getChildren());
                    }
                }
            }
        }
    }

    public Scene getScene() {
        return scene;
    }

    public EntityListEditor setScene(Scene scene) {
        this.scene = scene;
        return this;
    }

    public Scene getSceneEditor() {
        return sceneEditor;
    }

    public EntityListEditor setSceneEditor(Scene sceneEditor) {
        this.sceneEditor = sceneEditor;
        Objects.requireNonNull(getComponentInEntities(sceneEditor.getEntities(), EntitySelectorComp.class)).setEntityListEditor(this);
        Objects.requireNonNull(getComponentInEntities(sceneEditor.getEntities(), EntitiesPaintComp.class)).setEntityListEditor(this);
        updateRes();
        entityTree.reset();
        return this;
    }

    public JToolBar getToolBar() {
        return toolBar;
    }

    public JLabel getTitleLabel() {
        return titleLabel;
    }

    public OGLPanel getGamePanel() {
        return gamePanel;
    }

    public JPanel getEntityListPanel() {
        return entityListPanel;
    }

    public static EntityTree getEntityTree() {
        return entityTree;
    }

    public static void setEntityTree(EntityTree entityTree) {
        EntityListEditor.entityTree = entityTree;
    }
}
