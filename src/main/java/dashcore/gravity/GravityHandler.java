package dashcore.gravity;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dashcore.gravity.capability.IGravity;
import dashcore.gravity.event.GravityAffectedEvent;
import dashcore.gravity.event.GravityChangedEvent;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class GravityHandler {
    private final int delay;

    /**
     * List of current gravity sources
     */
    private final Cache<ICapabilityProvider, IGravity> gravitySources;

    /**
     * List of affected events
     */
    private final Cache<Entity, Cache<IGravity, Boolean>> affectingEntities;

    public GravityHandler() {
        gravitySources = CacheBuilder.newBuilder().weakKeys().weakValues().build();
        affectingEntities = CacheBuilder.newBuilder().weakKeys().build();
        delay = 20;
    }

    @SubscribeEvent
    public void onGravityChanged(GravityChangedEvent event) {
        ICapabilityProvider key = event.owner;
        IGravity value = event.cap;

        if (value.getGravityMultiplier() == 1) {
            gravitySources.invalidate(key);
        } else {
            gravitySources.put(key, value);
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        // only on end side
        if (event.phase != TickEvent.Phase.END)
            return;

        // only on server side
        if (event.side != Side.SERVER)
            return;

        // recalculating entities gravity sources
        if (FMLCommonHandler.instance().getMinecraftServerInstance().getTickCounter() % delay == 0) {
            recalculateSources();
        }

        // applying gravity
        handleGravity(false);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        // Only on end phase
        if (event.phase != TickEvent.Phase.END)
            return;

        // only on client side
        if (event.side != Side.CLIENT)
            return;

        // game was paused
        if (net.minecraft.client.Minecraft.getMinecraft().isGamePaused())
            return;

        // no client player exist
        if (net.minecraft.client.Minecraft.getMinecraft().player == null)
            return;

        // recalculating entities gravity sources
        if (net.minecraft.client.Minecraft.getMinecraft().world.getTotalWorldTime() % delay == 0) {
            recalculateSources();
        }

        // applying gravity
        handleGravity(true);
    }

    @SubscribeEvent
    public void onLanding(LivingFallEvent event) {
        Entity entity = event.getEntity();
        if (entity == null)
            return;

        Cache<IGravity, Boolean> sources = affectingEntities.getIfPresent(entity);
        if (sources == null || sources.size() < 1)
            return;

        double jumpHeight = sources
                .asMap()
                .keySet()
                .stream()
                .mapToDouble(IGravity::maxNoHarmJumpHeight)
                .average()
                .orElse(0);

        if (jumpHeight <= 0)
            return;

        event.setDistance((float) (event.getDistance() - jumpHeight));
    }

    private void handleGravity(Boolean client) {
        affectingEntities.asMap().forEach((entity, cache) -> {
            // no gravity sources
            if (cache.size() < 1
                    // singleplayer contains both sides
                    || entity.world.isRemote != client)
                return;

            // detecting current entity gravity
            double currentGravity = GravityUtils.getGravity(entity);
            if (currentGravity == 0)
                return;

            // calculating average gravitation level
            double mulitplier = cache
                    .asMap()
                    .keySet()
                    .stream()
                    .mapToDouble(IGravity::getGravityMultiplier)
                    .average()
                    .orElse(0);

            if (mulitplier == 0)
                return;

            double gravityTick = currentGravity * mulitplier;
            entity.motionY += currentGravity - gravityTick;
        });
    }

    private void recalculateSources() {
        // clear all
        affectingEntities.invalidateAll();

        gravitySources.asMap().forEach((provider, iGravity) -> {
            // posting event to find affecting entity
            GravityAffectedEvent event = new GravityAffectedEvent(provider, iGravity);
            MinecraftForge.EVENT_BUS.post(event);

            // event canceled or no affecting entities
            if (event.isCanceled() || event.affectedEntities.isEmpty())
                return;

            for (Entity entity : event.affectedEntities) {
                // find gravity sources for current entity
                Cache<IGravity, Boolean> cache = affectingEntities.getIfPresent(entity);
                if (cache == null) {
                    // creating new cache for new entity
                    cache = CacheBuilder.newBuilder().weakKeys().build();
                    // adding entity to map
                    affectingEntities.put(entity, cache);
                }

                cache.put(iGravity, true);
            }
        });
    }
}
