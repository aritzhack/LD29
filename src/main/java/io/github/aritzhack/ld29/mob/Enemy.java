package io.github.aritzhack.ld29.mob;

import io.github.aritzhack.aritzh.awt.render.AnimatedSprite;
import io.github.aritzhack.ld29.Game;
import io.github.aritzhack.ld29.level.Level;

/**
 * @author Aritz Lopez
 */
public class Enemy extends Mob {

    private final AnimatedSprite asprite = new AnimatedSprite(Game.SPRITES, "player", 4, 1000 / ANIM_SPEED);

    public Enemy(Level level, int x, int y) {
        super(level, x, y);
        this.speed = 2;
        this.sprite = asprite.getCurrentFrame(0);
    }

    @Override
    public void update() {
        this.sprite = this.asprite.getCurrentFrame(ANIM_DELTA);

        this.dx = Integer.signum((int) (this.level.getPlayer().getX() - this.x));
        this.dy = Integer.signum((int) (this.level.getPlayer().getY() - this.y));

        super.update();
    }
}
