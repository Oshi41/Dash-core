package dashcore.maze.description;

import net.minecraft.util.EnumFacing;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class IRoomDescriptionTest {
    private final IRoomDescription room4;
    private final List<IRoomDescription> room3 = new ArrayList<>();
    private final List<IRoomDescription> room2 = new ArrayList<>();
    private final List<IRoomDescription> room1 = new ArrayList<>();
    private final List<IRoomDescription> allRooms = new ArrayList<>();
    private final List<EnumFacing> orientations;

    public IRoomDescriptionTest() {

        orientations = Arrays.stream(EnumFacing.values())
                .filter(x -> x.getAxis() != EnumFacing.Axis.Y)
                .collect(Collectors.toList());

        SimpleDoor southDoor = new SimpleDoor(EnumFacing.SOUTH);
        SimpleDoor northDoor = new SimpleDoor(EnumFacing.NORTH);
        SimpleDoor eastDoor = new SimpleDoor(EnumFacing.EAST);
        SimpleDoor westDoor = new SimpleDoor(EnumFacing.WEST);

        List<SimpleDoor> allDoors = Arrays.asList(southDoor, northDoor, westDoor, eastDoor);
        room4 = new SimpleRoomDescription(null, allDoors.stream().toArray(SimpleDoor[]::new));
        for (SimpleDoor door : allDoors) {
            room1.add(new SimpleRoomDescription(null, door));
        }

        for (int i = 0; i < allDoors.size(); i++) {
            ArrayList<SimpleDoor> copy = new ArrayList<>(allDoors);
            copy.remove(i);
            room3.add(new SimpleRoomDescription(null, copy.stream().toArray(SimpleDoor[]::new)));
        }

        for (SimpleDoor firstDoor : allDoors) {
            for (SimpleDoor secondDoor : allDoors) {
                if (firstDoor == secondDoor)
                    continue;

                room2.add(new SimpleRoomDescription(null, firstDoor, secondDoor));
            }
        }

        allRooms.addAll(room1);
        allRooms.addAll(room2);
        allRooms.addAll(room3);
        allRooms.add(room4);
    }

    @Test
    public void canConnect_4doors_eachOther() {
        for (EnumFacing facing : orientations) {
            Assert.assertTrue(room4.canConnect(room4, facing));
        }
    }

    @Test
    public void canConnect_3doors_with4door() {
        for (IRoomDescription roomDescription : room3) {
            List<EnumFacing> facings = roomDescription
                    .getDoors()
                    .stream()
                    .map(x -> x.getFacing())
                    .collect(Collectors.toList());

            for (EnumFacing facing : orientations) {
                boolean shouldConnect = facings.contains(facing);

                if (shouldConnect != roomDescription.canConnect(room4, facing)) {

                    roomDescription.canConnect(room4, facing);
                    Assert.fail();
                }
            }
        }
    }

    @Test
    public void canConnect_4entries_allConnect() {
        for (IRoomDescription room : allRooms) {
            for (IDoor door : room.getDoors()) {
                Assert.assertTrue(room.canConnect(room4, door.getFacing()));
            }
        }
    }

    @Test
    public void canConnect_connectionCounts() {

        Map<Integer, List<IRoomDescription>> byConnections = new HashMap<>();
        byConnections.put(1, room1);
        byConnections.put(2, room2);
        byConnections.put(3, room3);

        byConnections.forEach((connections, rooms) -> {
            for (IRoomDescription roomDescription : rooms) {
                long connecCount = 0;

                for (EnumFacing facing : orientations) {
                    if (roomDescription.canConnect(room4, facing))
                        connecCount++;
                }

                Assert.assertEquals((long) connections, connecCount);
            }
        });


    }

    @Test
    public void canConnect() {
        for (IRoomDescription first : allRooms) {
            List<EnumFacing> firstFacings = first.getDoors().stream().map(x -> x.getFacing()).collect(Collectors.toList());
            for (IRoomDescription second : allRooms) {
                List<EnumFacing> secondFacings = second.getDoors().stream().map(x -> x.getFacing()).collect(Collectors.toList());

                for (EnumFacing firstFacing : firstFacings) {
                    boolean canConnect = secondFacings.contains(firstFacing.getOpposite());

                    Assert.assertEquals(canConnect, first.canConnect(second, firstFacing));
                }
            }
        }
    }
}