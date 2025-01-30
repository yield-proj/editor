package com.xebisco.yieldengine.gameeditor.fields;

import com.xebisco.yieldengine.core.io.texture.Texture;
import com.xebisco.yieldengine.gameeditor.Main;
import com.xebisco.yieldengine.uilib.DirectoryRestrictedFileSystemView;
import com.xebisco.yieldengine.uilib.DocumentChangeListener;
import com.xebisco.yieldengine.uilib.fields.FileField;
import com.xebisco.yieldengine.utils.FileExtensions;

public class TextureField extends FileField {

    private Texture tex;

    public TextureField(String name, Texture tex, FileExtensions extensions, boolean editable) {
        super(name, tex == null ? null : Main.getAsset(tex.getPath()), extensions, new DirectoryRestrictedFileSystemView(Main.getAssetsFolder()), editable);
        getTextField().getDocument().addDocumentListener((DocumentChangeListener) _ -> {
            updateTex();
        });
        updateTex();
    }

    private void updateTex() {
        if(tex != null) {
            tex.dispose();
            tex = null;
        }
        if (!getFileValue().exists() || getFileValue().isDirectory()) {
            tex = null;
            return;
        }
        tex = new Texture(Main.getAssetPath(getFileValue().getPath()));
        tex.load();
    }

    @Override
    public Texture getValue() {
        return tex;
    }
}
