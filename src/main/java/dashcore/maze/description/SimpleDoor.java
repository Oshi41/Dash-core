package dashcore.maze.description;

import net.minecraft.util.EnumFacing;

public class SimpleDoor implements IDoor {
    private final EnumFacing facing;

    /**
     * @param facing - facing of door
     */
    public SimpleDoor(EnumFacing facing) {
        this.facing = facing;
    }

    @Override
    public EnumFacing getFacing() {
        return facing;
    }

    @Override
    public boolean canConnect(IConnect other, EnumFacing facing) {
        if (other instanceof IDoor) {
            return getFacing() == facing
                    && getFacing() == ((IDoor) other).getFacing().getOpposite();
        }

        return false;
    }
}
