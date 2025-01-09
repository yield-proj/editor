package com.xebisco.yieldengine.gameeditor.settings;

import com.xebisco.yieldengine.uilib.Group;
import com.xebisco.yieldengine.utils.Visible;

import java.io.Serial;
import java.io.Serializable;

@Group(name = "Appearance")
public class Appearance implements Serializable {
    @Serial
    private static final long serialVersionUID = 1428290707197967229L;

    @Visible
    public String test1, test2, test3, test4;
    @Visible
    float test;
}
