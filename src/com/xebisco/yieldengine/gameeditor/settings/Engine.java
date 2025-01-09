package com.xebisco.yieldengine.gameeditor.settings;



import com.xebisco.yieldengine.utils.FileExtensions;
import com.xebisco.yieldengine.utils.Visible;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;

public class Engine implements Serializable {
    @Serial
    private static final long serialVersionUID = -713943905926435287L;

    @Visible
    @FileExtensions(value = {}, acceptDirectories = true)
    public File engineHome = new File(System.getProperty("user.home"), "YieldEngine");

    @Visible
    public String engineVersion = "dev1";

    public void reloadEngineJars() {
        //TODO update classpath
    }
}
