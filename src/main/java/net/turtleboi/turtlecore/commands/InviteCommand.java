package net.turtleboi.turtlecore.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.turtleboi.turtlecore.capabilities.party.PlayerPartyProvider;

public class InviteCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tbrpg")
                .then(Commands.literal("invite")
                        .then(Commands.argument("playerName", StringArgumentType.string())
                                .executes(InviteCommand::invitePlayer))));
    }

    private static int invitePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer invitingPlayer = source.getPlayerOrException();
        String playerName = StringArgumentType.getString(context, "playerName");
        ServerPlayer invitedPlayer = source.getServer().getPlayerList().getPlayerByName(playerName);

        if (invitedPlayer == null) {
            source.sendFailure(Component.literal("Player not found"));
            return 0;
        }

        invitingPlayer.getCapability(PlayerPartyProvider.PLAYER_PARTY).ifPresent(party -> {
            if (!party.getPartyMembers().contains(invitingPlayer.getUUID())) {
                party.addMember(invitingPlayer.getUUID());
            }
            party.addMember(invitedPlayer.getUUID());
        });

        //source.sendSuccess(Component.literal("Invited " + playerName + " to your party"), true);
        invitedPlayer.sendSystemMessage(Component.literal("You have been invited to join " + invitingPlayer.getName().getString() + "'s party"));
        return 1;
    }
}
