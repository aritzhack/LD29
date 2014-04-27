package io.github.aritzhack.ld29;

import javax.swing.JApplet;
import java.awt.HeadlessException;

/**
 * @author Aritz Lopez
 */
public class MainApplet extends JApplet {

    private final Game game;

    public MainApplet() throws HeadlessException {
        super();
        game = new Game(800, 592, true);
        this.add(game.getGame());
    }

    @Override
    public void start() {
        super.start();
        game.start();
    }
}
