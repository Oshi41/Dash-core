package dashcore.maze.algorythm;

import net.minecraft.util.math.ChunkPos;
import org.junit.Assert;
import org.junit.Test;

public class MazeGenerationTest {

    @Test
    public void generate() {
        int iteration = 0;

        // about 50 tries
        for (int i = 0; i < 50; i++) {
            ChunkPos chunkPos = new ChunkPos(i, i);

            // dynamic size
            for (int j = 15; j < 30; j++) {
                RoomInfo[][] generate = MazeGeneration.generate(chunkPos, j);

                System.out.println(String.format("%s iteration, size is %s", ++iteration, j));

                for (int x = 0; x < generate.length; x++) {
                    for (int z = 0; z < generate[x].length; z++) {
                        Assert.assertNotNull("Pos is: " + new ChunkPos(x, z).toString(), generate[x][z]);
                    }
                }
            }
        }
    }
}