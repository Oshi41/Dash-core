package dashcore.maze.description;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleRoomDescription implements IRoomDescription {
    private ResourceLocation templateLocation;
    private List<IDoor> doors;

    public SimpleRoomDescription(ResourceLocation templateLocation, IDoor... doors) {
        this.templateLocation = templateLocation;
        this.doors = Arrays.stream(doors).collect(Collectors.toList());
    }

    @Override
    public ResourceLocation getId() {
        return templateLocation;
    }

    @Override
    public Template loadTemplate(TemplateManager manager, PlacementSettings settings) {
        return manager.getTemplate(null, templateLocation);
    }

    @Override
    public List<IDoor> getDoors() {
        return doors;
    }
}
