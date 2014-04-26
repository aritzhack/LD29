package io.github.aritzhack.ld29;

import io.github.aritzhack.aritzh.awt.gameEngine.CanvasGame;
import io.github.aritzhack.aritzh.awt.gameEngine.IGame;
import io.github.aritzhack.aritzh.awt.render.BufferedImageRenderer;
import io.github.aritzhack.aritzh.awt.render.IRender;
import io.github.aritzhack.aritzh.awt.render.Sprite;
import io.github.aritzhack.aritzh.awt.render.SpriteSheetLoader;
import io.github.aritzhack.aritzh.logging.ILogger;
import io.github.aritzhack.aritzh.logging.SLF4JLogger;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Map;

/**
 * @author Aritz Lopez
 */
public class Game implements IGame {

    public static final String GAME_NAME = "LD29";
    public static final ILogger LOG = new SLF4JLogger(GAME_NAME);
    public static final int SPRITE_SIZE = 32;
    public static final Map<String, Sprite> SPRITES = SpriteSheetLoader.load("sheet.sht");

    private final CanvasGame game;
    private final Level level;
    private final IRender render;

    public Game(int width, int height, boolean noFrame) {
        this.game = new CanvasGame(this, width, height, noFrame, LOG);
        this.level = new Level(this, this.game.getWidth() / 32, this.game.getHeight() / 32);
        this.render = new BufferedImageRenderer(this.game.getWidth(), this.game.getHeight(), SPRITES);
        this.game.requestFocus();
    }

    @Override
    public void onStart() {
        this.level.initLevel(Level.Difficulty.EASY);
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onRender(Graphics g) {
        this.level.render(this.render);

        g.drawImage(this.render.getImage(), 0, 0, this.render.getWidth(), this.render.getHeight(), null);
    }

    @Override
    public void onUpdate() {
        if (game.getInputHandler().wasKeyTyped(KeyEvent.VK_1)) {
            level.initLevel(Level.Difficulty.EASY);
        } else if (game.getInputHandler().wasKeyTyped(KeyEvent.VK_2)) {
            level.initLevel(Level.Difficulty.NORMAL);
        } else if (game.getInputHandler().wasKeyTyped(KeyEvent.VK_3)) {
            level.initLevel(Level.Difficulty.HARD);
        }
        this.level.update(this);
        this.game.getInputHandler().clearMouseEvents();
    }

    @Override
    public String getGameName() {
        return GAME_NAME;
    }

    @Override
    public void onUpdatePS() {
        LOG.d("FPS: " + this.game.getFPS() + " | UPS: " + this.game.getUPS());
    }

    public CanvasGame getGame() {
        return this.game;
    }

    public void gameOver() {

    }
}
