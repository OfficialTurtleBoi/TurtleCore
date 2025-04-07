package net.turtleboi.turtlecore.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.turtleboi.turtlecore.capabilities.targeting.PlayerTargetingData;
import net.turtleboi.turtlecore.capabilities.targeting.PlayerTargetingProvider;
import net.turtleboi.turtlecore.network.CoreNetworking;
import net.turtleboi.turtlecore.network.packet.targeting.TargetS2CPacket;
import net.turtleboi.turtlecore.network.packet.targeting.ToggleLockS2CPacket;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class TargetingUtils {
    public static double range = 25.0;

    public static LivingEntity getTarget(Player player) {Vec3 lookVec = player.getLookAngle();
        Vec3 startVec = player.getEyePosition(1.0F);
        Vec3 endVec = startVec.add(lookVec.scale(range));

        EntityHitResult entityHitResult = rayTraceEntity(player, startVec, endVec);
        if (entityHitResult != null && entityHitResult.getEntity() instanceof LivingEntity) {
            return (LivingEntity) entityHitResult.getEntity();
        }
        return null;
    }

    public static EntityHitResult rayTraceEntity(Player player, Vec3 startVec, Vec3 endVec) {
        Level world = player.level();
        BlockHitResult blockHitResult = world.clip(new ClipContext(startVec, endVec, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

        if (blockHitResult.getType() != HitResult.Type.MISS) {
            BlockState blockState = world.getBlockState(blockHitResult.getBlockPos());
            if (blockState.isViewBlocking(world, blockHitResult.getBlockPos())) {
                endVec = blockHitResult.getLocation();
            }
        }

        AABB boundingBox = new AABB(startVec, endVec).inflate(1.0D, 1.0D, 1.0D);
        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, boundingBox, e -> e != player);

        EntityHitResult closestEntity = null;
        double closestDistance = range;

        for (LivingEntity entity : entities) {
            AABB entityBoundingBox = entity.getBoundingBox().inflate(entity.getPickRadius());
            Optional<Vec3> optionalHit = entityBoundingBox.clip(startVec, endVec);

            if (optionalHit.isPresent()) {
                double distance = startVec.distanceTo(optionalHit.get());

                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestEntity = new EntityHitResult(entity, optionalHit.get());
                }
            }
        }
        return closestEntity;
    }

    public static List<EntityHitResult> rayTraceEntities(Player player, Vec3 startVec, Vec3 endVec) {
        Level world = player.level();
        BlockHitResult blockHitResult = world.clip(new ClipContext(startVec, endVec,
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

        if (blockHitResult.getType() != HitResult.Type.MISS) {
            BlockState blockState = world.getBlockState(blockHitResult.getBlockPos());
            if (blockState.isViewBlocking(world, blockHitResult.getBlockPos())) {
                endVec = blockHitResult.getLocation();
            }
        }

        double range = startVec.distanceTo(endVec);
        AABB boundingBox = new AABB(startVec, endVec).inflate(1.0D, 1.0D, 1.0D);
        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, boundingBox, e -> e != player);

        List<EntityHitResult> hitResults = new ArrayList<>();
        for (LivingEntity entity : entities) {
            AABB entityBoundingBox = entity.getBoundingBox().inflate(entity.getPickRadius());
            Optional<Vec3> optionalHit = entityBoundingBox.clip(startVec, endVec);

            if (optionalHit.isPresent()) {
                double distance = startVec.distanceTo(optionalHit.get());
                if (distance <= range) {
                    hitResults.add(new EntityHitResult(entity, optionalHit.get()));
                }
            }
        }

        hitResults.sort(Comparator.comparingDouble(hit -> startVec.distanceTo(hit.getLocation())));
        return hitResults;
    }

    public static HitResult rayTraceClosest(Player player, Vec3 startVec, Vec3 endVec) {
        Level world = player.level();

        BlockHitResult blockHitResult = world.clip(new ClipContext(startVec, endVec, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        Vec3 closestHitPos = blockHitResult.getType() != HitResult.Type.MISS ? blockHitResult.getLocation() : endVec;
        double closestDistance = startVec.distanceTo(closestHitPos);

        AABB boundingBox = new AABB(startVec, endVec).inflate(1.0);
        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, boundingBox, e -> e != player);

        EntityHitResult closestEntityHit = null;

        for (LivingEntity entity : entities) {
            AABB entityBoundingBox = entity.getBoundingBox().inflate(entity.getPickRadius());
            Optional<Vec3> optionalHit = entityBoundingBox.clip(startVec, endVec);

            if (optionalHit.isPresent()) {
                Vec3 hitVec = optionalHit.get();
                double distance = startVec.distanceTo(hitVec);

                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestEntityHit = new EntityHitResult(entity, hitVec);
                }
            }
        }

        return closestEntityHit != null ? closestEntityHit : blockHitResult;
    }

    public static void toggleLockOn(Player player) {
        if (!isLockedOn(player)) {
            LivingEntity target = getTarget(player);
            if (target != null) {
                setLockedOn(player, true);
                CoreNetworking.sendToPlayer(new ToggleLockS2CPacket(true), (ServerPlayer) player);
                setLockedTarget(player, target);
                CoreNetworking.sendToPlayer(new TargetS2CPacket(target), (ServerPlayer) player);
            }
        } else {
            resetLockOn(player);
        }
    }


    public static void resetLockOn(Player player) {
        setLockedOn(player, false);
        CoreNetworking.sendToPlayer(new ToggleLockS2CPacket(false), (ServerPlayer) player);
        setLockedTarget(player, null);
        CoreNetworking.sendToPlayer(new TargetS2CPacket((LivingEntity) null), (ServerPlayer) player);
    }

    public static boolean isLockedOn(Player player) {
        AtomicBoolean result = new AtomicBoolean(false);
        player.getCapability(PlayerTargetingProvider.PLAYER_TARGET).ifPresent(targetData ->
                result.set(targetData.isLockedOn()));
        return result.get();
    }

    public static void setLockedOn(Player player, boolean result){
        player.getCapability(PlayerTargetingProvider.PLAYER_TARGET).ifPresent(targetData -> {
            targetData.setLockedOn(result);
            CoreNetworking.sendToPlayer(new ToggleLockS2CPacket(result), (ServerPlayer) player);
        });
    }

    public static LivingEntity getLockedTarget(Player player) {
        return player.getCapability(PlayerTargetingProvider.PLAYER_TARGET)
                .map(PlayerTargetingData::getLockedTarget)
                .orElse(null);
    }

    public static void setLockedTarget(Player player, LivingEntity entity){
        player.getCapability(PlayerTargetingProvider.PLAYER_TARGET).ifPresent(targetData -> {
            targetData.setLockedTarget(entity);
        });
    }
}
