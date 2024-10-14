package com.xebisco.yieldengine.projecteditor;

import com.formdev.flatlaf.icons.FlatMenuArrowIcon;
import com.xebisco.yieldengine.jarmng.JarMng;
import com.xebisco.yieldengine.project.Project;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ProjectCellRenderer implements ListCellRenderer<Project> {
    @Override
    public Component getListCellRendererComponent(JList<? extends Project> list, Project value, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(isSelected ? list.getSelectionBackground() : UIManager.getColor("Button.background"));
                g.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 10, 10, 10);
                boolean compatible = value.isCompatible();
                g.setColor(compatible ? isSelected ? list.getSelectionForeground() : list.getForeground() : Color.RED);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setFont(g.getFont().deriveFont(14f));
                String valueName = value.getName();
                g.drawString(valueName + (compatible ? "" : " (Incompatible)"), 12, (int) (8 + g.getFontMetrics().getStringBounds(valueName, g).getHeight()));
                g.setColor((isSelected ? list.getSelectionForeground() : list.getForeground()).darker());
                g.setFont(g.getFont().deriveFont(10f));
                File directory = value.getDirectory();
                g.drawString(directory.getAbsolutePath(), 12, getHeight() - 24);
                //g.drawString(DateFormat.getDateInstance().format(value.getLastModified()), 12, getHeight() - 24);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                (new FlatMenuArrowIcon()).paintIcon(this, g, getWidth() - 10 - 16, (getHeight() - 10) / 2 - 12 / 2);
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(70, 70));
        return panel;
    }
}
