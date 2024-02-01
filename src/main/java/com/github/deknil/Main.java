package com.github.deknil;

import javax.swing.*;
import java.awt.*;

/**
 * The Main class represents the entry point of the Sand Simulation program.
 * It creates a graphical user interface for simulating sand movement and interactions.
 */
public class Main {
    private static final int CELL_SIZE = 8; // Size of each cell in pixels
    private static final int MATRIX_SIZE = 32; // Size of the grid matrix
    private static final int LOGIC_UPDATE_TIME = 30; // Time interval for logic updates in milliseconds
    private static final Color CELL_ACTIVE = Color.ORANGE; // Color for active cells
    private static final Color CLEAR_COLOR = Color.WHITE; // Color for clear cells
    private static final Color[][] CELL = new Color[MATRIX_SIZE][MATRIX_SIZE]; // Matrix to represent cells
    private static final int totalCells = MATRIX_SIZE * MATRIX_SIZE; // Total number of cells
    private static int countEmptyCells = 0; // Count of empty cells
    private static int countFilledCells = 0; // Count of filled cells
    private static JSlider angleSlider; // Slider for adjusting angle
    private static Canvas canvas; // Canvas for drawing
    private static JLabel angleInfoLabel; // Label for displaying angle information
    private static JLabel cellCountLabel; // Label for displaying total cell count
    private static JLabel emptyCellLabel; // Label for displaying empty cell count
    private static JLabel filledCellLabel; // Label for displaying filled cell count

    /**
     * The main method initializes the graphical user interface, timer, and scene,
     * and starts the simulation.
     * @param args Command-line arguments (not used in this program).
     */
    public static void main(String[] args) {
        // Create the main window
        JFrame window = new JFrame("Sand Simulation");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(512, 512);
        window.setLocationRelativeTo(null);

        // Create and configure the angle slider
        angleSlider = new JSlider(-360, 360, 0);
        angleSlider.setMajorTickSpacing(180);
        angleSlider.setMinorTickSpacing(10);
        angleSlider.setPaintTicks(true);
        angleSlider.setPaintLabels(true);
        angleSlider.addChangeListener(e -> {
            canvas.repaint();
            updateInfoPanel();
        });

        // Create buttons for adding and removing sand
        JButton addButton = new JButton("Add sand");
        JButton removeButton = new JButton("Remove sand");
        addButton.addActionListener(e -> addSand());
        removeButton.addActionListener(e -> removeSand());

        // Create labels and panels for information and controls
        JLabel sliderLabel = new JLabel("Angle:");
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        angleInfoLabel = new JLabel("Angle: ");
        cellCountLabel = new JLabel("Cell Count: ");
        emptyCellLabel = new JLabel("Empty Cells: ");
        filledCellLabel = new JLabel("Filled Cells: ");
        infoPanel.add(angleInfoLabel);
        infoPanel.add(cellCountLabel);
        infoPanel.add(emptyCellLabel);
        infoPanel.add(filledCellLabel);

        JPanel sliderPanel = new JPanel(new FlowLayout());
        sliderPanel.add(addButton);
        sliderPanel.add(removeButton);
        sliderPanel.add(sliderLabel);
        sliderPanel.add(angleSlider);

        // Add panels to the main window
        window.getContentPane().add(infoPanel, BorderLayout.EAST);
        window.getContentPane().add(sliderPanel, BorderLayout.SOUTH);

        // Create the canvas for drawing and add it to the main window
        canvas = new Canvas();
        canvas.setDrawLogic(g -> render(g, angleSlider.getValue()));
        window.getContentPane().add(canvas, BorderLayout.CENTER);

        // Make the main window visible
        window.setVisible(true);

        // Create and start the logic update timer
        Timer timer = new Timer(LOGIC_UPDATE_TIME, e -> {
            updateLogic();
            canvas.repaint();
            updateInfoPanel();
        });
        timer.start();

        // Load the initial scene
        loadScene();
    }

    /**
     * Load the initial scene with sand and a figure.
     */
    private static void loadScene() {
        int centerX = MATRIX_SIZE / 2;
        int centerY = MATRIX_SIZE / 2;

        // Sand
        for (int x = centerX - 5; x < centerX + 5; x++) {
            for (int y = 0; y < centerY; y++) {
                CELL[x][y] = CELL_ACTIVE;
            }
        }

        // Figure
        int figureSize = MATRIX_SIZE;

        for (int x = 0; x < centerX; x++) {
            for (int y = x - centerX + figureSize / 2; y < centerY; y++) {
                if (isValidCellPosition(x, y)) {
                    CELL[x][y] = Color.RED;
                }
            }
        }

        for (int x = centerX + figureSize / 2; x > centerX; x--) {
            for (int y = centerX + figureSize / 2 - x; y < centerY; y++) {
                if (isValidCellPosition(x, y)) {
                    CELL[x][y] = Color.RED;
                }
            }
        }
    }

