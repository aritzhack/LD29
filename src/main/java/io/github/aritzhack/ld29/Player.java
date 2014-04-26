package io.github.aritzhack.ld29;

import io.github.aritzhack.aritzh.awt.gameEngine.input.InputHandler;
import io.github.aritzhack.aritzh.awt.render.AnimatedSprite;
import io.github.aritzhack.aritzh.awt.render.IRender;
import io.github.aritzhack.aritzh.awt.render.Sprite;

import java.awt.event.KeyEvent;
import java.util.ConcurrentModificationException;

import static io.github.aritzhack.ld29.Game.SPRITE_SIZE;

/**
 * @author Aritz Lopez
 */
public class Player extends Mob {

    private static final Sprite STILL_SPRITE = Game.SPRITES.get("player0");

    private final AnimatedSprite aSprite = new AnimatedSprite(Game.SPRITES, "player", 4, 1000 / ANIM_SPEED);
    private double angle = Math.PI / 4;

    public Player(Level level, int x, int y) {
        super(level, x, y);
        this.sprite = aSprite.getCurrentFrame(0);
    }

    @Override
    public void update() {
        this.aSprite.getCurrentFrame(ANIM_DELTA);

        this.dx = this.dy = 0;

        final InputHandler ih = this.getLevel().getGame().getGame().getInputHandler();
        if (ih.isKeyDown(KeyEvent.VK_W) || ih.isKeyDown(KeyEvent.VK_UP)) {
            this.dy--;
        }
        if (ih.isKeyDown(KeyEvent.VK_S) || ih.isKeyDown(KeyEvent.VK_DOWN)) {
            this.dy++;
        }
        if (ih.isKeyDown(KeyEvent.VK_A) || ih.isKeyDown(KeyEvent.VK_LEFT)) {
            this.dx--;
        }
        if (ih.isKeyDown(KeyEvent.VK_D) || ih.isKeyDown(KeyEvent.VK_RIGHT)) {
            this.dx++;
        }
        if (ih.wasKeyTyped(KeyEvent.VK_SPACE)) {
            this.getTileAtMe().show();
        }
        if (ih.wasKeyTyped(KeyEvent.VK_F)) {
            this.getTileAtMe().toggleFlag();
        }

        try {
            if (ih.getMouseEvents().stream().filter(e -> e.getAction() == InputHandler.MouseAction.RELEASED).count() != 0) {
                this.fire();
            }
        } catch (ConcurrentModificationException ignored) {} // Just in case...

        double dx = ih.getMousePos().x - this.getX() + 0.000000000001; // So that there is not ArithmeticException
        double dy = ih.getMousePos().y - this.getY();

        this.angle = dx > 0 ? Math.atan(dy / dx) + Math.PI / 2 : Math.atan(dy / dx) - Math.PI / 2;

        this.sprite = Mob.rotate(this.angle, (this.dx == 0 && this.dy == 0 ? STILL_SPRITE : this.aSprite.getCurrentFrame(0)));

        super.update();
    }

    public Tile getTileAtMe() {
        return this.level.getTileAt(this.getX() / SPRITE_SIZE, this.getY() / SPRITE_SIZE);
    }

    private void fire() {
        this.level.spawnMob(new Ray(this.level, this.x, this.y, this.angle));
    }

    @Override
    public void render(IRender render) {
        render.draw(this.x, this.y, this.sprite);
    }

    @Override
    public int getX() {
        return x + SPRITE_SIZE / 2;
    }

    @Override
    public int getY() {
        return y + SPRITE_SIZE / 2;
    }
}
