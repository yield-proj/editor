package com.xebisco.yieldengine.projecteditor;

import com.formdev.flatlaf.icons.FlatAbstractIcon;

import java.awt.*;

public class NullIcon extends FlatAbstractIcon {

    public NullIcon() {
        super(16, 16, null);
    }

    @Override
    protected void paintIcon(Component component, Graphics2D graphics2D) {

    }
}
