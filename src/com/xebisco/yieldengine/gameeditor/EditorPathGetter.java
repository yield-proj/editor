package com.xebisco.yieldengine.gameeditor;

import com.xebisco.yieldengine.core.io.IAbsolutePathGetter;

import java.io.File;

public class EditorPathGetter implements IAbsolutePathGetter {
    @Override
    public String getAbsolutePath(String s) {
        File editorFile = new File(s);
        if(editorFile.exists()) {
            return editorFile.getAbsolutePath();
        }
        return Main.getAsset(s).getAbsolutePath();
    }
}
