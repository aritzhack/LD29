package io.github.aritzhack.ld29.gui;

import io.github.aritzhack.aritzh.awt.gameEngine.input.InputHandler;
import io.github.aritzhack.aritzh.util.NotNull;
import io.github.aritzhack.aritzh.util.Nullable;
import io.github.aritzhack.ld29.Game;
import io.github.aritzhack.ld29.util.Util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Optional;

/**
 * @author Aritz Lopez
 */
public class Button {

    private static final Color COLOR_0 = new Color(196, 196, 196);
    private static final Color COLOR_1 = new Color(119, 119, 119);
    private static final Color COLOR_2 = new Color(63, 63, 63);

    private final Optional<String> text;
    private final Rectangle bounds;
    private final Optional<Runnable> clickHandler;
    private final int x, y, width, height;
    private boolean isPressed, isHovered;

    public Button(@Nullable String text, @NotNull Rectangle bounds, @Nullable Runnable clickHandler) {
        this.text = Optional.ofNullable(text);
        this.bounds = bounds;
        this.x = (int) bounds.getX();
        this.y = (int) bounds.getY();
        this.width = (int) bounds.getWidth();
        this.height = (int) bounds.getHeight();
        this.clickHandler = Optional.ofNullable(clickHandler);
    }

    public void update(Game game) {
        InputHandler ih = game.getGame().getInputHandler();
        if (this.isPressed &&
            ih.getMouseEvents()
                .stream()
                .filter(e -> e.getAction() == InputHandler.MouseAction.RELEASED)
                .filter(e -> this.bounds.contains(e.getPosition()))
                .anyMatch(e -> e.getButton() == InputHandler.MouseButton.LEFT)) {
            this.clickHandler.ifPresent(Runnable::run);
        }
        if (ih.getMouseEvents()
            .stream()
            .anyMatch(e -> e.getAction() == InputHandler.MouseAction.RELEASED)) {
            this.isPressed = false;
        } else if (!this.isPressed &&
            ih.getMouseEvents()
                .stream()
                .filter(e -> e.getAction() == InputHandler.MouseAction.PRESSED)
                .filter(e -> this.bounds.contains(e.getPosition()))
                .anyMatch(e -> e.getButton() == InputHandler.MouseButton.LEFT)) {
            this.isPressed = true;
        }
        this.isHovered = this.bounds.contains(ih.getMousePos());
    }

    public void render(Graphics g) {

        g.setColor(this.isPressed ? COLOR_2 : this.isHovered ? COLOR_0 : COLOR_1);
        g.fillRect(this.x, this.y, this.width, this.height);

        g.setColor(this.isPressed ? COLOR_0 : this.isHovered ? COLOR_1 : COLOR_2);
        g.drawRect(this.x, this.y, this.width, this.height);

        g.setColor(Color.black);
        g.fillRect(this.x, this.y, 1, 1);
        g.fillRect(this.x + this.width, this.y, 1, 1);
        g.fillRect(this.x, this.y + this.height, 1, 1);
        g.fillRect(this.x + this.width, this.y + this.height, 1, 1);

        g.setFont(Game.CONSOLAS_28);
        g.setColor(new Color(255, 255, 255));
        this.text.ifPresent(s -> Util.drawStringAligned(g, s, Util.HAlignment.CENTER, Util.VAlignment.CENTER, this.x + this.width / 2, this.y + this.height / 2, false, false, true));
    }

    public Optional<String> getText() {
        return text;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Optional<Runnable> getClickHandler() {
        return clickHandler;
    }

    @Override
    public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + clickHandler.hashCode();
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Button button = (Button) o;

        return height == button.height && width == button.width && x == button.x && y == button.y && clickHandler.equals(button.clickHandler) && text.equals(button.text);

    }
}
