package net.turtleboi.turtlecore.compat.aspects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.turtleboi.turtlecore.effect.CoreEffects;
import net.turtleboi.turtlecore.network.CoreNetworking;
import net.turtleboi.turtlecore.network.packet.util.SendParticlesS2C;
import net.turtleboi.turtlecore.util.EffectApplicationUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class AspectsCompat {
    public static double getInfernumAmplifier(LivingEntity livingEntity){
        double infernumAmplifier = 0;
        Object aspect = getAspect("INFERNUM_ASPECT");
        Object attribute = invokeGet(aspect);
        if (livingEntity.getAttribute((Attribute) attribute) != null) {
            infernumAmplifier = livingEntity.getAttribute((Attribute) attribute).getValue();
        }
        return infernumAmplifier;
    }

    public static double getGlaciusAmplifier(LivingEntity livingEntity){
        double glaciusAmplifier = 0;
        Object aspect = getAspect("GLACIUS_ASPECT");
        Object attribute = invokeGet(aspect);
        if (livingEntity.getAttribute((Attribute) attribute) != null) {
            glaciusAmplifier = livingEntity.getAttribute((Attribute) attribute).getValue();
        }
        return glaciusAmplifier;
    }

    public static double getTerraAmplifier(LivingEntity livingEntity){
        double terraAmplifier = 0;
        Object aspect = getAspect("TERRA_ASPECT");
        Object attribute = invokeGet(aspect);
        if (livingEntity.getAttribute((Attribute) attribute) != null) {
            terraAmplifier = livingEntity.getAttribute((Attribute) attribute).getValue();
        }
        return terraAmplifier;
    }

    public static double getTempestasAmplifier(LivingEntity livingEntity){
        double tempestasAmplifier = 0;
        Object aspect = getAspect("TEMPESTAS_ASPECT");
        Object attribute = invokeGet(aspect);
        if (livingEntity.getAttribute((Attribute) attribute) != null) {
            tempestasAmplifier = livingEntity.getAttribute((Attribute) attribute).getValue();
        }
        return tempestasAmplifier;
    }

    public static double getArcaniAmplifier(LivingEntity livingEntity){
        double arcaniAmplifier = 0;
        Object aspect = getAspect("ARCANI_ASPECT");
        Object attribute = invokeGet(aspect);
        if (livingEntity.getAttribute((Attribute) attribute) != null) {
            arcaniAmplifier = livingEntity.getAttribute((Attribute) attribute).getValue();
        }
        return arcaniAmplifier;
    }

    public static double getArcaniFactor(LivingEntity livingEntity) {
        double arcaniAmplifier = getArcaniAmplifier(livingEntity);
        return  1 + (arcaniAmplifier / 4.0);
    }

    public static double getUmbreAmplifier(LivingEntity livingEntity){
        double umbreAmplifier = 0;
        Object aspect = getAspect("UMBRE_ASPECT");
        Object attribute = invokeGet(aspect);
        if (livingEntity.getAttribute((Attribute) attribute) != null) {
            umbreAmplifier = livingEntity.getAttribute((Attribute) attribute).getValue();
        }
        return umbreAmplifier;
    }

    public static int getGlaciusFreezeDuration(LivingEntity chillerEntity, int freezeDuration) {
        double arcaniFactor = getArcaniFactor(chillerEntity);
        return (int) (freezeDuration * arcaniFactor);
    }

    public static void applyGlaciusTerraSynergy(LivingEntity chillerEntity, int pAmplifier){
        double arcaniFactor = getArcaniFactor(chillerEntity);
        double terraAmplifier = getTerraAmplifier(chillerEntity);
        if (terraAmplifier > 0) {
            if (!chillerEntity.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
                chillerEntity.addEffect(
                        new MobEffectInstance(
                                MobEffects.DAMAGE_RESISTANCE,
                                (int) (600 * terraAmplifier * arcaniFactor),
                                pAmplifier,
                                false,
                                true,
                                true
                        )
                );
            } else if (chillerEntity.hasEffect(MobEffects.DAMAGE_RESISTANCE) &&
                    chillerEntity.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier() <= pAmplifier) {
                chillerEntity.addEffect(
                        new MobEffectInstance(
                                MobEffects.DAMAGE_RESISTANCE,
                                (int) (600 * terraAmplifier * arcaniFactor),
                                pAmplifier,
                                false,
                                true,
                                true
                        )
                );
            }
        }
    }

    public static void applyGlaciusTempestasSynergy(LivingEntity stunningPlayer, LivingEntity stunnedEntity) {
        double arcaniFactor = getArcaniFactor(stunningPlayer);
        double glaciusAmplifier = getGlaciusAmplifier(stunningPlayer);
        if (glaciusAmplifier > 0) {
            if (stunnedEntity.tickCount % 20 == 0) {
                addColdAuraForEntity(stunnedEntity, 30, (int) glaciusAmplifier);
                stunnedEntity.level().playSound(
                        null,
                        stunnedEntity.getX(),
                        stunnedEntity.getY(),
                        stunnedEntity.getZ(),
                        SoundEvents.PLAYER_HURT_FREEZE,
                        SoundSource.HOSTILE,
                        1.0F,
                        0.4f / (stunnedEntity.level().getRandom().nextFloat() * 0.4f + 0.8f)
                );
                RandomSource random = stunnedEntity.level().getRandom();
                int count = (int) ((glaciusAmplifier + 1) * 20);
                for (int i = 0; i < count; i++) {
                    double theta = random.nextDouble() * Math.PI;
                    double phi = random.nextDouble() * 2 * Math.PI;
                    double speed = 0.2 + random.nextDouble() * 0.3;
                    double xSpeed = speed * Math.sin(theta) * Math.cos(phi);
                    double ySpeed = speed * Math.cos(theta);
                    double zSpeed = speed * Math.sin(theta) * Math.sin(phi);
                    double offX = (random.nextDouble() - 0.5) * 0.2;
                    double offY = stunnedEntity.getBbHeight() / 2;
                    double offZ = (random.nextDouble() - 0.5) * 0.2;

                    CoreNetworking.sendToNear(new SendParticlesS2C(
                            ParticleTypes.SNOWFLAKE,
                            stunnedEntity.getX() + offX,
                            stunnedEntity.getY() + offY,
                            stunnedEntity.getZ() + offZ,
                            xSpeed,ySpeed,zSpeed), stunnedEntity);
                }
                AABB ignitionArea = new AABB(stunnedEntity.getX() - 2, stunnedEntity.getY() - 2, stunnedEntity.getZ() - 2,
                        stunnedEntity.getX() + 2, stunnedEntity.getY() + 2, stunnedEntity.getZ() + 2);
                List<LivingEntity> chilledEntities = stunnedEntity.level().getEntitiesOfClass(LivingEntity.class, ignitionArea, e -> e != stunnedEntity && !(e instanceof Player));
                for (LivingEntity chilledEntity : chilledEntities) {
                    EffectApplicationUtil.setChiller(chilledEntity, stunningPlayer);
                    int chillTicks = (int) ((40 * glaciusAmplifier) * arcaniFactor);
                    chilledEntity.addEffect(new MobEffectInstance(CoreEffects.CHILLED.get(), chillTicks, (int) glaciusAmplifier - 1));
                }
            }
        }
    }

    public static void applyTempestasUmbreSynergy(LivingEntity stunningPlayer, LivingEntity stunnedEntity) {
        if (getUmbreAmplifier(stunningPlayer) > 0) {
            try {
                Class<?> singularityEntityClass = Class.forName("net.turtleboi.aspects.entity.entities.SingularityEntity");

                Class<?> modEntitiesClass = Class.forName("net.turtleboi.aspects.entity.ModEntities");
                Field singularityField = modEntitiesClass.getField("SINGULARITY");
                Object registryObj = singularityField.get(null);

                Method getMethod = registryObj.getClass().getMethod("get");
                Object singularityEntityType = getMethod.invoke(registryObj);

                Class<?> entityTypeClass = Class.forName("net.minecraft.world.entity.EntityType");
                Class<?> levelClass = Class.forName("net.minecraft.world.level.Level");
                Constructor<?> constructor = singularityEntityClass.getConstructor(entityTypeClass, levelClass);

                Object blackHole = constructor.newInstance(singularityEntityType, stunnedEntity.level());
                Method setOwnerMethod = singularityEntityClass.getMethod("setOwner", LivingEntity.class);
                setOwnerMethod.invoke(blackHole, stunnedEntity);

                Method setPosMethod = singularityEntityClass.getMethod("setPos", double.class, double.class, double.class);
                double x = stunnedEntity.getX();
                double y = stunnedEntity.getY() + stunnedEntity.getBbHeight();
                double z = stunnedEntity.getZ();
                setPosMethod.invoke(blackHole, x, y, z);
                stunnedEntity.level().addFreshEntity((Entity) blackHole);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void applyTempestasArcaniInfernumSynergy(LivingEntity stunningPlayer, LivingEntity stunnedEntity) {
        double arcaniAmplifier = getArcaniAmplifier(stunningPlayer);
        double infernumAmplifier = getInfernumAmplifier(stunningPlayer);
        if (getArcaniAmplifier(stunningPlayer) > 0) {
            Level level = stunnedEntity.level();
            if (level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < (arcaniAmplifier / 2); i++) {
                    LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, serverLevel);
                    lightning.setPos(stunnedEntity.getX(), stunnedEntity.getY(), stunnedEntity.getZ());
                    serverLevel.addFreshEntity(lightning);
                }

                if (infernumAmplifier > 0) {
                    AABB ignitionArea = new AABB(stunnedEntity.getX() - 2, stunnedEntity.getY() - 2, stunnedEntity.getZ() - 2,
                            stunnedEntity.getX() + 2, stunnedEntity.getY() + 2, stunnedEntity.getZ() + 2);
                    List<LivingEntity> ignitedEntities = stunnedEntity.level().getEntitiesOfClass(LivingEntity.class, ignitionArea, e -> e != stunnedEntity && !(e instanceof Player));
                    for (LivingEntity ignitedEntity : ignitedEntities) {
                        EffectApplicationUtil.setIgnitor(ignitedEntity, stunningPlayer);
                    }
                }
            }
        }
    }

    private static Object getAspect(String aspectFieldName) {
        try {
            Class<?> modAttributesClass = Class.forName("net.turtleboi.aspects.util.ModAttributes");
            Field field = modAttributesClass.getField(aspectFieldName);
            return field.get(null);
        } catch (Exception e) {
            return null;
        }
    }

    private static Object getEntity(String entityFieldName) {
        try {
            Class<?> modEntitiesClass = Class.forName("net.turtleboi.aspects.entity.ModEntities");
            Field field = modEntitiesClass.getField(entityFieldName);
            return field.get(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static void addColdAuraForEntity(Entity stunnedEntity, int auraTicks, double glaciusAmplifier) {
        try {
            Class<?> rendererClass = Class.forName("net.turtleboi.aspects.client.renderer.ColdAuraRenderer");
            Method addAuraMethod = rendererClass.getMethod("addAuraForEntity", Entity.class, long.class, int.class, int.class);
            addAuraMethod.invoke(null, stunnedEntity, System.currentTimeMillis(), auraTicks, (int) glaciusAmplifier);
        } catch (Exception ignored) {

        }
    }

    private static Object invokeGet(Object getObject) {
        try {
            Method getMethod = getObject.getClass().getMethod("get");
            return getMethod.invoke(getObject);
        } catch (Exception e) {
            return null;
        }
    }
}
