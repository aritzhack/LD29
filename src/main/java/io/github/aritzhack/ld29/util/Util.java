package io.github.aritzhack.ld29.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

/**
 * @author Aritz Lopez
 */
public enum Util {
    ;

    private static final int BORDER_SIZE = 4;

    public static void drawBeveled(Graphics g, Color baseColor, int x, int y, int width, int height, boolean downwards) {
        g.setColor(baseColor);
        g.fillRect(x, y, width, height);

        g.setColor(downwards ? baseColor.darker().darker() : baseColor.brighter().brighter());
        g.fillRect(x, y, width, BORDER_SIZE);
        g.fillRect(x, y, BORDER_SIZE, height);

        g.setColor(downwards ? baseColor.brighter().brighter() : baseColor.darker().darker());
        g.fillRect(x + BORDER_SIZE, y + height - BORDER_SIZE, width - BORDER_SIZE, BORDER_SIZE);
        g.fillRect(x + width - BORDER_SIZE, y + BORDER_SIZE, BORDER_SIZE, height - BORDER_SIZE);

        Polygon bottomLeft = new Polygon(new int[]{x, x + BORDER_SIZE, x + BORDER_SIZE}, new int[]{y + height, y + height, y + height - BORDER_SIZE}, 3);
        Polygon topRight = new Polygon(new int[]{x + width, x + width, x + width - BORDER_SIZE}, new int[]{y, y + BORDER_SIZE, y + BORDER_SIZE}, 3);

        g.fillPolygon(bottomLeft);
        g.fillPolygon(topRight);
    }
}