    /**
     * Update the information panel with current values.
     */
    private static void updateInfoPanel() {
        angleInfoLabel.setText("Angle: " + angleSlider.getValue() + " deg.");
        cellCountLabel.setText("Cell Count: " + totalCells);
        emptyCellLabel.setText("Empty Cells: " + countEmptyCells);
        filledCellLabel.setText("Filled Cells: " + countFilledCells);
    }

    /**
     * Remove sand from the first active cell found.
     */
    private static void removeSand() {
        canvas.repaint();
        updateInfoPanel();

        for (int y = 0; y < MATRIX_SIZE; y++) {
            for (int x = 0; x < MATRIX_SIZE; x++) {
                if (isCellActive(x, y)) {
                    CELL[x][y] = CLEAR_COLOR;
                    return;
                }
            }
        }
    }

    /**
     * Add sand to the grid based on the current angle.
     */
    private static void addSand() {
        canvas.repaint();
        updateInfoPanel();

        double angleRad = Math.toRadians(angleSlider.getValue());
        int centerX = MATRIX_SIZE / 2;
        int centerY = MATRIX_SIZE / 2;

        int newPosX = centerX + (int) (Math.sin(angleRad) * 3);
        int newPosY = centerY - (int) (Math.cos(angleRad) * 3);

        if (isValidCellPosition(newPosX, newPosY) && isCellEmpty(newPosX, newPosY)) {
            CELL[newPosX][newPosY] = CELL_ACTIVE;
        }
    }

    /**
     * Check if a cell at the specified coordinates is active.
     * @param x The x-coordinate of the cell.
     * @param y The y-coordinate of the cell.
     * @return true if the cell is active, false otherwise.
     */
    private static boolean isCellActive(int x, int y) {
        return CELL[x][y] == CELL_ACTIVE;
    }

    /**
     * Check if a cell at the specified coordinates is empty.
     * @param x The x-coordinate of the cell.
     * @param y The y-coordinate of the cell.
     * @return true if the cell is empty, false otherwise.
     */
    private static boolean isCellEmpty(int x, int y) {
        return CELL[x][y] == CLEAR_COLOR || CELL[x][y] == null;
    }

    /**
     * Check if the specified coordinates are within the valid cell positions.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return true if the coordinates are valid, false otherwise.
     */
    private static boolean isValidCellPosition(int x, int y) {
        return x >= 0 && x < MATRIX_SIZE && y >= 0 && y < MATRIX_SIZE;
    }

    /**
     * Update the logic of the sand simulation, including sand movement and counts.
     */
    private static void updateLogic() {
        countFilledCells = 0;

        boolean[][] tempGrid = new boolean[MATRIX_SIZE][MATRIX_SIZE];

        for (int y = MATRIX_SIZE - 1; y >= 0; y--) {
            for (int x = MATRIX_SIZE - 1; x >= 0; x--) {
                if (isCellActive(x, y)) {
                    countFilledCells++;

                    double angleRad = Math.toRadians(angleSlider.getValue());
                    int dx = (int) Math.signum(Math.sin(angleRad));
                    int dy = (int) Math.signum(Math.cos(angleRad));

                    int newPosX = x + dx;
                    int newPosY = y + dy;

                    boolean moved = false;

                    if(tempGrid[x][y]) continue;

                    if (isValidCellPosition(newPosX, newPosY) && isCellEmpty(newPosX, newPosY)) {
                        tempGrid[newPosX][newPosY] = true;
                        moveCell(x, y, newPosX, newPosY);
                        moved = true;
                    }

                    if (!moved && isValidCellPosition(newPosX, y) && isCellEmpty(newPosX, y)) {
                        tempGrid[newPosX][y] = true;
                        moveCell(x, y, newPosX, y);
                        moved = true;
                    }

                    if (!moved && isValidCellPosition(x, newPosY) && isCellEmpty(x, newPosY)) {
                        tempGrid[x][newPosY] = true;
                        moveCell(x, y, x, newPosY);
                    }
                }
            }
        }

        countEmptyCells = totalCells - countFilledCells;
    }

    /**
     * Move a sand cell from its current position to a new position.
     * @param x    The current x-coordinate of the cell.
     * @param y    The current y-coordinate of the cell.
     * @param newX The new x-coordinate to move the cell to.
     * @param newY The new y-coordinate to move the cell to.
     */
    private static void moveCell(int x, int y, int newX, int newY) {
        CELL[x][y] = CLEAR_COLOR;
        CELL[newX][newY] = CELL_ACTIVE;
    }

