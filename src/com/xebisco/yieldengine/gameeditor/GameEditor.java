package com.xebisco.yieldengine.gameeditor;

import com.formdev.flatlaf.extras.components.FlatTabbedPane;
import com.xebisco.yieldengine.core.EntityFactory;
import com.xebisco.yieldengine.core.Global;
import com.xebisco.yieldengine.core.LoopContext;
import com.xebisco.yieldengine.core.Scene;
import com.xebisco.yieldengine.core.camera.OrthoCamera;
import com.xebisco.yieldengine.gameeditor.editorfactories.CamControl;
import com.xebisco.yieldengine.gameeditor.editorfactories.EntitiesPaint;
import com.xebisco.yieldengine.gameeditor.editorfactories.Grid;
import com.xebisco.yieldengine.gameeditor.editorfactories.Selector;
import com.xebisco.yieldengine.glimpl.window.OGLPanel;
import com.xebisco.yieldengine.uilib.ProjectEditor;
import com.xebisco.yieldengine.uilib.SettingsWindow;
import com.xebisco.yieldengine.uilib.UIUtils;
import com.xebisco.yieldengine.uilib.comm.CommandsDialog;
import com.xebisco.yieldengine.uilib.projectmng.Project;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class GameEditor extends ProjectEditor<GameProject> {
    public static final String VERSION = "dev0";
    public static final long BUILD = 0;
    private EntityListEditor entityListEditor;
    private final JFrame frame;
    private final JSplitPane gameInspectorPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private final FlatTabbedPane rightTabbedPane = new FlatTabbedPane();

    public GameEditor(Project project) {
        super(project);
        frame = new JFrame();

        try {
            frame.setIconImage(ImageIO.read(Objects.requireNonNull(GameEditor.class.getResource("/icon/logo1.png"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        frame.setJMenuBar(menuBar());

        frame.setMinimumSize(new Dimension(1280, 720));
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                updateDialog();
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                setProjectFolder();
            }

            public void windowClosing(WindowEvent e) {
                if (UIUtils.confirm(frame, "Confirm exit?")) {
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    deviceObject.runCloseHooks();
                } else {
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (Inspector.SAVE_TIMER != null)
                    Inspector.SAVE_TIMER.stop();
            }
        });
        updateDialog();


        //MAIN PANEL

        JPanel contentPane = new JPanel(new BorderLayout());

        editorEngine(contentPane);

        gameInspectorPane.setLeftComponent(contentPane);

        rightTabbedPane.addTab("Inspector", Inspector.INSPECTOR_PANEL);

        gameInspectorPane.setRightComponent(rightTabbedPane);
        gameInspectorPane.setResizeWeight(1);

        frame.add(gameInspectorPane);

        setProjectFolder();

        //SETUP project
        Main.getAssetsFolder();

        try {
            Files.copy(Objects.requireNonNull(GameEditor.class.getResourceAsStream("/OpenSans-Regular.ttf")), Main.getAsset("OpenSans-Regular.ttf").toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(Objects.requireNonNull(GameEditor.class.getResourceAsStream("/yieldIcon.png")), Main.getAsset("yieldIcon.png").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        frame.setVisible(true);

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Inspector.setGlobal(null, true);
        });
    }

    private void setProjectFolder() {
        Main.setProjectFolder(getProject().getProjectDir());
    }

    private OGLPanel deviceObject;

    public void editorEngine(JPanel p) {
        try {
            deviceObject = (OGLPanel) Global.getOpenGLOpenALLCP0(-1, -1);

            deviceObject.setIgnoreCloseHooks(true);

            p.add(entityListEditor = new EntityListEditor((deviceObject)));

            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(200);
                    LoopContext loopContext = Global.getOpenGLOpenALLCP1(deviceObject, new EditorPathGetter());

                    setScene(new Scene(new ArrayList<>()));

                    loopContext.startThread();
                } catch (Throwable e) {
                    UIUtils.error(e, frame);
                    throw new RuntimeException(e);
                }
            }).exceptionally(e -> {
                throw new RuntimeException(e);
            });
        } catch (Throwable e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void setScene(Scene sceneObject) {
        Scene sceneEditor = createSceneEditor(sceneObject);
        Global.setCurrentScene(sceneEditor);
        sceneEditor.create();
        entityListEditor.setScene(sceneObject);
        entityListEditor.setSceneEditor(sceneEditor);
    }

    public Scene createSceneEditor(Scene sceneObject) {
        ArrayList<EntityFactory> entityFactories = new ArrayList<>();

        //entityFactories.add(Settings.engine.engineJars.getClassForName("com.xebisco.yieldengine.editorfactories.TestEF").getConstructor().newInstance());
        entityFactories.add(new CamControl());
        //entityFactories.add(Settings.engine.engineJars.getClassForName("com.xebisco.yieldengine.editorfactories.CF").getConstructor().newInstance());

        entityFactories.add(new EntitiesPaint());
        entityFactories.add(new Grid());
        entityFactories.add(new Selector());

        return new Scene(entityFactories);
    }

    public void updateDialog() {
        frame.setTitle(getProject().getName() + " | " + "Yield Editor");
    }

    public void saveProject() {
        //SAVE PROJECT FILE
        try (ObjectOutputStream oo = new ObjectOutputStream(Files.newOutputStream(getProject().getProjectPath().toPath()))) {
            oo.writeObject(getProject());
        } catch (IOException e) {
            UIUtils.error(e, frame);
        }
    }

    private JMenuBar menuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        //FILE

        JMenu newMenu = new JMenu("New");
        newMenu.setMnemonic('N');
        fileMenu.add(newMenu);

        JMenuItem saveProjectItem = new JMenuItem("Save");
        saveProjectItem.setMnemonic('S');
        saveProjectItem.setAccelerator(KeyStroke.getKeyStroke("control S"));
        saveProjectItem.addActionListener(_ -> saveProject());

        fileMenu.add(saveProjectItem);

        JMenuItem settingsMenuItem = new JMenuItem("Settings...");
        settingsMenuItem.setMnemonic('T');
        settingsMenuItem.addActionListener(_ -> {
            SettingsWindow.openSettings(frame);
        });
        fileMenu.add(settingsMenuItem);

        menuBar.add(fileMenu);

        JMenu projectMenu = new JMenu("Project");
        projectMenu.setMnemonic('P');

        JMenuItem projectPropertiesItem = new JMenuItem("Properties...");
        projectPropertiesItem.setMnemonic('P');
        projectPropertiesItem.addActionListener(_ -> {
            UIUtils.openDialog(getProject(), frame);
        });
        projectMenu.add(projectPropertiesItem);

        fileMenu.add(projectMenu);

        fileMenu.addSeparator();

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic('X');
        exitMenuItem.addActionListener(_ -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));
        fileMenu.add(exitMenuItem);

        //END FILE

        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');
        menuBar.add(editMenu);

        TransferActionListener actionListener = new TransferActionListener();

        JMenuItem menuItem;

        menuItem = new JMenuItem("Cut");
        menuItem.setActionCommand((String)TransferHandler.getCutAction().
                getValue(Action.NAME));
        menuItem.addActionListener(actionListener);
        menuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        menuItem.setMnemonic(KeyEvent.VK_T);
        editMenu.add(menuItem);

        menuItem = new JMenuItem("Copy");
        menuItem.setActionCommand((String)TransferHandler.getCopyAction().
                getValue(Action.NAME));
        menuItem.addActionListener(actionListener);
        menuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        menuItem.setMnemonic(KeyEvent.VK_C);
        editMenu.add(menuItem);

        menuItem = new JMenuItem("Paste");
        menuItem.setActionCommand((String)TransferHandler.getPasteAction().
                getValue(Action.NAME));
        menuItem.addActionListener(actionListener);
        menuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        menuItem.setMnemonic(KeyEvent.VK_P);
        editMenu.add(menuItem);

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');

        JMenuItem openCommandsDialogItem = new JMenuItem(new AbstractAction("Open Commands Dialog...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CommandsDialog();
            }
        });
        openCommandsDialogItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));

        viewMenu.add(openCommandsDialogItem);

        menuBar.add(viewMenu);

        JMenu navMenu = new JMenu("Navigate");
        navMenu.setMnemonic('N');
        menuBar.add(navMenu);

        menuItem = new JMenuItem("Camera to object");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        menuItem.addActionListener(_ -> ((OrthoCamera) entityListEditor.getSceneEditor().getCamera()).getTransform().position(Inspector.getEntity().getTransform().getTranslation()));
        navMenu.add(menuItem);

        JMenu sceneMenu = new JMenu("Scene");
        sceneMenu.setMnemonic('S');
        menuBar.add(sceneMenu);

        JMenuItem scenePropertiesItem = new JMenuItem("Properties...");
        scenePropertiesItem.setMnemonic('P');
        scenePropertiesItem.addActionListener(_ -> {
            UIUtils.openDialog(entityListEditor.getScene(), frame);
        });
        sceneMenu.add(scenePropertiesItem);

        //END SCENE

        JMenu buildMenu = new JMenu("Build");
        buildMenu.setMnemonic('B');
        menuBar.add(buildMenu);

        JMenu gitMenu = new JMenu("Git");
        gitMenu.setMnemonic('G');
        menuBar.add(gitMenu);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        menuBar.add(helpMenu);

        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.setMnemonic('A');
        aboutMenuItem.addActionListener(_ -> UIUtils.about(frame, "DEVELOPMENT VERSION", VERSION));

        helpMenu.add(aboutMenuItem);

        return menuBar;
    }
}
