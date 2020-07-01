package dashcore.registry;

import dashcore.structure.NbtChunkTemplate;
import dashcore.structure.NbtStructureStart;
import net.minecraft.world.gen.structure.MapGenStructureIO;

public class DashStructures {

    public static void register() {
        MapGenStructureIO.registerStructure(NbtStructureStart.class, "NbtStructureStart");
        MapGenStructureIO.registerStructureComponent(NbtChunkTemplate.class, "NbtChunkTemplate");
    }
}
