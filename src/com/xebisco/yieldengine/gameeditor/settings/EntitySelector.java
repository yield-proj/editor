package com.xebisco.yieldengine.gameeditor.settings;

import com.xebisco.yieldengine.utils.Editable;
import com.xebisco.yieldengine.utils.Visible;

import java.io.Serial;
import java.io.Serializable;

public class EntitySelector implements Serializable {
    @Serial
    private static final long serialVersionUID = 2164451309422980623L;

    @Visible
    @Editable
    public Float selectorMinimumDistance = 16f;
}
