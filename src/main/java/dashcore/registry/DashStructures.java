package dashcore.registry;

import dashcore.DashCore;
import dashcore.maze.description.IRoomDescription;
import dashcore.maze.description.RoomRegistry;
import dashcore.maze.description.SimpleDoor;
import dashcore.maze.description.SimpleRoomDescription;
import dashcore.maze.structure.ArcanaChunkRoom;
import dashcore.maze.structure.ArcanaMazeStart;
import dashcore.structure.NbtChunkTemplate;
import dashcore.structure.NbtStructureStart;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.MapGenStructureIO;

import java.util.ArrayList;
import java.util.List;

public class DashStructures {

    public static void register() {
        registerArcanaRooms();

        MapGenStructureIO.registerStructure(NbtStructureStart.class, "NbtStructureStart");
        MapGenStructureIO.registerStructureComponent(NbtChunkTemplate.class, "NbtChunkTemplate");

        MapGenStructureIO.registerStructure(ArcanaMazeStart.class, "ArcanaMazeStart");
        MapGenStructureIO.registerStructureComponent(ArcanaChunkRoom.class, "ArcanaChunkRoom");
    }

    private static void registerArcanaRooms() {
        List<IRoomDescription> arcanaRooms = new ArrayList<>();

        arcanaRooms.add(
                new SimpleRoomDescription(new ResourceLocation(DashCore.ModId, "arcana/1n"),
                        new SimpleDoor(EnumFacing.NORTH)));

        arcanaRooms.add(
                new SimpleRoomDescription(new ResourceLocation(DashCore.ModId, "arcana/1s"),
                        new SimpleDoor(EnumFacing.SOUTH)));

        arcanaRooms.add(
                new SimpleRoomDescription(new ResourceLocation(DashCore.ModId, "arcana/1e"),
                        new SimpleDoor(EnumFacing.EAST)));

        arcanaRooms.add(
                new SimpleRoomDescription(new ResourceLocation(DashCore.ModId, "arcana/1w"),
                        new SimpleDoor(EnumFacing.WEST)));

        arcanaRooms.add(
                new SimpleRoomDescription(new ResourceLocation(DashCore.ModId, "arcana/2ew"),
                        new SimpleDoor(EnumFacing.EAST), new SimpleDoor(EnumFacing.WEST)));

        arcanaRooms.add(
                new SimpleRoomDescription(new ResourceLocation(DashCore.ModId, "arcana/2ne"),
                        new SimpleDoor(EnumFacing.NORTH), new SimpleDoor(EnumFacing.EAST)));

        arcanaRooms.add(
                new SimpleRoomDescription(new ResourceLocation(DashCore.ModId, "arcana/2se"),
                        new SimpleDoor(EnumFacing.SOUTH), new SimpleDoor(EnumFacing.EAST)));

        arcanaRooms.add(
                new SimpleRoomDescription(new ResourceLocation(DashCore.ModId, "arcana/2sn"),
                        new SimpleDoor(EnumFacing.SOUTH), new SimpleDoor(EnumFacing.NORTH)));

        arcanaRooms.add(
                new SimpleRoomDescription(new ResourceLocation(DashCore.ModId, "arcana/2sw"),
                        new SimpleDoor(EnumFacing.SOUTH), new SimpleDoor(EnumFacing.WEST)));

        arcanaRooms.add(
                new SimpleRoomDescription(new ResourceLocation(DashCore.ModId, "arcana/2wn"),
                        new SimpleDoor(EnumFacing.WEST), new SimpleDoor(EnumFacing.NORTH)));

        arcanaRooms.add(
                new SimpleRoomDescription(new ResourceLocation(DashCore.ModId, "arcana/3ewn"),
                        new SimpleDoor(EnumFacing.EAST),
                        new SimpleDoor(EnumFacing.WEST),
                        new SimpleDoor(EnumFacing.NORTH)));

        arcanaRooms.add(
                new SimpleRoomDescription(new ResourceLocation(DashCore.ModId, "arcana/3ews"),
                        new SimpleDoor(EnumFacing.EAST),
                        new SimpleDoor(EnumFacing.WEST),
                        new SimpleDoor(EnumFacing.SOUTH)));

        arcanaRooms.add(
                new SimpleRoomDescription(new ResourceLocation(DashCore.ModId, "arcana/3nse"),
                        new SimpleDoor(EnumFacing.NORTH),
                        new SimpleDoor(EnumFacing.SOUTH),
                        new SimpleDoor(EnumFacing.EAST)));

        arcanaRooms.add(
                new SimpleRoomDescription(new ResourceLocation(DashCore.ModId, "arcana/3wns"),
                        new SimpleDoor(EnumFacing.WEST),
                        new SimpleDoor(EnumFacing.NORTH),
                        new SimpleDoor(EnumFacing.SOUTH)));

        arcanaRooms.add(
                new SimpleRoomDescription(new ResourceLocation(DashCore.ModId, "arcana/4"),
                        new SimpleDoor(EnumFacing.EAST),
                        new SimpleDoor(EnumFacing.WEST),
                        new SimpleDoor(EnumFacing.NORTH),
                        new SimpleDoor(EnumFacing.SOUTH)));

        arcanaRooms.forEach(x -> RoomRegistry.register(x.getId(), x));
    }
}
