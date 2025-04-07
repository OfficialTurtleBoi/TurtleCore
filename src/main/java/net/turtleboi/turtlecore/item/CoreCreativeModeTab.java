package net.turtleboi.turtlecore.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.turtleboi.turtlecore.TurtleCore;
import org.jetbrains.annotations.NotNull;

public class CoreCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TurtleCore.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TURTLECORE_TAB = CREATIVE_MODE_TABS.register("turtlecore_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(Items.TURTLE_EGG))
                    .title(Component.translatable("creativetab.turtlecore_tab"))
                    .displayItems((pParameters, pOutput) -> {

                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
