package dashcore.maze.structure;

import dashcore.maze.description.IRoomDescription;
import dashcore.maze.description.RoomRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponentTemplate;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Random;

public class ArcanaChunkRoom extends StructureComponentTemplate {
    private ResourceLocation id;

    /**
     * NBT ctor
     */
    public ArcanaChunkRoom() {

    }

    public ArcanaChunkRoom(BlockPos pos, IRoomDescription description, TemplateManager manager) {
        init(pos, description, manager);
    }

    private void init(BlockPos pos, IRoomDescription description, TemplateManager manager) {
        if (description == null)
            return;

        placeSettings = new PlacementSettings().setIgnoreEntities(true).setReplacedBlock(Blocks.AIR);
        Template template = description.loadTemplate(manager, placeSettings);
        setup(template, pos, placeSettings);

        id = description.getId();
    }

    @Override
    protected void writeStructureToNBT(NBTTagCompound tag) {
        super.writeStructureToNBT(tag);

        tag.setString("Rs", id.toString());
    }

    @Override
    protected void readStructureFromNBT(NBTTagCompound tag, TemplateManager manager) {
        super.readStructureFromNBT(tag, manager);

        IRoomDescription description = RoomRegistry.find(tag.getString("Rs"));
        if (description == null)
            return;

        init(templatePosition, description, manager);
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, World worldIn, Random rand, StructureBoundingBox sbb) {

    }
}
