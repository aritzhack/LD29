package io.github.aritzhack.ld29;

import io.github.aritzhack.aritzh.awt.render.AnimatedSprite;
import io.github.aritzhack.aritzh.awt.render.IRender;

/**
 * @author Aritz Lopez
 */
public class Enemy extends Mob {

    private final AnimatedSprite sprite = new AnimatedSprite(Game.SPRITES, "player", 4, 1000 / ANIM_SPEED);

    public Enemy(Level level, int x, int y) {
        super(level, x, y);
        this.SPEED = 2;
    }

    @Override
    public void update() {
        this.sprite.getCurrentFrame(ANIM_DELTA);

        this.dx = Integer.signum(this.level.getPlayer().getX() - this.x);
        this.dy = Integer.signum(this.level.getPlayer().getY() - this.y);

        super.update();
    }

    public void render(IRender render) {
        render.draw(this.x, this.y, 0, this.sprite);
    }
}
