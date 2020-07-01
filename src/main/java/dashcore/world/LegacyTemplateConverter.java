package dashcore.world;

import dashcore.world.interfaces.IChunkStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.io.File;
import java.util.Map;

public class LegacyTemplateConverter {

    public static void convert(WorldGenerator generator, File folder) {
        MockWorld world = new MockWorld();
        generator.generate(world, world.rand, BlockPos.ORIGIN);
        Map<ChunkPos, IChunkStorage> chunks = world.chunks;

        if (folder.mkdirs()) {
            if (folder.mkdir()) {
                for (IChunkStorage storage : chunks.values()) {
                    TemplateConverter template = new TemplateConverter(storage);
                    ChunkPos pos = storage.getPos();
                    File file = new File(folder, String.format("[%s,%s].nbt", pos.x, pos.z));
                    template.writeToFile(file);
                }
            }
        }
    }
}
