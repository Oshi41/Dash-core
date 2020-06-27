package dashcore.world;

import dashcore.DashCore;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import dashcore.world.interfaces.IChunkPrimer;
import dashcore.world.interfaces.IChunkStorage;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChunkStorage implements IChunkStorage {
    private static final String blocksKey = "Primer";
    private static final String tilesKey = "Tiles";
    private static final String entitiesKey = "Entities";

    private final ChunkPos pos;
    private final IChunkPrimer primer;
    private final List<Entity> entities;
    private final Map<BlockPos, TileEntity> tiles;
    private final World world;

    public ChunkStorage(ChunkPos pos, World world) {
        this.pos = pos;
        this.world = world;
        primer = new ChunkPrimer();
        entities = new ArrayList<>();
        tiles = new HashMap<>();
    }

    @Override
    public ChunkPos getPos() {
        return pos;
    }

    @Override
    public StructureBoundingBox getSize() {
        List<BlockPos> poses = Stream.concat(getBlocks().keySet().stream(), tiles.keySet().stream())
                .distinct()
                .collect(Collectors.toList());

        return new StructureBoundingBox(
                poses.stream().map(Vec3i::getX).min(Integer::compareTo).orElse(0),
                poses.stream().map(Vec3i::getY).min(Integer::compareTo).orElse(0),
                poses.stream().map(Vec3i::getZ).min(Integer::compareTo).orElse(0),

                poses.stream().map(Vec3i::getX).max(Integer::compareTo).orElse(0),
                poses.stream().map(Vec3i::getY).max(Integer::compareTo).orElse(0),
                poses.stream().map(Vec3i::getZ).max(Integer::compareTo).orElse(0)
        );
    }

    @Override
    public void add(Entity e) {
        if (e != null)
            entities.add(e);
    }

    @Override
    public void add(TileEntity e) {
        if (e != null)
            tiles.put(e.getPos(), e);
    }

    @Override
    public void add(IBlockState block, BlockPos pos) {
        if (block != null && pos != null)
            primer.setBlockState(pos.getX(), pos.getY(), pos.getZ(), block);
    }

    @Override
    public TileEntity getTile(BlockPos x) {
        return tiles.get(x);
    }

    @Override
    public IBlockState getState(BlockPos x) {
        return primer.getBlockState(x.getX(), x.getY(), x.getZ());
    }

    @Override
    public Map<BlockPos, TileEntity> getTiles() {
        return tiles;
    }

    @Override
    public Map<BlockPos, IBlockState> getBlocks() {
        LinkedHashMap<BlockPos, IBlockState> map = new LinkedHashMap<>();

        ChunkPos chunkPos = getPos();

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {
                    IBlockState state = primer.getBlockState(x, y, z);
                    if (state == null)
                        continue;

                    map.put(chunkPos.getBlock(x, y, z), state);
                }
            }
        }

        return map;
    }

    @Override
    public List<Entity> getEntities() {
        return entities;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();

        NBTTagList entities = new NBTTagList();
        this.entities.forEach(x -> entities.appendTag(x.serializeNBT()));

        NBTTagList tiles = new NBTTagList();
        this.tiles.values().forEach(x -> tiles.appendTag(x.serializeNBT()));

        compound.setTag(blocksKey, primer.serializeNBT());
        compound.setTag(tilesKey, tiles);
        compound.setTag(entitiesKey, entities);

        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        NBTBase blocksRaw = nbt.getTag(blocksKey);
        if (blocksRaw instanceof NBTTagCompound) {
            primer.deserializeNBT((NBTTagCompound) blocksRaw);
        }


        NBTBase tilesRaw = nbt.getTag(tilesKey);
        if (tilesRaw instanceof NBTTagList) {
            for (NBTBase nbtBase : ((NBTTagList) tilesRaw)) {
                if (nbtBase instanceof NBTTagCompound) {
                    try {
                        Entity entity = EntityList.createEntityFromNBT((NBTTagCompound) nbtBase, world);
                        entities.add(entity);
                    } catch (Exception e) {
                        DashCore.log.error(e);
                    }
                }
            }
        }

        NBTBase entitiesRaw = nbt.getTag(entitiesKey);
        if (entitiesRaw instanceof NBTTagList) {
            for (NBTBase nbtBase : ((NBTTagList) entitiesRaw)) {
                if (nbtBase instanceof NBTTagCompound) {
                    try {
                        TileEntity tileEntity = TileEntity.create(world, ((NBTTagCompound) nbtBase));
                        tiles.put(tileEntity.getPos(), tileEntity);
                    } catch (Exception e) {
                        DashCore.log.error(e);
                    }
                }
            }
        }
    }
}
