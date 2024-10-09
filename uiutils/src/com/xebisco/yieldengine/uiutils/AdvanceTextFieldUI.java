package com.xebisco.yieldengine.uiutils;

import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.Element;
import javax.swing.text.FieldView;
import javax.swing.text.View;
import java.awt.*;

public class AdvanceTextFieldUI extends BasicTextFieldUI {

    @Override
    public View create(Element elem) {
        return new FieldView(elem) {

            @Override
            protected Shape adjustAllocation(final Shape shape) {
                if (shape instanceof Rectangle) {
                    final Rectangle result = (Rectangle) super.adjustAllocation(shape);
                    /* set vertical text position to top */
                    result.y = ((Rectangle) shape).y;
                    return result;
                }

                return super.adjustAllocation(shape);
            }
        };
    }
}