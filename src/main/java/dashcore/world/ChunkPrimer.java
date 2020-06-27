package dashcore.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraftforge.registries.GameData;
import dashcore.world.interfaces.IChunkPrimer;

import javax.annotation.Nullable;
import java.util.Arrays;

public class ChunkPrimer implements IChunkPrimer {
    private static final ObjectIntIdentityMap<IBlockState> map = GameData.getBlockStateIDMap();
    private static final String key = "Blocks";
    private final int[] data = new int[65536];

    public ChunkPrimer() {
        // if 0 getBlockState will return Blocks.AIR
        // But it needs to return null
        Arrays.fill(data, -1);
    }

    @Override
    public void setBlockState(int x, int y, int z, IBlockState state) {
        // normalizing
        x = x % 16;
        z = z % 16;

        if (!checkBounds(x, y, z))
            return;

        this.data[getBlockIndex(x, y, z)] = map.get(state);
    }

    @Nullable
    @Override
    public IBlockState getBlockState(int x, int y, int z) {
        // normalizing
        x = x % 16;
        z = z % 16;

        if (!checkBounds(x, y, z))
            return null;

        return map.getByValue(this.data[getBlockIndex(x, y, z)]);
    }

    /**
     * Check bounds for chunk
     *
     * @param x - relative x position
     * @param y - y position
     * @param z - relative z position
     * @return
     */
    private boolean checkBounds(int x, int y, int z) {
        if (x < 0 || 16 < x)
            return false;

        if (z < 0 || 16 < z)
            return false;

        return y >= 0 && 255 >= y;
    }

    // Taken from original ChunkPrimer
    private int getBlockIndex(int x, int y, int z) {
        return x << 12 | z << 8 | y;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setIntArray(key, data);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (nbt.hasKey(key)) {
            int[] blocks = nbt.getIntArray(key);
            System.arraycopy(blocks, 0, data, 0, Math.min(blocks.length, data.length));
        }
    }
}
