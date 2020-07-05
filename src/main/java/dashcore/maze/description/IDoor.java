package dashcore.maze.description;

import net.minecraft.util.EnumFacing;

/**
 * Description of room door
 */
public interface IDoor extends IConnect {
    /**
     * Facing of door
     *
     * @return
     */
    EnumFacing getFacing();
}
