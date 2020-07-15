package dashcore.maze.description;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.List;

public interface IRoomDescription extends IConnect {

    /**
     * Id of room for resistry purpose
     *
     * @return
     */
    ResourceLocation getId();

    /**
     * Current room template.
     * Should be not more than chunk size
     *
     * @param manager  - current template manager
     * @param settings - modified settings for placement
     * @return
     */
    Template loadTemplate(TemplateManager manager, PlacementSettings settings);

    /**
     * Gets list of room doors
     *
     * @return
     */
    List<IDoor> getDoors();

    /**
     * Checks wherever can connect current room
     *
     * @param other
     * @return
     */
    @Override
    default boolean canConnect(IConnect other, EnumFacing facing) {
        if (other instanceof IRoomDescription) {
            List<IDoor> doors = getDoors();
            // closed room (???)
            if (!doors.isEmpty()) {

                List<IDoor> otherDoors = ((IRoomDescription) other).getDoors();
                // closed room (???)
                if (!otherDoors.isEmpty()) {
                    // loop through own doors
                    for (IDoor door : doors) {
                        // loop through others doors
                        for (IDoor otherDoor : otherDoors) {
                            // check if our door can be connected wth other
                            if (door.canConnect(otherDoor, facing))
                                return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
