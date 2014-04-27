package io.github.aritzhack.ld29.level;

import com.google.common.collect.Sets;
import io.github.aritzhack.aritzh.awt.render.IRender;
import io.github.aritzhack.ld29.Game;

import java.awt.Rectangle;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.aritzhack.ld29.Game.SPRITE_SIZE;

/**
 * @author Aritz Lopez
 */
public class Tile {

    private final Level level;
    private final int x;
    private final int y;
    private final Rectangle bounds;
    private final TileType type;
    private boolean isShowing = false, isPressed = false, isFlagged = false;

    public Tile(Level level, int x, int y, TileType type) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.type = type;
        this.bounds = new Rectangle(this.x * SPRITE_SIZE, this.y * SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE);
    }

    public void render(IRender r) {
        String spriteName;
        spriteName = (!this.isShowing ? (isFlagged ? "flagged" : "normal") : ((this.getType() == TileType.NORMAL && this.getAmountOfNeighbotMines() != 0) ? Integer.toString(this.getAmountOfNeighbotMines()) : this.type.getSprite(isPressed)));

        r.draw(this.x * SPRITE_SIZE, Game.TOP_MARGIN + this.y * SPRITE_SIZE, spriteName);
    }

    private int getAmountOfNeighbotMines() {
        return (int) this.getNeighborTiles().stream().filter(t -> t.getType() == TileType.MINE).count();
    }

    public TileType getType() {
        return type;
    }

    public Set<Tile> getNeighborTiles() {
        Set<Tile> set = Sets.newHashSet();
        set.add(this.level.getTileAt(this.x + 1, this.y/**/));
        set.add(this.level.getTileAt(this.x - 1, this.y/**/));
        set.add(this.level.getTileAt(this.x/**/, this.y + 1));
        set.add(this.level.getTileAt(this.x/**/, this.y - 1));
        set.add(this.level.getTileAt(this.x - 1, this.y - 1));
        set.add(this.level.getTileAt(this.x + 1, this.y - 1));
        set.add(this.level.getTileAt(this.x - 1, this.y + 1));
        set.add(this.level.getTileAt(this.x + 1, this.y + 1));
        set.remove(null);
        return set;
    }

    public void update(Game game) {
        if (this.isShowing && this.getType() == TileType.MINE) {
            for (Tile[] tiles : this.level.getTiles()) {
                for (Tile t : tiles) {
                    if (t.getType() == TileType.MINE) t.setShowing(true, false);
                    this.level.getGame().gameOver();
                }
            }
        }
    }

    public void setShowing(boolean isShowing, boolean showOthers) {
        this.isShowing = isShowing;
        if (showOthers) this.getNeighborsToShow().forEach(Tile::pressed);
    }

    public Set<Tile> getNeighborsToShow() {
        Set<Tile> set = Sets.newHashSet();

        // 3x3: Orthogonals
        set.add(this.level.getTileAt(this.x + 1, this.y/**/));
        set.add(this.level.getTileAt(this.x - 1, this.y/**/));
        set.add(this.level.getTileAt(this.x/**/, this.y + 1));
        set.add(this.level.getTileAt(this.x/**/, this.y - 1));

        // 3x3: Diagonals
        set.add(this.level.getTileAt(this.x - 1, this.y - 1));
        set.add(this.level.getTileAt(this.x + 1, this.y - 1));
        set.add(this.level.getTileAt(this.x - 1, this.y + 1));
        set.add(this.level.getTileAt(this.x + 1, this.y + 1));
        set.add(this.level.getTileAt(this.x + 1, this.y + 1));

        // 5x5: Top
        set.add(this.level.getTileAt(this.x - 2, this.y - 2));
        set.add(this.level.getTileAt(this.x - 1, this.y - 2));
        set.add(this.level.getTileAt(this.x/**/, this.y - 2));
        set.add(this.level.getTileAt(this.x + 1, this.y - 2));
        set.add(this.level.getTileAt(this.x + 2, this.y - 2));

        // 5x5: Bottom
        set.add(this.level.getTileAt(this.x - 2, this.y + 2));
        set.add(this.level.getTileAt(this.x - 1, this.y + 2));
        set.add(this.level.getTileAt(this.x/**/, this.y + 2));
        set.add(this.level.getTileAt(this.x + 1, this.y + 2));
        set.add(this.level.getTileAt(this.x + 2, this.y + 2));

        // 5x5: Left
        set.add(this.level.getTileAt(this.x - 2, this.y - 1));
        set.add(this.level.getTileAt(this.x - 2, this.y/**/));
        set.add(this.level.getTileAt(this.x - 2, this.y + 1));

        // 5x5: Right
        set.add(this.level.getTileAt(this.x + 2, this.y - 1));
        set.add(this.level.getTileAt(this.x + 2, this.y/**/));
        set.add(this.level.getTileAt(this.x + 2, this.y + 1));

        set.remove(null);
        return set.stream().filter(t -> !t.isShowing).filter(t -> t.getType() == TileType.NORMAL).collect(Collectors.toSet());
    }

    public void press() {
        this.setShowing(true, true);
        if (Level.RAND.nextInt(10) > 6) this.level.spawnEnemy();
    }

    public void pressed() {
        this.setShowing(true, false);
        if (Level.RAND.nextInt(10) > 3) this.level.spawnEnemy();
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;

        return isPressed == tile.isPressed && isShowing == tile.isShowing && x == tile.x && y == tile.y && type == tile.type;

    }

    public void toggleFlag() {
        this.isFlagged = !this.isFlagged;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public static enum TileType {
        MINE("mine"), NORMAL("empty");

        private final String sprite;

        TileType(String sprite) {
            this.sprite = sprite;
        }

        public String getSprite(boolean isPressed) {
            return this.sprite + (isPressed ? "_pressed" : "");
        }
    }
}
