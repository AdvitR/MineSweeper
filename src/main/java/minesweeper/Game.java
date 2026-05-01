package minesweeper;

import javax.swing.*;

public class Game {
    /**
     * Starts the Minesweeper Swing application.
     */
    public static void main(String[] args) {
        Runnable game = new RunMinesweeper();

        SwingUtilities.invokeLater(game);
    }

}
