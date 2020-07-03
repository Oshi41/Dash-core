package dashcore.debug;

import dashcore.DashCore;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class Generator extends Item {
    public Generator(String name) {
        setRegistryName(DashCore.ModId, name);
        setCreativeTab(CreativeTabs.MISC);
        setUnlocalizedName(name);
        setMaxStackSize(1);
        setMaxDamage(-1);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer p_180614_1_, World world, BlockPos pos, EnumHand p_180614_4_, EnumFacing p_180614_5_, float p_180614_6_, float p_180614_7_, float p_180614_8_) {
        EnumActionResult result = super.onItemUse(p_180614_1_, world, pos, p_180614_4_, p_180614_5_, p_180614_6_, p_180614_7_, p_180614_8_);

        if (result != EnumActionResult.FAIL) {
            generateStructure(world, pos.up(10));
        }

        return result;
    }

    private void generateStructure(World world, BlockPos pos) {
//        WorldGenerator generator = new DungeonComponentDramix();
//        generator.generate(world, world.rand, pos);

        TemplateManager manager = world.getSaveHandler().getStructureTemplateManager();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                ChunkPos chunkPos = new ChunkPos(i, j);
                Template template = manager.getTemplate(null, new ResourceLocation(DashCore.ModId, "dramix/" + chunkPos.toString()));
                template.addBlocksToWorld(world, pos, new PlacementSettings());
                pos = pos.up(17);
            }
        }

    }
}
