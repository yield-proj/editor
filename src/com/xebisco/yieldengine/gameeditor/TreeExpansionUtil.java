package com.xebisco.yieldengine.gameeditor;

import javax.swing.*;

public class TreeExpansionUtil {

    public static String getExpansionState(JTree tree) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < tree.getRowCount(); i++) {
            if (tree.isExpanded(i)) {
                sb.append(i).append(",");
            }
        }
        return sb.toString();
    }


    public static void setExpansionState(JTree tree, String s) {
        String[] indexes = s.split(",");

        for (String st : indexes) {
            int row = Integer.parseInt(st);
            tree.expandRow(row);
        }
    }
}
