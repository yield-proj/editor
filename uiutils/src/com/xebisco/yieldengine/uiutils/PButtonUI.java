package com.xebisco.yieldengine.uiutils;

import javax.swing.*;
import javax.swing.plaf.ButtonUI;
import java.awt.*;

public class PButtonUI extends ButtonUI {
    @Override
    public void update(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        //ButtonModel model = b.getModel();
        Font font = b.getFont();
        FontMetrics fm = g.getFontMetrics(font);
        int w = fm.stringWidth(b.getText());
        int h = fm.getHeight();

        int x = c.getWidth() / 2 - w / 2;
        int y = c.getHeight() / 2 - h / 2;

        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(b.getBackground());
        g.fillRoundRect(2, 2, b.getWidth()  - 4, b.getHeight() - 4, 15, 15);
        //g.fillRect(x,y, w, h);

        Icon i = b.getIcon();
        if(i != null) {

        }

        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(b.getForeground());
        ((Graphics2D) g).drawString(b.getText(), x, y + h / 1.5f);
    }
}
