package minesweeper;

import org.junit.jupiter.api.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * You can use this file (and others) to test your
 * implementation.
 */

public class MinesweeperTest {
    private Minesweeper game;

    @BeforeEach
    void setUp() {
        game = new Minesweeper(10, false);
    }

    @Test
    public void testCreateMines() {
        int mineCount = 0;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (game.getCell(row, col).isAMine()) {
                    mineCount++;
                }
            }
        }
        assertEquals(15, mineCount);
    }

    @Test
    public void testRevealAdjacent() {
        // Set up a known configuration of mines
        Set<Point> mineLocations = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            mineLocations.add(new Point(0, i));
        }
        game.placeMines(mineLocations);

        Cell cell = game.getCell(2, 2);
        game.handleCell(cell);
        assertTrue(cell.isRevealed());
        for (int col = 0; col < 10; col++) {
            assertFalse(game.getCell(0, col).isRevealed());
        }
        for (int row = 1; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                assertTrue(game.getCell(row, col).isRevealed());
            }
        }
    }

    @Test
    public void testWinConditionTrue() {
        // Set up a known configuration of mines
        Set<Point> mineLocations = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            mineLocations.add(new Point(0, i));
        }
        game.placeMines(mineLocations);

        // Reveal all cells except the mines
        Cell cell = game.getCell(2, 2);
        game.handleCell(cell);

        // Check if the game is won
        assertTrue(game.hasWon);
    }
    @Test
    public void testWinConditionFalse() {
        // Set up a known configuration of mines
        Set<Point> mineLocations = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            mineLocations.add(new Point(0, i));
        }
        game.placeMines(mineLocations);

        // Reveal all cells except the mines
        Cell cell = game.getCell(1, 1);
        game.handleCell(cell);

        // Check if the game is won
        assertFalse(game.hasWon);
    }

    @Test
    void testResetAllCells() {
        // Simulating some gameplay
        Set<Point> mineLocations = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            mineLocations.add(new Point(0, i));
        }
        game.placeMines(mineLocations);

        // Reveal all cells except the mines
        Cell cell = game.getCell(2, 2);
        game.handleCell(cell);

        // Reset the board
        game.resetAllCells();

        // Check each cell to ensure it's in the initial state
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Cell gcell = game.getCell(row, col);
                assertFalse(gcell.isRevealed(), "Cell should not be revealed after reset");
                assertFalse(gcell.isAMine(), "Cell should not be a mine after reset");
                assertEquals(0, gcell.getValue(),
                        "Cell value should be reset to 0");
            }
        }
    }

    @Test
    void testEdgeCornerCellReveal() {
        Set<Point> mineLocations = new HashSet<>();
        // Place a mine in a way that affects corner/edge cells
        mineLocations.add(new Point(1, 1)); // Affects the top-left corner cell
        game.placeMines(mineLocations);
        Cell cornerCell = game.getCell(0, 0);
        game.handleCell(cornerCell); // Revealing the top-left corner cell

        // Check if the corner cell is revealed correctly
        assertTrue(cornerCell.isRevealed());
        assertEquals(1, cornerCell.getValue());
    }

    @Test
    void testEdgeCellReveal() {
        Set<Point> mineLocations = new HashSet<>();
        // Place a mine in a way that affects corner/edge cells
        mineLocations.add(new Point(1, 1)); // Affects the top-left corner cell
        game.placeMines(mineLocations);
        Cell edgeCell = game.getCell(0, 2); // Top edge cell
        game.handleCell(edgeCell); // Revealing the edge cell

        // Check if the edge cell is revealed correctly
        assertTrue(edgeCell.isRevealed());
        assertEquals(1, edgeCell.getValue()); // Assuming it has 1 adjacent mine
    }

    @Test
    void testSaveGameState() {
        game.createMines();

        game.saveGameState();

        File saveFile = new File(Minesweeper.SAVE_FILE);
        assertTrue(saveFile.exists() && saveFile.length() > 0,
                "Game state should be saved to file.");
    }

    private void createSavedFile(Set<Point> mineLocations) {
        try (PrintWriter out = new PrintWriter(Minesweeper.SAVE_FILE)) {
            out.println(game.gridSize); // Write the grid size
            for (int row = 0; row < game.gridSize; row++) {
                for (int col = 0; col < game.gridSize; col++) {
                    boolean isMine = mineLocations.contains(new Point(row, col));
                    // Write the cell state, e.g., "row col isMine value isRevealed"
                    out.println(row + " " + col + " " + isMine + " " + (isMine ? 10 : 0) +
                            " " + false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testLoadGameState() {
        Set<Point> knownMineLocations = new HashSet<>();
        // Add known mine locations
        knownMineLocations.add(new Point(0, 0));
        knownMineLocations.add(new Point(1, 1));

        createSavedFile(knownMineLocations);

        game.loadGameState();

        boolean stateMatches = true;
        for (Point mineLocation : knownMineLocations) {
            Cell cell = game.getCell(mineLocation.x, mineLocation.y);
            if (!cell.isAMine()) {
                stateMatches = false;
                break;
            }
        }
        assertTrue(stateMatches,
                "Loaded game state should match the known configuration of mines.");
    }

}

