package minesweeper;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;


public class Minesweeper {
    // The value assigned to cells marked as mines.
    // 10 is an arbitrary suitable choice as no cell will have > 8 neighbouring mines.
    public static final int MINE = 10;
    //The size in pixels for the frame
    private static final int SIZE = 500;
    // The number of mines at generated is the grid size * this constant
    static final double MINE_MULTIPLIER = 1.5;
    private static Cell[][] cells;
    private JFrame frame;
    private JLabel timerLabel;
    private JButton restart;
    private JButton quit;
    private JButton saveGame;
    final int gridSize;
    boolean hasWon = false; // Used for testing
    static final String SAVE_FILE = "minesweeper_save.txt";
    private boolean initializeGUI;
    private Timer timer;
    private int timeElapsed;
    private int secondsElapsed;


    /**
     * Constructor sets up game state.
     */
    public Minesweeper(int gridSize, boolean initializeGUI) {
        this.initializeGUI = initializeGUI;
        this.gridSize = gridSize;
        cells = new Cell[gridSize][gridSize];

        if (initializeGUI) {
            frame = new JFrame("Minesweeper");
            frame.setSize(SIZE, SIZE);
            frame.setLayout(new BorderLayout());

            initializeButtonPanel();
            initializeBoard();

            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        } else {
            initializeBoard();
        }
    }

    private void initializeButtonPanel() {
        JPanel buttonPanel = new JPanel();

        restart = new JButton("Restart");
        quit = new JButton("End Game");
        saveGame = new JButton("Save Game");
        JButton instructionsButton = new JButton("Instructions");

        restart.addActionListener(e -> resetGame());
        quit.addActionListener(e -> revealBoard("You Gave Up!"));
        saveGame.addActionListener(e -> saveGameState());
        instructionsButton.addActionListener(e -> showInstructions());

        buttonPanel.add(restart);
        buttonPanel.add(quit);
        buttonPanel.add(saveGame);
        buttonPanel.add(instructionsButton);

        timerLabel = new JLabel("Time: 0");
        buttonPanel.add(timerLabel);
        frame.add(buttonPanel, BorderLayout.SOUTH);

    }

