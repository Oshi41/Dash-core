package dashcore.world;

import dashcore.world.interfaces.IChunkStorage;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MockWorld extends World {
    public final Map<ChunkPos, IChunkStorage> chunks;

    public MockWorld() {
        super(null, null, new WorldProvider() {
            @Override
            public DimensionType getDimensionType() {
                return DimensionType.OVERWORLD;
            }
        }, null, false);
        this.chunks = new HashMap<>();
    }

    @Override
    protected IChunkProvider createChunkProvider() {
        return null;
    }

    @Override
    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
        return false;
    }

    @Override
    public boolean addTileEntity(TileEntity tile) {
        IChunkStorage storage = provide(tile.getPos());
        storage.add(tile);
        return true;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos x) {
        return provide(x).getTiles().get(x);
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState state, int flag) {
        if (this.isOutsideBuildHeight(pos)) {
            return false;
        }

        IChunkStorage storage = provide(pos);
        storage.add(state, pos);

        if (state.getBlock() instanceof ITileEntityProvider) {
            TileEntity tileEntity = state.getBlock().createTileEntity(this, state);
            setTileEntity(pos, tileEntity);
        }

        return true;
    }

    @Nullable
    @Override
    public IBlockState getBlockState(BlockPos x) {
        return provide(x).getState(x);
    }

    @Override
    public void tick() {

    }

    @Override
    protected void updateWeather() {

    }

    @Override
    protected void tickPlayers() {

    }

    @Override
    public void immediateBlockTick(BlockPos p_189507_1_, IBlockState p_189507_2_, Random p_189507_3_) {

    }

    @Override
    public void updateBlockTick(BlockPos p_175654_1_, Block p_175654_2_, int p_175654_3_, int p_175654_4_) {

    }

    @Override
    public boolean tickUpdates(boolean p_72955_1_) {
        return false;
    }

    @Override
    public boolean isBlockTickPending(BlockPos p_175691_1_, Block p_175691_2_) {
        return false;
    }

    @Override
    public void updateEntityWithOptionalForce(Entity p_72866_1_, boolean p_72866_2_) {

    }

    @Override
    protected void updateBlocks() {

    }

    @Override
    public void updateAllPlayersSleepingFlag() {

    }

    @Override
    public void updateEntities() {

    }

    @Override
    public void updateEntity(Entity p_72870_1_) {

    }

    @Override
    public void updateComparatorOutputLevel(BlockPos p_175666_1_, Block p_175666_2_) {

    }

    @Override
    public void updateObservingBlocksAt(BlockPos p_190522_1_, Block p_190522_2_) {

    }

    @Override
    public void updateWeatherBody() {

    }

    @Override
    public boolean isUpdateScheduled(BlockPos p_184145_1_, Block p_184145_2_) {
        return false;
    }

    @Override
    public void notifyBlockUpdate(BlockPos p_184138_1_, IBlockState p_184138_2_, IBlockState p_184138_3_, int p_184138_4_) {

    }

    @Override
    public void scheduleUpdate(BlockPos p_175684_1_, Block p_175684_2_, int p_175684_3_) {

    }

    @Override
    public void scheduleBlockUpdate(BlockPos p_180497_1_, Block p_180497_2_, int p_180497_3_, int p_180497_4_) {

    }

    @Override
    public void markBlockRangeForRenderUpdate(BlockPos p_175704_1_, BlockPos p_175704_2_) {

    }

    @Override
    public void markBlockRangeForRenderUpdate(int p_147458_1_, int p_147458_2_, int p_147458_3_, int p_147458_4_, int p_147458_5_, int p_147458_6_) {

    }

    private IChunkStorage provide(BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        return chunks.computeIfAbsent(chunkPos, x -> new ChunkStorage(x, this));
    }
}
