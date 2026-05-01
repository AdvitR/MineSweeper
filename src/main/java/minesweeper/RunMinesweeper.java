package minesweeper;

import javax.swing.*;
import java.io.File;

import static minesweeper.Minesweeper.SAVE_FILE;

public class RunMinesweeper implements Runnable {
    private final int gridSize;

    public RunMinesweeper(final int gridSize) {
        this.gridSize = gridSize;
    }

    public RunMinesweeper() {
        this(10);
    }
    public void run() {
        int option = JOptionPane.showConfirmDialog(null,
                "Do you want to load the previous game?",
                "Minesweeper", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            File saveFile = new File(SAVE_FILE);
            if (saveFile.exists()) {
                Minesweeper game = new Minesweeper(gridSize, true);
                game.loadGameState();
            } else {
                JOptionPane.showMessageDialog(null,
                        "No saved game found. Starting a new game.");
                new Minesweeper(gridSize, true);
            }
        } else {
            new Minesweeper(gridSize, true);
        }
    }
}
