package dashcore.gravity.event;

import dashcore.gravity.GravityUtils;
import dashcore.gravity.capability.IGravity;
import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Used to detect entities affected with current gravity
 */
public class GravityAffectedEvent extends Event {
    /**
     * Gravity source
     */
    public final ICapabilityProvider owner;

    /**
     * Gravity capability
     */
    public final IGravity cap;

    /**
     * List of affected entities
     */
    public final Set<Entity> affectedEntities;

    public GravityAffectedEvent(ICapabilityProvider owner, IGravity cap) {
        this.owner = owner;
        this.cap = cap;
        affectedEntities = new HashSet<>();

        if (owner instanceof Entity) {
            // adding entity to affect list
            forEntity(((Entity) owner));
        } else if (owner instanceof Chunk) {

            // adding all entities inside chunk as affected
            Arrays.stream(((Chunk) owner).getEntityLists())
                    .flatMap(Collection::parallelStream)
                    .collect(Collectors.toList())
                    .forEach(this::forEntity);
        }
    }

    /**
     * adding entity to affected list if it has gravity
     *
     * @param e
     */
    private void forEntity(Entity e) {
        if (GravityUtils.getGravity(e) != 0) {
            affectedEntities.add(e);
        }
    }
}
