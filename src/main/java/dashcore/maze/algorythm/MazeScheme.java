package dashcore.maze.algorythm;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import dashcore.util.PositionUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.*;
import java.util.stream.Collectors;

public class MazeScheme {
    private static String clearPath = " ";
    private static String mazePath = "#";
    private static String cross = "+";
    private static String column = "|";
    private static String row = "-";
    private static String start = "S";
    private static String end = "E";

    private final Random random;

    /**
     * Size of the whole maze
     */
    private final int mazeSize;

    private String[][] maze2D;
    private RoomInfo[][] rooms;

    /**
     * @param size   even number
     * @param random random for generation
     */
    public MazeScheme(int size, Random random) {
        this.random = random;

        // fixing size
        if (size % 2 == 1) {
            size++;
        }
        if (size < 4) {
            size = 4;
        }

        this.mazeSize = size;

        // avoiding null ref exceptions

        maze2D = new String[1][1];
        rooms = new RoomInfo[1][1];
    }

    static RoomInfo[][] convert(String[][] maze, ChunkPos start) {
        int mazeSize = (maze.length - 1) / 2;
        RoomInfo[][] rooms = new RoomInfo[mazeSize][mazeSize];

        List<EnumFacing> horizontalFacings = Arrays
                .stream(EnumFacing.values()).filter(x -> x.getAxis() != EnumFacing.Axis.Y)
                .collect(Collectors.toList());

        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                // current cell position
                final BlockPos current = new BlockPos(1 + i * 2, 0, 1 + j * 2);
                // absolute chunk position for cell
                final ChunkPos roomPos = new ChunkPos(start.x + i, start.z + j);

                // is on path to center
                boolean isPath = maze[current.getX()][current.getZ()].equals(mazePath);

                boolean isBoss = isPath
                        && (i == mazeSize - 2 || i == mazeSize - 1)
                        && (j == mazeSize - 2 || j == mazeSize - 1);

                // list of entries
                Set<EnumFacing> facings = horizontalFacings
                        .stream()
                        .filter(x -> {
                            BlockPos offset = current.offset(x);

                            // check bounds
                            if (offset.getX() < 0 || offset.getZ() < 0)
                                return false;

                            if (offset.getX() >= maze.length || offset.getZ() >= maze.length)
                                return false;

                            // check wherever there is no wall
                            return maze[offset.getX()][offset.getZ()].equals(clearPath);
                        })
                        .collect(Collectors.toSet());

                rooms[i][j] = new RoomInfo(roomPos, facings, isPath, isBoss);
            }
        }

        return rooms;
    }

    /**
     * Generates 4 possible directions randomly shuffled.
     *
     * @param random
     * @return
     */
    static List<String> generateRandomDirections(Random random) {
        List<String> directions = Lists.newArrayList("NORTH", "EAST", "SOUTH", "WEST");
        Collections.shuffle(directions, random);
        return directions;
    }

    /**
     * Generates simple maze
     *
     * @param rand     - rand for generation
     * @param rotation - possible rotation of maze
     * @param partSize - size of maze
     * @return
     */
    @VisibleForTesting
    static String[][] generatePart(Random rand, Rotation rotation, int partSize) {

        //
        // Creates new matrix
        //
        String[][] maze2D = new String[2 * partSize + 1][2 * partSize + 1];
        // 2D Array
        for (int columnIndex = 0; columnIndex < (2 * partSize + 1); columnIndex++) {
            for (int rowIndex = 0; rowIndex < (2 * partSize + 1); rowIndex++) {
                // Start of maze
                if (rowIndex == 1 && columnIndex == 0) {
                    maze2D[columnIndex][rowIndex] = start;
                    // End of maze
                } else if (rowIndex == 2 * partSize - 1 && columnIndex == 2 * partSize) {
                    maze2D[columnIndex][rowIndex] = end;
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

        //
        // End of creating matrix
        //

        //
        // Generating maze
        //
        Stack<Cell> location = new Stack<>();
        int size = (maze2D.length - 1) / 2;
        int totalCells = size * size;
        int visitedCells = 1;
        Cell current = new Cell(0, 0);

        while (visitedCells < totalCells) {

            // Generates a unique direction
            List<String> direction = generateRandomDirections(rand);
            String random = validSpot(maze2D, current, direction);

            if (random.equals("BACKTRACK")) {
                // // DEBUGGING: Prints BACKTRACKING
                // System.out.println("\t PROCESSS: " + rand);

                current = location.pop();
                continue;
            }

            current = move(maze2D, current, random);
            visitedCells = visitedCells + 1;
            location.push(current);
        }

        //
        // End of matrix generation
        //

        // removing hash symbols
        removeHashSymbol(maze2D, false);

        //
        // Solving matrix
        //

        location = new Stack<>();
        int mazeSize = (maze2D.length - 1) / 2;
        totalCells = mazeSize * mazeSize;
        visitedCells = 1;
        current = new Cell(0, 0);
        // adding first cell as initial path
        location.push(current);
        maze2D[1][1] = "0";

        while (visitedCells < totalCells) {
            // Generates a unique direction
            List<String> direction = generateRandomDirections(rand);

            // Finds a valid spot on the 2D array
            String random = DFSValid(maze2D, current, direction);
            // System.out.println("The FINAL DIRECTION: " + rand);

            if (random.equals("BACKTRACK")) {
                maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = clearPath;
                current = location.pop();
                continue;
            }

            current = DFSMove(maze2D, current, random, visitedCells);
            visitedCells = visitedCells + 1;
            location.push(current);

            if (current.getx() == mazeSize - 1 && current.gety() == mazeSize - 1) {
                break;
            }
        }

        //
        // end of solving
        //

        // removing all but spaces
        removeHashSymbol(maze2D, true);

        // indicated maze path by hash symbol
        while (!location.isEmpty()) {
            Cell cell = location.pop();

            while (cell != null) {
                maze2D[2 * cell.gety() + 1][2 * cell.getx() + 1] = mazePath;
                cell = cell.getNext();
            }
        }

        // performing final rotation
        return rotate(maze2D, rotation);
    }

    /**
     * Perform maze rotation
     *
     * @param rotation
     * @return
     */
    @VisibleForTesting
    static String[][] rotate(String[][] maze2D, Rotation rotation) {
        maze2D = PositionUtil.rotate2DGrid(maze2D, rotation);

        if (rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90) {
            for (int i = 0; i < maze2D.length; i++) {
                for (int j = 0; j < maze2D[i].length; j++) {
                    String path = maze2D[i][j];
                    if (path.equals(column)) {
                        path = row;
                    } else if (path.equals(row)) {
                        path = column;
                    }

                    maze2D[i][j] = path;
                }
            }
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
    static String validSpot(String[][] maze2D, Cell current, List<String> direction) {
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

        if (random.equals("NORTH")) {
            if (current.gety() - 1 < 0) {
                // System.out.println("Do not go NORTH because outside of range of the 2D
                // array");
                return validSpot(maze2D, current, direction);
            }
            if ((maze2D[y - 3][x].equals(mazePath) || maze2D[y - 1][x].equals(mazePath))
                    || (maze2D[y - 2][x - 1].equals(mazePath) || maze2D[y - 2][x + 1].equals(mazePath))) {
                // System.out.println("Do not go NORTH because that cell is not enclosed by
                // walls");
                return validSpot(maze2D, current, direction);
            }
        } else if (random.equals("EAST")) {
            if (current.getx() + 1 >= size) {
                // System.out.println("Do not go EAST because outside of range of the 2D
                // array");
                return validSpot(maze2D, current, direction);
            }
            if (((maze2D[y + 1][x + 2].equals(mazePath) || maze2D[y - 1][x + 2].equals(mazePath))
                    || (maze2D[y][x + 1].equals(mazePath) || maze2D[y][x + 3].equals(mazePath)))) {
                // System.out.println("Do not go EAST because that cell is not enclosed by
                // walls");
                return validSpot(maze2D, current, direction);
            }
        } else if (random.equals("SOUTH")) {
            if (current.gety() + 1 >= size) {
                // System.out.println("Do not go SOUTH because outside of range of the 2D
                // array");
                return validSpot(maze2D, current, direction);
            }
            if (((maze2D[y + 1][x].equals(mazePath) || maze2D[y + 3][x].equals(mazePath))
                    || (maze2D[y + 2][x - 1].equals(mazePath) || maze2D[y + 2][x + 1].equals(mazePath)))) {
                // System.out.println("Do not go SOUTH because that cell is not enclosed by
                // walls");
                return validSpot(maze2D, current, direction);
            }
        } else if (random.equals("WEST")) {
            if (current.getx() - 1 < 0) {
                // System.out.println("Do not go WEST because outside of range of the 2D
                // array");
                return validSpot(maze2D, current, direction);
            }
            if (((maze2D[y - 1][x - 2].equals(mazePath) || maze2D[y + 1][x - 2].equals(mazePath))
                    || (maze2D[y][x - 3].equals(mazePath) || maze2D[y][x - 1].equals(mazePath)))) {
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
    static Cell move(String[][] maze2D, Cell current, String random) {

        // // Prints out the coordinates of the current cell object
        // System.out.println(" X-coordinate: " + current.getx() + ", Y-coordinate: " +
        // current.gety());

        maze2D[1][1] = mazePath;

        if (random.equals("NORTH")) {
            // NORTH and delete wall from bottom from next cell
            current.setNext(new Cell(current.getx(), current.gety() - 1));
            current = current.getNext();
            // Breaks the bottom wall from next cell
            maze2D[2 * current.gety() + 2][2 * current.getx() + 1] = mazePath;

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = mazePath;
        } else if (random.equals("EAST")) {
            // EAST and delete wall from left from next cell
            current.setNext(new Cell(current.getx() + 1, current.gety()));
            current = current.getNext();
            // Breaks the left wall from next cell
            maze2D[2 * current.gety() + 1][2 * current.getx()] = mazePath;

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = mazePath;
        } else if (random.equals("SOUTH")) {
            // SOUTH and delete wall from top from next cell
            current.setNext(new Cell(current.getx(), current.gety() + 1));
            current = current.getNext();
            // Breaks the top wall from next cell
            maze2D[2 * current.gety()][2 * current.getx() + 1] = mazePath;

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = mazePath;
        } else if (random.equals("WEST")) {
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
     * Replacing symbols to space
     *
     * @param maze          - maze (2D array)
     * @param includeDigits - should replace digits too
     */
    static void removeHashSymbol(String[][] maze, boolean includeDigits) {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                String current = maze[i][j];

                if (current.equals(mazePath)
                        // maze room is single char string
                        || (includeDigits && Character.isDigit(current.charAt(0)))) {
                    maze[i][j] = clearPath;
                }
            }
        }
    }

    /**
     * Checks if direction is valid in DFS
     *
     * @param maze2D    The maze from DFS method
     * @param current   The current located cell
     * @param direction The list of directions
     * @return random The valid random direction
     */
    static String DFSValid(String[][] maze2D, Cell current, List<String> direction) {
        int size = (maze2D.length - 1) / 2;
        int x = 2 * current.getx() + 1;
        int y = 2 * current.gety() + 1;

        // When the size of the list is 0, return "BACKTRACK"
        if (direction.size() == 0) {
            // maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = clearPath;
            return "BACKTRACK";
        }

        String random = direction.remove(0);

        if (random.equals("NORTH")) {
            if (current.gety() - 1 < 0) {
                // System.out.println("Do not go NORTH because outside of range of the 2D
                // array");
                return DFSValid(maze2D, current, direction);
            }
            if (!maze2D[y - 1][x].equals(clearPath)) {
                // System.out.println("Do not go NORTH because there is a wall");
                return DFSValid(maze2D, current, direction);
            }
        } else if (random.equals("EAST")) {
            if (current.getx() + 1 >= size) {
                // System.out.println("Do not go EAST because outside of range of the 2D
                // array");
                return DFSValid(maze2D, current, direction);
            }
            if (!maze2D[y][x + 1].equals(clearPath)) {
                // System.out.println("Do not go EAST because there is a wall");
                return DFSValid(maze2D, current, direction);
            }
        } else if (random.equals("SOUTH")) {
            if (current.gety() + 1 >= size) {
                // System.out.println("Do not go SOUTH because outside of range of the 2D
                // array");
                return DFSValid(maze2D, current, direction);
            }
            if (!maze2D[y + 1][x].equals(clearPath)) {
                // System.out.println("Do not go SOUTH because there is a wall");
                return DFSValid(maze2D, current, direction);
            }
        } else if (random.equals("WEST")) {
            if (current.getx() - 1 < 0) {
                // System.out.println("Do not go WEST because outside of range of the 2D
                // array");
                return DFSValid(maze2D, current, direction);
            }
            if (!maze2D[y][x - 1].equals(clearPath)) {
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
    static Cell DFSMove(String[][] maze2D, Cell current, String random, int count) {

        String path = Integer.toString(count % 10);

        if (random.equals("NORTH")) {
            // NORTH and delete wall from bottom from next cell
            current.setNext(new Cell(current.getx(), current.gety() - 1));
            current = current.getNext();
            // Breaks the bottom wall from next cell
            maze2D[2 * current.gety() + 2][2 * current.getx() + 1] = mazePath;

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = path;
        } else if (random.equals("EAST")) {
            // EAST and delete wall from left from next cell
            current.setNext(new Cell(current.getx() + 1, current.gety()));
            current = current.getNext();
            // Breaks the left wall from next cell
            maze2D[2 * current.gety() + 1][2 * current.getx()] = mazePath;

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = path;
        } else if (random.equals("SOUTH")) {
            // SOUTH and delete wall from top from next cell
            current.setNext(new Cell(current.getx(), current.gety() + 1));
            current = current.getNext();
            // Breaks the top wall from next cell
            maze2D[2 * current.gety()][2 * current.getx() + 1] = mazePath;

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = path;
        } else if (random.equals("WEST")) {
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

    static String toString(String[][] maze2D) {
        String maze = "";
        int size = maze2D.length;
        for (int columnIndex = 0; columnIndex < size; columnIndex++) {
            for (int rowIndex = 0; rowIndex < size; rowIndex++) {
                if (maze2D[columnIndex][rowIndex].equals(cross)) {
                    maze = maze + cross;
                } else if (maze2D[columnIndex][rowIndex].equals(row)) {
                    maze = maze + "---";
                } else if (maze2D[columnIndex][rowIndex].equals(column)) {
                    maze = maze + column;
                } else if (maze2D[columnIndex][rowIndex].equals(mazePath) && columnIndex % 2 == 1) {
                    // Hash symbol and column is odd
                    if (rowIndex % 2 == 0) {
                        maze = maze + mazePath;
                    } else if (rowIndex % 2 == 1) {
                        maze = maze + " " + mazePath + " ";
                    }
                } else if (maze2D[columnIndex][rowIndex].equals(mazePath) && columnIndex % 2 == 0) {
                    // Hash symbol and column is even
                    maze = maze + " " + mazePath + " ";
                } else if (maze2D[columnIndex][rowIndex].equals(start) || maze2D[columnIndex][rowIndex].equals(end)) {
                    String path = clearPath;
                    if (columnIndex % 2 == 0) {
                        path = "   ";
                    }
                    maze = maze + path;
                } else if (maze2D[columnIndex][rowIndex].equals(clearPath) && columnIndex % 2 == 1 && rowIndex % 2 == 0) {
                    // Spacing for the wall
                    maze = maze + clearPath;
                } else if (maze2D[columnIndex][rowIndex].equals(clearPath)) {
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
        return maze + "\n";
    }

    /**
     * Size of maze
     *
     * @return
     */
    public int getMazeSize() {
        return mazeSize;
    }

    /**
     * Return generated rooms
     *
     * @return
     */
    public RoomInfo[][] getRooms() {
        return rooms;
    }

    /**
     * Generates random solved maze
     *
     * @return
     */
    public MazeScheme generate(ChunkPos start) {
//        String[][] topLeft = generatePart(random, Rotation.NONE, mazeSize / 2);
//        String[][] topRight = generatePart(random, Rotation.CLOCKWISE_90, mazeSize / 2);
//        String[][] bottomRight = generatePart(random, Rotation.CLOCKWISE_180, mazeSize / 2);
//        String[][] bottomLeft = generatePart(random, Rotation.COUNTERCLOCKWISE_90, mazeSize / 2);

        maze2D = generatePart(random, Rotation.NONE, mazeSize);

//        for (int i = 0; i < topLeft.length; i++) {
//            for (int j = 0; j < topLeft.length; j++) {
//                maze2D[i][j] = topLeft[i][j];
//                maze2D[i][j + mazeSize] = bottomLeft[i][j];
//                maze2D[i + mazeSize][j] = topRight[i][j];
//                maze2D[i + mazeSize][j + mazeSize] = bottomRight[i][j];
//            }
//        }

        rooms = convert(maze2D, start);
        return this;
    }

    /**
     * Performing maze rotation
     *
     * @param rotation
     * @return
     */
    public MazeScheme rotate(Rotation rotation) {
        maze2D = rotate(maze2D, rotation);
        ChunkPos start = new ChunkPos(0, 0);

        if (rooms.length > 0 && rooms[0] != null) {
            start = rooms[0][0].getChunkPos();
        }

        rooms = convert(maze2D, start);
        return this;
    }

    @Override
    public String toString() {
        return toString(maze2D);
    }

    static class Cell {
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
