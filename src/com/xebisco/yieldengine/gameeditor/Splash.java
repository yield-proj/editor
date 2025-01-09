package com.xebisco.yieldengine.gameeditor;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Splash extends JDialog {

    public Splash() {
        setUndecorated(true);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        add(new JLabel(new ImageIcon(Objects.requireNonNull(Splash.class.getResource("/image/splash.png")))));
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        add(progressBar, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void close() {
        dispose();
    }
}
