package io.github.aritzhack.ld29;

import com.google.common.collect.Sets;
import io.github.aritzhack.aritzh.awt.audio.Sound;
import io.github.aritzhack.aritzh.awt.gameEngine.CanvasGame;
import io.github.aritzhack.aritzh.awt.gameEngine.IGame;
import io.github.aritzhack.aritzh.awt.gameEngine.input.InputHandler;
import io.github.aritzhack.aritzh.awt.render.BufferedImageRenderer;
import io.github.aritzhack.aritzh.awt.render.IRender;
import io.github.aritzhack.aritzh.awt.render.Sprite;
import io.github.aritzhack.aritzh.awt.render.SpriteSheetLoader;
import io.github.aritzhack.aritzh.logging.ILogger;
import io.github.aritzhack.aritzh.logging.SLF4JLogger;
import io.github.aritzhack.ld29.level.Level;

import javax.imageio.ImageIO;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @author Aritz Lopez
 */
public class Game implements IGame {

    public static final String GAME_NAME = "LD29";
    public static final ILogger LOG = new SLF4JLogger(GAME_NAME);
    public static final int SPRITE_SIZE = 32, TOP_MARGIN = 80;
    public static final Map<String, Sprite> SPRITES = SpriteSheetLoader.load("sheet.sht");
    public static final Sound BG_SOUND = new Sound(Game.class.getResourceAsStream("/audio/bg.wav"));
    public static final Font CONSOLAS_28 = new Font("Consolas", Font.BOLD, 28);

    public static boolean altMove = false;

    private final CanvasGame game;
    private final Level level;
    private final IRender render;
    private final BufferedImage bgImage;

    private final Set<Button> buttons = Sets.newHashSet();

    private GameStage stage = GameStage.MAIN_MENU;

    public Game(int width, int height, boolean noFrame) {
        this.game = new CanvasGame(this, width, height, noFrame, LOG);
        this.level = new Level(this, this.game.getWidth() / SPRITE_SIZE, (this.game.getHeight() - TOP_MARGIN) / SPRITE_SIZE);
        this.render = new BufferedImageRenderer(this.game.getWidth(), this.game.getHeight(), SPRITES);
        this.game.requestFocus();

        BufferedImage temp;
        try {
            temp = ImageIO.read(this.getClass().getResourceAsStream("/bgImage.png"));
        } catch (IOException e) {
            e.printStackTrace();
            temp = null;
        }
        this.bgImage = temp;

        this.buttons.add(new Button("Start", new Rectangle(this.getWidth() / 2 - 240 / 2, 150, 240, 44), this::startGame));

        this.buttons.add(new Button("About", new Rectangle(this.getWidth() / 2 - 240 / 2, 250, 240, 44), this::about));

        this.buttons.add(new Button("Exit", new Rectangle(this.getWidth() / 2 - 240 / 2, 350, 240, 44), this.game::stop));
    }

    public int getWidth() {
        return this.getGame().getWidth();
    }

    public CanvasGame getGame() {
        return this.game;
    }

    private void about() {
        this.stage = GameStage.ABOUT;
    }

    @Override
    public void onStart() {
        this.level.initLevel(Level.Difficulty.EASY);
        BG_SOUND.play();
    }

    @Override
    public void onStop() {
        BG_SOUND.stop();
    }

    @Override
    public void onRender(Graphics g) {
        this.render.clear();

        switch (this.stage) {
            case MAIN_MENU:
                g.drawImage(this.bgImage, 0, 0, this.getWidth(), this.getHeight(), null);
                this.buttons.forEach(b -> b.render(g));
                break;
            case GAME:
                this.level.render(g);
                this.level.render(this.render);
                g.drawImage(this.render.getImage(), 0, 0, this.render.getWidth(), this.render.getHeight(), null);
                break;
            case ABOUT:
                break;
        }

    }

    @Override
    public void onUpdate() {

        if (!BG_SOUND.isPlaying()) BG_SOUND.play();

        switch (this.stage) {
            case MAIN_MENU:
                this.buttons.forEach(b -> b.update(this));
                break;
            case GAME:
                final InputHandler ih = game.getInputHandler();
                if (ih.wasKeyTyped(KeyEvent.VK_1)) {
                    level.initLevel(Level.Difficulty.EASY);
                } else if (ih.wasKeyTyped(KeyEvent.VK_2)) {
                    level.initLevel(Level.Difficulty.NORMAL);
                } else if (ih.wasKeyTyped(KeyEvent.VK_3)) {
                    level.initLevel(Level.Difficulty.HARD);
                }
                if (ih.wasKeyTyped(KeyEvent.VK_F2)) {
                    Game.altMove = !Game.altMove;
                }
                this.level.update(this);
                this.game.getInputHandler().clearMouseEvents();
                break;
            case ABOUT:

                break;
        }
    }

    @Override
    public String getGameName() {
        return GAME_NAME;
    }

    @Override
    public void onUpdatePS() {
        LOG.d("FPS: " + this.game.getFPS() + " | UPS: " + this.game.getUPS());
    }

    public void startGame() {
        this.stage = GameStage.GAME;
    }

    public void mainMenu() {
        this.stage = GameStage.MAIN_MENU;
    }

    public int getHeight() {
        return this.getGame().getHeight();
    }

    private static enum GameStage {
        MAIN_MENU, ABOUT, GAME
    }
}
