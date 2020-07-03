package dashcore.maze;

import dashcore.util.PositionUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This program generates mazes and solves them using Breadth-first Search and
 * Depth-first Search algorithms
 *
 * @author Nhat Nguyen
 * @author Jasmine Mai
 */
public class MazeGeneration {

    /**
     * Generates squeare maze with path from top left to bottom right
     *
     * @param start - start of the maze
     * @param size  - size of maze
     * @return
     */
    public static Map<ChunkPos, RoomInfo> generate(ChunkPos start, int size) {
        String[][] maze2D = maze2D(size);
        String[][] generatedMaze = emptyHash(generator(maze2D));

        // Creates an single path of the maze
        String[][] mazePath = backtrackingDelete(generatedMaze);
        emptyHash(mazePath);
        hashList(mazePath);


        List<RoomInfo> infos = new ArrayList<>();

        return null;
    }

    public static void main(String[] args) {
        String[][] maze2D = maze2D(16);
        String[][] generatedMaze = emptyHash(generator(maze2D));

        // Creates an single path of the maze
        String[][] mazePath = backtrackingDelete(generatedMaze);
        emptyHash(mazePath);
        hashList(mazePath);

        System.out.println(convert2D(mazePath));

        Stream.of(Rotation.CLOCKWISE_90, Rotation.CLOCKWISE_180, Rotation.COUNTERCLOCKWISE_90)
                .forEach(x -> {
                    List<RoomInfo> infos = convert(mazePath, 16, x);
                    String[][] maze = convert(infos);

                    System.out.println(convert2D(maze));
                });

    }

