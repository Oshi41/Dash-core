package dashcore.util;

import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.Random;
import java.util.function.Function;

public class PositionUtil {

    /**
     * Transforming block position with mirror and rotation. Center of rotation is BlockPos.ORIGIN (0;0;0)
     *
     * @param pos        - current position
     * @param mirrorIn   - mirror. Nullable
     * @param rotationIn - rotation. Nullable
     * @return
     */
    public static BlockPos transformedBlockPos(BlockPos pos, @Nullable Mirror mirrorIn, @Nullable Rotation rotationIn) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        boolean flag = true;

        if (mirrorIn == null) {
            mirrorIn = Mirror.NONE;
        }

        if (rotationIn == null) {
            rotationIn = Rotation.NONE;
        }

        switch (mirrorIn) {
            case LEFT_RIGHT:
                k = -k;
                break;
            case FRONT_BACK:
                i = -i;
                break;
            default:
                flag = false;
        }

        switch (rotationIn) {
            case COUNTERCLOCKWISE_90:
                return new BlockPos(k, j, -i);
            case CLOCKWISE_90:
                return new BlockPos(-k, j, i);
            case CLOCKWISE_180:
                return new BlockPos(-i, j, -k);
            default:
                return flag ? new BlockPos(i, j, k) : pos;
        }
    }

    // region Grid rotation

    public static <T> T[][] rotate2DGrid(T[][] source, Rotation rotation) {
        if (rotation == null
                || rotation == Rotation.NONE
                || source == null
                || source.length == 0)
            return source;

        int rotateTimes = 0;

        switch (rotation) {
            case COUNTERCLOCKWISE_90:
                rotateTimes = 3;
                break;

            case CLOCKWISE_180:
                rotateTimes = 2;
                break;

            case CLOCKWISE_90:
                rotateTimes = 1;
        }

        for (int i = 0; i < rotateTimes; i++) {
            source = rotateClockwise(source);
        }

        return source;
    }


    private static <T> T[][] rotateClockwise(T[][] matrix) {
        T[][] result = (T[][]) Array.newInstance(matrix[0][0].getClass(), matrix[0].length, matrix.length);

        int i1 = 0;
        for (int j = 0; j < matrix[0].length; j++) {
            int j1 = 0;
            for (int i = matrix.length - 1; i >= 0; i--) {
                result[i1][j1] = matrix[i][j];
                j1++;
            }
            i1++;
        }

        return result;
    }

    // endregion

    /**
     * Gets random height from chunk coords
     *
     * @param world
     * @return
     */
    public static Function<ChunkPos, BlockPos> getYSurfaceLevel(World world) {
        WeakReference<World> reference = new WeakReference<>(world);

        return chunkPos -> {
            int x = chunkPos.getXStart();
            int z = chunkPos.getZStart();
            int y = 0;
            if (reference.get() != null) {
                y = reference.get().getHeight(x, z);
            }
            return new BlockPos(x, y, z);
        };
    }

    /**
     * Gets random possible rotation
     *
     * @return
     */
    public static Function<ChunkPos, Rotation> getRandomRotation(Random r) {
        Rotation[] values = Rotation.values();
        Random random = new Random(r.nextLong());
        return chunkPos -> values[random.nextInt(values.length)];
    }
}
