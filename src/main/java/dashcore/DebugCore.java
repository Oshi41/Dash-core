package dashcore;

import dashcore.gravity.GravityHandler;
import dashcore.gravity.capability.GravityProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DebugCore {

    public static void setup() {
        MinecraftForge.EVENT_BUS.register(new DebugCore());
        MinecraftForge.EVENT_BUS.register(new GravityHandler());
    }

    @SubscribeEvent
    public void attachCap(final AttachCapabilitiesEvent<Chunk> event) {
        if (event.getObject().getWorld().provider.getDimension() == DimensionType.THE_END.getId()) {
            event.addCapability(new ResourceLocation(DashCore.ModId, "gravity"), new GravityProvider(0.2, event.getObject()));
        }
    }
}
