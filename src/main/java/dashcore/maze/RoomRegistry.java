package dashcore.maze;

import dashcore.DashCore;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class RoomRegistry {
    private static final Random r = new Random();
    private static final Map<ResourceLocation, IRoomDescription> rooms = new HashMap<>();

    public static IRoomDescription find(ResourceLocation location) {
        return rooms.get(location);
    }

    public static IRoomDescription find(String id) {
        return find(new ResourceLocation(id));
    }

    public static void register(ResourceLocation id, IRoomDescription room) {
        if (rooms.containsKey(id)) {
            DashCore.log.warn("Key is already existing: " + id.toString() + ". Owerwriting");
        }

        rooms.put(id, room);
    }

    /**
     * Returns random connected room. If null passed, any random room
     *
     * @param toConnect - room to connect
     * @return
     */
    @Nullable
    public static IRoomDescription getRandom(@Nullable IRoomDescription toConnect) {
        IRoomDescription result;

        if (toConnect == null) {
            int count = r.nextInt(rooms.size());
            result = rooms.values().stream().skip(count).findFirst().orElse(rooms.values().stream().findFirst().orElse(null));
        } else {
            List<IRoomDescription> suitable = rooms.values().stream().filter(x -> x.canConnect(toConnect)).collect(Collectors.toList());
            Collections.shuffle(suitable);
            result = suitable.stream().findFirst().orElse(null);
        }

        if (result == null) {
            DashCore.log.warn("Can't find possible maze room to connect. Please, report it to mod authors.");
        }

        return result;
    }
}
