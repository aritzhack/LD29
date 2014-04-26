package io.github.aritzhack.ld29;

/**
 * @author Aritz Lopez
 */
public abstract class Mob {

    protected final Level level;
    protected int x;
    protected int y;
    protected int dx;
    protected int dy;

    public Mob(Level level, int x, int y) {
        this.level = level;
        this.x = x;
        this.y = y;
    }

    public void update() {
        this.x += dx;
        this.y += dy;
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
}
