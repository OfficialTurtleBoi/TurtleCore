package net.turtleboi.turtlecore.client.data;

import net.minecraft.resources.ResourceLocation;
import net.turtleboi.turtlecore.TurtleCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpikeData {
    public final float width;
    public final float height;
    public final ResourceLocation texture;
    public final float zTranslationOffset;
    public final float xAngleOffset;
    public final float yAngleOffset;
    public final float zAngleOffset;

    public SpikeData(float width, float height, ResourceLocation texture, float zTranslationOffset,
                     float xAngleOffset, float yAngleOffset, float zAngleOffset) {
        this.width = width;
        this.height = height;
        this.texture = texture;
        this.zTranslationOffset = zTranslationOffset;
        this.xAngleOffset = xAngleOffset;
        this.yAngleOffset = yAngleOffset;
        this.zAngleOffset = zAngleOffset;
    }

    public SpikeData(float width, float height, ResourceLocation texture, Random random) {
        this.width = width;
        this.height = height;
        this.texture = texture;
        this.zTranslationOffset = random.nextFloat();
        this.xAngleOffset = (random.nextFloat() * 60F) - 30F;
        this.yAngleOffset = (random.nextFloat() * 10F) - 5F;
        this.zAngleOffset = (random.nextFloat() * 30F) - 15F;
    }

    public static SpikeData[] createPremadeSpikes(Random random) {
        return new SpikeData[] {
                new SpikeData(5, 8, new ResourceLocation(TurtleCore.MOD_ID, "textures/spell_effects/ice_spikes/spike1.png"), random),
                new SpikeData(8, 11, new ResourceLocation(TurtleCore.MOD_ID, "textures/spell_effects/ice_spikes/spike2.png"), random),
                new SpikeData(8, 14, new ResourceLocation(TurtleCore.MOD_ID, "textures/spell_effects/ice_spikes/spike3.png"), random),
                new SpikeData(6, 11, new ResourceLocation(TurtleCore.MOD_ID, "textures/spell_effects/ice_spikes/spike4.png"), random),
                new SpikeData(6, 19, new ResourceLocation(TurtleCore.MOD_ID, "textures/spell_effects/ice_spikes/spike5.png"), random),
                new SpikeData(8, 8, new ResourceLocation(TurtleCore.MOD_ID, "textures/spell_effects/ice_spikes/spike6.png"), random)
        };
    }

    public static SpikeData[] getPremadeSpikesByIds(Random random, int... ids) {
        SpikeData[] allSpikes = createPremadeSpikes(random);
        List<SpikeData> selected = new ArrayList<>();
        for (int id : ids) {
            if (id >= 1 && id <= allSpikes.length) {
                selected.add(allSpikes[id - 1]);
            }
        }
        return selected.toArray(new SpikeData[0]);
    }
}
