package dashcore.registry;

import dashcore.DashCore;
import dashcore.debug.Generator;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
@GameRegistry.ObjectHolder(DashCore.ModId)
public class DashItems {
    @GameRegistry.ObjectHolder("generator")
    public static Item generator = new Generator("generator");

    @GameRegistry.ObjectHolder("debug_portal")
    public static Item debug_portal;

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(generator);
    }
}
