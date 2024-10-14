package com.xebisco.yieldengine.gameeditor.settings;

import com.xebisco.yieldengine.uiutils.fields.annotations.Config;
import com.xebisco.yieldengine.uiutils.fields.annotations.Visible;

import java.io.Serializable;

@Config(title = "Appearance")
public class Appearance implements Serializable {
    @Visible
    String test1, test2;
}
