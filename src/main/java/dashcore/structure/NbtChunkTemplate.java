package dashcore.structure;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponentTemplate;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Random;

public class NbtChunkTemplate extends StructureComponentTemplate {
    private ResourceLocation location;
    private BlockPos pos;

    /**
     * Nbt ctor
     */
    public NbtChunkTemplate() {

    }

    /**
     * @param manager
     * @param location - already checked location so template exist for sure
     * @param pos
     */
    public NbtChunkTemplate(TemplateManager manager, ResourceLocation location, BlockPos pos) {
        this.location = location;
        this.pos = pos;

        setup(manager.getTemplate(null, location), pos, placeSettings);
    }

    // region NBT

    @Override
    protected void writeStructureToNBT(NBTTagCompound tagCompound) {
        super.writeStructureToNBT(tagCompound);

        tagCompound.setString("Rs", location.toString());
    }

    @Override
    protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager manager) {
        super.readStructureFromNBT(tagCompound, manager);

        Template template = manager.getTemplate(null, new ResourceLocation(tagCompound.getString("Rs")));
        setup(template, pos, placeSettings);
    }

    //endregion

    @Override
    protected void handleDataMarker(String function, BlockPos pos, World worldIn, Random rand, StructureBoundingBox sbb) {

    }
}
