package dashcore.maze;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;

import java.util.List;

public class RoomInfo {
    /**
     * Amount of enties on sides
     */
    public final List<EnumFacing> entries;
    /**
     * Is room placed on path to center
     */
    public final boolean isPathToCenter;
    /**
     * Detects if this is a boss room
     */
    public final boolean isBossRoom;
    private ChunkPos chunkPos;

    public RoomInfo(ChunkPos chunkPos, List<EnumFacing> entries, boolean isPathToCenter, boolean isBossRoom) {
        this.chunkPos = chunkPos;
        this.entries = entries;
        this.isPathToCenter = isPathToCenter;
        this.isBossRoom = isBossRoom;
    }

    /**
     * Current chunk position
     *
     * @return
     */
    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public void setChunkPos(ChunkPos chunkPos) {
        this.chunkPos = chunkPos;
    }
}
