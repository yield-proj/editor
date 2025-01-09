package com.xebisco.yieldengine.gameeditor;

import com.xebisco.yieldengine.uilib.projectmng.Project;

import java.io.File;

public class GameProject extends Project {
    private transient File projectPath;

    public File getProjectPath() {
        return projectPath;
    }

    public GameProject setProjectPath(File projectPath) {
        this.projectPath = projectPath;
        return this;
    }
}
