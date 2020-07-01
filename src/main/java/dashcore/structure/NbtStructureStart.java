package dashcore.structure;

import dashcore.DashCore;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class NbtStructureStart extends StructureStart {
    private final ResourceLocation folder;
    private final TemplateManager manager;
    private final ChunkPos size;

    /**
     * Nbt ctor
     */
    public NbtStructureStart() {
        this(null, null, BlockPos.ORIGIN, new ChunkPos(0, 0));
    }

    /**
     * Generates large structure from nbt 16*16 parts (single chunk)
     * Use names for linking parts together
     * File names are basing on relative chunk poses from first part '[0,0].nbt'
     *
     * @param folder         - folder with nbt files
     * @param manager        - template manager
     * @param structureStart - position of structure. Uses the Y level
     * @param size           - size of structure. First structure part is always at [0,0], and algorithm will check all parts till
     *                       size
     */
    public NbtStructureStart(ResourceLocation folder, TemplateManager manager, BlockPos structureStart, ChunkPos size) {
        super(structureStart.getX() / 16, structureStart.getZ() / 16);
        this.folder = folder;
        this.manager = manager;
        this.size = size;

        for (int xPos = 0; xPos <= size.x; xPos++) {
            for (int zPos = 0; zPos <= size.z; zPos++) {
                ChunkPos currentChunkPos = new ChunkPos(xPos, zPos);

                ResourceLocation chunkLocation = new ResourceLocation(folder.getResourceDomain(), folder.getResourcePath() + "/" + currentChunkPos.toString());
                Template template = manager.getTemplate(null, chunkLocation);

                if (template == null || BlockPos.ORIGIN.equals(template.getSize())) {
                    DashCore.log.warn(String.format("Current structure chunk is null or empty: %s", chunkLocation.toString()));
                    continue;
                }

                // adding chunk to component
                components.add(new NbtChunkTemplate(manager, chunkLocation, currentChunkPos.getBlock(structureStart.getX(), structureStart.getY(), structureStart.getZ())));
            }
        }

        updateBoundingBox();
    }
}
