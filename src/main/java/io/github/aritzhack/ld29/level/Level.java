package io.github.aritzhack.ld29.level;

import com.google.common.collect.Sets;
import io.github.aritzhack.aritzh.awt.gameEngine.input.InputHandler;
import io.github.aritzhack.aritzh.awt.render.IRender;
import io.github.aritzhack.ld29.Game;
import io.github.aritzhack.ld29.mob.Enemy;
import io.github.aritzhack.ld29.mob.Mob;
import io.github.aritzhack.ld29.mob.Player;
import io.github.aritzhack.ld29.mob.Ray;
import io.github.aritzhack.ld29.util.Util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @author Aritz Lopez
 */
public class Level {

    public static final Color BG_COLOR = new Color(133, 133, 133);
    public static final Random RAND = new Random(System.currentTimeMillis());

    private final Tile[][] tiles;
    private final int width;
    private final int height;
    private final Player player;
    private final Set<Mob> mobs = Sets.newHashSet();
    private final Stack<Mob> toSpawn = new Stack<>();
    private final int SMILEY_SIZE = (int) (Game.SPRITE_SIZE * 1.2);
    private final Rectangle smileyBounds;
    private boolean smileyIsPressed = false;
    private Game game;
    private int enemyDamage = 0;

    public Level(Game game, int width, int height) {
        this.game = game;
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
        this.player = new Player(this, Game.TOP_MARGIN + 10, 10);
        this.mobs.add(this.player);
        this.smileyBounds = new Rectangle(this.game.getWidth() / 2 - SMILEY_SIZE / 2, Game.TOP_MARGIN / 2 - SMILEY_SIZE / 2, SMILEY_SIZE, SMILEY_SIZE);
    }

