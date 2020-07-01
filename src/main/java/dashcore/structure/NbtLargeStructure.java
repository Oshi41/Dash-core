package dashcore.structure;

import dashcore.DashCore;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.template.TemplateManager;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Function;

public class NbtLargeStructure extends MapGenStructure {
    private final ResourceLocation folder;
    private final ChunkPos size;
    private final Function<ChunkPos, BlockPos> posFunc;
    private final int chunkDistance;
    private final TemplateManager manager;

    /**
     * @param world         - current world
     * @param folder        - folder with nbt files
     * @param chunkDistance - chunk distance between structure
     * @param size          - size of structure
     * @param posFunc       - get position from chunk coords
     */
    public NbtLargeStructure(World world,
                             ResourceLocation folder,
                             int chunkDistance,
                             ChunkPos size,
                             Function<ChunkPos, BlockPos> posFunc) {
        this.folder = folder;
        this.size = size;
        this.posFunc = posFunc;
        manager = world.getSaveHandler().getStructureTemplateManager();

        int minSize = Math.max(2, Math.max(size.x, size.z));

        if (chunkDistance < minSize) {
            DashCore.log.warn(String.format("Chunk distance of structure (%s) is too small (%s), change it to %s", folder.toString(), chunkDistance, minSize));
            chunkDistance = minSize;
        }

        this.chunkDistance = chunkDistance;
    }

    @Override
    public String getStructureName() {
        return folder.getResourcePath();
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
        this.world = worldIn;

        // todo think about step by structure size
        return findNearestStructurePosBySpacing(world, this, pos, this.chunkDistance, 8, 10387312, false, 100, findUnexplored);
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        int i = chunkX;
        int j = chunkZ;
        if (chunkX < 0) {
            i = chunkX - chunkDistance - 1;
        }

        if (chunkZ < 0) {
            j = chunkZ - chunkDistance - 1;
        }

        int k = i / chunkDistance;
        int l = j / chunkDistance;

        int folderHash = folder.hashCode();

        Random random = this.world.setRandomSeed(k, l, folderHash);
        k *= chunkDistance;
        l *= chunkDistance;

        int smallerParameter = (int) (chunkDistance * 0.75);

        k += (random.nextInt(smallerParameter) + random.nextInt(smallerParameter)) / 2;
        l += (random.nextInt(smallerParameter) + random.nextInt(smallerParameter)) / 2;
        return chunkX == k && chunkZ == l;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        BlockPos position = posFunc.apply(new ChunkPos(chunkX, chunkZ));
        return new NbtStructureStart(folder, manager, position, size);
    }
}
