package com.xebisco.yieldengine.uiutils;

import com.formdev.flatlaf.icons.FlatClearIcon;
import com.xebisco.yieldengine.uiutils.fields.FieldPanel;
import com.xebisco.yieldengine.uiutils.fields.FieldsPanel;
import com.xebisco.yieldengine.uiutils.fields.FileExtensions;
import sun.reflect.annotation.AnnotationParser;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.Point;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowFocusListener;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static final FileExtensions IMAGE_FILE_EXTENSIONS = getFileExtensionsInstance(new String[]{"PNG", "JPG", "JPEG", "BMP", "WBMP", "GIF"}, "Image Files");
    public static final FileExtensions DIRECTORY = getFileExtensionsInstance(new String[]{}, "Directories");

    public static <T extends Annotation> T getAnnotationInstance(Class<T> annotationClass, Map<String, Object> values) {
        //noinspection unchecked
        return (T) AnnotationParser.annotationForMap(annotationClass, values);
    }

    public static FileExtensions getFileExtensionsInstance(String[] extensions, String description) {
        Map<String, Object> values = new HashMap<>();
        values.put("extensions", extensions);
        values.put("description", description);
        return getAnnotationInstance(FileExtensions.class, values);
    }

    /*public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
    }*/

    public static void showError(String error, Window owner) {
        JOptionPane.showMessageDialog(owner, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static JButton bigButton(AbstractAction button) {
        JButton b = new JButton(button);
        //b.setBorderPainted(false);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return b;
    }

    public static HashMap<String, Serializable> showOptions(String title, Window owner, boolean showApplyButton, FieldPanel<?>... fieldPanels) {
        FieldsPanel fieldsPanel = new FieldsPanel(fieldPanels);
        for (WindowFocusListener windowFocusListener : owner.getWindowFocusListeners()) {
            windowFocusListener.windowLostFocus(null);
        }

        FieldsDialog dialog = new FieldsDialog(owner, title, fieldsPanel);
        dialog.setTitle("");
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //dialog.setUndecorated(true);

        //noinspection unchecked
        final HashMap<String, Serializable>[] values = new HashMap[]{fieldsPanel.getMap()};

        if (!showApplyButton) values[0] = null;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton(new AbstractAction("OK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                values[0] = fieldsPanel.getMap();
                dialog.dispose();
            }
        });

        buttonPanel.add(okButton);
        dialog.getRootPane().setDefaultButton(okButton);

        if (showApplyButton) {
            JButton applyButton = new JButton(new AbstractAction("Apply") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    values[0] = fieldsPanel.getMap();
                }
            });
            buttonPanel.add(applyButton);
        }

        JButton cancelButton = new JButton(new AbstractAction("Cancel") {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(fieldsPanel);
        //dialog.setShape(new RoundRectangle2D.Float(0, 0, dialog.getWidth(), dialog.getHeight(), 20, 20));
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.getRootPane().putClientProperty("JRootPane.titleBarBackground", dialog.getBackground().darker());

        dialog.setVisible(true);

        return values[0];
    }

    private static java.util.List<JDialog> messages = new ArrayList<>();

    public static void showMessage(String message, Runnable open, Date date) {
        boolean[] ignore = new boolean[]{false};

        JDialog dialog = new JDialog();

        final Point[] startClick = {new Point()};

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setAlwaysOnTop(true);
        dialog.setUndecorated(true);
        JLabel label = new JLabel("<html>" + message + "</html>");
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        label.setFocusable(true);

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    ignore[0] = true;
                    dialog.dispose();
                    messages.remove(dialog);
                    open.run();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                ignore[0] = true;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                startClick[0] = e.getPoint();
            }
        });

        label.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int deltaX = e.getX() - startClick[0].x;
                int deltaY = e.getY() - startClick[0].y;

                dialog.setLocation(dialog.getLocation().x + deltaX, dialog.getLocation().y + deltaY);
            }
        });

        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.TOP);
        dialog.add(label);
        dialog.setSize(new Dimension(250, 100));
        dialog.setShape(new RoundRectangle2D.Double(0, 0, dialog.getWidth(), dialog.getHeight(), 20, 20));

        JPanel upperPanel = new JPanel(new BorderLayout());
        JButton closeButton = new JButton(new AbstractAction("", new FlatClearIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                ignore[0] = true;
                dialog.dispose();
                messages.remove(dialog);
            }
        });
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);

        upperPanel.add(closeButton, BorderLayout.EAST);

        JLabel title = new JLabel("   Message");
        title.setEnabled(false);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        upperPanel.add(title, BorderLayout.WEST);

        dialog.add(upperPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JLabel dateLabel = new JLabel(DateFormat.getDateInstance().format(date));
        dateLabel.setFont(dateLabel.getFont().deriveFont(8f));
        dateLabel.setEnabled(false);
        bottomPanel.add(dateLabel, BorderLayout.WEST);

        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 3, 5));

        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width - 270, Toolkit.getDefaultToolkit().getScreenSize().height - 160);
        boolean pass = false;
        int t = 0;

        while (!pass && t < 100) {
            t++;
            for(JDialog d : messages) {
                if(d.getX() == dialog.getX() && d.getY() == dialog.getY()) {
                    dialog.setLocation(d.getX(), dialog.getY() - 120);
                }
            }
        }

        messages.add(dialog);

        dialog.setOpacity(0);
        dialog.setVisible(true);

        Timer timer1 = new Timer(50, _ -> {
        });
        timer1.addActionListener(_ -> {
            if (dialog.isShowing()) {
                dialog.setOpacity(Math.min(1, dialog.getOpacity() + 0.1f));
                if (dialog.getOpacity() >= 1f) timer1.stop();
            }
        });
        timer1.start();

        Timer timer = new Timer(10000, _ -> {
        });
        timer.addActionListener(_ -> {
            if (!ignore[0]) {
                dialog.dispose();
                messages.remove(dialog);
            }
            timer.stop();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public static void showMessage(String message) {
        showMessage(message, () -> {
        });
    }

    public static void showMessage(String message, Runnable open) {
        showMessage(message, open, new Date());
    }

    public static HashMap<String, Serializable> showOptions(Window owner, FieldPanel<?>... fieldPanels) {
        return showOptions(null, owner, true, fieldPanels);
    }

    public static JButton menuItemButton(String name, Integer mnemonic, JComponent... menuItem) {
        JButton button = new JButton();
        if (mnemonic != null) button.setMnemonic(mnemonic);

        JPopupMenu popupMenu = new JPopupMenu();
        for (JComponent item : menuItem) {
            popupMenu.add(item);
        }

        button.setAction(new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                popupMenu.show(button, button.getMousePosition().x, button.getMousePosition().y);
            }
        });

        return button;
    }

    public static File getFile(JRootPane rootPane, File currentDir, FileExtensions fileExtensions) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (fileExtensions != null) {
            if (fileExtensions.extensions().length == 0) chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }
                    for (String ext : fileExtensions.extensions()) {
                        if (f.getName().toUpperCase().endsWith(ext.toUpperCase())) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public String getDescription() {
                    return fileExtensions.description();
                }
            });
        }

        chooser.setCurrentDirectory(currentDir);

        if (chooser.showOpenDialog(rootPane) == JFileChooser.APPROVE_OPTION)
            return chooser.getSelectedFile();
        return null;
    }
}
