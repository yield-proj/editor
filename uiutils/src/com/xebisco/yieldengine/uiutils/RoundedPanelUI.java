package com.xebisco.yieldengine.uiutils;

import javax.swing.*;
import javax.swing.plaf.PanelUI;
import java.awt.*;

public class RoundedPanelUI extends PanelUI {
    public void update(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            g.setColor(c.getBackground());
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.fillRoundRect(0, 0, c.getWidth(),c.getHeight(), 10, 10);
        }
        paint(g, c);
    }
}
