package dashcore.debug.dimension;

import dashcore.registry.DashDimensions;
import net.minecraft.init.Biomes;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nullable;

public class DebugDimensionProvider extends WorldProvider {

    @Override
    protected void init() {
        biomeProvider = new BiomeProviderSingle(Biomes.VOID);
        nether = false;
        hasSkyLight = true;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new DebugChunkGenerator(this.world, world.rand.nextLong());
    }

    @Nullable
    @Override
    public String getSaveFolder() {
        return "Ds_Debug";
    }

    @Override
    public int getAverageGroundLevel() {
        return 5;
    }

    @Override
    public DimensionType getDimensionType() {
        return DashDimensions.debugDimension;
    }
}
