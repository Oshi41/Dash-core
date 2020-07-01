package dashcore.registry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public class DashBlocks {
    public static Block ancientBrick = new Block(Material.ROCK);
    public static Block arcaniumMetal = new Block(Material.ROCK);
    public static Block heatTrap = new Block(Material.ROCK);

    public static Block soulStone = new Block(Material.ROCK);
    public static Block degradedBrick = new Block(Material.ROCK);
    public static Block dungeonLamp = new Block(Material.ROCK);
    public static Block ancientStone = new Block(Material.ROCK);
    public static Block dramixAltar = new Block(Material.ROCK);
    public static Block arcaniumPower = new Block(Material.ROCK);
    public static Block parasectaAltar = new Block(Material.ROCK);

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        String id = "divinerpg";

        registry.register(ancientBrick.setRegistryName(id, "ancient_brick"));
        registry.register(arcaniumMetal.setRegistryName(id, "arcanium_metal"));
        registry.register(heatTrap.setRegistryName(id, "heat_trap"));

        registry.register(soulStone.setRegistryName(id, "soul_stone"));
        registry.register(degradedBrick.setRegistryName(id, "degraded_brick"));
        registry.register(dungeonLamp.setRegistryName(id, "dungeon_lamp"));
        registry.register(ancientStone.setRegistryName(id, "ancient_stone"));
        registry.register(dramixAltar.setRegistryName(id, "dramix_altar"));
        registry.register(arcaniumPower.setRegistryName(id, "arcanium_power"));
        registry.register(parasectaAltar.setRegistryName(id, "parasecta_altar"));
    }
}
