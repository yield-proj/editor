package com.xebisco.yieldengine.projecteditor;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.xebisco.yieldengine.uiutils.Utils;

import javax.swing.*;
import java.awt.*;

public class PEEntry {
    public static void main(String[] args) {
        FlatMacDarkLaf.setup();

        JFrame.setDefaultLookAndFeelDecorated(true);

        UIManager.put("SearchField.searchIconColor", Color.WHITE);
        UIManager.put("Actions.Grey", Color.WHITE);
        UIManager.put("Objects.Grey", Color.WHITE);

        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("ProgressBar.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("TextPane.arc", 10);

        UIManager.put("Button.background", new Color(48, 48, 49));


        UIManager.getLookAndFeelDefaults().put("defaultFont", new Font("Monospace", Font.BOLD, 12));

        ProjectEditor pe = new ProjectEditor();
        pe.setLocationRelativeTo(null);
        pe.setVisible(true);
    }
}
