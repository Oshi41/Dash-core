package dashcore.gravity.capability;

import dashcore.gravity.event.GravityChangedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

public class GravityCapability<T extends ICapabilityProvider> implements IGravity {
    /**
     * Capability owner
     */
    private final WeakReference<T> owner;
    private double gravity;

    /**
     * NBT ctor
     */
    public GravityCapability() {
        this(null, 0);
    }

    /**
     * Supports change gravity event
     *
     * @param owner
     * @param multiplier
     */
    public GravityCapability(T owner, double multiplier) {
        this.owner = new WeakReference<>(owner);
        setGravityMultiplier(multiplier);
    }

    @Override
    public double getGravityMultiplier() {
        return gravity;
    }

    @Override
    public void setGravityMultiplier(double value) {
        if (gravity == value)
            return;

        gravity = value;
        fireChanges();
    }

    @Nullable
    @Override
    public ICapabilityProvider getOwner() {
        return owner.get();
    }

    private void fireChanges() {
        ICapabilityProvider provider = owner.get();
        if (provider == null)
            return;

        MinecraftForge.EVENT_BUS.post(new GravityChangedEvent(this, provider));
    }
}
