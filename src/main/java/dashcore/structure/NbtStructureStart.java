package dashcore.structure;

import dashcore.DashCore;
import dashcore.util.PositionUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
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
        this(null, null, BlockPos.ORIGIN, Rotation.NONE, new ChunkPos(0, 0));
    }

    /**
     * Generates large structure from nbt 16*16 parts (single chunk)
     * Use names for linking parts together
     * File names are basing on relative chunk poses from first part '[0,0].nbt'
     *
     * @param folder         - folder with nbt files
     * @param manager        - template manager
     * @param structureStart - position of structure. Uses the Y level
     * @param rotation
     * @param size           - size of structure. First structure part is always at [0,0], and algorithm will check all parts till
     */
    public NbtStructureStart(ResourceLocation folder, TemplateManager manager, BlockPos structureStart, final Rotation rotation, ChunkPos size) {
        super(structureStart.getX() / 16, structureStart.getZ() / 16);
        this.folder = folder;
        this.manager = manager;
        this.size = size;

        ChunkPos[][] poses = new ChunkPos[size.x][size.z];

        for (int i = 0; i < size.x; i++) {
            for (int j = 0; j < size.z; j++) {
                poses[i][j] = new ChunkPos(i, j);
            }
        }

        poses = PositionUtil.rotate2DGrid(poses, rotation);

        Rotation roomRotation = rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90
                // need to get opposite
                ? rotation.add(rotation).add(rotation)
                : rotation;

        for (int i = 0; i < poses.length; i++) {
            for (int j = 0; j < poses[i].length; j++) {
                ResourceLocation templateLocation = new ResourceLocation(folder.getResourceDomain(),
                        folder.getResourcePath() + "/" + poses[i][j].toString());
                Template template = manager.getTemplate(null, templateLocation);

                if (template == null || BlockPos.ORIGIN.equals(template.getSize())) {
                    DashCore.log.warn(String.format("Current structure chunk is null or empty: %s", templateLocation.toString()));
                    continue;
                }

                // adding chunk to component
                components.add(new NbtChunkTemplate(manager,
                        templateLocation,
                        roomRotation,
                        new ChunkPos(i, j)
                                .getBlock(structureStart.getX(),
                                        structureStart.getY(),
                                        structureStart.getZ())));

            }
        }

        updateBoundingBox();
    }
}
