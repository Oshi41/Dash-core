package dashcore.world;

import com.google.common.collect.Lists;
import dashcore.world.interfaces.IChunkStorage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class TemplateConverter {
    public final BlockPos size;
    /**
     * blocks in the structure
     */
    private final List<net.minecraft.world.gen.structure.template.Template.BlockInfo> blocks = Lists.newArrayList();
    /**
     * entities in the structure
     */
    private final List<net.minecraft.world.gen.structure.template.Template.EntityInfo> entities = Lists.newArrayList();

    public TemplateConverter(IChunkStorage chunk) {
        chunk.getBlocks().forEach((pos, state) -> {
            NBTTagCompound compound = null;
            TileEntity tile = chunk.getTile(pos);
            if (tile != null) {
                compound = tile.serializeNBT();
            }

            blocks.add(new net.minecraft.world.gen.structure.template.Template.BlockInfo(getFixedPosition(pos), state, compound));
        });

        chunk.getEntities().forEach((e) -> {
            entities.add(new net.minecraft.world.gen.structure.template.Template.EntityInfo(Vec3d.ZERO, getFixedPosition(e.getPosition()), e.serializeNBT()));
        });

        StructureBoundingBox box = chunk.getSize();

        size = new BlockPos(box.getXSize(), box.getYSize(), box.getZSize());
    }

    public void writeToFile(File file) {
        file.getParentFile().mkdirs();

        NBTTagCompound nbt = writeToNBT(new NBTTagCompound());
        FileOutputStream stream = null;

        try {
            stream = new FileOutputStream(file);
            CompressedStreamTools.writeCompressed(nbt, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        BasicPalette template$basicpalette = new BasicPalette();
        NBTTagList nbttaglist = new NBTTagList();

        for (net.minecraft.world.gen.structure.template.Template.BlockInfo template$blockinfo : this.blocks) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setTag("pos", this.writeInts(template$blockinfo.pos.getX(), template$blockinfo.pos.getY(), template$blockinfo.pos.getZ()));
            nbttagcompound.setInteger("state", template$basicpalette.idFor(template$blockinfo.blockState));

            if (template$blockinfo.tileentityData != null) {
                nbttagcompound.setTag("nbt", template$blockinfo.tileentityData);
            }

            nbttaglist.appendTag(nbttagcompound);
        }

        NBTTagList nbttaglist1 = new NBTTagList();

        for (net.minecraft.world.gen.structure.template.Template.EntityInfo template$entityinfo : this.entities) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setTag("pos", this.writeDoubles(template$entityinfo.pos.x, template$entityinfo.pos.y, template$entityinfo.pos.z));
            nbttagcompound1.setTag("blockPos", this.writeInts(template$entityinfo.blockPos.getX(), template$entityinfo.blockPos.getY(), template$entityinfo.blockPos.getZ()));

            if (template$entityinfo.entityData != null) {
                nbttagcompound1.setTag("nbt", template$entityinfo.entityData);
            }

            nbttaglist1.appendTag(nbttagcompound1);
        }

        NBTTagList nbttaglist2 = new NBTTagList();

        for (IBlockState iblockstate : template$basicpalette) {
            nbttaglist2.appendTag(NBTUtil.writeBlockState(new NBTTagCompound(), iblockstate));
        }

        net.minecraftforge.fml.common.FMLCommonHandler.instance().getDataFixer().writeVersionData(nbt); //Moved up for MC updating reasons.
        nbt.setTag("palette", nbttaglist2);
        nbt.setTag("blocks", nbttaglist);
        nbt.setTag("entities", nbttaglist1);
        nbt.setTag("size", this.writeInts(this.size.getX(), this.size.getY(), this.size.getZ()));
        nbt.setString("author", "DivineRPSStructureCreator");
        nbt.setInteger("DataVersion", 1343);
        return nbt;
    }

    private NBTTagList writeInts(int... values) {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i : values) {
            nbttaglist.appendTag(new NBTTagInt(i));
        }

        return nbttaglist;
    }

    private NBTTagList writeDoubles(double... values) {
        NBTTagList nbttaglist = new NBTTagList();

        for (double d0 : values) {
            nbttaglist.appendTag(new NBTTagDouble(d0));
        }

        return nbttaglist;
    }

    /**
     * Gets chunk position (0..15 on X/Z coords)
     *
     * @param pos - absolute position
     * @return
     */
    private BlockPos getFixedPosition(BlockPos pos) {
        int maxChunkSize = 16;
        return new BlockPos(pos.getX() % maxChunkSize, pos.getY(), pos.getZ() % maxChunkSize);
    }

    static class BasicPalette implements Iterable<IBlockState> {
        public static final IBlockState DEFAULT_BLOCK_STATE = Blocks.AIR.getDefaultState();
        final ObjectIntIdentityMap<IBlockState> ids;
        private int lastId;

        private BasicPalette() {
            this.ids = new ObjectIntIdentityMap<IBlockState>(16);
        }

        public int idFor(IBlockState state) {
            int i = this.ids.get(state);

            if (i == -1) {
                i = this.lastId++;
                this.ids.put(state, i);
            }

            return i;
        }

        @Nullable
        public IBlockState stateFor(int id) {
            IBlockState iblockstate = this.ids.getByValue(id);
            return iblockstate == null ? DEFAULT_BLOCK_STATE : iblockstate;
        }

        public Iterator<IBlockState> iterator() {
            return this.ids.iterator();
        }

        public void addMapping(IBlockState p_189956_1_, int p_189956_2_) {
            this.ids.put(p_189956_1_, p_189956_2_);
        }
    }
}
