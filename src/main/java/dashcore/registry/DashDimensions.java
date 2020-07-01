package dashcore.registry;

import dashcore.DashCore;
import dashcore.debug.dimension.DebugDimensionProvider;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class DashDimensions {
    public static DimensionType debugDimension;

    public static void register() {
        debugDimension = DimensionType.register(
                DashCore.ModId + ":debug",
                "_debug",
                15,
                DebugDimensionProvider.class,
                false
        );

        DimensionManager.registerDimension(debugDimension.getId(), debugDimension);
    }
}
