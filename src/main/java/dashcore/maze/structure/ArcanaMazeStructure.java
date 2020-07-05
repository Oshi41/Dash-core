package dashcore.maze.structure;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

import javax.annotation.Nullable;

public class ArcanaMazeStructure extends MapGenStructure {
    private int size;

    public ArcanaMazeStructure(int size) {
        this.size = size;
    }

    @Override
    public String getStructureName() {
        return "ArcanaMaze";
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
        this.world = worldIn;

        return findNearestStructurePosBySpacing(world, this, pos, this.size, 8, 10387312, false, 100, findUnexplored);
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        return chunkX % size == 0 || chunkZ % size == 0;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return null;
    }
}
