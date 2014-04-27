package io.github.aritzhack.ld29.mob;

import io.github.aritzhack.aritzh.awt.render.AnimatedSprite;
import io.github.aritzhack.ld29.Game;
import io.github.aritzhack.ld29.level.Level;

/**
 * @author Aritz Lopez
 */
public class Enemy extends Mob {

    private final AnimatedSprite asprite = new AnimatedSprite(Game.SPRITES, "player", 4, 1000 / ANIM_SPEED);
    private double angle = Math.PI / 4;
    private int damageTimeout = 0;

    public Enemy(Level level, int x, int y) {
        super(level, x, y);
        this.speed = 1.9;
        this.sprite = asprite.getCurrentFrame(0);
    }

    @Override
    public void update() {

        double dx = this.level.getPlayer().getX() - this.x;
        double dy = this.level.getPlayer().getY() - this.y;

        this.angle = dx > 0 ? Math.atan(dy / dx) + Math.PI / 2 : Math.atan(dy / dx) - Math.PI / 2;

        this.sprite = Mob.rotate(this.angle, this.asprite.getCurrentFrame(ANIM_DELTA));

        this.dy = -Math.cos(this.angle) * this.speed;
        this.dx = Math.sin(this.angle) * this.speed;

        if (this.damageTimeout > 0) this.damageTimeout--;

        super.update();
    }

    public boolean canDamage() {
        return this.damageTimeout == 0;
    }

    public void damage(Player player, int damage) {
        this.damaged();
        player.hurt(damage);
    }

    public void damaged() {
        this.damageTimeout = 90;
    }
}
