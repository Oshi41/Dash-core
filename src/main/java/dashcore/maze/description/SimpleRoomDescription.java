package dashcore.maze.description;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.List;

public class SimpleRoomDescription implements IRoomDescription {
    private ResourceLocation templateLocation;
    private List<IDoor> doors;

    public SimpleRoomDescription(ResourceLocation templateLocation, List<IDoor> doors) {
        this.templateLocation = templateLocation;
        this.doors = doors;
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
