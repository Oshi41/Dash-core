package dashcore.debug;

import dashcore.DashCore;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;

import java.util.Random;

public class SimplePortal extends Block {
    private final DimensionType type;

    public SimplePortal(DimensionType type, String name) {
        super(Material.PORTAL);
        setSoundType(SoundType.GLASS);
        setRegistryName(DashCore.ModId, name);
        setUnlocalizedName(name);
        setLightLevel(1);
        setCreativeTab(CreativeTabs.MISC);
        setBlockUnbreakable();

        this.type = type;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return null;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entity) {
        if (worldIn.isRemote)
            return;

        boolean canTransfer = entity.timeUntilPortal <= 0;
        entity.timeUntilPortal = 40;

        if (canTransfer) {
            DimensionType destination = type;
            if (worldIn.provider.getDimensionType() == destination) {
                destination = DimensionType.OVERWORLD;
            }

            entity.changeDimension(destination.getId(), new CommandTeleporter(pos));
        }
    }

    private static class CommandTeleporter implements ITeleporter {
        private final BlockPos targetPos;

        private CommandTeleporter(BlockPos targetPos) {
            this.targetPos = targetPos;
        }

        @Override
        public void placeEntity(World world, Entity entity, float yaw) {
            entity.moveToBlockPosAndAngles(targetPos, yaw, entity.rotationPitch);
        }
    }
}