    private void initializeBoard() {
        if (initializeGUI) {
            initializeTimer();
        }
        JPanel gridPanel = new JPanel();
        if (initializeGUI) {
            gridPanel.setLayout(new GridLayout(gridSize, gridSize));
        }

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col] = new Cell(this, row, col);
                if (initializeGUI) {
                    gridPanel.add(cells[row][col]);
                }
            }
        }

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                List<Cell> neighbors = findNeighbors(row, col);
                cells[row][col].setNeighbors(neighbors);
            }
        }
        createMines();
        if (initializeGUI) {
            frame.add(gridPanel, BorderLayout.CENTER);
        }
    }

    private List<Cell> findNeighbors(int row, int col) {
        List<Cell> neighbours = new ArrayList<>();
        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int colOffset = -1; colOffset <= 1; colOffset++) {
                // Skip the cell itself
                if (rowOffset == 0 && colOffset == 0) {
                    continue;
                }

                int neighborRow = row + rowOffset;
                int neighborCol = col + colOffset;

                // Check bounds and add valid neighbors
                if (neighborRow >= 0 && neighborRow < gridSize &&
                        neighborCol >= 0 && neighborCol < gridSize) {
                    neighbours.add(cells[neighborRow][neighborCol]);
                }
            }
        }

        return neighbours;
    }

    void resetAllCells() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col].reset();
            }
        }
    }

    void createMines() {
        resetAllCells();

        final int numMines = (int) (MINE_MULTIPLIER * gridSize);
        final Random random = new Random();

        // Set to keep track of occupied positions
        Set<String> occupiedPositions = new HashSet<>();

        int minesPlaced = 0;
        while (minesPlaced < numMines) {
            int row = random.nextInt(gridSize);
            int col = random.nextInt(gridSize);

            String pos = row + "," + col;
            if (!occupiedPositions.contains(pos)) {
                cells[row][col].setValue(MINE);
                occupiedPositions.add(pos);
                minesPlaced++;
            }
        }

        // Initialize neighbour counts
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (!cells[row][col].isAMine()) {
                    cells[row][col].countSurroundingMines();
                }
            }
        }
    }
    public void handleCell(Cell cell) {
        System.out.println("Clicked Cell: Row " + cell.row + ", Col "
                + cell.col + ", Value " + cell.getValue());

        if (cell.isAMine()) {
            revealBoard("You clicked on a mine!");
            return;
        }

        if (cell.getValue() == 0) {
            revealAdjacent(cell, new HashSet<>());
        } else {
            cell.reveal();
        }

        checkIfWon();
    }

    private void revealAdjacent(Cell cell, Set<Cell> revealed) {
        // Base case: If the cell is already revealed or marked in the set, return.
        if (revealed.contains(cell)) {
            return;
        }

        // Reveal the cell and mark it in the set.
        cell.reveal();
        revealed.add(cell);

        // If the cell's value is 0, recursively reveal its neighbors.
        if (cell.getValue() == 0) {
            for (Cell neighbour : cell.getNeighbours()) {
                revealAdjacent(neighbour, revealed);
            }
        }
    }

    private void revealBoard(String message) {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col].reveal();
                cells[row][col].setEnabled(false);
            }
        }
        showEndGameDialog("Game Over!");
    }

    void checkIfWon() {
        boolean win = true;
        outer:
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (!cell.isRevealed() && !cell.isAMine()) {
                    win = false;
                    break outer;
                }
            }
        }

        if (win) {
            hasWon = true;
            showEndGameDialog("You Win!");
        }

    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    // Method for testing: manually place mines
    public void placeMines(Set<Point> mineLocations) {
        resetAllCells(); // Reset the board before placing mines

        for (Point location : mineLocations) {
            if (location.x >= 0 && location.x < gridSize &&
                    location.y >= 0 && location.y < gridSize) {
                cells[location.x][location.y].setValue(MINE);
            }
        }

        // Initialize neighbour counts
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (!cells[row][col].isAMine()) {
                    cells[row][col].countSurroundingMines();
                }
            }
        }
    }

    public void saveGameState() {
        try (PrintWriter out = new PrintWriter(new FileWriter(SAVE_FILE))) {
            out.println(gridSize);
            for (int row = 0; row < gridSize; row++) {
                for (int col = 0; col < gridSize; col++) {
                    Cell cell = cells[row][col];
                    out.println(cell.row + " " + cell.col + " " + cell.isAMine() +
                            " " + cell.getValue() + " " + cell.isRevealed());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGameState() {
        try (Scanner in = new Scanner(new File(SAVE_FILE))) {
            int savedGridSize = Integer.parseInt(in.nextLine());
            if (savedGridSize != gridSize) {
                JOptionPane.showMessageDialog(null,
                        "Saved game grid size does not match. Starting a new game.");
                return;
            }

            while (in.hasNextLine()) {
                String line = in.nextLine();
                String[] parts = line.split(" ");
                int savedRow = Integer.parseInt(parts[0]);
                int savedCol = Integer.parseInt(parts[1]);
                boolean isMine = Boolean.parseBoolean(parts[2]);
                int value = Integer.parseInt(parts[3]);
                boolean isRevealed = Boolean.parseBoolean(parts[4]);

                System.out.println("Reading: " + line); // Debugging

                Cell cell = cells[savedRow][savedCol];
                if (isMine) {
                    cell.setValue(MINE);
                } else {
                    cell.setValue(value);
                }
                if (isRevealed) {
                    cell.reveal(); // Ensure this method only changes visual state
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void showEndGameDialog(String message) {
        if (initializeGUI) {
            JDialog dialog = new JDialog(frame, "Game Over", true); // true for modal
            dialog.setLayout(new FlowLayout());
            timer.stop();
            JLabel label = new JLabel(message);
            JButton playAgainButton = new JButton("Play Again");
            JButton exitButton = new JButton("Exit");

            playAgainButton.addActionListener(e -> {
                resetGame();
                dialog.dispose(); // Close the dialog
            });

            exitButton.addActionListener(e -> {
                dialog.dispose(); // Close the dialog
                System.exit(0); // Terminate the program
            });

            dialog.add(label);
            dialog.add(playAgainButton);
            dialog.add(exitButton);
            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
        }
    }

    private void resetGame() {
        resetAllCells();
        createMines();
        resetTimer();
        timer.start();
        frame.revalidate();
        frame.repaint();
    }

    private void showInstructions() {
        String instructions = "Minesweeper Instructions:\n\n" +
                "1. Click to uncover cells.\n\n" +
                "2. Numbers show nearby mines.\n\n" +
                "3. Avoid mines, win by uncovering all safe cells.\n\n" +
                "4. Use logic to find safe moves.\n\n" +
                "5. Reset: Start over.\n\n" +
                "6. End Game: Check for a win.\n\n" +
                "7. Save Game: Save your progress.\n\n" +
                "8. Instructions: View these instructions again.";

        JOptionPane.showMessageDialog(frame, instructions,
                "Instructions", JOptionPane.INFORMATION_MESSAGE);
    }
    private void initializeTimer() {
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                secondsElapsed++;
                updateTimerDisplay(); // Update the display each second
            }
        });
        timer.start(); // Start the timer
    }
    private void updateTimerDisplay() {
        if (timerLabel != null) {
            timerLabel.setText("Time: " + secondsElapsed); // Update timer label
        }
    }
    private void resetTimer() {
        secondsElapsed = 0; // Reset the elapsed seconds
        updateTimerDisplay(); // Update the timer display immediately
    }

}
