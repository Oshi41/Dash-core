package dashcore;

import dashcore.gravity.capability.GravityCapability;
import dashcore.gravity.capability.GravityStorage;
import dashcore.gravity.capability.IGravity;
import dashcore.registry.DashDimensions;
import dashcore.registry.DashStructures;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;

@Mod(
        modid = DashCore.ModId,
        name = DashCore.ModName,
        version = DashCore.Version
)
public class DashCore {
    /**
     * Required variables
     */
    public static final String ModId = "dc";
    public static final String ModName = "Dash Core";
    public static final String Version = "0.0.1";

    @Mod.Instance
    public static DashCore instance;

    public static org.apache.logging.log4j.Logger log = LogManager.getLogger();

    public DashCore() {
        DebugCore.setup();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log = event.getModLog();
        DashStructures.register();
        DashDimensions.register();

        CapabilityManager.INSTANCE.register(IGravity.class, new GravityStorage(), GravityCapability::new);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        DebugCore.afterLoading();
    }
}

