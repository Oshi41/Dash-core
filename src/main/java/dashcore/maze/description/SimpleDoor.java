package dashcore.maze.description;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class SimpleDoor implements IDoor {
    private final EnumFacing facing;
    private StructureBoundingBox doorBlocks;

    /**
     * @param facing     - facing of door
     * @param doorBlocks - chunk position of door blocks
     */
    public SimpleDoor(EnumFacing facing, StructureBoundingBox doorBlocks) {
        this.facing = facing;
        this.doorBlocks = doorBlocks;
    }

    @Override
    public EnumFacing getFacing() {
        return facing;
    }

    @Override
    public boolean canConnect(IConnect other, EnumFacing facing) {
        if (other instanceof SimpleDoor) {
            StructureBoundingBox otherDoorBlocks = ((SimpleDoor) other).doorBlocks;
            // moving door to other chunk
            otherDoorBlocks.offset(facing.getFrontOffsetX() * 16, facing.getFrontOffsetY() * 16, facing.getFrontOffsetZ() * 16);

            // moving current door 'behind' to be sure doors can meet
            StructureBoundingBox currentDoor = new StructureBoundingBox(doorBlocks);
            currentDoor.offset(facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());

            return currentDoor.intersectsWith(otherDoorBlocks);
        }

        return false;
    }
}
