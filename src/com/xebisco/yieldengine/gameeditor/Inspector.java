package com.xebisco.yieldengine.gameeditor;

import com.xebisco.yieldengine.shipruntime.PreMadeEntityFactory;
import com.xebisco.yieldengine.uilib.UIUtils;
import com.xebisco.yieldengine.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class Inspector {
    public final static JPanel INSPECTOR_PANEL = new JPanel(new BorderLayout());
    public static Timer SAVE_TIMER;
    private final static JScrollPane SCROLL_PANE = new JScrollPane();
    private static boolean movingEntity = false;
    private static PreMadeEntityFactory entity;

    static {
        INSPECTOR_PANEL.setMinimumSize(new Dimension(320, 320));
        INSPECTOR_PANEL.setBorder(BorderFactory.createEmptyBorder(5, 2, 5, 2));
        INSPECTOR_PANEL.add(SCROLL_PANE, BorderLayout.CENTER);
        INSPECTOR_PANEL.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (entity != null)
                    set(entity);
            }
        });
    }

    public static void set(PreMadeEntityFactory entity) {
        Inspector.entity = entity;
        if (SAVE_TIMER != null) SAVE_TIMER.stop();

        java.util.List<Runnable> applyList = new ArrayList<>();

        SCROLL_PANE.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        SCROLL_PANE.setMinimumSize(new Dimension(100, 100));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        if (entity != null) {
            SAVE_TIMER = new Timer(100, _ -> {
                if (!movingEntity)
                    applyList.forEach(Runnable::run);
            });
            SAVE_TIMER.start();

            Pair<Runnable, JPanel> p = UIUtils.getObjectsFieldsPanel(new Object[]{entity});

            panel.add(p.second());
            applyList.add(p.first());
        } else {
            panel.add(new JLabel("Empty"));
        }
        SCROLL_PANE.setViewportView(panel);

        SCROLL_PANE.setBorder(null);

        //INSPECTOR_PANEL.repaint();
    }

    public static boolean isMovingEntity() {
        return movingEntity;
    }

    public static void setMovingEntity(boolean movingEntity) {
        if (Inspector.movingEntity && !movingEntity) {
            set(entity);
        }
        Inspector.movingEntity = movingEntity;
    }
}
