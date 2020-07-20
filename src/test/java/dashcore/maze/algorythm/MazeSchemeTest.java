package dashcore.maze.algorythm;

import com.google.common.collect.Lists;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.ChunkPos;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MazeSchemeTest {

    @Test
    public void getSize() {
        for (int i = 0; i < 8; i++) {
            MazeScheme scheme = new MazeScheme(i, new Random());
            Assert.assertEquals(0, scheme.getMazeSize() % 2);
        }
    }

    @Test
    public void generate_andWriteResults() {
        for (int size = 8; size < 30; size += 2) {
            MazeScheme scheme = new MazeScheme(size, new Random()).generate(new ChunkPos(0, 0));
            System.out.println(scheme);
        }
    }

    @Test
    public void generate_noNullRooms() {
        for (int size = 16; size < 32; size += 2) {
            for (int tries = 0; tries < 100; tries++) {
                MazeScheme scheme = new MazeScheme(size, new Random()).generate(new ChunkPos(0, 0));
                RoomInfo[][] rooms = scheme.getRooms();
                for (int i = 0; i < rooms.length; i++) {
                    for (int j = 0; j < rooms[i].length; j++) {
                        Assert.assertNotNull(rooms[i][j]);
                    }
                }
            }
        }
    }

    @Test
    public void generate_sameMaze() {
        for (Rotation rotation : Rotation.values()) {
            for (int size = 8; size <= 32; size += 2) {
                List<String> results = new ArrayList<>();
                Random random = new Random();
                int seed = random.nextInt();

                for (int tries = 0; tries < 5; tries++) {
                    random = new Random(seed);
                    MazeScheme scheme = new MazeScheme(size, random);
                    scheme.generate(new ChunkPos(0, 0));
                    results.add(scheme.toString());
                }

                for (String s : results) {

                    if (!s.equals(results.get(0))) {
                        Assert.fail(String.join("\n", results));
                    }
                }

                System.out.println(String.format("maze with size (%s) checked", size));
            }

            System.out.println("rotation passed: " + rotation);
        }
    }

    @Test
    public void generate_differentMazes() {
        for (int size = 8; size <= 32; size += 2) {
            List<String> results = new ArrayList<>();

            for (int tries = 0; tries < 10; tries++) {
                Random random = new Random(tries);
                MazeScheme scheme = new MazeScheme(size, random);
                scheme.generate(new ChunkPos(0, 0));
                results.add(scheme.toString());
            }

            for (int i = 1; i < results.size(); i++) {
                String s = results.get(0);
                String s1 = results.get(i);
                if (s.equals(results.get(i))) {
                    Assert.fail(String.format("Should be not equal:\n%s\n%s", s, s1));
                }
            }
        }
    }

    @Test
    public void rotate() {
        for (int size = 8; size <= 32; size += 2) {
            for (Rotation rotation90Degrees : Lists.newArrayList(Rotation.COUNTERCLOCKWISE_90, Rotation.CLOCKWISE_90)) {
                MazeScheme scheme = new MazeScheme(size, new Random()).generate(new ChunkPos(0, 0));
                String initial = scheme.toString();

                for (int i = 0; i < 4; i++) {
                    scheme.rotate(rotation90Degrees);
                }

                String sameMaze = scheme.toString();

                Assert.assertEquals(String.format("Should be equal:\n%s%s", initial, sameMaze), initial, sameMaze);
            }

            for (Rotation rotation180Degrees : Lists.newArrayList(Rotation.CLOCKWISE_180)) {
                MazeScheme scheme = new MazeScheme(size, new Random()).generate(new ChunkPos(0, 0));

                String maze2D = scheme.toString();

                for (int i = 0; i < 2; i++) {
                    scheme.rotate(rotation180Degrees);
                }

                String sameMaze = scheme.toString();

                Assert.assertEquals(String.format("Should be equal:\n%s%s", maze2D, sameMaze), maze2D, sameMaze);
            }
        }
    }
}