package io.github.aritzhack.ld29.level;

import com.google.common.collect.Sets;
import io.github.aritzhack.aritzh.awt.render.IRender;
import io.github.aritzhack.ld29.Game;
import io.github.aritzhack.ld29.mob.Enemy;
import io.github.aritzhack.ld29.mob.Mob;
import io.github.aritzhack.ld29.mob.Player;
import io.github.aritzhack.ld29.util.Util;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @author Aritz Lopez
 */
public class Level {

    public static final Color BG_COLOR = new Color(133, 133, 133);
    private static final Random RAND = new Random(System.currentTimeMillis());

    private final Tile[][] tiles;
    private final int width;
    private final int height;
    private final Player player;
    private final Set<Mob> mobs = Sets.newHashSet();
    private final Stack<Mob> toSpawn = new Stack<>();
    private Game game;

    public Level(Game game, int width, int height) {
        this.game = game;
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
        this.player = new Player(this, Game.TOP_MARGIN + 10, 10);
        this.mobs.add(this.player);
        //this.enemies.add(new Enemy(this, 200, 200));
    }

    public void initLevel(Difficulty difficulty) {
        RAND.setSeed(System.currentTimeMillis());

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
    }

    public void render(Graphics g) {
        Util.drawBeveled(g, BG_COLOR, 0, 0, this.game.getGame().getWidth(), Game.TOP_MARGIN, false);

        int margin1 = 10;

        Util.drawBeveled(g, BG_COLOR, margin1, margin1, this.game.getGame().getWidth() - margin1 * 2, Game.TOP_MARGIN - margin1 * 2, true);

    }

    public void update(Game game) {
        this.toSpawn.forEach(this.mobs::add);
        this.toSpawn.clear();
        this.mobs.removeAll(this.mobs.stream().filter(Mob::isDead).collect(Collectors.toSet()));
        for (Tile[] tiles : this.tiles) {
            for (Tile t : tiles) {
                t.update(game);
            }
        }
        this.mobs.forEach(Mob::update);
    }

    public void spawnEnemy(int tx, int ty) {
        this.toSpawn.push(new Enemy(this, Game.TOP_MARGIN + tx * Game.SPRITE_SIZE, ty * Game.SPRITE_SIZE));
    }

    public void spawnMob(Mob mob) {
        this.toSpawn.push(mob);
    }

    public void showAll() {
        for (Tile[] tiles : this.tiles) {
            for (Tile t : tiles) {
                t.setShowing(true);
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
        EASY(10), NORMAL(20), HARD(30);

        private final int mineProbability;

        private Difficulty(int mineProbability) {
            this.mineProbability = mineProbability;
        }

        public int getMineProbability() {
            return mineProbability;
        }

    }
}
