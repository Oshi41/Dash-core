package dashcore;

import dashcore.debug.DungeonComponentDramix;
import dashcore.debug.DungeonComponentParasecta;
import dashcore.gravity.GravityHandler;
import dashcore.gravity.capability.GravityProvider;
import dashcore.world.MockWorld;
import dashcore.world.TemplateConverter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DebugCore {

    public static void setup() {
        MinecraftForge.EVENT_BUS.register(new DebugCore());
        MinecraftForge.EVENT_BUS.register(new GravityHandler());
    }

    public static void afterLoading() {
        Map<String, WorldGenerator> rooms = new HashMap<String, WorldGenerator>() {
            {
                put("dramix", new DungeonComponentDramix());
                put("parasecta", new DungeonComponentParasecta());
            }
        };

        File folder = new File("D:\\IdeaProjects\\Dash-core\\src\\main\\resources\\assets\\dc\\structures");

        rooms.forEach((s, generator) -> {
            File roomFolder = new File(folder, s);

            if (roomFolder.exists())
                return;

            roomFolder.mkdirs();

            MockWorld world = new MockWorld();
            generator.generate(world, world.rand, BlockPos.ORIGIN);
            world.chunks.forEach((chunkPos, iChunkStorage) -> {
                TemplateConverter template = new TemplateConverter(iChunkStorage);
                File file = new File(roomFolder, chunkPos.toString() + ".nbt");
                template.writeToFile(file);
            });
        });
    }

    @SubscribeEvent
    public void attachCap(final AttachCapabilitiesEvent<Chunk> event) {
        if (event.getObject().getWorld().provider.getDimension() == DimensionType.THE_END.getId()) {
            event.addCapability(new ResourceLocation(DashCore.ModId, "gravity"), new GravityProvider(0.2, event.getObject()));
        }
    }
}