    /**
     * Render the grid and sand cells based on the current angle.
     * @param g     The Graphics object for drawing.
     * @param angle The current angle for rendering.
     */
    private static void render(Graphics g, int angle) {
        int cornerStartX = 5;
        int cornerStartY = 5;
        int cornerEndX = g.getClipBounds().width - 10;
        int cornerEndY = g.getClipBounds().height - 10;

        int centerX = (cornerStartX + cornerEndX) / 2;
        int centerY = (cornerStartY + cornerEndY) / 2;

        // Border
        g.drawRect(cornerStartX, cornerStartY, cornerEndX - cornerStartX, cornerEndY - cornerStartY);

        // Center
        g.fillOval(centerX - 2, centerY - 2, 4, 4);

        // Convert angle to radians
        double angleRad = Math.toRadians(angle);

        drawGrid(g, centerX, centerY, angleRad);
    }

    /**
     * Draw the grid and sand cells on the canvas.
     * @param g         The Graphics object for drawing.
     * @param centerX   The x-coordinate of the center.
     * @param centerY   The y-coordinate of the center.
     * @param angleRad  The angle in radians for rendering.
     */
    private static void drawGrid(Graphics g, int centerX, int centerY, double angleRad) {
        // Calculate the total grid size
        int gridWidth = MATRIX_SIZE * CELL_SIZE;
        int gridHeight = MATRIX_SIZE * CELL_SIZE;

        // Calculate the starting point so the grid will be centered
        int startGridX = centerX - gridWidth / 2;
        int startGridY = centerY - gridHeight / 2;

        // Draw grid using lines for each cell
        for (int row = 0; row < MATRIX_SIZE; row++) {
            for (int col = 0; col < MATRIX_SIZE; col++) {
                int cellStartX = startGridX + col * CELL_SIZE;
                int cellStartY = startGridY + row * CELL_SIZE;
                int cellEndX = cellStartX + CELL_SIZE;
                int cellEndY = cellStartY + CELL_SIZE;

                if (CELL[col][row] == null) CELL[col][row] = CLEAR_COLOR;

                // Rotate each corner point
                Point p1 = getRotatedPoint(cellStartX, cellStartY, centerX, centerY, angleRad);
                Point p2 = getRotatedPoint(cellEndX, cellStartY, centerX, centerY, angleRad);
                Point p3 = getRotatedPoint(cellStartX, cellEndY, centerX, centerY, angleRad);
                Point p4 = getRotatedPoint(cellEndX, cellEndY, centerX, centerY, angleRad);

                drawCell(g, row, col, p1, p2, p3, p4);
            }
        }
    }

    /**
     * Draw a sand cell at the specified coordinates with rotation.
     *
     * @param g    The Graphics object for drawing.
     * @param row  The row index of the cell.
     * @param col  The column index of the cell.
     * @param p1   The rotated start point.
     * @param p2   The rotated end point.
     * @param p3   The rotated start point.
     * @param p4   The rotated end point.
     */
    private static void drawCell(Graphics g, int row, int col, Point p1, Point p2, Point p3, Point p4) {
        // Fill cell
        Polygon polygon = new Polygon();
        polygon.addPoint(p1.x, p1.y);
        polygon.addPoint(p2.x, p2.y);
        polygon.addPoint(p4.x, p4.y);
        polygon.addPoint(p3.x, p3.y);

        g.setColor(CELL[col][row]);
        g.fillPolygon(polygon);

        // Border
        g.setColor(new Color(0, 0, 0, 50));
        g.drawLine(p1.x, p1.y, p2.x, p2.y); // Up
        g.drawLine(p2.x, p2.y, p4.x, p4.y); // Right
        g.drawLine(p3.x, p3.y, p4.x, p4.y); // Down
        g.drawLine(p1.x, p1.y, p3.x, p3.y); // Left
    }

    /**
     * Rotate a point around the center point with a specified angle.
     * @param x        The x-coordinate of the point.
     * @param y        The y-coordinate of the point.
     * @param centerX  The x-coordinate of the center point.
     * @param centerY  The y-coordinate of the center point.
     * @param angleRad The angle in radians for rotation.
     * @return The rotated point.
     */
    private static Point getRotatedPoint(int x, int y, int centerX, int centerY, double angleRad) {
        int xRotated = (int) ((x - centerX) * Math.cos(angleRad) - (y - centerY) * Math.sin(angleRad) + centerX);
        int yRotated = (int) ((x - centerX) * Math.sin(angleRad) + (y - centerY) * Math.cos(angleRad) + centerY);
        return new Point(xRotated, yRotated);
    }
}
