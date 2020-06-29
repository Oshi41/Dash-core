package dashcore.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;

public class NbtUtil {

    // region ChunkPos

    private static final String ChunkPosKey = "ChunkPos";

    public static void write(NBTTagCompound nbt, ChunkPos pos) {
        if (nbt == null || pos == null)
            return;

        nbt.setIntArray(ChunkPosKey, new int[]{pos.x, pos.z});
    }

    public static ChunkPos read(NBTTagCompound nbt) {
        if (nbt != null) {
            int[] array = nbt.getIntArray(ChunkPosKey);
            if (array != null && array.length == 2) {
                return new ChunkPos(array[0], array[1]);
            }
        }

        return null;
    }

    // endregion
}
