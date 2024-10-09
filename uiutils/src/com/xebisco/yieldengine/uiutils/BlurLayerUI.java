package com.xebisco.yieldengine.uiutils;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Arrays;

public class BlurLayerUI extends LayerUI<Component> {

    private BufferedImage mOffscreenImage;
    private final BufferedImageOp mOperation;

    public BlurLayerUI() {
        int blurValue = 8;
        int blurCount = blurValue * blurValue;
        float ninth = 1.0f / blurCount;
        float[] blurKernel = new float[blurCount];
        Arrays.fill(blurKernel, ninth);
        mOperation = new ConvolveOp(new Kernel(blurValue, blurValue, blurKernel), ConvolveOp.EDGE_NO_OP, null);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        int w = c.getWidth();
        int h = c.getHeight();
        if (w == 0 || h == 0) {
            return;
        }
        // only create the offscreen image if the one we have is the wrong size.
        if (mOffscreenImage == null || mOffscreenImage.getWidth() != w || mOffscreenImage.getHeight() != h) {
            mOffscreenImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        }
        Graphics2D ig2 = mOffscreenImage.createGraphics();
        ig2.setClip(g.getClip());
        super.paint(ig2, c);
        ig2.dispose();
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(mOffscreenImage, mOperation, 0, 0);
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillRect(0, 0, c.getWidth(), c.getHeight());
    }

}