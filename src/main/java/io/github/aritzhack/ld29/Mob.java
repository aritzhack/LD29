package io.github.aritzhack.ld29;

import io.github.aritzhack.aritzh.awt.render.IRender;
import io.github.aritzhack.aritzh.awt.render.Sprite;

/**
 * @author Aritz Lopez
 */
public abstract class Mob {

    protected static final long ANIM_SPEED = 6;
    protected static final long ANIM_DELTA = 1_000_000_000L / 60L;
    protected final Level level;

    protected int x;
    protected int SPEED = 4;
    protected int y;
    protected int dx;
    protected int dy;
    protected int health = 10;

    public Mob(Level level, int x, int y) {
        this.level = level;
        this.x = x;
        this.y = y;
    }

    public static Sprite rotate(double radians, Sprite s) {
        final double cos = Math.cos(radians), sin = Math.sin(radians);
        final int[] rotated = new int[s.getPixels().length];

        final int centerX = s.getWidth() / 2;
        final int centerY = s.getHeight() / 2;

        for (int x = 0; x < s.getWidth(); x++)
            for (int y = 0; y < s.getHeight(); y++) {
                final int m = x - centerX;
                final int n = y - centerY;
                final int j = ((int) (m * cos + n * sin)) + centerX;
                final int k = ((int) (n * cos - m * sin)) + centerY;
                if (j >= 0 && j < s.getWidth() && k >= 0 && k < s.getHeight())
                    rotated[(y * s.getWidth() + x)] = s.getPixels()[(k * s.getWidth() + j)];
            }
        return new Sprite(s.getWidth(), s.getHeight(), rotated);
    }

    public void update() {
        this.x += dx * SPEED;
        this.y += dy * SPEED;
        int x2 = this.x;
        int y2 = this.y;
        this.x = Math.max(Math.min(this.x, this.level.getGame().getGame().getWidth() - Game.SPRITE_SIZE), 0);
        this.y = Math.max(Math.min(this.y, this.level.getGame().getGame().getHeight() - Game.SPRITE_SIZE), 0);
        if(this.x != x2 || this.y != y2) this.onCollideWithWall();
    }

    public void render(IRender render) {

    }

    public void onCollideWithWall() {

    }

    public Level getLevel() {
        return level;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHealth() {
        return health;
    }

    public boolean isDead() {
        return this.health <= 0;
    }
}
