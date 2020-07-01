package dashcore.debug;

import dashcore.util.PositionUtil;
import net.minecraft.util.Rotation;

public class Tests {
    public static void main(String[] params) {
        Integer[][] array = new Integer[4][3];

        int counter = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                array[i][j] = counter++;
            }
        }

        System.out.println("Original:");
        displayMatrix(array);

        for (Rotation value : Rotation.values()) {
            Integer[][] copy = PositionUtil.rotate2DGrid(array, value);
            System.out.println(value);
            displayMatrix(copy);
        }
    }

    static <T> void displayMatrix(T[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++)
                System.out.print(matrix[i][j] + " ");
            System.out.println();
        }
    }
}
