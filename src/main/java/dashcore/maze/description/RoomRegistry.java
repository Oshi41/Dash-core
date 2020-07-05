package dashcore.maze.description;

import dashcore.DashCore;
import dashcore.maze.algorythm.RoomInfo;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.*;
import java.util.stream.Collectors;

public class RoomRegistry {
    private static final Map<ResourceLocation, IRoomDescription> rooms = new HashMap<>();

    public static IRoomDescription find(ResourceLocation location) {
        return rooms.get(location);
    }

    public static IRoomDescription find(String id) {
        return find(new ResourceLocation(id));
    }

    /**
     * Registering room for arcana
     *
     * @param id
     * @param room
     */
    public static void register(ResourceLocation id, IRoomDescription room) {
        if (rooms.containsKey(id)) {
            DashCore.log.warn("Key is already existing: " + id.toString() + ". Owerwriting");
        }

        rooms.put(id, room);
    }

    public static IRoomDescription[][] build(RoomInfo[][] maze) {
        int size = maze.length;
        IRoomDescription[][] result = new IRoomDescription[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                RoomInfo current = maze[i][j];

                // possible neighbours we need to connect with
                Map<EnumFacing, IRoomDescription> neighbours = new HashMap<>();

                for (EnumFacing facing : EnumFacing.values()) {
                    if (facing.getAxis() == EnumFacing.Axis.Y)
                        continue;

                    int tempI = i + facing.getFrontOffsetX();
                    int tempJ = j + facing.getFrontOffsetZ();

                    if (tempI < 0 || tempJ < 0 || tempI >= size || tempJ >= size)
                        continue;

                    IRoomDescription roomDescription = result[tempI][tempJ];
                    if (roomDescription == null)
                        continue;

                    if (roomDescription
                            .getDoors()
                            .stream()
                            // get all possible connection side
                            .map(IDoor::getFacing)
                            .collect(Collectors.toSet())
                            // check if neighbour should have a connection to current room
                            .contains(facing.getOpposite())) {
                        neighbours.put(facing, roomDescription);
                    }
                }

                IRoomDescription description = get(current, neighbours);

                if (description == null) {
                    String message = "Can't find any possible room for scheme\n"
                            + current.toString()
                            + "\n";

                    if (!neighbours.isEmpty()) {
                        message += "With neighbours:\n";

                        for (Map.Entry<EnumFacing, IRoomDescription> entry : neighbours.entrySet()) {
                            message += String.format("%s: %s\n", entry.getKey(), entry.getValue().getId());
                        }
                    }

                    message += "\nPlease, report it to mod authors";

                    CrashReport.makeCrashReport(new Error(message), message);
                }

                result[i][j] = description;
            }
        }

        return result;
    }

    private static IRoomDescription get(RoomInfo info, Map<EnumFacing, IRoomDescription> neighbours) {
        List<IRoomDescription> suitable = rooms
                .values()
                .stream()
                .filter(x -> {
                    Set<EnumFacing> possibleFacings = x.getDoors().stream().map(IDoor::getFacing).collect(Collectors.toSet());
                    // exactly the same facings
                    return info.getEntries().containsAll(possibleFacings) && possibleFacings.size() == info.getEntries().size();
                })
                .filter(x -> {

                    for (Map.Entry<EnumFacing, IRoomDescription> entry : neighbours.entrySet()) {
                        EnumFacing facing = entry.getKey();
                        IRoomDescription room = entry.getValue();

                        if (!x.canConnect(room, facing))
                            return false;
                    }

                    return true;
                }).collect(Collectors.toList());

        Collections.shuffle(suitable);
        return suitable.stream().findFirst().orElse(null);
    }
}
