package io.github.aritzhack.ld29;

import io.github.aritzhack.aritzh.awt.render.IRender;

import java.util.Random;

/**
 * @author Aritz Lopez
 */
public class Level {

    private static final Random RAND = new Random(System.currentTimeMillis());

    private final Tile[][] tiles;
    private final int width;
    private final int height;

    public Level(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
    }

    public void initLevel(Difficulty difficulty) {
        RAND.setSeed(System.currentTimeMillis());

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                this.tiles[x][y] = new Tile(this, x, y, RAND.nextInt(100) < difficulty.getMinePosibility() ? Tile.TileType.MINE : Tile.TileType.NORMAL);
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
    }

    public void update(Game game) {
        for (Tile[] tiles : this.tiles) {
            for (Tile t : tiles) {
                t.update(game);
            }
        }
    }

    public void showAll() {
        for (Tile[] tiles : this.tiles) {
            for (Tile t : tiles) {
                t.setShowing(true);
            }
        }
    }

    public static enum Difficulty {
        EASY(10), NORMAL(20), HARD(30);

        private final int minePosibility;

        private Difficulty(int minePosibility) {
            this.minePosibility = minePosibility;
        }

        public int getMinePosibility() {
            return minePosibility;
        }

    }
}
