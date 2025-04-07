package net.turtleboi.turtlecore.util;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class PartyUtils {

    private static final String TEAM_PREFIX = "party_";

    public static void createParty(ServerPlayer leader) {
        Scoreboard scoreboard = leader.level().getScoreboard();
        String teamName = TEAM_PREFIX + leader.getUUID();

        if (scoreboard.getPlayerTeam(teamName) == null) {
            PlayerTeam team = scoreboard.addPlayerTeam(teamName);
            team.setAllowFriendlyFire(false);
            team.setNameTagVisibility(Team.Visibility.ALWAYS);
            team.setCollisionRule(Team.CollisionRule.NEVER);
            scoreboard.addPlayerToTeam(leader.getName().getString(), team);
            leader.sendSystemMessage(Component.literal("Party created. You are the leader."));
        } else {
            leader.sendSystemMessage(Component.literal("You already have a party."));
        }
    }

    public static void invitePlayer(ServerPlayer inviter, ServerPlayer invitee) {
        invitee.sendSystemMessage(Component.literal(inviter.getName().getString() + " has invited you to their party."));
    }

    public static void acceptInvite(ServerPlayer invitee, ServerPlayer inviter) {
        Scoreboard scoreboard = inviter.level().getScoreboard();
        String teamName = TEAM_PREFIX + inviter.getUUID();
        PlayerTeam team = scoreboard.getPlayerTeam(teamName);

        if (team != null) {
            scoreboard.addPlayerToTeam(invitee.getName().getString(), team);
            invitee.sendSystemMessage(Component.literal("You have joined " + inviter.getName().getString() + "'s party."));
            inviter.sendSystemMessage(Component.literal(invitee.getName().getString() + " has joined your party."));
        } else {
            invitee.sendSystemMessage(Component.literal("Failed to join party. The party does not exist."));
        }
    }

    public static void leaveParty(ServerPlayer player) {
        Scoreboard scoreboard = player.level().getScoreboard();
        PlayerTeam team = scoreboard.getPlayersTeam(player.getName().getString());

        if (team != null && team.getName().startsWith(TEAM_PREFIX)) {
            scoreboard.removePlayerFromTeam(player.getName().getString(), team);
            player.sendSystemMessage(Component.literal("You have left the party."));
        } else {
            player.sendSystemMessage(Component.literal("You are not in a party."));
        }
    }

    public static boolean isAlly(ServerPlayer player, ServerPlayer target) {
        Scoreboard scoreboard = player.level().getScoreboard();
        PlayerTeam playerTeam = scoreboard.getPlayersTeam(player.getName().getString());
        PlayerTeam targetTeam = scoreboard.getPlayersTeam(target.getName().getString());

        return playerTeam != null && playerTeam.equals(targetTeam);
    }
}
