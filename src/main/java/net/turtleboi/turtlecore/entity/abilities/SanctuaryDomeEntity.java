package net.turtleboi.turtlecore.entity.abilities;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.turtleboi.turtlecore.util.PartyUtils;

import java.util.List;
import java.util.Set;
import java.util.EnumSet;

public class SanctuaryDomeEntity extends Entity {
    private static final Set<MobCategory> ALLOWED_CATEGORIES = EnumSet.of(
            MobCategory.AMBIENT,
            MobCategory.CREATURE,
            MobCategory.AXOLOTLS,
            MobCategory.UNDERGROUND_WATER_CREATURE,
            MobCategory.WATER_CREATURE
    );
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(SanctuaryDomeEntity.class, EntityDataSerializers.INT);
    private Player owner;
    private double radius;
    private int healTicks;
    public double growthFactor;
    public float alpha;

    public SanctuaryDomeEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.radius = 4;
        this.growthFactor = 0.0;
        this.alpha = 0.75f;
        this.setGlowingTag(true);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER_ID, 0);
    }

    public void setOwner(Player owner) {
        this.owner = owner;
        this.entityData.set(OWNER_ID, owner.getId());
        this.setNoGravity(true);
        this.setInvulnerable(true);
    }

    public Player getOwner() {
        if (this.level().isClientSide) {
            return (Player) this.level().getEntity(this.entityData.get(OWNER_ID));
        }
        return this.owner;
    }

    private boolean isAllowedCategory(MobCategory category) {
        return ALLOWED_CATEGORIES.contains(category);
    }

    public double getRadius() {
        return radius;
    }

    public double getGrowthFactor(){
        return growthFactor;
    }

    @Override
    public void tick() {
        super.tick();

        Player currentOwner = getOwner();
        if (currentOwner == null || !currentOwner.isAlive()) {
            this.discard();
            return;
        }

        if (this.tickCount < 10) {
            growthFactor = this.tickCount / 10.0;
        } else {
            growthFactor = 1.0;
        }

        if (this.tickCount >= (8 * 20) && this.tickCount < (12 * 20 - 20)) {
            float oscillation = 0.25f * (float) Math.sin((this.tickCount - (8 * 20)) / 5.0);
            alpha = 0.75f + oscillation;
        } else if (this.tickCount >= (12 * 20 - 20)) {
            alpha = 0.75f * (1.0f - ((this.tickCount - (12 * 20 - 20)) / 20.0f));
        } else {
            alpha = 0.75f;
        }

        double currentRadius = radius * growthFactor;

        if (!this.level().isClientSide()) {
            List<Entity> nearbyEntities = this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(currentRadius));
            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity livingEntity) {
                    MobCategory category = entity.getType().getCategory();
                    if (isAlly(owner, entity) || entity == owner || isAllowedCategory(category)) {
                        ++healTicks;
                        if (healTicks >= 20) {
                            livingEntity.heal(3.0F);
                            healTicks = 0;
                        }
                    } else {
                        repelEntityFromDome(livingEntity, currentRadius);
                    }
                } else if (entity instanceof net.minecraft.world.entity.projectile.Projectile) {
                    if (isHostileProjectile(entity, currentOwner)) {
                        repelProjectileFromDome(entity, currentRadius);
                    }
                }
            }
        }

        if (this.tickCount > 12 * 20) {
            this.discard();
        }
    }

    private boolean isAlly(Player player, Entity entity) {
        if (entity instanceof Player targetPlayer) {
            if (player instanceof ServerPlayer serverPlayer && targetPlayer instanceof ServerPlayer serverTargetPlayer) {
                return PartyUtils.isAlly(serverPlayer, serverTargetPlayer);
            }
        }
        return false;
    }


    private void repelEntityFromDome(LivingEntity entity, double currentRadius) {
        double dx = entity.getX() - this.getX();
        double dz = entity.getZ() - this.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance < currentRadius) {
            double repelStrength = (1 / distance) * 0.5 + 0.2;

            double norm = Math.sqrt(dx * dx + dz * dz);
            dx = dx / norm;
            dz = dz / norm;

            entity.setDeltaMovement(dx * repelStrength, entity.getDeltaMovement().y, dz * repelStrength);
        }
    }

    private void repelProjectileFromDome(Entity entity, double currentRadius) {
        double dx = entity.getX() - this.getX();
        double dz = entity.getZ() - this.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance < currentRadius) {
            // Reverse the projectile's direction
            double newDx = -entity.getDeltaMovement().x;
            double newDz = -entity.getDeltaMovement().z;
            entity.setDeltaMovement(newDx, entity.getDeltaMovement().y, newDz);

            // Optionally, you can increase the speed of the reflected projectile
            double speedMultiplier = 1.5; // Adjust this value as needed
            entity.setDeltaMovement(entity.getDeltaMovement().scale(speedMultiplier));

            // Update the entity's position slightly to avoid continuous collision
            entity.setPos(entity.getX() + newDx * 0.1, entity.getY(), entity.getZ() + newDz * 0.1);
        }
    }

    private boolean isHostileProjectile(Entity entity, Player owner) {
        if (entity instanceof net.minecraft.world.entity.projectile.Projectile projectile) {
            Entity shooter = projectile.getOwner();
            return shooter != owner && shooter instanceof LivingEntity livingShooter && !isAlly(owner, livingShooter);
        }
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        // Save data logic
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        // Load data logic
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
