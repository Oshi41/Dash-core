package dashcore.maze.algorythm;

import dashcore.util.NbtUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashSet;
import java.util.Set;

/**
 * Description of current room inside maze
 */
public class RoomInfo implements INBTSerializable<NBTTagCompound> {
    private final Set<EnumFacing> entries;
    private boolean isPathToCenter;
    private boolean isBossRoom;
    private ChunkPos chunkPos;

    /**
     * NBT ctor
     */
    public RoomInfo() {
        this(new ChunkPos(0, 0), new HashSet<>(), false, false);
    }

    public RoomInfo(ChunkPos chunkPos, Set<EnumFacing> entries, boolean isPathToCenter, boolean isBossRoom) {
        this.chunkPos = chunkPos;
        this.entries = entries;
        this.isPathToCenter = isPathToCenter;
        this.isBossRoom = isBossRoom;
    }

    /**
     * Amount of enties on sides
     */
    public Set<EnumFacing> getEntries() {
        return entries;
    }

    /**
     * Is room placed on path to center
     */
    public boolean isPathToCenter() {
        return isPathToCenter;
    }

    /**
     * Detects if this is a boss room
     */
    public boolean isBossRoom() {
        return isBossRoom;
    }

    /**
     * Current chunk position
     */
    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        NbtUtil.write(nbt, chunkPos);
        nbt.setBoolean("boss", isBossRoom);
        nbt.setBoolean("path", isPathToCenter);

        NBTTagList entries = new NBTTagList();
        for (EnumFacing entry : this.entries) {
            entries.appendTag(new NBTTagString(entry.toString()));
        }

        nbt.setTag("entries", entries);

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        chunkPos = NbtUtil.read(nbt);
        isBossRoom = nbt.getBoolean("boss");
        isPathToCenter = nbt.getBoolean("path");
        entries.clear();

        // 8 is for NBTTagString
        NBTTagList entries = nbt.getTagList("entries", 8);

        for (int i = 0; i < entries.tagCount(); i++) {
            EnumFacing facing = EnumFacing.byName(entries.getStringTagAt(i));
            if (facing != null) {
                this.entries.add(facing);
            }
        }
    }

    @Override
    public String toString() {
        String result = String.format(
                "+%s+\n" +
                        "%s%s%s\n" +
                        "+%s+\n",
                entries.contains(EnumFacing.SOUTH) ? "↑" : "-",
                entries.contains(EnumFacing.EAST) ? "←" : "|",
                isBossRoom ? "B" : isPathToCenter ? "#" : " ",
                entries.contains(EnumFacing.WEST) ? "→" : "|",
                entries.contains(EnumFacing.NORTH) ? "↓" : "-"
        );

        return result;
    }
}
