package io.github.aritzhack.underMine.mob;

import io.github.aritzhack.aritzh.awt.audio.Sound;
import io.github.aritzhack.aritzh.awt.gameEngine.input.InputHandler;
import io.github.aritzhack.aritzh.awt.render.AnimatedSprite;
import io.github.aritzhack.aritzh.awt.render.Sprite;
import io.github.aritzhack.underMine.Game;
import io.github.aritzhack.underMine.level.Level;
import io.github.aritzhack.underMine.level.Tile;

import java.awt.event.KeyEvent;
import java.util.ConcurrentModificationException;

import static io.github.aritzhack.underMine.Game.SPRITE_SIZE;

/**
 * @author Aritz Lopez
 */
public class Player extends Mob {

    private static final Sprite STILL_SPRITE = Game.SPRITES.get("playerr0");
    private static final Sound JUMP = new Sound(Player.class.getResource("/audio/jump.wav"));
    private static final int MAX_HEALTH = 200;
    private static final Sound LASER = new Sound(Player.class.getResource("/audio/laser.wav"));

    private final AnimatedSprite aSprite = new AnimatedSprite(Game.SPRITES, "playerr", 4, 1000 / ANIM_SPEED);
    private double angle = Math.PI / 4;

    public Player(Level level, int x, int y) {
        super(level, x, y);
        this.sprite = aSprite.getCurrentFrame(0);
        this.health = MAX_HEALTH;
    }

    @Override
    public void update() {
        this.aSprite.getCurrentFrame(ANIM_DELTA);
        final InputHandler ih = this.getLevel().getGame().getGame().getInputHandler();

        double dx = ih.getMousePos().x - this.getX() - this.sprite.getWidth() / 2 + 0.000000000001; // So that there is not ArithmeticException
        double dy = ih.getMousePos().y - this.getY() - this.sprite.getHeight() / 2;

        this.angle = dx > 0 ? Math.atan(dy / dx) + Math.PI / 2 : Math.atan(dy / dx) - Math.PI / 2;

        this.dx = this.dy = 0;

        final double cos = Math.cos(this.angle);
        final double sin = Math.sin(this.angle);

        final double mCos = Math.cos(-this.angle);
        final double mSin = Math.sin(-this.angle);

        if (ih.isKeyDown(KeyEvent.VK_W) || ih.isKeyDown(KeyEvent.VK_UP)) {
            if (Game.altMove) {
                this.dy -= cos;
                this.dx += sin;
            } else this.dy--;
        }
        if (ih.isKeyDown(KeyEvent.VK_S) || ih.isKeyDown(KeyEvent.VK_DOWN)) {
            if (Game.altMove) {
                this.dy += cos;
                this.dx -= sin;
            } else this.dy++;
        }
        if (ih.isKeyDown(KeyEvent.VK_A) || ih.isKeyDown(KeyEvent.VK_LEFT)) {
            if (Game.altMove) {
                this.dx += mCos;
                this.dy -= mSin;
            } else this.dx--;
        }
        if (ih.isKeyDown(KeyEvent.VK_D) || ih.isKeyDown(KeyEvent.VK_RIGHT)) {
            if (Game.altMove) {
                this.dx -= mCos;
                this.dy += mSin;
            } else this.dx++;
        }

        if (!this.level.win && !this.level.gameOver) {

            if (ih.wasKeyTyped(KeyEvent.VK_SPACE)) {
                this.getTileAtMe().press();
                JUMP.play();
            }

            if (ih.wasKeyTyped(KeyEvent.VK_F)) {
                this.getTileAtMe().toggleFlag();
            }
        }

        try {
            if (ih.getMouseEvents().stream().filter(e -> e.getAction() == InputHandler.MouseAction.RELEASED).filter(e -> e.getPosition().getY() > Game.TOP_MARGIN).count() != 0) {
                this.fire();
            }
        } catch (ConcurrentModificationException ignored) {} // Just in case...

        this.sprite = Mob.rotate(this.angle, (this.dx == 0 && this.dy == 0 ? STILL_SPRITE : this.aSprite.getCurrentFrame(0)));

        super.update();
    }

    public Tile getTileAtMe() {
        return this.level.getTileAt((int) (x + this.sprite.getWidth() / 2) / SPRITE_SIZE, (int) ((y - Game.TOP_MARGIN + this.sprite.getHeight() / 2) / SPRITE_SIZE));
    }

    private void fire() {
        this.level.spawnMob(new Ray(this.level, this.x, this.y, this.angle));
        LASER.play();
    }

    public void hurt(int count) {
        this.health -= count;
    }

    public void revive() {
        this.health = MAX_HEALTH;
    }

    public void heal(int enemyDamage) {
        this.health = Math.min(MAX_HEALTH, this.health + enemyDamage);
    }
}
