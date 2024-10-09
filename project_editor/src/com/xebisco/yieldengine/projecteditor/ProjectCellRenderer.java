package com.xebisco.yieldengine.projecteditor;

import com.formdev.flatlaf.icons.FlatMenuArrowIcon;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;

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
                g.setColor(value.isCompatible() ? isSelected ? list.getSelectionForeground() : list.getForeground() : Color.RED);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setFont(g.getFont().deriveFont(14f));
                g.drawString(value.getName() + (value.isCompatible() ? "" : " (Incompatible)"), 12, (int) (8 + g.getFontMetrics().getStringBounds(value.getName(), g).getHeight()));
                g.setColor((isSelected ? list.getSelectionForeground() : list.getForeground()).darker());
                g.setFont(g.getFont().deriveFont(10f));
                g.drawString(value.getDirectory().getAbsolutePath(), 12, getHeight() - 24);
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
