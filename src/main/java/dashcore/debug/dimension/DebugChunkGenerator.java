package dashcore.debug.dimension;

import dashcore.maze.structure.ArcanaMazeStructure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.MapGenStructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DebugChunkGenerator extends ChunkGeneratorFlat {
    private final Map<String, MapGenStructure> structureGenerators = new HashMap<>();
    private final World worldIn;
    private final Random random;

    public DebugChunkGenerator(World worldIn, long seed) {
        super(worldIn, seed, false, "");
        this.worldIn = worldIn;
        this.random = new Random(seed);

        structureGenerators.put("ArcanaMaze", new ArcanaMazeStructure(32));

//        structureGenerators.put("Dramix", new NbtLargeStructure(worldIn,
//                new ResourceLocation(DashCore.ModId, "dramix"),
//                5,
//                new ChunkPos(2, 2),
//                chunkPos -> chunkPos.getBlock(0, 15, 0),
//                PositionUtil.getRandomRotation(random))
//        );

//        structureGenerators.put("Parasecta", new NbtLargeStructure(worldIn,
//                new ResourceLocation(DashCore.ModId, "parasecta"),
//                5,
//                new ChunkPos(2, 2),
//                chunkPos -> chunkPos.getBlock(0,30,0),
//                PositionUtil.getRandomRotation(random))
//        );
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        ChunkPrimer primer = new ChunkPrimer();

//        for (int x = 0; x < 16; x++) {
//            for (int z = 0; z < 16; z++) {
//                primer.setBlockState(x, 0, z, Blocks.BEDROCK.getDefaultState());
//                primer.setBlockState(x, 5, z, Blocks.GRASS.getDefaultState());
//
//                for (int y = 1; y < 5; y++) {
//                    primer.setBlockState(x, y, z, Blocks.DIRT.getDefaultState());
//                }
//            }
//        }

        for (MapGenBase mapgenbase : this.structureGenerators.values()) {
            mapgenbase.generate(worldIn, chunkX, chunkZ, primer);
        }

        Chunk chunk = new Chunk(worldIn, primer, chunkX, chunkZ);
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(int x, int z) {
        ChunkPos chunkPos = new ChunkPos(x, z);

        this.random.setSeed(this.worldIn.getSeed());
        long k = this.random.nextLong() / 2L * 2L + 1L;
        long l = this.random.nextLong() / 2L * 2L + 1L;
        this.random.setSeed((long) x * k + (long) z * l ^ this.worldIn.getSeed());

        for (MapGenStructure mapgenstructure : this.structureGenerators.values()) {
            mapgenstructure.generateStructure(worldIn, random, chunkPos);
        }

        BlockPos chunkSTart = chunkPos.getBlock(0, 0, 0);

        Biome biome = worldIn.getBiome(chunkSTart);
        biome.decorate(worldIn, random, chunkSTart);
    }
}
