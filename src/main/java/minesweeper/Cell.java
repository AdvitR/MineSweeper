package minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.List;
public class Cell extends JPanel {
    final int col;
    final int row;
    private int value;
    private boolean isRevealed = false;
    private final Minesweeper minesweeper;
    private JLabel label;
    private static final Color EXPOSED_COLOR = new Color(194, 178, 128); // Sand color
    private static final Color UNEXPOSED_COLOR = new Color(96, 128, 56); // Grass color
    // Temporary storage for neighbours of a cell to avoid declaring
    // new arrays every time a cell's neighbours are to be retrieved.
    private static Cell[] storeNeighbours = new Cell[8];
    private List<Cell> neighbors;

    public Cell(Minesweeper minesweeper, int row, final int col) {
        this.minesweeper = minesweeper;
        this.row = row;
        this.col = col;
        // Set layout to BorderLayout
        setLayout(new BorderLayout());
        // Create label with centered text
        label = new JLabel("", SwingConstants.CENTER);
        // Add label to the center of the panel
        add(label, BorderLayout.CENTER);
        // Set preferred size for the cell
        setPreferredSize(new Dimension(50, 50));
        setBorder(BorderFactory.createLineBorder(Color.black));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                minesweeper.handleCell(Cell.this);

            }

        });

    }
    private void setText(String text) {
        label.setText(text);
    }

    public void reset() {
        setValue(0);
        setEnabled(true);
        isRevealed = false;
        setText("");
        this.setBackground(UNEXPOSED_COLOR);
        label.setForeground(Color.BLACK);
        repaint();
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isAMine() {
        return value == Minesweeper.MINE;
    }

    public void countSurroundingMines() {
        for (Cell neighbour : this.getNeighbours()) {
            if (neighbour.isAMine()) {
                value++;
            }
        }
    }

    public int getValue() {
        return value;
    }

    public void reveal() {
        setEnabled(false);
        isRevealed = true;
        if (isAMine()) {
            this.setBackground(Color.RED);
            setText(""); // No text for mines
        } else {
            setText(value > 0 ? String.valueOf(value) : "");
            setColorBasedOnValue(value); // Set color based on the number
        }
        repaint();
    }

    private void setColorBasedOnValue(int value) {
        switch (value) {
            case 1:
                label.setForeground(Color.BLUE);
                break;
            case 2:
                label.setForeground(Color.GREEN);
                break;
            case 3:
                label.setForeground(Color.RED);
                break;
            default:
                label.setForeground(Color.BLACK); // Default color for other numbers
        }
    }

    public List<Cell> getNeighbours() {
        return neighbors;
    }

    public void setNeighbors(List<Cell> neighbors) {
        this.neighbors = neighbors;
    }

    public boolean isRevealed() {
        return isRevealed;
    }
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            isRevealed = true; // Disable the cell by revealing it
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Set the background color based on whether the cell is revealed
        if (isRevealed) {
            if (!isAMine()) {
                this.setBackground(EXPOSED_COLOR);
            }
        } else {
            this.setBackground(UNEXPOSED_COLOR);
        }

        // Ensure the label is repainted as well
        label.repaint();
    }

    // To ensure proper comparison of cells when adding them to a set.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Cell cell = (Cell) obj;
        return row == cell.row &&
                col == cell.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
