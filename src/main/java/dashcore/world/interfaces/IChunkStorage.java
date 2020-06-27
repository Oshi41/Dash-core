package dashcore.world.interfaces;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;
import java.util.Map;

public interface IChunkStorage extends INBTSerializable<NBTTagCompound> {

    /**
     * Position of current chunk
     *
     * @return chunk pos
     */
    ChunkPos getPos();

    /**
     * Getting actual size of chunk blocks
     * Heavy calculation
     *
     * @return
     */
    StructureBoundingBox getSize();

    /**
     * Adding entity to chunk
     *
     * @param e
     */
    void add(Entity e);

    /**
     * Adding tile entity
     *
     * @param e
     */
    void add(TileEntity e);

    /**
     * Adding block to pos in chunk
     *
     * @param block
     * @param pos
     */
    void add(IBlockState block, BlockPos pos);

    /**
     * Getting tile from coords
     *
     * @param x
     * @return
     */
    TileEntity getTile(BlockPos x);

    /**
     * Get state from coords
     *
     * @param x
     * @return
     */
    IBlockState getState(BlockPos x);

    /**
     * List of chunk tiles
     *
     * @return reference to map with tiles
     */
    Map<BlockPos, TileEntity> getTiles();

    /**
     * Chunk blocks. Absolute positions
     * Heavy calculations
     *
     * @return New map instance with chunk blocks
     */
    Map<BlockPos, IBlockState> getBlocks();

    /**
     * Entities list
     *
     * @return entities list reference
     */
    List<Entity> getEntities();
}
