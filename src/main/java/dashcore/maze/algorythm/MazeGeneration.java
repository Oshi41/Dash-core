package dashcore.maze.algorythm;

import dashcore.util.PositionUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This program generates mazes and solves them using Breadth-first Search and
 * Depth-first Search algorithms
 * <p>
 * https://github.com/nattwasm/maze
 *
 * @author Nhat Nguyen
 * @author Jasmine Mai
 */
public class MazeGeneration {
    private static String clearPath = " ";
    private static String mazePath = "#";
    private static String cross = "+";
    private static String column = "|";
    private static String row = "-";
    private static List<EnumFacing> enumFacings = Arrays
            .stream(EnumFacing.values()).filter(x -> x.getAxis() != EnumFacing.Axis.Y)
            .collect(Collectors.toList());

    /**
     * Generates squeare maze with path from top left to bottom right
     *
     * @param start - start of the maze
     * @param size  - size of maze
     * @return
     */
    public static RoomInfo[][] generate(ChunkPos start, int size) {
        int mazeSize = size / 2;

        String[][] topLeft = generate(mazeSize, Rotation.NONE);
        String[][] topRight = generate(mazeSize, Rotation.CLOCKWISE_90);
        String[][] bottomRight = generate(mazeSize, Rotation.CLOCKWISE_180);
        String[][] bottomLeft = generate(mazeSize, Rotation.COUNTERCLOCKWISE_90);

        String[][] mainScheme = new String[size * 2 + 1][size * 2 + 1];

        for (int i = 0, iMax = topLeft.length; i < iMax; i++) {
            String[] row = ArrayUtils.addAll(topLeft[i], Arrays.stream(topRight[i]).skip(1).toArray(String[]::new));
            mainScheme[i] = row;

            if (i != 0) {
                String[] row2 = ArrayUtils.addAll(bottomLeft[i], Arrays.stream(bottomRight[i]).skip(1).toArray(String[]::new));
                mainScheme[i + iMax - 1] = row2;
            }
        }

        RoomInfo[][] result = new RoomInfo[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // current cell position
                final BlockPos current = new BlockPos(1 + i * 2, 0, 1 + j * 2);
                // absolute chunk position for cell
                final ChunkPos roomPos = new ChunkPos(start.x + i, start.z + j);

                // is on path to center
                boolean isPath = mainScheme[current.getX()][current.getZ()] == mazePath;

                boolean isBoss = isPath
                        && (i == mazeSize || i + 1 == mazeSize)
                        && (j == mazeSize || j + 1 == mazeSize);

                // list of entries
                Set<EnumFacing> facings = enumFacings
                        .stream()
                        .filter(x -> {
                            BlockPos offset = current.offset(x);
                            // check wherever there is no wall
                            return mainScheme[offset.getX()][offset.getZ()] == clearPath;
                        })
                        .collect(Collectors.toSet());

                result[i][j] = new RoomInfo(roomPos, facings, isPath, isBoss);
            }
        }

        System.out.println(start);
        System.out.println(convert2D(mainScheme));

