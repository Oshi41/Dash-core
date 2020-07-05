package dashcore.maze.structure;

import dashcore.maze.algorythm.MazeGeneration;
import dashcore.maze.description.IRoomDescription;
import dashcore.maze.description.RoomRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class ArcanaMazeStart extends StructureStart {
    /**
     * NBT ctor
     */
    public ArcanaMazeStart() {

    }

    public ArcanaMazeStart(TemplateManager manager, int x, int y, int z, int size) {
        super(x, z);

        ChunkPos chunkPos = new ChunkPos(x, z);
        IRoomDescription[][] maze = RoomRegistry.build(MazeGeneration.generate(chunkPos, size));

        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                BlockPos currentChunkPosition = chunkPos.getBlock(i * 16, y, j * 16);
                IRoomDescription info = maze[i][j];
                components.add(new ArcanaChunkRoom(currentChunkPosition, info, manager));
            }
        }

        updateBoundingBox();
    }
}
