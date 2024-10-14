package com.xebisco.yieldengine.projecteditor;


import com.xebisco.yieldengine.uiutils.fields.annotations.FileExtensions;
import com.xebisco.yieldengine.uiutils.fields.annotations.Visible;

import java.io.File;
import java.io.Serializable;

public class Configurations implements Serializable {
    @Visible
    @FileExtensions(extensions = {}, description = "Directories")
    public File workspaceDirectory = new File(System.getProperty("user.home"), "YieldWorkspace");

    @Visible
    public String yieldEditorVersion = "dev0";

    public transient Workspace workspace;
}
