package net.turtleboi.turtlecore.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

public class CooldownResetEvent extends Event {
    private final Player player;

    public CooldownResetEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
