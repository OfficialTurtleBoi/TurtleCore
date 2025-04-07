package net.turtleboi.turtlecore.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class CoreKeyBinding {
    public static final String KEY_CATEGORY_TBL = "key.category.turtlecore.tbl";
    public static final String KEY_LOCK_ON = "key.category.turtlecore.lock_on";
    public static final KeyMapping LOCK_ON = new KeyMapping(KEY_LOCK_ON, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, KEY_CATEGORY_TBL);
}