    public void initLevel(Difficulty difficulty) {
        RAND.setSeed(System.currentTimeMillis());
        this.enemyDamage = difficulty.getEnemyDamage();

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                this.tiles[x][y] = new Tile(this, x, y, RAND.nextInt(100) < difficulty.getMineProbability() ? Tile.TileType.MINE : Tile.TileType.NORMAL);
            }
        }
    }

    public Tile[][] getTiles() {
        return this.tiles;
    }

    public Tile getTileAt(int x, int y) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) return null;
        return this.tiles[x][y];
    }

    public void render(IRender r) {

        for (Tile[] tiles : this.tiles) {
            for (Tile t : tiles) {
                t.render(r);
            }
        }
        this.mobs.forEach(e -> e.render(r));

        r.draw(r.getWidth() / 2 - Game.SPRITE_SIZE / 2, Game.TOP_MARGIN / 2 - Game.SPRITE_SIZE / 2, "smiley");
        final int leftMargin = 115;
        r.draw(leftMargin + 20, Game.TOP_MARGIN / 2 - 25, "healthbg");
        for (int i = 0; i < this.player.getHealth() / 20f; i++) {
            r.draw(leftMargin + 35 + i * 20, Game.TOP_MARGIN / 2 - 17, "health_point");
        }
    }

    public void render(Graphics g) {
        Util.drawBeveled(g, BG_COLOR, 0, 0, this.game.getWidth(), Game.TOP_MARGIN, false);

        int margin1 = 10;

        Util.drawBeveled(g, BG_COLOR, margin1, margin1, this.game.getWidth() - margin1 * 2, Game.TOP_MARGIN - margin1 * 2, true);


        Util.drawBeveled(g, BG_COLOR, this.smileyBounds.x, this.smileyBounds.y, this.smileyBounds.width, this.smileyBounds.height, smileyIsPressed);

        g.setFont(Game.CONSOLAS_28);

        final int enemyCount = this.mobs.stream().filter(m -> m instanceof Enemy).mapToInt(m -> 1).sum();
        int mineCount = 0;
        for (Tile[] tiles : this.tiles) {
            for (Tile t : tiles) {
                if (t.getType() == Tile.TileType.MINE) mineCount++;
                if (t.isFlagged()) mineCount--;
            }
        }

        g.setColor(Color.red.darker());
        Util.drawStringAligned(g, "Enemies: " + enemyCount, Util.HAlignment.RIGHT, Util.VAlignment.CENTER, this.game.getWidth() - 30, Game.TOP_MARGIN / 2, true, false);
        g.setColor(Color.green.darker());

        Util.drawStringAligned(g, "Health:", Util.HAlignment.LEFT, Util.VAlignment.CENTER, 25, Game.TOP_MARGIN / 2, true, false);

        g.setColor(Color.black);
        Util.drawStringAligned(g, "Mines: " + mineCount, Util.HAlignment.LEFT, Util.VAlignment.CENTER, this.game.getWidth() / 2 + 35, Game.TOP_MARGIN / 2, true, false);

    }

    public void update(Game game) {

        this.toSpawn.forEach(this.mobs::add);
        this.toSpawn.clear();
        clearDeadMobs();

        this.mobs
            .stream()
            .filter(ray -> ray instanceof Ray) // Get only rays
            .forEach(r -> this.mobs // Then, for each ray,
                .stream()
                .filter(enemy -> enemy instanceof Enemy) // Get only enemies
                .filter(e -> e.getBounds().intersects(r.getBounds())) // Get only those that collide with the ray
                .forEach(((Ray) r)::kill)); // Kill both the ray and the enemy

        clearDeadMobs();

        this.mobs
            .stream()
            .filter(e -> e instanceof Enemy)
            .filter(e -> ((Enemy) e).canDamage())
            .filter(e -> e.getBounds().intersects(this.player.getBounds()))
            .forEach(e -> ((Enemy) e).damage(this.player, this.enemyDamage));

        if (this.player.isDead()) this.game.gameOver();

        for (Tile[] tiles : this.tiles) {
            for (Tile t : tiles) {
                t.update(game);
            }
        }
        this.mobs.forEach(Mob::update);

        final InputHandler ih = this.game.getGame().getInputHandler();
        while (!ih.getMouseEvents().empty()) {
            InputHandler.MouseInputEvent e = ih.getMouseEvents().pop();
            if (e.getAction() == (this.smileyIsPressed ? InputHandler.MouseAction.RELEASED : InputHandler.MouseAction.PRESSED)) {
                this.smileyIsPressed = this.smileyIsPressed ^ this.smileyBounds.contains(e.getPosition());
            }
        }
    }

    private void clearDeadMobs() {this.mobs.removeAll(this.mobs.stream().filter(Mob::isDead).filter(m -> !(m instanceof Player)).collect(Collectors.toSet()));}

    public void spawnEnemy() {
        int x = RAND.nextInt(this.game.getWidth());
        int y = Game.TOP_MARGIN + RAND.nextInt(this.game.getHeight() - Game.TOP_MARGIN);

        switch (RAND.nextInt(4)) {
            case 0: // TOP
                y = Game.TOP_MARGIN + 30;
                break;
            case 1: // BOTTOM
                y = this.game.getHeight() - 30;
                break;
            case 2: // LEFT
                x = 30;
                break;
            case 3: // RIGHT
                x = this.game.getWidth() - 30;
                break;
        }
        this.toSpawn.add(new Enemy(this, x, y));
    }

    public void spawnMob(Mob mob) {
        this.toSpawn.push(mob);
    }

    public void showAll() {
        for (Tile[] tiles : this.tiles) {
            for (Tile t : tiles) {
                t.setShowing(true, false);
            }
        }
    }

    public Game getGame() {
        return this.game;
    }

    public Player getPlayer() {
        return player;
    }

    public static enum Difficulty {
        EASY(10, 1), NORMAL(20, 2), HARD(30, 4);

        private final int mineProbability;
        private final int enemyDamage;

        private Difficulty(int mineProbability, int enemyDamage) {
            this.mineProbability = mineProbability;
            this.enemyDamage = enemyDamage;
        }

        public int getMineProbability() {
            return mineProbability;
        }

        public int getEnemyDamage() {
            return enemyDamage;
        }
    }
}
