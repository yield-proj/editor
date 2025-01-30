package com.xebisco.yieldengine.gameeditor.fields;

import com.xebisco.yieldengine.core.io.audio.Audio;
import com.xebisco.yieldengine.core.io.text.Font;
import com.xebisco.yieldengine.core.io.text.FontProperties;
import com.xebisco.yieldengine.core.io.texture.Texture;
import com.xebisco.yieldengine.gameeditor.Main;
import com.xebisco.yieldengine.uilib.DirectoryRestrictedFileSystemView;
import com.xebisco.yieldengine.uilib.DocumentChangeListener;
import com.xebisco.yieldengine.uilib.fields.FileField;
import com.xebisco.yieldengine.utils.FileExtensions;

public class AudioField extends FileField {

    private Audio audio;

    public AudioField(String name, Audio audio, FileExtensions extensions, boolean editable) {
        super(name, audio == null ? null : Main.getAsset(audio.getPath()), extensions, new DirectoryRestrictedFileSystemView(Main.getAssetsFolder()), editable);
        getTextField().getDocument().addDocumentListener((DocumentChangeListener) _ -> {
            updateAudio();
        });
        updateAudio();
    }

    private void updateAudio() {
        if(audio != null) {
            audio.dispose();
            audio = null;
        }
        if(!getFileValue().exists() || getFileValue().isDirectory()) {
            audio = null;
            return;
        }
        audio = new Audio(Main.getAssetPath(getFileValue().getPath()));
        audio.load();
    }

    @Override
    public Audio getValue() {
        return audio;
    }
}