    private static List<RoomInfo> convert(String[][] maze, int size, Rotation rotation) {

        ChunkPos[][] posesArray = new ChunkPos[size][size];
        Map<ChunkPos, RoomInfo> rooms = new HashMap<>();

        List<EnumFacing> enumFacings = Arrays
                .stream(EnumFacing.values()).filter(x -> x.getAxis() != EnumFacing.Axis.Y)
                .collect(Collectors.toList());

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                ChunkPos key = new ChunkPos(i, j);
                posesArray[i][j] = key;

                // current cell position
                final BlockPos current = new BlockPos(1 + i * 2, 0, 1 + j * 2);

                List<EnumFacing> facings = enumFacings
                        .stream()
                        .filter(x -> {
                            BlockPos offset = current.offset(x);
                            // check wherever there is no wall
                            return maze[offset.getX()][offset.getZ()] == " ";
                        })
                        .collect(Collectors.toList());

                boolean isBoss = i == j && i == size - 1;
                boolean isPath = maze[current.getX()][current.getZ()] == "#";

                rooms.put(key, new RoomInfo(key, facings, isPath, isBoss));
            }
        }

        ArrayList<RoomInfo> result = new ArrayList<>();

        posesArray = PositionUtil.rotate2DGrid(posesArray, rotation);

        for (int i = 0; i < posesArray.length; i++) {
            for (int j = 0; j < posesArray[i].length; j++) {
                ChunkPos fixedLocation = new ChunkPos(i, j);
                ChunkPos prevLocation = posesArray[i][j];
                RoomInfo roomInfo = rooms.get(prevLocation);
                roomInfo.setChunkPos(fixedLocation);

                result.add(roomInfo);
            }
        }

        return result;
    }

    private static String[][] convert(List<RoomInfo> info) {
        int size = (int) Math.sqrt(info.size());
        String[][] result = new String[2 * size + 1][2 * size + 1];

        for (RoomInfo roomInfo : info) {
            int x = roomInfo.getChunkPos().x * 2 + 1;
            int z = roomInfo.getChunkPos().z * 2 + 1;
            BlockPos current = new BlockPos(x, 0, z);
            result[current.getX()][current.getZ()] = roomInfo.isPathToCenter ? "#" : " ";

            for (int i = -1; i < 2; i += 2) {
                for (int j = -1; j < 2; j += 2) {
                    result[x + i][z + j] = "+";
                }
            }

            for (EnumFacing f : EnumFacing.values()) {
                if (f.getAxis() != EnumFacing.Axis.Y) {
                    result[x + f.getFrontOffsetX()][z + f.getFrontOffsetZ()] = roomInfo.entries.contains(f)
                            ? " "
                            : f.getAxis() == EnumFacing.Axis.Z
                            ? "-"
                            : "|";
                }
            }
        }

        return result;
    }

    /**
     * Converts 2D array maze to the string representation
     *
     * @param maze2D The maze that will be convert to string representation
     * @return maze The string representation of the maze
     */
    public static String convert2D(String[][] maze2D) {
        String maze = "";
        int size = maze2D.length;
        for (int columnIndex = 0; columnIndex < size; columnIndex++) {
            for (int rowIndex = 0; rowIndex < size; rowIndex++) {
                if (maze2D[columnIndex][rowIndex] == "+") {
                    maze = maze + "+";
                } else if (maze2D[columnIndex][rowIndex] == "-") {
                    maze = maze + "---";
                } else if (maze2D[columnIndex][rowIndex] == "|") {
                    maze = maze + "|";
                } else if (maze2D[columnIndex][rowIndex] == "#" && columnIndex % 2 == 1) {
                    // Hash symbol and column is odd
                    if (rowIndex % 2 == 0) {
                        maze = maze + " ";
                    } else if (rowIndex % 2 == 1) {
                        maze = maze + "   ";
                    }
                } else if (maze2D[columnIndex][rowIndex] == "#" && columnIndex % 2 == 0) {
                    // Hash symbol and column is even
                    maze = maze + "   ";
                } else if (maze2D[columnIndex][rowIndex] == "S" || maze2D[columnIndex][rowIndex] == "E") {
                    maze = maze + "   ";
                } else if (maze2D[columnIndex][rowIndex] == " " && columnIndex % 2 == 1 && rowIndex % 2 == 0) {
                    // Spacing for the wall
                    maze = maze + " ";
                } else if (maze2D[columnIndex][rowIndex] == " ") {
                    // Spacing for the cell
                    maze = maze + "   ";
                } else {
                    maze = maze + " " + maze2D[columnIndex][rowIndex] + " ";
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
                        maze2D[columnIndex][rowIndex] = "+";
                        // Column is odd
                    } else {
                        maze2D[columnIndex][rowIndex] = "|";
                    }
                    // Row is odd
                } else {
                    // Column is even
                    if (columnIndex % 2 == 0) {
                        maze2D[columnIndex][rowIndex] = "-";
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
            if ((maze2D[y - 3][x] == "#" || maze2D[y - 1][x] == "#")
                    || (maze2D[y - 2][x - 1] == "#" || maze2D[y - 2][x + 1] == "#")) {
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
            if (((maze2D[y + 1][x + 2] == "#" || maze2D[y - 1][x + 2] == "#")
                    || (maze2D[y][x + 1] == "#" || maze2D[y][x + 3] == "#"))) {
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
            if (((maze2D[y + 1][x] == "#" || maze2D[y + 3][x] == "#")
                    || (maze2D[y + 2][x - 1] == "#" || maze2D[y + 2][x + 1] == "#"))) {
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
            if (((maze2D[y - 1][x - 2] == "#" || maze2D[y + 1][x - 2] == "#")
                    || (maze2D[y][x - 3] == "#" || maze2D[y][x - 1] == "#"))) {
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

        maze2D[1][1] = "#";

        if (random == "NORTH") {
            // NORTH and delete wall from bottom from next cell
            current.setNext(new Cell(current.getx(), current.gety() - 1));
            current = current.getNext();
            // Breaks the bottom wall from next cell
            maze2D[2 * current.gety() + 2][2 * current.getx() + 1] = "#";

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = "#";
        } else if (random == "EAST") {
            // EAST and delete wall from left from next cell
            current.setNext(new Cell(current.getx() + 1, current.gety()));
            current = current.getNext();
            // Breaks the left wall from next cell
            maze2D[2 * current.gety() + 1][2 * current.getx()] = "#";

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = "#";
        } else if (random == "SOUTH") {
            // SOUTH and delete wall from top from next cell
            current.setNext(new Cell(current.getx(), current.gety() + 1));
            current = current.getNext();
            // Breaks the top wall from next cell
            maze2D[2 * current.gety()][2 * current.getx() + 1] = "#";

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = "#";
        } else if (random == "WEST") {
            // WEST and delete wall from right from next cell
            current.setNext(new Cell(current.getx() - 1, current.gety()));
            current = current.getNext();
            // Breaks the right wall from next cell
            maze2D[2 * current.gety() + 1][2 * current.getx() + 2] = "#";

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = "#";
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
                if (maze2D[columnIndex][rowIndex] == "#") {
                    maze2D[columnIndex][rowIndex] = " ";
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
            // maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = " ";
            return "BACKTRACK";
        }

        String random = direction.remove(0);

        if (random == "NORTH") {
            if (current.gety() - 1 < 0) {
                // System.out.println("Do not go NORTH because outside of range of the 2D
                // array");
                return DFSValid(maze2D, current, direction);
            }
            if (maze2D[y - 1][x] != " ") {
                // System.out.println("Do not go NORTH because there is a wall");
                return DFSValid(maze2D, current, direction);
            }
        } else if (random == "EAST") {
            if (current.getx() + 1 >= size) {
                // System.out.println("Do not go EAST because outside of range of the 2D
                // array");
                return DFSValid(maze2D, current, direction);
            }
            if (maze2D[y][x + 1] != " ") {
                // System.out.println("Do not go EAST because there is a wall");
                return DFSValid(maze2D, current, direction);
            }
        } else if (random == "SOUTH") {
            if (current.gety() + 1 >= size) {
                // System.out.println("Do not go SOUTH because outside of range of the 2D
                // array");
                return DFSValid(maze2D, current, direction);
            }
            if (maze2D[y + 1][x] != " ") {
                // System.out.println("Do not go SOUTH because there is a wall");
                return DFSValid(maze2D, current, direction);
            }
        } else if (random == "WEST") {
            if (current.getx() - 1 < 0) {
                // System.out.println("Do not go WEST because outside of range of the 2D
                // array");
                return DFSValid(maze2D, current, direction);
            }
            if (maze2D[y][x - 1] != " ") {
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
            maze2D[2 * current.gety() + 2][2 * current.getx() + 1] = "#";

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = path;
        } else if (random == "EAST") {
            // EAST and delete wall from left from next cell
            current.setNext(new Cell(current.getx() + 1, current.gety()));
            current = current.getNext();
            // Breaks the left wall from next cell
            maze2D[2 * current.gety() + 1][2 * current.getx()] = "#";

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = path;
        } else if (random == "SOUTH") {
            // SOUTH and delete wall from top from next cell
            current.setNext(new Cell(current.getx(), current.gety() + 1));
            current = current.getNext();
            // Breaks the top wall from next cell
            maze2D[2 * current.gety()][2 * current.getx() + 1] = "#";

            // DEBUGGING: Visualizing
            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = path;
        } else if (random == "WEST") {
            // WEST and delete wall from right from next cell
            current.setNext(new Cell(current.getx() - 1, current.gety()));
            current = current.getNext();
            // Breaks the right wall from next cell
            maze2D[2 * current.gety() + 1][2 * current.getx() + 2] = "#";

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
                maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = " ";
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
            } else if (maze2D[2 * current.gety()][2 * current.getx() + 1] == " "
                    && maze2D[2 * current.gety() - 1][2 * current.getx() + 1] != " "
                    && maze2D[2 * current.gety() - 1][2 * current.getx() + 1] != "#") {

                path.add(new Cell(current.getx(), current.gety() - 1));
                current.setNext(new Cell(current.getx(), current.gety() - 1));

            }

            // EAST
            if (current.getx() + 1 >= size) {
                // Do Nothing
            } else if (maze2D[2 * current.gety() + 1][2 * current.getx() + 2] == " "
                    && maze2D[2 * current.gety() + 1][2 * current.getx() + 3] != " "
                    && maze2D[2 * current.gety() + 1][2 * current.getx() + 3] != "#") {

                path.add(new Cell(current.getx() + 1, current.gety()));
                current.setNext(new Cell(current.getx() + 1, current.gety()));
            }

            // SOUTH
            if (current.gety() + 1 >= size) {
                // Do Nothing
            } else if (maze2D[2 * current.gety() + 2][2 * current.getx() + 1] == " "
                    && maze2D[2 * current.gety() + 3][2 * current.getx() + 1] != " "
                    && maze2D[2 * current.gety() + 3][2 * current.getx() + 1] != "#") {

                path.add(new Cell(current.getx(), current.gety() + 1));
                current.setNext(new Cell(current.getx(), current.gety() + 1));
            }

            // WEST
            if (current.getx() - 1 < 0) {
                // Do Nothing
            } else if (maze2D[2 * current.gety() + 1][2 * current.getx()] == " "
                    && maze2D[2 * current.gety() + 1][2 * current.getx() - 1] != " "
                    && maze2D[2 * current.gety() + 1][2 * current.getx() - 1] != "#") {

                path.add(new Cell(current.getx() - 1, current.gety()));
                current.setNext(new Cell(current.getx() - 1, current.gety()));
            }

            maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = "#";
            current = current.getNext();
        }

        maze2D[2 * current.gety() + 1][2 * current.getx() + 1] = "#";

        // Deletes all the extra numbers
        for (int columnIndex = 0; columnIndex < maze2D.length; columnIndex++) {
            for (int rowIndex = 0; rowIndex < maze2D.length; rowIndex++) {
                if (!(maze2D[columnIndex][rowIndex] == "+" || maze2D[columnIndex][rowIndex] == "-"
                        || maze2D[columnIndex][rowIndex] == "|" || maze2D[columnIndex][rowIndex] == "#")) {
                    maze2D[columnIndex][rowIndex] = " ";
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