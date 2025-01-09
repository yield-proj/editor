package com.xebisco.yieldengine.gameeditor;

import com.xebisco.yieldengine.uilib.ProjectEditor;
import com.xebisco.yieldengine.uilib.UIUtils;
import com.xebisco.yieldengine.uilib.projectmng.ProjectMng;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        UIUtils.setupLaf();

        SwingUtilities.invokeAndWait(() -> {
            ProjectMng mng = new ProjectMng(ProjectEditor.class);
            mng.setLocationRelativeTo(null);
            mng.setVisible(true);
        });
    }
}
