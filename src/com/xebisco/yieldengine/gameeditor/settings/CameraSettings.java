package com.xebisco.yieldengine.gameeditor.settings;

import com.xebisco.yieldengine.utils.Editable;
import com.xebisco.yieldengine.utils.Visible;

import java.io.Serial;

public class CameraSettings implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = -6336010662499980400L;

    @Visible
    @Editable
    public Float cameraSpeed = 400f, zoomIntensity = 5f;
}
