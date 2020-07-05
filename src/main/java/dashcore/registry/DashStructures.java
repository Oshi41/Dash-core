package dashcore.registry;

import dashcore.maze.structure.ArcanaChunkRoom;
import dashcore.maze.structure.ArcanaMazeStart;
import dashcore.structure.NbtChunkTemplate;
import dashcore.structure.NbtStructureStart;
import net.minecraft.world.gen.structure.MapGenStructureIO;

public class DashStructures {

    public static void register() {
        MapGenStructureIO.registerStructure(NbtStructureStart.class, "NbtStructureStart");
        MapGenStructureIO.registerStructureComponent(NbtChunkTemplate.class, "NbtChunkTemplate");

        MapGenStructureIO.registerStructure(ArcanaMazeStart.class, "ArcanaMazeStart");
        MapGenStructureIO.registerStructureComponent(ArcanaChunkRoom.class, "ArcanaChunkRoom");
    }
}
