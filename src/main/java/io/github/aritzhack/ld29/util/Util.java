package io.github.aritzhack.ld29.util;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;

/**
 * @author Aritz Lopez
 */
public enum Util {
    ;

    private static final int BORDER_SIZE = 4;

    private static final Color BRIGHT = Color.lightGray, DARK = Color.darkGray.darker();

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

    public static void drawStringAligned(Graphics g, String s, HAlignment h, VAlignment v, int x, int y, boolean withBevel, boolean downwards) {
        final FontMetrics fm = g.getFontMetrics();
        final Rectangle2D b = fm.getStringBounds(s, g);

        int nx = x, ny = y;

        switch (h) {
            case LEFT:
                nx = x;
                break;
            case CENTER:
                nx = x - (int) b.getWidth() / 2;
                break;
            case RIGHT:
                nx = x - (int) b.getWidth();
                break;
        }

        switch (v) {
            case TOP:
                ny = y;
                break;
            case CENTER:
                ny = y + (int) (b.getHeight() / 4);
                break;
            case BOTTOM:
                ny = y + (int) b.getHeight() / 2;
                break;
        }

        if (withBevel) {
            Color c = g.getColor();
            g.setColor(downwards ? DARK : BRIGHT);
            g.drawString(s, nx - 1, ny - 1);
            g.setColor(downwards ? BRIGHT : DARK);
            g.drawString(s, nx + 1, ny + 1);
            g.setColor(c);
        }
        g.drawString(s, nx, ny);

    }

    public static enum HAlignment {
        LEFT, CENTER, RIGHT
    }

    public static enum VAlignment {
        TOP, CENTER, BOTTOM
    }
}
