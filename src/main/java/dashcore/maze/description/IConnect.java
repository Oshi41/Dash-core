package dashcore.maze.description;

import net.minecraft.util.EnumFacing;

public interface IConnect {
    /**
     * Check if other object can be connected from facing
     *
     * @param other  - other connection object
     * @param facing - facing of connection
     * @return
     */
    boolean canConnect(IConnect other, EnumFacing facing);
}
