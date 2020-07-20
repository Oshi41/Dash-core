package dashcore.maze.structure;

import dashcore.maze.algorythm.MazeScheme;
import dashcore.maze.description.IRoomDescription;
import dashcore.maze.description.RoomRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Random;

public class ArcanaMazeStart extends StructureStart {
    /**
     * NBT ctor
     */
    public ArcanaMazeStart() {

    }

    public ArcanaMazeStart(TemplateManager manager, int x, int z, int size, Random random) {
        super(x, z);

        ChunkPos chunkPos = new ChunkPos(x, z);

        // can add layer here
        for (int height = 5; height < 10; height += 16) {
            MazeScheme mazeScheme = new MazeScheme(size, random).generate(chunkPos);
            IRoomDescription[][] maze = RoomRegistry.build(mazeScheme.getRooms());

            for (int i = 0; i < maze.length; i++) {
                for (int j = 0; j < maze[i].length; j++) {
                    BlockPos currentChunkPosition = chunkPos.getBlock(i * 16, height, j * 16);
                    IRoomDescription info = maze[i][j];
                    ArcanaChunkRoom room = new ArcanaChunkRoom(currentChunkPosition, info, manager);
                    components.add(room);
                }
            }
        }


        updateBoundingBox();
    }
}
