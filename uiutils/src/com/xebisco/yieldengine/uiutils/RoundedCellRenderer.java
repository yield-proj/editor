package com.xebisco.yieldengine.uiutils;

import javax.swing.*;
import java.awt.*;

public class RoundedCellRenderer extends JLabel implements ListCellRenderer<Object> {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.toString());
        setOpaque(false);
        setHorizontalAlignment(JLabel.CENTER);
        setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
        setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());

        setPreferredSize(new Dimension(150, 35));
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        super.paintComponent(g);
    }
}
