package com.xebisco.yieldengine.gameeditor;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatClearIcon;
import com.xebisco.yieldengine.gameeditor.settings.Appearance;
import com.xebisco.yieldengine.jarmng.JarMng;
import com.xebisco.yieldengine.uiutils.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GameEditor extends JFrame {
    private final Object project;
    public static final JarMng PROJECT_JAR = new JarMng(new File(".", "/out/artifacts/project_jar/project.jar"));

    public static final String VERSION = "dev0";
    public static final long BUILD = 0;
    private final File projectFile;
    private static Map<Class<?>, Serializable> settingsArray = new HashMap<>();
    private static File editorSettingsFile = new File(System.getProperty("user.home"), ".yield_config");

    static class Settings {
        static Appearance appearance;
    }

    static {
        boolean createSettings = false;
        if(editorSettingsFile.exists()) {
            try(ObjectInputStream oi = new ObjectInputStream(Files.newInputStream(editorSettingsFile.toPath()))) {
                //noinspection unchecked
                settingsArray = (Map<Class<?>, Serializable>) oi.readObject();
            } catch (IOException | ClassNotFoundException e) {
                Utils.showError(e, null);
                createSettings = true;
            }
        } else {
            createSettings = true;
        }

        if(createSettings) {
            settingsArray = new HashMap<>();
            for(Field f : Settings.class.getDeclaredFields()) {
                f.setAccessible(true);
                try {
                    f.set(null, f.getType().getConstructor().newInstance());
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                try {
                    settingsArray.put(f.getType(), (Serializable) f.get(null));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            for(Field f : Settings.class.getDeclaredFields()) {
                f.setAccessible(true);
                try {
                    f.set(null, settingsArray.get(f.getType()));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try(ObjectOutputStream oo = new ObjectOutputStream(Files.newOutputStream(editorSettingsFile.toPath()))) {
                oo.writeObject(settingsArray);
            } catch (IOException e) {
                Utils.showError(e, null);
                throw new RuntimeException(e);
            }
        }, "Save Editor Settings"));
    }

    public GameEditor(Object project, File projectFile) {
        this.project = project;
        this.projectFile = projectFile;

        try {
            Utils.setIcon(ImageIO.read(Objects.requireNonNull(GameEditor.class.getResource("/icon/logo1.png"))), this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setJMenuBar(menuBar());

        setMinimumSize(new Dimension(1280, 720));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                updateDialog();
            }
        });
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (Utils.confirm(GameEditor.this, "Confirm exit?")) dispose();
            }
        });
        updateDialog();
    }

    public void updateDialog() {
        setTitle(projectGetName() + " | " + "Yield Editor");
    }

    public String projectGetName() {
        return PROJECT_JAR.invokeMethod(project, "getName", String.class);
    }

    public void saveProject() {
        //SAVE PROJECT FILE
        try(ObjectOutputStream oo = new ObjectOutputStream(Files.newOutputStream(projectFile.toPath()))) {
            oo.writeObject(project);
        } catch (IOException e) {
            Utils.showError(e, this);
            throw new RuntimeException(e);
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

        JMenu projectMenu = new JMenu("Project");
        projectMenu.setMnemonic('P');

        JMenuItem projectPropertiesItem = new JMenuItem("Properties...");
        projectPropertiesItem.setMnemonic('P');
        projectPropertiesItem.addActionListener(_ -> {
            Utils.showOptions(GameEditor.this, true, project);
        });
        projectMenu.add(projectPropertiesItem);

        JMenuItem saveProjectItem = new JMenuItem("Save");
        saveProjectItem.setMnemonic('S');
        saveProjectItem.setAccelerator(KeyStroke.getKeyStroke("control S"));
        saveProjectItem.addActionListener(_ -> saveProject());
        projectMenu.add(saveProjectItem);

        fileMenu.add(projectMenu);

        JMenuItem settingsMenuItem = new JMenuItem("Settings...", new FlatSVGIcon(GameEditor.class.getResource("/icon/settings.svg")));
        settingsMenuItem.setMnemonic('T');
        settingsMenuItem.addActionListener(_ -> {
            Utils.showSettings("Editor Settings", GameEditor.this, true, settingsArray.values().toArray(new Serializable[0]));
        });
        fileMenu.add(settingsMenuItem);

        fileMenu.addSeparator();

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic('X');
        exitMenuItem.addActionListener(_ -> dispatchEvent(new WindowEvent(GameEditor.this, WindowEvent.WINDOW_CLOSING)));
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        //FILE

        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');
        menuBar.add(editMenu);

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');
        menuBar.add(viewMenu);

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
        aboutMenuItem.addActionListener(_ -> {
            JDialog dialog = new JDialog(GameEditor.this, "About", true);
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setMinimumSize(new Dimension(400, 350));

            JLabel title = new JLabel("<html><h1>Yield Editor " + VERSION + "</h1><p>Build: " + BUILD + "</p></html>");
            try {
                title.setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(GameEditor.class.getResource("/icon/logo1.png"))).getScaledInstance(64, 64, Image.SCALE_SMOOTH)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            title.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            title.setIconTextGap(10);
            dialog.add(title, BorderLayout.NORTH);

            JLabel label = new JLabel("""
                    <html>
                        <p>DEVELOPMENT VERSION</p><p> </p>
                        <p> VM:\s""" + System.getProperty("java.vm.name") + """
                        </p>
                        <p>VM version:\s""" + System.getProperty("java.vm.version") + """
                        </p>
                        <p> </p>
                        <p>
                            Xebisco @2022-2024
                        </p>
                    </html>
                    """);
            label.setVerticalAlignment(SwingConstants.TOP);
            label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            dialog.add(label);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton closeButton = new JButton("Close");
            closeButton.setMnemonic('C');
            closeButton.addActionListener(_ -> dialog.dispose());
            dialog.getRootPane().setDefaultButton(closeButton);
            buttonPanel.add(closeButton);

            dialog.add(buttonPanel, BorderLayout.SOUTH);

            dialog.setLocationRelativeTo(GameEditor.this);
            dialog.setVisible(true);
        });

        helpMenu.add(aboutMenuItem);

        return menuBar;
    }
}
