package io.github.aritzhack.underMine.mob;

import io.github.aritzhack.aritzh.awt.render.Sprite;
import io.github.aritzhack.underMine.Game;
import io.github.aritzhack.underMine.level.Level;

/**
 * @author Aritz Lopez
 */
public class Ray extends Mob {

    private static final Sprite DEFAULT_SPRITE = Game.SPRITES.get("ray");

    public Ray(Level level, double x, double y, double direction) {
        super(level, x, y);
        this.sprite = rotate(direction, DEFAULT_SPRITE);
        this.dx = Math.sin(direction) * speed;
        this.dy = -Math.cos(direction) * speed;
    }

    @Override
    public void onCollideWithWall() {
        super.onCollideWithWall();
        this.health = 0;
    }

    public void kill(Mob other) {
        if (this.isDead()) return;
        this.kill();
        other.kill();
    }
}
