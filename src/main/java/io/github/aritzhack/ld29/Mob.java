package io.github.aritzhack.ld29;

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

    public Mob(Level level, int x, int y) {
        this.level = level;
        this.x = x;
        this.y = y;
    }

    public void update() {
        this.x += dx * SPEED;
        this.y += dy * SPEED;
        this.x = Math.max(Math.min(this.x, this.level.getGame().getGame().getWidth() - Game.SPRITE_SIZE), 0);
        this.y = Math.max(Math.min(this.y, this.level.getGame().getGame().getHeight() - Game.SPRITE_SIZE), 0);
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
