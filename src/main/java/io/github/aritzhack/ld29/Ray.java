package io.github.aritzhack.ld29;

import io.github.aritzhack.aritzh.awt.render.IRender;
import io.github.aritzhack.aritzh.awt.render.Sprite;

/**
 * @author Aritz Lopez
 */
public class Ray extends Mob {

    private static final Sprite DEFAULT_SPRITE = Game.SPRITES.get("ray");

    private final Sprite sprite;

    public Ray(Level level, int x, int y, double direction) {
        super(level, x, y);
        this.sprite = Mob.rotate(direction, DEFAULT_SPRITE);
        this.dx = (int) (Math.sin(direction) * SPEED);
        this.dy = -(int) (Math.cos(direction) * SPEED);
    }

    @Override
    public void render(IRender render) {
        render.draw(this.x, this.y, this.sprite);
    }

    @Override
    public void onCollideWithWall() {
        super.onCollideWithWall();
        this.health = 0;
    }
}
