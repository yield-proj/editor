package com.xebisco.yieldengine.projecteditor;


import com.xebisco.yieldengine.uiutils.fields.FileExtensions;
import com.xebisco.yieldengine.uiutils.fields.Visible;

import java.io.File;
import java.io.Serializable;

public class Configurations implements Serializable {
    @Visible
    @FileExtensions(extensions = {}, description = "Directories")
    public File workspaceDirectory = new File(System.getProperty("user.home"), "YieldWorkspace");

    public transient Workspace workspace;
}
