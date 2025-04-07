package net.turtleboi.turtlecore.entity.weapons;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.turtleboi.turtlecore.enchantment.CoreEnchantments;
import net.turtleboi.turtlecore.enchantment.RuptureEnchantment;
import net.turtleboi.turtlecore.enchantment.SeekingEnchantment;
import net.turtleboi.turtlecore.util.TargetingUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public abstract class AbstractThrowableWeapon extends AbstractArrow {
    private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(AbstractThrowableWeapon.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(AbstractThrowableWeapon.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(AbstractThrowableWeapon.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<ItemStack> ITEM_STACK = SynchedEntityData.defineId(AbstractThrowableWeapon.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> HIT_BLOCK = SynchedEntityData.defineId(AbstractThrowableWeapon.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RETURNING = SynchedEntityData.defineId(AbstractThrowableWeapon.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> ROTATE_VARIATION = SynchedEntityData.defineId(AbstractThrowableWeapon.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Byte> LOYALTY_LEVEL = SynchedEntityData.defineId(AbstractThrowableWeapon.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> SEEKING_LEVEL = SynchedEntityData.defineId(AbstractThrowableWeapon.class, EntityDataSerializers.BYTE);

    protected ItemStack weaponItem = ItemStack.EMPTY;
    private boolean dealtDamage = false;
    private BlockState lastState;
    private BlockPos hitBlockPos;
    public float lastYP = 0.0F;
    public float lastZP = 0.0F;
    private int returnTickCount = 0;

    public AbstractThrowableWeapon(EntityType<? extends AbstractThrowableWeapon> entityType, Level level) {
        super(entityType, level);
    }

    public AbstractThrowableWeapon(EntityType<? extends AbstractThrowableWeapon> entityType, Level level, LivingEntity thrower, ItemStack itemStack, ResourceLocation resourceLocation, Float thrownDamage) {
        super(entityType, thrower, level);
        if (itemStack != null && weaponItem.isEmpty()) {
            weaponItem = itemStack.copy();
        }
        entityData.set(ITEM_STACK, weaponItem);
        entityData.set(TEXTURE, resourceLocation.toString());
        entityData.set(ID_FOIL, weaponItem.hasFoil());
        entityData.set(DAMAGE, thrownDamage);
        entityData.set(HIT_BLOCK, false);
        entityData.set(RETURNING, false);

        entityData.set(LOYALTY_LEVEL, (byte) EnchantmentHelper.getLoyalty(weaponItem));
        entityData.set(SEEKING_LEVEL, (byte) EnchantmentHelper.getTagEnchantmentLevel(CoreEnchantments.SEEKING.get(), weaponItem));

        float rotateVariation = 1.0F + level.random.nextFloat() * 24.0F;
        entityData.set(ROTATE_VARIATION, rotateVariation);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ITEM_STACK, ItemStack.EMPTY);
        entityData.define(ID_FOIL, false);
        entityData.define(TEXTURE, "");
        entityData.define(DAMAGE, 0.0F);
        entityData.define(HIT_BLOCK, false);
        entityData.define(RETURNING, false);
        entityData.define(ROTATE_VARIATION, 0.0F);
        entityData.define(LOYALTY_LEVEL, (byte) 0);
        entityData.define(SEEKING_LEVEL, (byte) 0);
    }

    public float getWeaponDamage() {
        return entityData.get(DAMAGE);
    }

    public ResourceLocation getTexture() {
        return new ResourceLocation(entityData.get(TEXTURE));
    }

    public float getRotateVariation() {
        return entityData.get(ROTATE_VARIATION);
    }

    public byte getLoyaltyLevel() {
        return entityData.get(LOYALTY_LEVEL);
    }

    public boolean isReturning() {
        return entityData.get(RETURNING);
    }

    public byte getSeekingLevel() {
        return entityData.get(SEEKING_LEVEL);
    }

    @Override
    public void tick() {
        super.tick();
        weaponItem = entityData.get(ITEM_STACK);
        Player owner = (Player) getOwner();

        if (owner == null){
            return;
        }

        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        if (!level().isClientSide) {
            if (weaponItem != null && !weaponItem.isEmpty()) {
                int seekingLevel = EnchantmentHelper.getTagEnchantmentLevel(CoreEnchantments.SEEKING.get(), weaponItem);
                if (seekingLevel > 0 && CoreEnchantments.SEEKING.get() instanceof SeekingEnchantment seekingEnchantment) {
                    if (seekingEnchantment.isSeeking(this)) {
                        seekingEnchantment.applyEffect(owner, this, seekingLevel);
                    }
                }
            }


            if (this.shouldFall()) {
                this.startFalling();
            }

            if (getLoyaltyLevel() > 0) {
                if (dealtDamage || hasHitBlock()) {
                    if (!isAcceptableReturnOwner()) {
                        spawnAtLocation(getPickupItem(), 0.1F);
                        discard();
                    } else {
                        entityData.set(RETURNING, true);
                        returnTickCount++;
                        setNoGravity(true);
                        setNoPhysics(true);
                        Vec3 vec3 = new Vec3(owner.getX() - getX(), (owner.getEyeY() - 1) - getY(), owner.getZ() - getZ());
                        setPosRaw(getX(), getY() + vec3.y * 0.015 * getLoyaltyLevel(), getZ());
                        if (level().isClientSide) {
                            yOld = getY();
                        }
                        double d0 = 0.1 * getLoyaltyLevel();
                        setDeltaMovement(getDeltaMovement().scale(0.95).add(vec3.normalize().scale(d0)));
                        if (returnTickCount == 0) {
                            playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
                        }
                        if (isReturning()) {
                            if (distanceTo(owner) < 1.0D) {
                                if (!level().isClientSide && dealtDamage && isAlive() && tickCount() > 20) {
                                    owner.take(this, 1);
                                    owner.getInventory().add(getPickupItem());
                                    discard();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isAcceptableReturnOwner() {
        Entity owner = getOwner();
        return owner != null && owner.isAlive() && !owner.isSpectator();
    }

    public boolean shouldFall() {
        if (hitBlockPos == null) {
            return false;
        }

        if (hasHitBlock() && !inGround) {
            BlockState currentState = this.level().getBlockState(this.hitBlockPos);
            return !currentState.equals(this.lastState) || currentState.isAir();
        }
        return false;
    }

    private void startFalling() {
        setNoGravity(false);
        this.setDeltaMovement(new Vec3(0, -0.25, 0));
    }

    @Override
    public void shoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
        super.shoot(pX, pY, pZ, pVelocity, pInaccuracy);
        if (getSeekingLevel() > 0 && CoreEnchantments.SEEKING.get() instanceof SeekingEnchantment seekingEnchantment) {
            seekingEnchantment.startSeeking(this);
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        if (this.isReturning()) {
            this.entityData.set(HIT_BLOCK, true);
        } else {
            this.entityData.set(HIT_BLOCK, true);
            this.lastState = this.level().getBlockState(blockHitResult.getBlockPos());
            this.hitBlockPos = blockHitResult.getBlockPos();
            Vec3 vec3 = blockHitResult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
            this.setDeltaMovement(vec3);
            Vec3 adjustment = vec3.normalize().scale(0.05000000074505806);
            this.setPosRaw(this.getX() - adjustment.x, this.getY() - adjustment.y, this.getZ() - adjustment.z);
            setNoGravity(true);
            this.inGround = true;
            playSound(SoundEvents.TRIDENT_HIT_GROUND, 10.0F, 1.0F);
        }
        this.dealtDamage = true;
    }

    public boolean hasHitBlock() {
        return entityData.get(HIT_BLOCK);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        if (!isReturning()) {
            Entity owner = getOwner();
            Entity target = result.getEntity();
            float damage = getWeaponDamage();

            if (target instanceof LivingEntity livingTarget) {
                damage += EnchantmentHelper.getDamageBonus(weaponItem, livingTarget.getMobType());
                int fireAspectLevel = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.FIRE_ASPECT, weaponItem);
                if (fireAspectLevel > 0) {
                    livingTarget.setSecondsOnFire(fireAspectLevel * 4);
                }
                int ruptureLevel = EnchantmentHelper.getTagEnchantmentLevel(CoreEnchantments.RUPTURE.get(), weaponItem);
                if (ruptureLevel > 0 && CoreEnchantments.RUPTURE.get() instanceof RuptureEnchantment ruptureEnchantment) {
                    ruptureEnchantment.applyEffect(livingTarget, (LivingEntity) owner, ruptureLevel);
                }
            }

            if (!dealtDamage) {
                DamageSource source = new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.THROWN), this, owner == null ? this : owner);

                if (target.hurt(source, damage)) {
                    if (target instanceof LivingEntity) {
                        assert owner != null;
                        EnchantmentHelper.doPostHurtEffects((LivingEntity) target, owner);
                        EnchantmentHelper.doPostDamageEffects((LivingEntity) owner, target);
                        if (getSeekingLevel() > 0 && CoreEnchantments.SEEKING.get() instanceof SeekingEnchantment seekingEnchantment){
                            seekingEnchantment.stopSeeking(this);
                        }
                    }
                    dealtDamage = true;
                }

                if (!level().isClientSide) {
                    weaponItem.hurtAndBreak(1, (LivingEntity) getOwner(), (player) -> {
                        player.broadcastBreakEvent(player.getUsedItemHand());
                        playSound(SoundEvents.ITEM_BREAK, 10.0F, 1.0F);
                    });
                    entityData.set(ITEM_STACK, weaponItem.copy());
                    if (weaponItem.isEmpty()) {
                        discard();
                    }
                }
            }
            setDeltaMovement(getDeltaMovement().multiply(-0.01, -0.1, -0.01));
            playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
        }
    }

    @Override
    public void doPostHurtEffects(@NotNull LivingEntity target) {
        super.doPostHurtEffects(target);

    }


    @Override
    public void playerTouch(@NotNull Player player) {
        Entity owner = getOwner();
        if (!level().isClientSide && dealtDamage && isAlive() && tickCount() > 20 && player == owner) {
            player.take(this, 1);
            player.getInventory().add(getPickupItem());
            discard();
        }
    }

    public @NotNull ItemStack getPickupItem() {
        if (weaponItem.isEmpty()) {
            return getDefaultWeaponItem();
        }
        return weaponItem.copy();
    }

    protected abstract ItemStack getDefaultWeaponItem();

    private int tickCount() {
        return tickCount;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put(getWeaponTagName(), weaponItem.save(new CompoundTag()));
        compound.putBoolean("DealtDamage", dealtDamage);
        compound.putBoolean("HitBlock", entityData.get(HIT_BLOCK));
        compound.putBoolean("Returning", entityData.get(RETURNING));
        compound.putFloat("LastYP", lastYP);
        compound.putFloat("LastZP", lastZP);
        compound.putFloat("RotateVariation", entityData.get(ROTATE_VARIATION));
        compound.putByte("LoyaltyLevel", getLoyaltyLevel());
        compound.putByte("SeekingLevel", getSeekingLevel());
        if (!entityData.get(TEXTURE).isEmpty()) {
            compound.putString("Texture", entityData.get(TEXTURE));
        }
        compound.putFloat("Damage", entityData.get(DAMAGE));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains(getWeaponTagName(), 10)) {
            weaponItem = ItemStack.of(compound.getCompound(getWeaponTagName()));
        }
        dealtDamage = compound.getBoolean("DealtDamage");
        if (compound.contains("HitBlock")) {
            entityData.set(HIT_BLOCK, compound.getBoolean("HitBlock"));
        }
        if (compound.contains("Returning")) {
            entityData.set(RETURNING, compound.getBoolean("Returning"));
        }
        if (compound.contains("LastYP")) {
            lastYP = compound.getFloat("LastYP");
        }
        if (compound.contains("LastZP")) {
            lastZP = compound.getFloat("LastZP");
        }
        if (compound.contains("RotateVariation")) {
            entityData.set(ROTATE_VARIATION, compound.getFloat("RotateVariation"));
        }
        if (compound.contains("LoyaltyLevel")) {
            entityData.set(LOYALTY_LEVEL, compound.getByte("LoyaltyLevel"));
        }
        if (compound.contains("SeekingLevel")) {
            entityData.set(SEEKING_LEVEL, compound.getByte("SeekingLevel"));
        }
        entityData.set(ID_FOIL, weaponItem.hasFoil());
        if (compound.contains("Texture", 8)) {
            entityData.set(TEXTURE, compound.getString("Texture"));
        }
        if (compound.contains("Damage", 5)) {
            entityData.set(DAMAGE, compound.getFloat("Damage"));
        }
        entityData.set(ITEM_STACK, weaponItem);
    }

    protected abstract String getWeaponTagName();
}