        return result;
    }

    /**
     * Creating maze scheme with rotation.
     * Rotation.NON - top left corner is start, bottom right is end
     *
     * @param size     - size of maze
     * @param rotation - rotation for maze scheme
     * @return
     */
    private static String[][] generate(int size, Rotation rotation) {
        String[][] emptyMaze = maze2D(size);
        String[][] generatedMaze = emptyHash(generator(emptyMaze));

        // Creates an single path of the maze
        String[][] solvedMaze = backtrackingDelete(generatedMaze);
        emptyHash(solvedMaze);
        hashList(solvedMaze);

        solvedMaze = PositionUtil.rotate2DGrid(solvedMaze, rotation);

        if (rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90) {
            for (int i = 0; i < solvedMaze.length; i++) {
                for (int j = 0; j < solvedMaze[i].length; j++) {
                    String path = solvedMaze[i][j];
                    if (path == column) {
                        path = row;
                    } else if (path == row) {
                        path = column;
                    }

                    solvedMaze[i][j] = path;
                }
            }
        }

        return solvedMaze;
    }

    /**
     * Converts 2D array maze to the string representation
     *
     * @param maze2D The maze that will be convert to string representation
     * @return maze The string representation of the maze
     */
    private static String convert2D(String[][] maze2D) {
        String maze = "";
        int size = maze2D.length;
        for (int columnIndex = 0; columnIndex < size; columnIndex++) {
            for (int rowIndex = 0; rowIndex < size; rowIndex++) {
                if (maze2D[columnIndex][rowIndex] == cross) {
                    maze = maze + cross;
                } else if (maze2D[columnIndex][rowIndex] == row) {
                    maze = maze + "---";
                } else if (maze2D[columnIndex][rowIndex] == column) {
                    maze = maze + column;
                } else if (maze2D[columnIndex][rowIndex] == mazePath && columnIndex % 2 == 1) {
                    // Hash symbol and column is odd
                    if (rowIndex % 2 == 0) {
                        maze = maze + mazePath;
                    } else if (rowIndex % 2 == 1) {
                        maze = maze + " " + mazePath + " ";
                    }
                } else if (maze2D[columnIndex][rowIndex] == mazePath && columnIndex % 2 == 0) {
                    // Hash symbol and column is even
                    maze = maze + " " + mazePath + " ";
                } else if (maze2D[columnIndex][rowIndex] == "S" || maze2D[columnIndex][rowIndex] == "E") {
                    maze = maze + "   ";
                } else if (maze2D[columnIndex][rowIndex] == clearPath && columnIndex % 2 == 1 && rowIndex % 2 == 0) {
                    // Spacing for the wall
                    maze = maze + clearPath;
                } else if (maze2D[columnIndex][rowIndex] == clearPath) {
                    // Spacing for the cell
                    maze = maze + "   ";
                } else {
                    maze = maze + clearPath + maze2D[columnIndex][rowIndex] + clearPath;
                }

                // When rowIndex is at end AND columnIndex is not at end, add a new line
                if (rowIndex == (size - 1) && columnIndex != (size - 1)) {
                    maze = maze + System.lineSeparator();
                }
            }
        }
        return maze;
    }

    /**
     * Creates an empty non-generated maze
     *
     * @param size The size of the maze
     * @return maze2D The empty non-generated maze
     */
    private static String[][] maze2D(int size) {
        String[][] maze2D = new String[2 * size + 1][2 * size + 1];
        // 2D Array
        for (int columnIndex = 0; columnIndex < (2 * size + 1); columnIndex++) {
            for (int rowIndex = 0; rowIndex < (2 * size + 1); rowIndex++) {
                // Start of maze
                if (rowIndex == 1 && columnIndex == 0) {
                    maze2D[columnIndex][rowIndex] = "S";
                    // End of maze
                } else if (rowIndex == 2 * size - 1 && columnIndex == 2 * size) {
                    maze2D[columnIndex][rowIndex] = "E";
                    // Row is even
                } else if (rowIndex % 2 == 0) {
                    // Column is even
                    if (columnIndex % 2 == 0) {
                        maze2D[columnIndex][rowIndex] = cross;
                        // Column is odd
                    } else {
                        maze2D[columnIndex][rowIndex] = column;
                    }
                    // Row is odd
                } else {
                    // Column is even
                    if (columnIndex % 2 == 0) {
                        maze2D[columnIndex][rowIndex] = row;
                        // Column is odd
                    } else {
                        maze2D[columnIndex][rowIndex] = "0";
                    }
                }
            }
        }

        return maze2D;
    }

    /**
     * Creates a valid generated maze that has a path from the begining to end
     *
     * @param maze2D The empty non-generated maze
     * @return maze2D The empty generated maze
     */
    private static String[][] generator(String[][] maze2D) {
        Stack<Cell> location = new Stack<Cell>();
        int size = (maze2D.length - 1) / 2;
        int totalCells = size * size;
        int visitedCells = 1;
        Cell current = new Cell(0, 0);

        while (visitedCells < totalCells) {

            // Generates a unique direction
            ArrayList<String> direction = new ArrayList<>();
            Collections.addAll(direction, "NORTH", "EAST", "SOUTH", "WEST");
            Collections.shuffle(direction);

            String random = validSpot(maze2D, current, direction);

            if (random == "BACKTRACK") {
                // // DEBUGGING: Prints BACKTRACKING
                // System.out.println("\t PROCESSS: " + random);

                current = location.pop();
                continue;
            }

            current = move(maze2D, current, random);
            visitedCells = visitedCells + 1;
            location.push(current);
        }

        return maze2D;
    }

    /**
     * The valid spot returns all the valid spot given a cell location
     *
     * @param maze2D    The maze from maze2D method
     * @param current   The current located cell
     * @param direction The list of directions
     * @return random The valid random direction
     */
    private static String validSpot(String[][] maze2D, Cell current, ArrayList<String> direction) {
        int size = (maze2D.length - 1) / 2;

        int x = 2 * current.getx() + 1;
        int y = 2 * current.gety() + 1;

        // When the size of the list is 0, return -1
        if (direction.size() == 0) {
            return "BACKTRACK";
        }

        String random = direction.remove(0);

        // // DEBUGGING: Prints current direction
        // System.out.println("DIRECTION: " + random);

        if (random == "NORTH") {
            if (current.gety() - 1 < 0) {
                // System.out.println("Do not go NORTH because outside of range of the 2D
                // array");
                return validSpot(maze2D, current, direction);
            }
            if ((maze2D[y - 3][x] == mazePath || maze2D[y - 1][x] == mazePath)
                    || (maze2D[y - 2][x - 1] == mazePath || maze2D[y - 2][x + 1] == mazePath)) {
                // System.out.println("Do not go NORTH because that cell is not enclosed by
                // walls");
                return validSpot(maze2D, current, direction);
            }
        } else if (random == "EAST") {
            if (current.getx() + 1 >= size) {
                // System.out.println("Do not go EAST because outside of range of the 2D
                // array");
                return validSpot(maze2D, current, direction);
            }
            if (((maze2D[y + 1][x + 2] == mazePath || maze2D[y - 1][x + 2] == mazePath)
                    || (maze2D[y][x + 1] == mazePath || maze2D[y][x + 3] == mazePath))) {
                // System.out.println("Do not go EAST because that cell is not enclosed by
                // walls");
                return validSpot(maze2D, current, direction);
            }
        } else if (random == "SOUTH") {
            if (current.gety() + 1 >= size) {
                // System.out.println("Do not go SOUTH because outside of range of the 2D
                // array");
                return validSpot(maze2D, current, direction);
            }
            if (((maze2D[y + 1][x] == mazePath || maze2D[y + 3][x] == mazePath)
                    || (maze2D[y + 2][x - 1] == mazePath || maze2D[y + 2][x + 1] == mazePath))) {
                // System.out.println("Do not go SOUTH because that cell is not enclosed by
                // walls");
                return validSpot(maze2D, current, direction);
            }
        } else if (random == "WEST") {
            if (current.getx() - 1 < 0) {
                // System.out.println("Do not go WEST because outside of range of the 2D
                // array");
                return validSpot(maze2D, current, direction);
            }
            if (((maze2D[y - 1][x - 2] == mazePath || maze2D[y + 1][x - 2] == mazePath)
                    || (maze2D[y][x - 3] == mazePath || maze2D[y][x - 1] == mazePath))) {
                // System.out.println("Do not go WEST because that cell is not enclosed by
                // walls");
                return validSpot(maze2D, current, direction);
            }
        }
        return random;
    }

    /**
     * Move the next cell and break the wall in between
     *
     * @param maze2D  The maze from the maze2D
     * @param current The current located cell
     * @param random  The valid random direction
     * @return current The new current cell
     */
    private static Cell move(String[][] maze2D, Cell current, String random) {

        // // Prints out the coordinates of the current cell object
        // System.out.println(" X-coordinate: " + current.getx() + ", Y-coordinate: " +
        // current.gety());

        maze2D[1][1] = mazePath;

        if (random == "NORTH") {
            // NORTH and delete wall from bottom from next cell
            current.setNext(new Cell(current.getx(), current.gety() - 1));
            current = current.getNext();
            // Breaks the bottom wall from next cell
            maze2D[2 * current.gety() + 2][2 * current.getx() + 1] = mazePath;

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = mazePath;
        } else if (random == "EAST") {
            // EAST and delete wall from left from next cell
            current.setNext(new Cell(current.getx() + 1, current.gety()));
            current = current.getNext();
            // Breaks the left wall from next cell
            maze2D[2 * current.gety() + 1][2 * current.getx()] = mazePath;

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = mazePath;
        } else if (random == "SOUTH") {
            // SOUTH and delete wall from top from next cell
            current.setNext(new Cell(current.getx(), current.gety() + 1));
            current = current.getNext();
            // Breaks the top wall from next cell
            maze2D[2 * current.gety()][2 * current.getx() + 1] = mazePath;

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = mazePath;
        } else if (random == "WEST") {
            // WEST and delete wall from right from next cell
            current.setNext(new Cell(current.getx() - 1, current.gety()));
            current = current.getNext();
            // Breaks the right wall from next cell
            maze2D[2 * current.gety() + 1][2 * current.getx() + 2] = mazePath;

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = mazePath;
        }

        // // DEBUGGING: Printing maze at each step
        // System.out.println("NEW X-coordinate: " + current.getx() + ", NEW
        // Y-coordinate: " + current.gety());
        // for (String[] row : maze2D) {
        // System.out.println(Arrays.toString(row));
        // }
        // System.out.println();

        return current;
    }

    /**
     * Delete all the hash symbols in the maze
     *
     * @param maze2D The maze with hash symbols
     * @return maze2D The maze with deleted hash symbols
     */
    private static String[][] emptyHash(String[][] maze2D) {
        int size = maze2D.length;
        for (int columnIndex = 0; columnIndex < size; columnIndex++) {
            for (int rowIndex = 0; rowIndex < size; rowIndex++) {
                if (maze2D[columnIndex][rowIndex] == mazePath) {
                    maze2D[columnIndex][rowIndex] = clearPath;
                }
            }

        }
        return maze2D;
    }

    /**
     * Checks if direction is valid in DFS
     *
     * @param maze2D    The maze from DFS method
     * @param current   The current located cell
     * @param direction The list of directions
     * @return random The valid random direction
     */
    private static String DFSValid(String[][] maze2D, Cell current, ArrayList<String> direction) {
        int size = (maze2D.length - 1) / 2;
        int x = 2 * current.getx() + 1;
        int y = 2 * current.gety() + 1;

        // When the size of the list is 0, return "BACKTRACK"
        if (direction.size() == 0) {
            // maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = clearPath;
            return "BACKTRACK";
        }

        String random = direction.remove(0);

        if (random == "NORTH") {
            if (current.gety() - 1 < 0) {
                // System.out.println("Do not go NORTH because outside of range of the 2D
                // array");
                return DFSValid(maze2D, current, direction);
            }
            if (maze2D[y - 1][x] != clearPath) {
                // System.out.println("Do not go NORTH because there is a wall");
                return DFSValid(maze2D, current, direction);
            }
        } else if (random == "EAST") {
            if (current.getx() + 1 >= size) {
                // System.out.println("Do not go EAST because outside of range of the 2D
                // array");
                return DFSValid(maze2D, current, direction);
            }
            if (maze2D[y][x + 1] != clearPath) {
                // System.out.println("Do not go EAST because there is a wall");
                return DFSValid(maze2D, current, direction);
            }
        } else if (random == "SOUTH") {
            if (current.gety() + 1 >= size) {
                // System.out.println("Do not go SOUTH because outside of range of the 2D
                // array");
                return DFSValid(maze2D, current, direction);
            }
            if (maze2D[y + 1][x] != clearPath) {
                // System.out.println("Do not go SOUTH because there is a wall");
                return DFSValid(maze2D, current, direction);
            }
        } else if (random == "WEST") {
            if (current.getx() - 1 < 0) {
                // System.out.println("Do not go WEST because outside of range of the 2D
                // array");
                return DFSValid(maze2D, current, direction);
            }
            if (maze2D[y][x - 1] != clearPath) {
                // System.out.println("Do not go WEST because there is a wall");
                return DFSValid(maze2D, current, direction);
            }
        }
        return random;
    }

    /**
     * Move to the direction given in DFS
     *
     * @param maze2D  The maze from DFS method
     * @param current The current located cell
     * @param random  The valid random direction
     * @param count   The number presented in each cell
     * @return current The new current cell
     */
    private static Cell DFSMove(String[][] maze2D, Cell current, String random, int count) {

        String path = Integer.toString(count % 10);

        if (random == "NORTH") {
            // NORTH and delete wall from bottom from next cell
            current.setNext(new Cell(current.getx(), current.gety() - 1));
            current = current.getNext();
            // Breaks the bottom wall from next cell
            maze2D[2 * current.gety() + 2][2 * current.getx() + 1] = mazePath;

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = path;
        } else if (random == "EAST") {
            // EAST and delete wall from left from next cell
            current.setNext(new Cell(current.getx() + 1, current.gety()));
            current = current.getNext();
            // Breaks the left wall from next cell
            maze2D[2 * current.gety() + 1][2 * current.getx()] = mazePath;

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = path;
        } else if (random == "SOUTH") {
            // SOUTH and delete wall from top from next cell
            current.setNext(new Cell(current.getx(), current.gety() + 1));
            current = current.getNext();
            // Breaks the top wall from next cell
            maze2D[2 * current.gety()][2 * current.getx() + 1] = mazePath;

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = path;
        } else if (random == "WEST") {
            // WEST and delete wall from right from next cell
            current.setNext(new Cell(current.getx() - 1, current.gety()));
            current = current.getNext();
            // Breaks the right wall from next cell
            maze2D[2 * current.gety() + 1][2 * current.getx() + 2] = mazePath;

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = path;
        }

        // DEBUGGING: Printing maze at each step
        // System.out.println("NEW X-coordinate: " + current.getx() + ", NEW
        // Y-coordinate: " + current.gety());
        // print2D(maze2D);
        // System.out.println();

        return current;

    }

    /**
     * Deletes the numbers from the bracktracking
     *
     * @param maze2D The DFS or BFS maze
     * @return maze2D The maze with a single path
     */
    private static String[][] backtrackingDelete(String[][] maze2D) {
        Stack<Cell> location = new Stack<Cell>();
        int size = (maze2D.length - 1) / 2;
        int totalCells = size * size;
        int visitedCells = 1;
        Cell current = new Cell(0, 0);
        maze2D[1][1] = "0";

        while (visitedCells < totalCells) {
            // Generates a unique direction
            ArrayList<String> direction = new ArrayList<>();
            Collections.addAll(direction, "NORTH", "EAST", "SOUTH", "WEST");
            Collections.shuffle(direction);

            // Finds a valid spot on the 2D array
            String random = DFSValid(maze2D, current, direction);
            // System.out.println("The FINAL DIRECTION: " + random);

            if (random == "BACKTRACK") {
                maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = clearPath;
                current = location.pop();
                continue;
            }

            current = DFSMove(maze2D, current, random, visitedCells);
            visitedCells = visitedCells + 1;
            location.push(current);

            if (current.getx() == size - 1 && current.gety() == size - 1) {
                return maze2D;
            }
        }
        return maze2D;
    }

    /**
     * Delete every other number not on path and convert path to hash
     *
     * @param maze2D The maze2D from after process through backtrackingDelete method
     * @return path The list the cell locations
     */
    private static ArrayList<Cell> hashList(String[][] maze2D) {
        int size = (maze2D.length - 1) / 2;
        ArrayList<Cell> path = new ArrayList<>();
        Cell current = new Cell(0, 0);
        path.add(current);

        while (current.getx() != size - 1 || current.gety() != size - 1) {

            // NORTH
            if (current.gety() - 1 < 0) {
                // Do Nothing
            } else if (maze2D[2 * current.gety()][2 * current.getx() + 1] == clearPath
                    && maze2D[2 * current.gety() - 1][2 * current.getx() + 1] != clearPath
                    && maze2D[2 * current.gety() - 1][2 * current.getx() + 1] != mazePath) {

                path.add(new Cell(current.getx(), current.gety() - 1));
                current.setNext(new Cell(current.getx(), current.gety() - 1));

            }

            // EAST
            if (current.getx() + 1 >= size) {
                // Do Nothing
            } else if (maze2D[2 * current.gety() + 1][2 * current.getx() + 2] == clearPath
                    && maze2D[2 * current.gety() + 1][2 * current.getx() + 3] != clearPath
                    && maze2D[2 * current.gety() + 1][2 * current.getx() + 3] != mazePath) {

                path.add(new Cell(current.getx() + 1, current.gety()));
                current.setNext(new Cell(current.getx() + 1, current.gety()));
            }

            // SOUTH
            if (current.gety() + 1 >= size) {
                // Do Nothing
            } else if (maze2D[2 * current.gety() + 2][2 * current.getx() + 1] == clearPath
                    && maze2D[2 * current.gety() + 3][2 * current.getx() + 1] != clearPath
                    && maze2D[2 * current.gety() + 3][2 * current.getx() + 1] != mazePath) {

                path.add(new Cell(current.getx(), current.gety() + 1));
                current.setNext(new Cell(current.getx(), current.gety() + 1));
            }

            // WEST
            if (current.getx() - 1 < 0) {
                // Do Nothing
            } else if (maze2D[2 * current.gety() + 1][2 * current.getx()] == clearPath
                    && maze2D[2 * current.gety() + 1][2 * current.getx() - 1] != clearPath
                    && maze2D[2 * current.gety() + 1][2 * current.getx() - 1] != mazePath) {

                path.add(new Cell(current.getx() - 1, current.gety()));
                current.setNext(new Cell(current.getx() - 1, current.gety()));
            }

            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = mazePath;
            current = current.getNext();
        }

        maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = mazePath;

        // Deletes all the extra numbers
        for (int columnIndex = 0; columnIndex < maze2D.length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < maze2D.length; rowIndex++) {
                if (!(maze2D[columnIndex][rowIndex] == cross || maze2D[columnIndex][rowIndex] == row
                        || maze2D[columnIndex][rowIndex] == column || maze2D[columnIndex][rowIndex] == mazePath)) {
                    maze2D[columnIndex][rowIndex] = clearPath;
                }
            }
        }

        return path;
    }

    static public class Cell {
        private Cell node;
        private int x;
        private int y;

        /**
         * Constructor has the location of each Cell
         *
         * @param x The x-coordinate
         * @param y The y-coordinate
         */
        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Gets the x-coordinate
         *
         * @return x The x-coordinate
         */
        public int getx() {
            return x;
        }

        /**
         * Gets the y-coordinate
         *
         * @return y The y-coordinate
         */
        public int gety() {
            return y;
        }

        /**
         * Gets the next node
         *
         * @return node The next node
         */
        public Cell getNext() {
            return node;
        }

        /**
         * Sets the next node
         *
         * @param node The next node being set
         */
        public void setNext(Cell node) {
            this.node = node;
        }

        /**
         * String Representation of the Cell class
         */
        public String toString() {
            return "[" + x + ":" + y + "]";
        }
    }

}