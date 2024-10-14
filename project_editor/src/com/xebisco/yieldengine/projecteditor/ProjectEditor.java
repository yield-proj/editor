package com.xebisco.yieldengine.projecteditor;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatClearIcon;
import com.formdev.flatlaf.icons.FlatFileChooserNewFolderIcon;
import com.formdev.flatlaf.icons.FlatFileViewFloppyDriveIcon;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import com.xebisco.yieldengine.jarmng.JarMng;
import com.xebisco.yieldengine.project.Project;
import com.xebisco.yieldengine.uiutils.BlurLayerUI;
import com.xebisco.yieldengine.uiutils.RoundedCellRenderer;
import com.xebisco.yieldengine.uiutils.Utils;
import com.xebisco.yieldengine.uiutils.fields.BooleanFieldPanel;
import com.xebisco.yieldengine.uiutils.fields.ComboFieldPanel;
import com.xebisco.yieldengine.uiutils.fields.StringFieldPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ProjectEditor extends JFrame {
    private final JPanel contentPane = new JPanel(new BorderLayout());

    public static ArrayList<File> workspaces;
    private final JScrollPane projectsScrollPane, workspacesScrollPane;

    public static Configurations config;
    public static final File INSTALLS_FOLDER = new File(System.getProperty("user.home"), ".yieldproj_installs");

    static {
        INSTALLS_FOLDER.mkdir();
        File workspaceListFile = new File(System.getProperty("user.home"), ".yieldproj_workspaceList");
        if (workspaceListFile.exists()) {
            try (ObjectInputStream oi = new ObjectInputStream(new FileInputStream(workspaceListFile))) {
                //noinspection unchecked
                workspaces = (ArrayList<File>) oi.readObject();
            } catch (IOException | ClassNotFoundException e) {
                Utils.showError(e.getMessage(), null);
            }
        } else {
            workspaces = new ArrayList<>();
        }

        File configFile = new File(System.getProperty("user.home"), ".yieldproj_config");

        if (configFile.exists()) {
            try (ObjectInputStream oi = new ObjectInputStream(new FileInputStream(configFile))) {
                config = (Configurations) oi.readObject();
            } catch (IOException | ClassNotFoundException e) {
                Utils.showError(e.getMessage(), null);
            }
        } else {
            config = new Configurations();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try (ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(workspaceListFile))) {
                oo.writeObject(workspaces);
            } catch (IOException e) {
                Utils.showError(e.getMessage(), null);
            }
            try (ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(configFile))) {
                oo.writeObject(config);
            } catch (IOException e) {
                Utils.showError(e.getMessage(), null);
            }
        }));
    }

    public static boolean LOADED = false;

    public ProjectEditor() {
        super("Project Editor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 40));

        //contentPane.add(mainTab());


        projectsScrollPane = new JScrollPane();
        projectsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        projectsScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));

        workspacesScrollPane = new JScrollPane();
        workspacesScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        reloadProjects();


        JList<String> list = new JList<>(new String[]{"Projects", "Workspace", "Installs", "Options"});
        list.setBackground(getBackground());
        list.addListSelectionListener(_ -> {
            try {
                updatePanel((JPanel) ProjectEditor.class.getDeclaredMethod("tab" + list.getSelectedValue().replace(" ", "_")).invoke(ProjectEditor.this));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        list.setCellRenderer(new RoundedCellRenderer());

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 5, 5));

        listPanel.add(list, BorderLayout.CENTER);


        JPanel mainContentPane = new JPanel(new BorderLayout());

        JPanel showPanel = new JPanel(new BorderLayout());
        if (!LOADED) {
            JPanel loadingPanel = new JPanel();
            loadingPanel.add(new JLabel("Loading..."));
            showPanel.add(loadingPanel);
            CompletableFuture.runAsync(() -> {
                //TODO check updates

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                LOADED = true;

                for (WindowFocusListener windowFocusListener : getWindowFocusListeners()) {
                    windowFocusListener.windowGainedFocus(null);
                }
            });
        } else {
            showPanel.add(mainContentPane);
        }

        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                if (LOADED) {
                    showPanel.removeAll();
                    showPanel.add(mainContentPane);
                    showPanel.updateUI();
                }
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                JLayer<Component> blurLayer = new JLayer<>(mainContentPane, new BlurLayerUI());
                showPanel.removeAll();
                showPanel.add(blurLayer);
                showPanel.updateUI();
            }
        });

        mainContentPane.add(contentPane, BorderLayout.CENTER);
        mainContentPane.add(listPanel, BorderLayout.WEST);

        setContentPane(showPanel);

        list.setSelectedIndex(0);

        try {
            Utils.setIcon(ImageIO.read(Objects.requireNonNull(ProjectEditor.class.getResource("/icon/logo1.png"))), this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setJMenuBar(new JMenuBar());

        setMinimumSize(new Dimension(600, 300));
        setSize(new Dimension(800, 600));
    }

    private void updatePanel(JPanel panel) {
        contentPane.removeAll();
        contentPane.add(panel);
        contentPane.revalidate();
        contentPane.repaint();
    }

    private JPanel searchMainTab() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JTextField searchField = new JTextField(30);
        searchField.setLayout(new BorderLayout());
        searchField.putClientProperty("JTextField.leadingIcon", new FlatSearchIcon());
        JButton closeButton = new JButton(new FlatClearIcon());
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(Cursor.getDefaultCursor());
        closeButton.setPreferredSize(new Dimension(16, 16));
        searchField.add(closeButton, BorderLayout.LINE_END);
        closeButton.addActionListener(_ -> updatePanel(tabProjects()));

        mainPanel.add(searchField, BorderLayout.NORTH);

        SwingUtilities.invokeLater(searchField::requestFocus);

        return mainPanel;
    }

    private JPanel tabInstalls() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel titlePanel = new JPanel(new BorderLayout());

        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        JLabel titleLabel = new JLabel("Installs");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD).deriveFont(16f));
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JButton searchButton = new JButton(new NullIcon());
        searchButton.setBorderPainted(false);
        searchButton.setContentAreaFilled(false);
        titlePanel.add(searchButton, BorderLayout.EAST);

        mainPanel.add(titlePanel, BorderLayout.NORTH);


        return mainPanel;
    }

    public JPanel tabWorkspace() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel titlePanel = new JPanel(new BorderLayout());

        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        JLabel titleLabel = new JLabel("Workspace");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD).deriveFont(16f));
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JButton searchButton = new JButton(new NullIcon());
        searchButton.setBorderPainted(false);
        searchButton.setContentAreaFilled(false);
        titlePanel.add(searchButton, BorderLayout.EAST);

        mainPanel.add(titlePanel, BorderLayout.NORTH);


        return mainPanel;
    }

    private void reloadProjects() {
        config.workspace = Workspace.create(config.workspaceDirectory);
        JList<Project> projectJList = new JList<>(config.workspace.getProjects().toArray(new Project[0]));
        projectJList.setCellRenderer(new ProjectCellRenderer());

        projectJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                projectJList.setSelectedIndex(e.getY() / 70);
                Project project = projectJList.getSelectedValue();
                File projectFile = new File(project.getDirectory(), "project.serp");
                if (e.getClickCount() == 2) {
                    openEditor(project, projectFile);
                    return;
                }
                if (e.getX() < projectJList.getWidth() - 70) return;
                JPopupMenu menu = new JPopupMenu(project.getName());
                menu.add(new AbstractAction("Open in Editor") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        openEditor(project, projectFile);
                    }
                });
                menu.add(new AbstractAction("Open in Explorer") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            Desktop.getDesktop().open(project.getDirectory());
                        } catch (IOException ex) {
                            Utils.showError(ex.getMessage(), ProjectEditor.this);
                        }
                    }
                });
                menu.add(new AbstractAction("Copy Absolute Path") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Utils.showMessage("Copied path");
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(project.getDirectory().getAbsolutePath()), null);
                    }
                });
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        projectJList.setBackground(getBackground());
        projectsScrollPane.setViewportView(projectJList);
        projectsScrollPane.repaint();
    }

    public void openEditor(Object project, File projectFile) {
        try {
            JarMng jarMng = new JarMng(new File(".", "/out/artifacts/YieldEditor_jar/YieldEditor.jar"));
            //Class<?> projectClass = jarMng.getClassForName("com.xebisco.yieldengine.project.Project");
            Class<?> gameEditorClass = jarMng.getClassForName("com.xebisco.yieldengine.gameeditor.GameEditor");
            Object gameEditorObject = gameEditorClass.getDeclaredConstructor(Object.class, File.class).newInstance(project, projectFile);
            Method setVisibleMethod = gameEditorClass.getMethod("setVisible", boolean.class);

            dispose();

            setVisibleMethod.invoke(gameEditorObject, true);
        } catch (Exception e) {
            Utils.showError(e, this);
        }

    }

    private JPanel tabProjects() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel titlePanel = new JPanel(new BorderLayout());

        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        JLabel titleLabel = new JLabel("Projects");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD).deriveFont(16f));
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JButton searchButton = new JButton(new FlatSearchIcon());
        searchButton.setBorderPainted(false);
        searchButton.setContentAreaFilled(false);
        titlePanel.add(searchButton, BorderLayout.EAST);

        searchButton.addActionListener(_ -> updatePanel(searchMainTab()));

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel inner = new JPanel(new BorderLayout());

        JPanel mainButtonsPanel = new JPanel();
        mainButtonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainButtonsPanel.setPreferredSize(new Dimension(80, 80));
        mainButtonsPanel.setLayout(new BoxLayout(mainButtonsPanel, BoxLayout.X_AXIS));

        mainButtonsPanel.add(Utils.bigButton(new AbstractAction("Open", new FlatFileViewFloppyDriveIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory() || f.getName().endsWith(".serp");
                    }

                    @Override
                    public String getDescription() {
                        return "Yield Project Files (.serp)";
                    }
                });
                if (chooser.showOpenDialog(ProjectEditor.this) == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try (ObjectInputStream oi = new ObjectInputStream(Files.newInputStream(file.toPath()))) {
                        openEditor(oi.readObject(), file);
                    } catch (IOException | ClassNotFoundException ex) {
                        Utils.showError(ex.getMessage(), ProjectEditor.this);
                    }
                }
            }
        }));
        mainButtonsPanel.add(Box.createHorizontalStrut(8));
        mainButtonsPanel.add(Utils.bigButton(new AbstractAction("Clone from Git", new FlatSVGIcon(ProjectEditor.class.getResource("/icon/github_dark.svg"))) {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        }));
        mainButtonsPanel.add(Box.createHorizontalStrut(8));
        mainButtonsPanel.add(Utils.bigButton(new AbstractAction("New", new FlatFileChooserNewFolderIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                NewProjectConfig projectConfig = new NewProjectConfig();
                if (Utils.showOptions(ProjectEditor.this, false, projectConfig) == null) return;

                if (projectConfig.name.isBlank()) {
                    Utils.showError("Empty Name!", ProjectEditor.this);
                    actionPerformed(null);
                } else {
                    Project project = new Project();

                    project.setName(projectConfig.name);

                    File projectDir = new File(config.workspaceDirectory, project.getName());
                    if (projectDir.mkdirs()) {
                        File projectFile = new File(projectDir, "project.serp");
                        try (ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(projectFile))) {
                            oo.writeObject(project);
                        } catch (IOException ex) {
                            Utils.showError(ex.getMessage(), ProjectEditor.this);
                        }
                        reloadProjects();
                    } else {
                        Utils.showError("Failed to create directory!", ProjectEditor.this);
                    }
                }

            }
        }));

        inner.add(mainButtonsPanel, BorderLayout.NORTH);

        inner.add(projectsScrollPane, BorderLayout.CENTER);

        mainPanel.add(inner, BorderLayout.CENTER);

        return mainPanel;
    }

}
