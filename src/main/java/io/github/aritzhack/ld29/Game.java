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
import io.github.aritzhack.ld29.gui.Button;
import io.github.aritzhack.ld29.level.Level;
import io.github.aritzhack.ld29.util.Util;

import javax.imageio.ImageIO;
import java.awt.Color;
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
    public static final Sound BG_SOUND = new Sound(Game.class.getResource("/audio/bg.wav"));
    public static final Font CONSOLAS_28 = new Font("Consolas", Font.BOLD, 28);

    public static boolean altMove = false;

    private final CanvasGame game;
    private final Set<Button> mainMenuButtons = Sets.newHashSet();
    private final Set<Button> aboutButtons = Sets.newHashSet();
    private Level level;
    private IRender render;
    private BufferedImage bgImage;
    private GameStage stage = GameStage.MAIN_MENU;

    public Game(int width, int height, boolean noFrame) {
        this.game = new CanvasGame(this, width, height, noFrame, LOG);
    }

    private void about() {
        this.stage = GameStage.ABOUT;
    }


    @Override
    public void onStart() {
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

        this.mainMenuButtons.add(new Button("Start", new Rectangle(this.getWidth() / 2 - 240 / 2, 125, 240, 44), this::startGame));

        this.mainMenuButtons.add(new Button("How to play", new Rectangle(this.getWidth() / 2 - 240 / 2, 225, 240, 44), this::howToPlay));

        this.mainMenuButtons.add(new Button("About", new Rectangle(this.getWidth() / 2 - 240 / 2, 325, 240, 44), this::about));

        this.mainMenuButtons.add(new Button("Exit", new Rectangle(this.getWidth() / 2 - 240 / 2, 425, 240, 44), this.game::stop));

        this.aboutButtons.add(new Button("Back to Main Menu", new Rectangle(this.getWidth() / 2 - 300 / 2, this.getHeight() - 60, 300, 44), this::mainMenu));

        this.level.initLevel(Level.Difficulty.EASY);
        BG_SOUND.play();
    }

    public int getWidth() {
        return this.getGame().getWidth();
    }

    public int getHeight() {
        return this.getGame().getHeight();
    }

    public CanvasGame getGame() {
        return this.game;
    }

    @Override
    public void onStop() {
        BG_SOUND.stop();
    }

    @Override
    public void onRender(Graphics g) {
        this.render.clear();

        int x = this.getWidth() / 2;

        switch (this.stage) {
            case MAIN_MENU:
                g.drawImage(this.bgImage, 0, 0, this.getWidth(), this.getHeight(), null);
                this.mainMenuButtons.forEach(b -> b.render(g));
                break;
            case GAME:
                this.level.render(g);
                this.level.render(this.render);
                g.drawImage(this.render.getImage(), 0, 0, this.render.getWidth(), this.render.getHeight(), null);
                break;
            case ABOUT:
                g.setFont(CONSOLAS_28);
                g.setColor(Color.white);

                g.drawImage(this.bgImage, 0, 0, this.getWidth(), this.getHeight(), null);

                Util.drawCenteredString(g, "This game was made in less than 48 hours for the", x, 50, true);
                Util.drawCenteredString(g, "29th Ludum Dare. A programming competition held", x, 80, true);
                Util.drawCenteredString(g, "every 4 months in which participants must develop", x, 110, true);
                Util.drawCenteredString(g, "a game from scratch in a weekend.", x, 140, true);

                Util.drawCenteredString(g, "This compo's theme was \"Beneath the surface\"", x, 200, true);

                Util.drawCenteredString(g, "This game is based off of the classic minesweeper,", x, 260, true);
                Util.drawCenteredString(g, "and adds enemies  that spawn from BENEATH THE", x, 290, true);
                Util.drawCenteredString(g, "SURFACE, from where the mines are hidden, so that", x, 320, true);
                Util.drawCenteredString(g, "it's not as slow and boring as the original game.", x, 350, true);

                this.aboutButtons.forEach(b -> b.render(g));
                break;
            case HTP:
                g.setFont(CONSOLAS_28);
                g.setColor(Color.white);

                g.drawImage(this.bgImage, 0, 0, this.getWidth(), this.getHeight(), null);

                x = 50;

                Util.drawStringAligned(g, "Controls", Util.HAlignment.CENTER, Util.VAlignment.CENTER, this.getWidth() / 2 - 30, 50, false, false, true);
                g.setColor(Color.green);
                Util.drawStringAligned(g, "WASD or ARROW KEYS:", Util.HAlignment.LEFT, Util.VAlignment.CENTER, x, 90, false, false, true);
                Util.drawStringAligned(g, "SPACE:", Util.HAlignment.LEFT, Util.VAlignment.CENTER, x, 130, false, false, true);
                Util.drawStringAligned(g, "F:", Util.HAlignment.LEFT, Util.VAlignment.CENTER, x, 170, false, false, true);
                Util.drawStringAligned(g, "F2:", Util.HAlignment.LEFT, Util.VAlignment.CENTER, x, 210, false, false, true);
                Util.drawStringAligned(g, "1, 2, 3:", Util.HAlignment.LEFT, Util.VAlignment.CENTER, x, 250, false, false, true);

                x = this.getWidth() / 2;

                //g.setColor(new Color(0, 171, 0));

                Util.drawStringAligned(g, "Movement", Util.HAlignment.LEFT, Util.VAlignment.CENTER, x, 90, false, false, true);
                Util.drawStringAligned(g, "Jumping (clicking a mine)", Util.HAlignment.LEFT, Util.VAlignment.CENTER, x, 130, false, false, true);
                Util.drawStringAligned(g, "Placing a flag", Util.HAlignment.LEFT, Util.VAlignment.CENTER, x, 170, false, false, true);
                Util.drawStringAligned(g, "Toggle movement mode", Util.HAlignment.LEFT, Util.VAlignment.CENTER, x, 210, false, false, true);
                Util.drawStringAligned(g, "Change difficulty", Util.HAlignment.LEFT, Util.VAlignment.CENTER, x, 250, false, false, true);

                g.setColor(new Color(150, 152, 255));

                Util.drawStringAligned(g, "This is a typical minesweeper, but with a twist.", Util.HAlignment.CENTER, Util.VAlignment.CENTER, x, 300, false, false, true);
                Util.drawStringAligned(g, "When you jump on a tile, the non-mine tiles in a", Util.HAlignment.CENTER, Util.VAlignment.CENTER, x, 330, false, false, true);
                Util.drawStringAligned(g, "5x5 around you are shown. Each time a tile is shown,", Util.HAlignment.CENTER, Util.VAlignment.CENTER, x, 360, false, false, true);
                Util.drawStringAligned(g, "there's a chance an enemy will spawn. In order to", Util.HAlignment.CENTER, Util.VAlignment.CENTER, x, 390, false, false, true);
                Util.drawStringAligned(g, "restore health, jump onto more tiles!", Util.HAlignment.CENTER, Util.VAlignment.CENTER, x, 420, false, false, true);
                g.setColor(Color.red);
                Util.drawStringAligned(g, "But be careful, and do not jump on mines!", Util.HAlignment.CENTER, Util.VAlignment.CENTER, x, 480, false, false, true);


                this.aboutButtons.forEach(b -> b.render(g));
                break;
        }

    }

    @Override
    public void onUpdate() {

        if (!BG_SOUND.isPlaying()) BG_SOUND.play();

        switch (this.stage) {
            case MAIN_MENU:
                this.mainMenuButtons.forEach(b -> b.update(this));
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
            case HTP:
                this.aboutButtons.forEach(b -> b.update(this));
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

    public void howToPlay() {
        this.stage = GameStage.HTP;
    }

    public void startGame() {
        this.stage = GameStage.GAME;
        this.level.reinit();
        this.level.initLevel(this.level.getDifficulty());
    }

    public void start() {
        this.game.start();
    }

    public void mainMenu() {
        this.stage = GameStage.MAIN_MENU;
    }

    private static enum GameStage {
        MAIN_MENU, ABOUT, GAME, HTP
    }
}
