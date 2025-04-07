package net.turtleboi.turtlecore.init;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CoreAttributeModifiers {
    public static UUID generateUUIDFromName(String name) {
        return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
    }

     //Applies a permanent modifier to the entity's attribute.
     public static void applyPermanentModifier(LivingEntity livingEntity, Attribute attribute, String name, double value, AttributeModifier.Operation operation) {
         UUID uuid = generateUUIDFromName(name);
         AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
         if (attributeInstance != null) {
             AttributeModifier existingModifier = attributeInstance.getModifier(uuid);
             if (existingModifier != null) {
                 attributeInstance.removeModifier(existingModifier);
             }
             attributeInstance.addPermanentModifier(new AttributeModifier(uuid, name, value, operation));
         }
     }

   //Applies a transient (temporary) modifier to the entity's attribute.
   public static void applyTransientModifier(LivingEntity livingEntity, Attribute attribute, String name, double value, AttributeModifier.Operation operation) {
       UUID uuid = generateUUIDFromName(name);
       AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
       if (attributeInstance != null) {
           AttributeModifier existingModifier = attributeInstance.getModifier(uuid);
           if (existingModifier != null) {
               attributeInstance.removeModifier(existingModifier);
           }
           attributeInstance.addTransientModifier(new AttributeModifier(uuid, name, value, operation));
       }
   }

    //Removes a modifier by its UUID from the entity's attribute.
    public static void removeModifier(LivingEntity livingEntity, Attribute attribute, String name) {
        UUID uuid = generateUUIDFromName(name);
        AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
        if (attributeInstance != null) {
            attributeInstance.removeModifier(uuid);
        }
    }

    //Removes all modifiers with a specific name from the entity's attribute.
    public static void removeModifierByName(Player player, Attribute attribute, String attributeName) {
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        if (attributeInstance != null) {
            for (AttributeModifier modifier : attributeInstance.getModifiers()) {
                if (modifier.getName().contains(attributeName)) {
                    attributeInstance.removeModifier(modifier.getId());
                }
            }
        }
    }

    //Removes all modifiers with a specific prefix from the entity's attribute.
    public static void removeModifiersByPrefix(LivingEntity livingEntity, Attribute attribute, String prefix) {
        AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
        if (attributeInstance != null) {
            attributeInstance.getModifiers().stream()
                    .filter(modifier -> modifier.getName().startsWith(prefix))
                    .forEach(attributeInstance::removeModifier);
        }
    }

    //Removes all modifiers with a specific suffix from the entity's attribute.
    public static void removeModifiersBySuffix(LivingEntity livingEntity, Attribute attribute, String suffix) {
        AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
        if (attributeInstance != null) {
            attributeInstance.getModifiers().stream()
                    .filter(modifier -> modifier.getName().endsWith(suffix))
                    .forEach(attributeInstance::removeModifier);
        }
    }
}
