package dashcore.gravity.event;

import dashcore.gravity.capability.IGravity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Gravity changed event
 */
public class GravityChangedEvent extends Event {
    /**
     * Current gravity capability
     */
    public final IGravity cap;

    /**
     * Owner of capability
     */
    public final ICapabilityProvider owner;

    public GravityChangedEvent(IGravity cap, ICapabilityProvider owner) {
        this.cap = cap;
        this.owner = owner;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
