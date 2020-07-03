package dashcore.maze;

import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.List;

public interface IRoomDescription {
    /**
     * Current room template.
     * Should be not more than chunk size
     *
     * @return
     */
    Template loadTemplate(TemplateManager manager);

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
    default boolean canConnect(IRoomDescription other) {
        if (other != null) {
            List<IDoor> doors = getDoors();
            if (!doors.isEmpty()) {

                List<IDoor> otherDoors = other.getDoors();
                if (!otherDoors.isEmpty()) {
                    for (IDoor door : doors) {
                        if (otherDoors.stream().anyMatch(x -> x.isConnected(door)))
                            return true;
                    }
                }
            }
        }

        return false;
    }
}
