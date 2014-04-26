package io.github.aritzhack.ld29;

import com.google.common.collect.Sets;
import io.github.aritzhack.aritzh.awt.render.IRender;

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
                    if (t.getType() == TileType.MINE) t.show();
                    this.level.getGame().gameOver();
                }
            }
        }
    }

    public void show() {
        this.setShowing(true);
    }

    public void setShowing(boolean isShowing) {
        if (isShowing && !this.isShowing && this.type == TileType.NORMAL) {
            this.isShowing = true;
            final Set<Tile> allConnected = Sets.newHashSet();
            this.getHiddenNeighbors().parallelStream().filter(t -> t.getType() == TileType.NORMAL).forEach(t -> allConnected.addAll(t.getHiddenNeighbors().stream().filter(t2 -> t2.getType() == TileType.NORMAL).collect(Collectors.toSet())));
            allConnected.stream().distinct().forEach(Tile::show);
            return;
        }
        this.isShowing = isShowing;
    }

    public Set<Tile> getHiddenNeighbors() {
        Set<Tile> set = Sets.newHashSet();

        // Orthogonals
        Tile mr = this.level.getTileAt(this.x + 1, this.y/**/);
        Tile ml = this.level.getTileAt(this.x - 1, this.y/**/);
        Tile mb = this.level.getTileAt(this.x/**/, this.y + 1);
        Tile mt = this.level.getTileAt(this.x/**/, this.y - 1);

        // Diagonals
        Tile lt = this.level.getTileAt(this.x - 1, this.y - 1);
        Tile rt = this.level.getTileAt(this.x + 1, this.y - 1);
        Tile lb = this.level.getTileAt(this.x - 1, this.y + 1);
        Tile rb = this.level.getTileAt(this.x + 1, this.y + 1);

        // Add always orthogonals
        set.add(mr);
        set.add(ml);
        set.add(mb);
        set.add(mt);

        // Diagonals only if the two nearby orthogonals are normal
        if (ml != null && ml.getType() == TileType.NORMAL && mt != null && mt.getType() == TileType.NORMAL) set.add(lt);
        if (ml != null && ml.getType() == TileType.NORMAL && mb != null && mb.getType() == TileType.NORMAL) set.add(lb);
        if (mr != null && mr.getType() == TileType.NORMAL && mt != null && mt.getType() == TileType.NORMAL) set.add(rt);
        if (mr != null && mr.getType() == TileType.NORMAL && mb != null && mb.getType() == TileType.NORMAL) set.add(rb);
        set.remove(null);
        return set;
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
