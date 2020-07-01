package dashcore.structure;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponentTemplate;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Random;

public class NbtChunkTemplate extends StructureComponentTemplate {
    private ResourceLocation location;

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
    public NbtChunkTemplate(TemplateManager manager, ResourceLocation location, Rotation rotation, BlockPos pos) {
        this.location = location;
        placeSettings = new PlacementSettings()
                .setIgnoreEntities(true)
                .setReplacedBlock(Blocks.AIR)
                .setRotation(rotation);
        setup(manager.getTemplate(null, location), pos, placeSettings);
    }

    // region NBT

    @Override
    protected void writeStructureToNBT(NBTTagCompound tagCompound) {
        super.writeStructureToNBT(tagCompound);

        tagCompound.setString("Rs", location.toString());
        tagCompound.setString("Rt", placeSettings.getRotation().name());
    }

    @Override
    protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager manager) {
        super.readStructureFromNBT(tagCompound, manager);

        location = new ResourceLocation(tagCompound.getString("Rs"));
        Rotation rotation = Rotation.valueOf(tagCompound.getString("Rt"));
        placeSettings.setRotation(rotation);

        Template template = manager.getTemplate(null, location);
        setup(template, templatePosition, placeSettings);
    }

    //endregion

    @Override
    protected void handleDataMarker(String function, BlockPos pos, World worldIn, Random rand, StructureBoundingBox sbb) {

    }
}
