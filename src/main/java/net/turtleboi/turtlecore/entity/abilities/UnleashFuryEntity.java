package net.turtleboi.turtlecore.entity.abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class UnleashFuryEntity extends Entity {
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(UnleashFuryEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TEXTURE_INDEX = SynchedEntityData.defineId(UnleashFuryEntity.class, EntityDataSerializers.INT);

    private int textureTickCounter = 0;
    private Player owner;
    protected int age;

    public UnleashFuryEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER_ID, 0);
        this.entityData.define(TEXTURE_INDEX, 0);
    }


    public void setOwner(Player owner) {
        this.owner = owner;
        if (owner != null) {
            this.entityData.set(OWNER_ID, owner.getId());
        }
    }

    public Player getOwner() {
        if (this.level().isClientSide) {
            return (Player)this.level().getEntity(this.entityData.get(OWNER_ID));
        }
        return this.owner;
    }

    @Override
    public void tick() {
        super.tick();

        age = this.tickCount;

        if (!this.level().isClientSide) {
            textureTickCounter++;
            int framesPerTick = 2;
            if (textureTickCounter >= 1) {
                for (int i = 0; i < framesPerTick; i++) {
                    int newIndex = (this.getTextureIndex() + 1) % getMaxTextureIndex();
                    this.setTextureIndex(newIndex);
                }
                textureTickCounter = 0;
            }
        }

        Player currentOwner = getOwner();
        if (currentOwner == null || !currentOwner.isAlive()) {
            this.discard();
        }

        if (currentOwner != null) {
            //++age;
            double x = currentOwner.getX();
            double y = currentOwner.getY() + 1;
            double z = currentOwner.getZ();
            this.setPos(x, y, z);

            if (age >= 11) {
                this.discard();
                this.setOwner(null);
            }
        }
    }

    public int getTextureIndex() {
        return this.entityData.get(TEXTURE_INDEX);
    }

    public void setTextureIndex(int index) {
        this.entityData.set(TEXTURE_INDEX, index);
    }

    public int getMaxTextureIndex() {
        return 21;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
