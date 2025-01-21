package com.xebisco.yieldengine.gameeditor;

import com.xebisco.yieldengine.core.Component;
import com.xebisco.yieldengine.shipruntime.PreMadeEntityFactory;
import com.xebisco.yieldengine.uilib.UIUtils;
import com.xebisco.yieldengine.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Inspector {
    public final static JPanel INSPECTOR_PANEL = new JPanel(new BorderLayout());
    public static Timer SAVE_TIMER;
    private final static JScrollPane SCROLL_PANE = new JScrollPane();

    static {
        INSPECTOR_PANEL.add(SCROLL_PANE, BorderLayout.CENTER);
    }

    public static void set(PreMadeEntityFactory entity) {

        if(SAVE_TIMER != null) SAVE_TIMER.stop();

        java.util.List<Runnable> applyList = new ArrayList<>();

        SCROLL_PANE.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        if(entity != null) {
            SAVE_TIMER = new Timer(200, _ -> applyList.forEach(Runnable::run));
            SAVE_TIMER.start();
            for(Component component : entity.getComponents()) {
                Pair<Runnable, JPanel> p = getComponentPanel(component);
                panel.add(p.second());
                applyList.add(p.first());
            }
        } else {
            panel.add(new JLabel("Empty"));
        }
        SCROLL_PANE.setViewportView(panel);

        SCROLL_PANE.setBorder(null);

        INSPECTOR_PANEL.repaint();
    }

    public static Pair<Runnable, JPanel> getComponentPanel(Component component) {
        return UIUtils.getObjectsFieldsPanel(new Object[]{component});
    }
}
