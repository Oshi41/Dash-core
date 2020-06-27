package dashcore.world.interfaces;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public interface IChunkPrimer extends INBTSerializable<NBTTagCompound> {
    /**
     * Set block at current relative poses
     *
     * @param x     - [0..15]
     * @param y     - [0..255]
     * @param z     - [0..15]
     * @param state - block state
     */
    void setBlockState(int x, int y, int z, IBlockState state);

    /**
     * Gets blocks from relative poses
     *
     * @param x - [0..15]
     * @param y - [0..255]
     * @param z - [0..15]
     * @return
     */
    @Nullable
    IBlockState getBlockState(int x, int y, int z);
}
