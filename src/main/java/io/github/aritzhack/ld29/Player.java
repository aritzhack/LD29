package io.github.aritzhack.ld29;

import io.github.aritzhack.aritzh.awt.gameEngine.input.InputHandler;
import io.github.aritzhack.aritzh.awt.render.AnimatedSprite;
import io.github.aritzhack.aritzh.awt.render.IRender;

import java.awt.event.KeyEvent;

import static io.github.aritzhack.ld29.Game.SPRITE_SIZE;

/**
 * @author Aritz Lopez
 */
public class Player extends Mob {

    private static final int SPEED = 4;
    private static final long ANIM_SPEED = 3;
    private static final long ANIM_DELTA = 1_000_000_000L / 60L;

    private AnimatedSprite sprite = new AnimatedSprite(Game.SPRITES, "player", 5, 1000);

    public Player(Level level, int x, int y) {
        super(level, x, y);
    }

    @Override
    public void update() {
        this.sprite.getCurrentFrame(ANIM_DELTA * ANIM_SPEED);

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

        this.dx *= SPEED;
        this.dy *= SPEED;
        super.update();
    }

    public Tile getTileAtMe() {
        return this.level.getTileAt(this.getX() / SPRITE_SIZE, this.getY() / SPRITE_SIZE);
    }

    @Override
    public int getX() {
        return x + SPRITE_SIZE / 2;
    }

    @Override
    public int getY() {
        return y + SPRITE_SIZE / 2;
    }

    public void render(IRender render) {
        render.draw(this.x, this.y, 0, this.sprite);
    }
}
