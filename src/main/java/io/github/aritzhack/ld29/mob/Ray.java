package io.github.aritzhack.ld29.mob;

import io.github.aritzhack.aritzh.awt.render.IRender;
import io.github.aritzhack.aritzh.awt.render.Sprite;
import io.github.aritzhack.ld29.Game;
import io.github.aritzhack.ld29.level.Level;

/**
 * @author Aritz Lopez
 */
public class Ray extends Mob {

    private static final Sprite DEFAULT_SPRITE = Game.SPRITES.get("ray");

    private final Sprite sprite;

    public Ray(Level level, double x, double y, double direction) {
        super(level, x, y);
        this.sprite = rotate(direction, DEFAULT_SPRITE);
        this.dx = (int) (Math.sin(direction) * speed);
        this.dy = -(int) (Math.cos(direction) * speed);
    }

    @Override
    public void onCollideWithWall() {
        super.onCollideWithWall();
        this.health = 0;
    }

    @Override
    public void render(IRender render) {
        render.draw((int) this.x, (int) this.y, this.sprite);
    }
}
